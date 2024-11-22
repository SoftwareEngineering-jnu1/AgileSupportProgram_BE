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
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class TimelineIntegrationTest {

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
        member1.setEmailId("user1@example.com");
        member1.setUsername("User One");
        member1.setPassword("password123");
        memoryMemberRepository.save(member1);

        // 프로젝트 데이터 설정
        NewProjectDTO projectDTO = new NewProjectDTO();
        projectDTO.setProjectName("Test Project");
        projectDTO.setMembersEmailId(List.of("user1@example.com"));
        memoryProjectRepository.save(projectDTO);

        // Epic 데이터 설정
        NewEpicDTO epicDTO = new NewEpicDTO();
        epicDTO.setTitle("Test Epic");
        epicDTO.setStartDate(LocalDate.parse("2024-01-01"));
        epicDTO.setEndDate(LocalDate.parse("2024-02-01"));
        Long projectId = memoryProjectRepository.findAll().get(0).getId();
        memoryEpicRepository.save(epicDTO, projectId);

        // Issue 데이터 설정
        NewIssueDTO issueDTO = new NewIssueDTO();
        issueDTO.setTitle("Test Issue");
        issueDTO.setStartDate(LocalDate.parse("2024-01-05"));
        issueDTO.setEndDate(LocalDate.parse("2024-01-15"));
        issueDTO.setMainMemberName("User One");
        issueDTO.setProgressStatus("In Progress");
        Long epicId = memoryEpicRepository.findAll().get(0).getId();
        memoryIssueRepository.save(issueDTO, epicId);

        // 로그인 세션 생성
        Member loginMember = memoryMemberRepository.findByEmailId("user1@example.com").orElse(null);
        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
    }

    @Test
    void 타임라인목록조회_정상_케이스() throws Exception {
        // 프로젝트 ID 조회
        Long projectId = memoryProjectRepository.findAll().get(0).getId();

        // API 호출 및 검증
        mockMvc.perform(get("/SE/project/" + projectId + "/timeline")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }


    @Test
    void 타임라인목록조회_세션인증오류_케이스() throws Exception {
        // 프로젝트 ID 조회
        Long projectId = memoryProjectRepository.findAll().get(0).getId();

        // API 호출 (세션 없이)
        mockMvc.perform(get("/SE/project/" + projectId + "/timeline")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.data").value("Unauthorized access"));
    }
}
