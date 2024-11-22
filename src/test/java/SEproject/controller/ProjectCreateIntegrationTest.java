package SEproject.controller;

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
import SEproject.dto.NewMemberDTO;
import SEproject.dto.NewProjectDTO;
import SEproject.repository.memoryrepository.MemoryMemberRepository;
import SEproject.repository.memoryrepository.MemoryProjectRepository;
import SEproject.web.SessionConst;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectCreateIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemoryMemberRepository memoryMemberRepository;

    @Autowired
    private MemoryProjectRepository memoryProjectRepository;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        // 회원 데이터 설정
        NewMemberDTO member1 = new NewMemberDTO();
        member1.setUsername("User One");
        member1.setPassword("password123");
        member1.setEmailId("user1@example.com");
        memoryMemberRepository.save(member1);

        NewMemberDTO member2 = new NewMemberDTO();
        member2.setEmailId("user2@example.com");
        member2.setPassword("password123");
        member2.setUsername("User Two");
        memoryMemberRepository.save(member2);

        // 로그인 세션 생성
        Member loginMember = memoryMemberRepository.findByEmailId("user1@example.com").orElse(null);
        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
    }

    @Test
    void 프로젝트생성_정상_케이스() throws Exception {
        // 요청 데이터 준비
        NewProjectDTO newProjectDTO = new NewProjectDTO();
        newProjectDTO.setProjectName("Test Project");
        newProjectDTO.setMembersEmailId(List.of("user1@example.com", "user2@example.com"));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(newProjectDTO);

        // API 호출 및 검증
        mockMvc.perform(post("/SE/project/new")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.projectName").value("Test Project"));

        // 프로젝트 저장소 확인
        assertTrue(memoryProjectRepository.findAll().stream()
                .anyMatch(project -> "Test Project".equals(project.getProjectName())));
    }

    @Test
    void 프로젝트생성_세션오류_케이스() throws Exception {
        // 요청 데이터 준비
        NewProjectDTO newProjectDTO = new NewProjectDTO();
        newProjectDTO.setProjectName("Unauthorized Project");
        newProjectDTO.setMembersEmailId(List.of("user1@example.com"));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(newProjectDTO);

        // API 호출 (인증 없이)
        mockMvc.perform(post("/SE/project/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.data").value("Unauthorized access"));
    }
}

