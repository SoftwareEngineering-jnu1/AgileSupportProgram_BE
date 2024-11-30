package SEproject.controller;

import SEproject.dto.NewMemberDTO;
import SEproject.repository.memoryrepository.MemoryIssueRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import SEproject.domain.Epic;
import SEproject.domain.Member;
import SEproject.dto.EditEpicDTO;
import SEproject.dto.NewEpicDTO;
import SEproject.dto.NewProjectDTO;
import SEproject.repository.memoryrepository.MemoryEpicRepository;
import SEproject.repository.memoryrepository.MemoryMemberRepository;
import SEproject.repository.memoryrepository.MemoryProjectRepository;
import SEproject.web.SessionConst;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EpicCreateEditIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemoryMemberRepository memoryMemberRepository;

    @Autowired
    private MemoryProjectRepository memoryProjectRepository;

    @Autowired
    private MemoryEpicRepository memoryEpicRepository;

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

        // 로그인 세션 생성
        Member loginMember = memoryMemberRepository.findByEmailId("user1@example.com").orElse(null);
        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
    }

    @Test
    void 에픽생성_정상_케이스() throws Exception {
        // 요청 데이터 준비
        Map<String, Object> newEpicDTO = new HashMap<>();
        newEpicDTO.put("title", "Test Epic");
        newEpicDTO.put("startDate", "2024-01-01"); // LocalDate 대신 문자열 사용
        newEpicDTO.put("endDate", "2024-02-01");   // LocalDate 대신 문자열 사용

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(newEpicDTO);

        // 프로젝트 ID 조회
        Long projectId = memoryProjectRepository.findAll().get(0).getId();

        // API 호출 및 검증
        mockMvc.perform(post("/SE/project/" + projectId + "/addepic")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.title").value("Test Epic"));

        // 저장소 확인
        assertTrue(memoryEpicRepository.findAll().stream()
                .anyMatch(epic -> "Test Epic".equals(epic.getTitle())));
    }

    @Test
    void 에픽수정_정상_케이스() throws Exception {
        // Epic 데이터 설정
        NewEpicDTO newEpicDTO = new NewEpicDTO();
        newEpicDTO.setTitle("Initial Epic");
        newEpicDTO.setStartDate(LocalDate.parse("2024-01-01")); // String -> LocalDate 변환
        newEpicDTO.setEndDate(LocalDate.parse("2024-02-01"));   // String -> LocalDate 변환
        Long projectId = memoryProjectRepository.findAll().get(0).getId();
        Epic savedEpic = memoryEpicRepository.save(newEpicDTO, projectId);

        // 수정 데이터 준비
        Map<String, Object> editEpicDTO = new HashMap<>();
        editEpicDTO.put("title", "Updated Epic");
        editEpicDTO.put("startDate", "2024-01-15"); // LocalDate 대신 문자열 사용
        editEpicDTO.put("endDate", "2024-02-15");   // LocalDate 대신 문자열 사용

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(editEpicDTO);

        // API 호출 및 검증
        mockMvc.perform(post("/SE/project/" + projectId + "/" + savedEpic.getId() + "/edit")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.title").value("Updated Epic"));

        // 저장소 확인
        Epic updatedEpic = memoryEpicRepository.findById(savedEpic.getId());
        assertEquals("Updated Epic", updatedEpic.getTitle());
        assertEquals(LocalDate.parse("2024-01-15"), updatedEpic.getStartDate());
        assertEquals(LocalDate.parse("2024-02-15"), updatedEpic.getEndDate());
    }
}