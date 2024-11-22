package SEproject.controller;

import SEproject.dto.NewMemberDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import SEproject.domain.Issue;
import SEproject.domain.Member;
import SEproject.dto.NewEpicDTO;
import SEproject.dto.NewIssueDTO;
import SEproject.dto.NewProjectDTO;
import SEproject.repository.memoryrepository.MemoryEpicRepository;
import SEproject.repository.memoryrepository.MemoryIssueRepository;
import SEproject.repository.memoryrepository.MemoryMemberRepository;
import SEproject.repository.memoryrepository.MemoryProjectRepository;
import SEproject.web.SessionConst;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class IssueIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemoryMemberRepository memoryMemberRepository;

    @Autowired
    private MemoryProjectRepository memoryProjectRepository;

    @Autowired
    private MemoryEpicRepository memoryEpicRepository;

    @Autowired
    private MemoryIssueRepository memoryIssueRepository;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        // 회원 데이터 설정
        NewMemberDTO member1 = new NewMemberDTO();
        member1.setUsername("User One");
        member1.setPassword("password123");
        member1.setEmailId("user1@example.com");
        memoryMemberRepository.save(member1);

        // 프로젝트 데이터 설정
        NewProjectDTO projectDTO = new NewProjectDTO();
        projectDTO.setProjectName("Test Project");
        projectDTO.setMembersEmailId(List.of("user1@example.com"));
        memoryProjectRepository.save(projectDTO);

        // Epic 데이터 설정
        NewEpicDTO newEpicDTO = new NewEpicDTO();
        newEpicDTO.setTitle("Test Epic");
        newEpicDTO.setStartDate(LocalDate.parse("2024-01-01"));
        newEpicDTO.setEndDate(LocalDate.parse("2024-02-01"));
        Long projectId = memoryProjectRepository.findAll().get(0).getId();
        memoryEpicRepository.save(newEpicDTO, projectId);

        // 로그인 세션 생성
        Member loginMember = memoryMemberRepository.findByEmailId("user1@example.com").orElse(null);
        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
    }

    @Test
    void 이슈생성_정상_케이스() throws Exception {
        // 요청 데이터 준비
        Map<String, Object> newIssueDTO = new HashMap<>();
        newIssueDTO.put("title", "Test Issue");
        newIssueDTO.put("startDate", "2024-01-10"); // LocalDate 대신 문자열
        newIssueDTO.put("endDate", "2024-01-20");
        newIssueDTO.put("mainMemberName", "User One");
        newIssueDTO.put("progressStatus", "In Progress");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(newIssueDTO);

        // Epic ID 조회
        Long epicId = memoryEpicRepository.findAll().get(0).getId();
        Long projectId = memoryProjectRepository.findAll().get(0).getId();

        // API 호출 및 검증
        mockMvc.perform(post("/SE/project/" + projectId + "/" + epicId + "/addissue")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.title").value("Test Issue"));

        // 저장소 확인
        assertTrue(memoryIssueRepository.findAll().stream()
                .anyMatch(issue -> "Test Issue".equals(issue.getTitle())));
    }

    @Test
    void 이슈수정_정상_케이스() throws Exception {
        // Issue 데이터 설정
        NewIssueDTO newIssueDTO = new NewIssueDTO();
        newIssueDTO.setTitle("Initial Issue");
        newIssueDTO.setStartDate(LocalDate.parse("2024-01-10"));
        newIssueDTO.setEndDate(LocalDate.parse("2024-01-20"));
        newIssueDTO.setMainMemberName("User One");
        newIssueDTO.setProgressStatus("To Do");
        Long epicId = memoryEpicRepository.findAll().get(0).getId();
        Issue savedIssue = memoryIssueRepository.save(newIssueDTO, epicId);

        // 수정 데이터 준비
        Map<String, Object> editIssueDTO = new HashMap<>();
        editIssueDTO.put("title", "Updated Issue");
        editIssueDTO.put("startDate", "2024-01-15"); // LocalDate 대신 문자열
        editIssueDTO.put("endDate", "2024-01-25");
        editIssueDTO.put("mainMemberName", "User One");
        editIssueDTO.put("progressStatus", "In Progress");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(editIssueDTO);

        // 프로젝트 ID 조회
        Long projectId = memoryProjectRepository.findAll().get(0).getId();

        // API 호출 및 검증
        mockMvc.perform(post("/SE/project/" + projectId + "/" + epicId + "/" + savedIssue.getId() + "/edit")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.issue.title").value("Updated Issue")); // 경로 수정

        // 저장소 확인
        Issue updatedIssue = memoryIssueRepository.findById(savedIssue.getId());
        assertEquals("Updated Issue", updatedIssue.getTitle());
        assertEquals(LocalDate.parse("2024-01-15"), updatedIssue.getStartDate());
        assertEquals(LocalDate.parse("2024-01-25"), updatedIssue.getEndDate());
    }
}