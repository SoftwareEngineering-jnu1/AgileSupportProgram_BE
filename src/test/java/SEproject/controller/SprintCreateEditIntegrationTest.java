package SEproject.controller;

import SEproject.domain.Member;
import SEproject.domain.SprintRetrospective;
import SEproject.dto.NewEpicDTO;
import SEproject.dto.NewMemberDTO;
import SEproject.dto.NewProjectDTO;
import SEproject.dto.SubmitRetrospectiveDTO;
import SEproject.repository.memoryrepository.MemoryEpicRepository;
import SEproject.repository.memoryrepository.MemoryMemberRepository;
import SEproject.repository.memoryrepository.MemoryProjectRepository;
import SEproject.repository.memoryrepository.MemorySprintRetrospective;
import SEproject.web.SessionConst;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SprintReviewIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemoryMemberRepository memoryMemberRepository;

    @Autowired
    private MemoryProjectRepository memoryProjectRepository;

    @Autowired
    private MemoryEpicRepository memoryEpicRepository;

    @Autowired
    private MemorySprintRetrospective memorySprintRetrospective;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        // 회원 데이터 설정
        NewMemberDTO member1 = new NewMemberDTO();
        member1.setUsername("User One");
        member1.setPassword("password123");
        member1.setEmailId("user1@example.com");
        NewMemberDTO member2 = new NewMemberDTO();
        member2.setUsername("User Two");
        member2.setPassword("password123");
        member2.setEmailId("user2@example.com");
        memoryMemberRepository.save(member1);
        memoryMemberRepository.save(member2);

        // 프로젝트 데이터 설정
        NewProjectDTO projectDTO = new NewProjectDTO();
        projectDTO.setProjectName("Test Project");
        projectDTO.setMembersEmailId(List.of("user1@example.com", "user2@example.com"));
        memoryProjectRepository.save(projectDTO);

        // Epic 데이터 설정
        NewEpicDTO epicDTO = new NewEpicDTO();
        epicDTO.setTitle("Test Epic");
        epicDTO.setStartDate(LocalDate.parse("2024-01-01"));
        epicDTO.setEndDate(LocalDate.parse("2024-02-01"));
        Long projectId = memoryProjectRepository.findAll().get(0).getId();
        memoryEpicRepository.save(epicDTO, projectId);

        // Epic에 스프린트 이름 설정
        memoryEpicRepository.findAll().get(0).setSprintName("Sprint 1");

        // 로그인 세션 생성
        Member loginMember = memoryMemberRepository.findByEmailId("user1@example.com").orElse(null);
        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
    }

    @Test
    void 스프린트리뷰_정상_케이스() throws Exception {
        // Epic 및 Project ID 조회
        Long projectId = memoryProjectRepository.findAll().get(0).getId();
        Long epicId = memoryEpicRepository.findAll().get(0).getId();

        // Epic에 SprintName 설정 (필수)
        memoryEpicRepository.findById(epicId).setSprintName("Sprint 1");

        // 스프린트 리뷰 데이터 준비
        SubmitRetrospectiveDTO retrospectiveDTO = new SubmitRetrospectiveDTO();
        retrospectiveDTO.setStop("Stop unnecessary meetings");
        retrospectiveDTO.setStart("Start daily stand-ups");
        retrospectiveDTO.setContinueAction("Continue collaboration");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(retrospectiveDTO);

        // API 호출
        mockMvc.perform(post("/SE/project/" + projectId + "/kanbanboard/" + epicId + "/review")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

}