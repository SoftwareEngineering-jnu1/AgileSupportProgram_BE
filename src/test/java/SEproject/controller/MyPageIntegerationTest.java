package SEproject.controller;

import SEproject.domain.Member;
import SEproject.dto.GetMyPage;
import SEproject.dto.NewMemberDTO;
import SEproject.dto.NewProjectDTO;
import SEproject.repository.memoryrepository.MemoryEpicRepository;
import SEproject.repository.memoryrepository.MemoryMemberRepository;
import SEproject.repository.memoryrepository.MemoryProjectRepository;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MyPageIntegerationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemoryMemberRepository memoryMemberRepository;

    @Autowired
    private MemoryProjectRepository memoryProjectRepository;

    @Autowired
    private MemoryEpicRepository memoryEpicRepository;

    private MockHttpSession session;
    private Long memberId;

    @BeforeEach
    void setUp() {
        memoryMemberRepository.clear();
        memoryProjectRepository.clear();
        // 회원 데이터 설정
        NewMemberDTO member1 = new NewMemberDTO();
        member1.setUsername("User One");
        member1.setPassword("password123");
        member1.setEmailId("user1@example.com");
        memoryMemberRepository.save(member1);

        NewMemberDTO member2 = new NewMemberDTO();
        member2.setUsername("User Two");
        member2.setPassword("password456");
        member2.setEmailId("user2@example.com");
        memoryMemberRepository.save(member2);

        // 프로젝트 데이터 설정
        NewProjectDTO projectDTO = new NewProjectDTO();
        projectDTO.setProjectName("Test Project");
        projectDTO.setMembersEmailId(List.of("user1@example.com", "user2@example.com"));
        memoryProjectRepository.save(projectDTO);

        // 로그인 세션 생성
        Member loginMember = memoryMemberRepository.findByEmailId("user1@example.com").orElse(null);
        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        memberId = loginMember.getId();
    }

    @Test
    void 마이페이지조회_정상_케이스() throws Exception {
        mockMvc.perform(get("/SE/members/" + memberId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.username").value("User One"))
                .andExpect(jsonPath("$.data.projectAndEpic['Test Project']").exists())
                .andExpect(jsonPath("$.data.projectAndEpic['Test Project']").isMap());
    }
}