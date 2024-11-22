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
import SEproject.domain.Epic;
import SEproject.domain.Member;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class SprintIntegrationTest {

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

        // Epic 데이터 설정
        NewEpicDTO epicDTO = new NewEpicDTO();
        epicDTO.setTitle("Test Epic");
        epicDTO.setStartDate(LocalDate.parse("2024-01-01"));
        epicDTO.setEndDate(LocalDate.parse("2024-02-01"));
        Long projectId = memoryProjectRepository.findAll().get(0).getId();
        memoryEpicRepository.save(epicDTO, projectId);

        // 로그인 세션 생성
        Member loginMember = memoryMemberRepository.findByEmailId("user1@example.com").orElse(null);
        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
    }

    @Test
    void 스프린트생성_정상_케이스() throws Exception {
        // 요청 데이터 준비
        Map<String, Object> newSprintDTO = new HashMap<>();
        newSprintDTO.put("epicTitle", "Test Epic");
        newSprintDTO.put("sprintName", "Sprint 1");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(newSprintDTO);

        // 프로젝트 ID 조회
        Long projectId = memoryProjectRepository.findAll().get(0).getId();

        // API 호출 및 검증
        mockMvc.perform(post("/SE/project/" + projectId + "/kanbanboard/newsprint")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.epicId").isNotEmpty());

        // 저장소 확인
        Epic epic = memoryEpicRepository.findByTitle("Test Epic").orElse(null);
        assertNotNull(epic);
        assertEquals("Sprint 1", epic.getSprintName());
    }

    @Test
    void 스프린트수정_정상_케이스() throws Exception {
        // Epic 데이터 설정
        Epic epic = memoryEpicRepository.findByTitle("Test Epic").orElse(null);
        assertNotNull(epic);
        epic.setSprintName("Initial Sprint");

        // 수정 데이터 준비
        Map<String, Object> editSprintDTO = new HashMap<>();
        editSprintDTO.put("epicTitle", "Test Epic");
        editSprintDTO.put("sprintName", "Updated Sprint");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(editSprintDTO);

        // 프로젝트 ID 조회
        Long projectId = memoryProjectRepository.findAll().get(0).getId();

        // API 호출 및 검증
        mockMvc.perform(post("/SE/project/" + projectId + "/kanbanboard/newsprint")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        // 저장소 확인
        Epic updatedEpic = memoryEpicRepository.findByTitle("Test Epic").orElse(null);
        assertNotNull(updatedEpic);
        assertEquals("Updated Sprint", updatedEpic.getSprintName());
    }
}
