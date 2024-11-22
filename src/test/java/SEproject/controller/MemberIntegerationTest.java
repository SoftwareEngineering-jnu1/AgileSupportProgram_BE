package SEproject.controller;

import SEproject.domain.Epic;
import SEproject.domain.Member;
import SEproject.domain.Project;
import SEproject.dto.LoginMemberDTO;
import SEproject.dto.NewEpicDTO;
import SEproject.dto.NewMemberDTO;
import SEproject.dto.NewProjectDTO;
import SEproject.repository.EpicRepository;
import SEproject.repository.ProjectRepository;
import SEproject.repository.memoryrepository.MemoryMemberRepository;
import SEproject.service.MemberService;
import SEproject.web.SessionConst;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MemberIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemoryMemberRepository memberRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private EpicRepository epicRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void clearRepository() {
        MemoryMemberRepository.store.clear();
        MemoryMemberRepository.sequence.set(0L);
    }

    @Test
    void 회원가입_정상_케이스() throws Exception {
        // given
        NewMemberDTO newMemberDTO = new NewMemberDTO();
        newMemberDTO.setEmailId("test@example.com");
        newMemberDTO.setPassword("password123");
        newMemberDTO.setUsername("testUser");

        // when
        mockMvc.perform(post("/SE/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMemberDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        // then
        Member savedMember = memberRepository.findByEmailId("test@example.com").orElse(null);
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getEmailId()).isEqualTo("test@example.com");
        assertThat(savedMember.getPassword()).isEqualTo("password123");
        assertThat(savedMember.getUsername()).isEqualTo("testUser");
    }

    @Test
    void 회원가입_데이터_누락_케이스() throws Exception {
        // given
        NewMemberDTO invalidMemberDTO = new NewMemberDTO();
        invalidMemberDTO.setEmailId("");
        invalidMemberDTO.setPassword("");
        invalidMemberDTO.setUsername("");

        // when
        mockMvc.perform(post("/SE/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMemberDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("fail"));

        // then
        assertThat(memberRepository.findAll()).isEmpty();
    }

    @Test
    void 로그인_정상_케이스() throws Exception {
        // given
        NewMemberDTO newMemberDTO = new NewMemberDTO();
        newMemberDTO.setEmailId("test@example.com");
        newMemberDTO.setPassword("password123");
        newMemberDTO.setUsername("testUser");
        memberService.createMember(newMemberDTO);

        LoginMemberDTO loginMemberDTO = new LoginMemberDTO();
        loginMemberDTO.setEmailId("test@example.com");
        loginMemberDTO.setPassword("password123");

        // when
        var mvcResult = mockMvc.perform(post("/SE/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginMemberDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn(); // MvcResult를 반환받음

        // then
        HttpSession session = mvcResult.getRequest().getSession(false); // MvcResult를 통해 HttpSession 가져오기
        assertThat(session).isNotNull();
        Member loggedInMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        assertThat(loggedInMember).isNotNull();
        assertThat(loggedInMember.getEmailId()).isEqualTo("test@example.com");
        assertThat(loggedInMember.getUsername()).isEqualTo("testUser");
    }

    @Test
    void 로그인_데이터_누락_케이스() throws Exception {
        // given
        LoginMemberDTO invalidLoginDTO = new LoginMemberDTO();
        invalidLoginDTO.setEmailId(""); // 누락된 데이터
        invalidLoginDTO.setPassword(""); // 누락된 데이터

        // when
        mockMvc.perform(post("/SE/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.data").value("Invalid login data"));

        // then
        assertThat(memberRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    void 로그아웃_정상_케이스() throws Exception {
        // given
        NewMemberDTO newMemberDTO = new NewMemberDTO();
        newMemberDTO.setEmailId("test@example.com");
        newMemberDTO.setPassword("password123");
        newMemberDTO.setUsername("testUser");
        memberService.createMember(newMemberDTO);

        LoginMemberDTO loginMemberDTO = new LoginMemberDTO();
        loginMemberDTO.setEmailId("test@example.com");
        loginMemberDTO.setPassword("password123");

        // 로그인 수행하여 세션 생성
        var loginResult = mockMvc.perform(post("/SE/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginMemberDTO)))
                .andExpect(status().isOk())
                .andReturn();

        HttpSession session = loginResult.getRequest().getSession(false);
        assertThat(session).isNotNull(); // 로그인 성공으로 세션이 존재해야 함

        // when
        // 로그아웃 요청 수행
        mockMvc.perform(post("/SE/logout")
                        .session((MockHttpSession) session)) // 기존 세션 전달
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").value("Logout successful"));

        // then
        // 세션을 다시 가져오려고 시도했을 때 null이어야 함
        HttpSession invalidSession = loginResult.getRequest().getSession(false);
        assertThat(invalidSession).isNull(); // 로그아웃 후 세션이 무효화되었는지 확인
    }
    @Test
    void 프로젝트_목록_조회_정상_케이스() throws Exception {
        // given
        NewMemberDTO newMemberDTO = new NewMemberDTO();
        newMemberDTO.setEmailId("test@example.com");
        newMemberDTO.setPassword("password123");
        newMemberDTO.setUsername("testUser");
        Member savedMember = memberService.createMember(newMemberDTO);

        NewProjectDTO newProjectDTO = new NewProjectDTO();
        newProjectDTO.setProjectName("Project 1");
        newProjectDTO.getMembersEmailId().add(newMemberDTO.getEmailId());
        Project savedProject = projectRepository.save(newProjectDTO);

        NewEpicDTO newEpicDTO1 = new NewEpicDTO();
        newEpicDTO1.setStartDate(LocalDate.now());
        newEpicDTO1.setEndDate(LocalDate.now().plusDays(1));
        newEpicDTO1.setTitle("testEpic1");
        Epic savedEpic1 = epicRepository.save(newEpicDTO1, savedProject.getId());

        NewEpicDTO newEpicDTO2 = new NewEpicDTO();
        newEpicDTO2.setStartDate(LocalDate.now());
        newEpicDTO2.setEndDate(LocalDate.now().plusDays(1));
        newEpicDTO2.setTitle("testEpic2");
        Epic savedEpic2 = epicRepository.save(newEpicDTO2, savedProject.getId());

        savedEpic1.setIsCompleted(true);

        LoginMemberDTO loginMemberDTO = new LoginMemberDTO();
        loginMemberDTO.setEmailId("test@example.com");
        loginMemberDTO.setPassword("password123");
        var loginResult = mockMvc.perform(post("/SE/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginMemberDTO)))
                .andExpect(status().isOk())
                .andReturn();

        HttpSession session = loginResult.getRequest().getSession(false);
        assertThat(session).isNotNull();

        // when
        var result = mockMvc.perform(get("/SE/projects/" + savedMember.getId())
                        .session((MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        // then
        String jsonResponse = result.getResponse().getContentAsString();
        ApiResponse response = objectMapper.readValue(jsonResponse, ApiResponse.class);

        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> projectData = (Map<String, Map<String, Object>>) response.getData();

        assertThat(projectData).containsKey("Project 1");
        Map<String, Object> project1Data = projectData.get("Project 1");

        // 타입 변환: Integer -> Long
        Long totalEpics = ((Number) project1Data.get("totalEpics")).longValue();
        Long completedEpics = ((Number) project1Data.get("completedEpics")).longValue();

        // 검증
        assertThat(totalEpics).isEqualTo(2L);
        assertThat(completedEpics).isEqualTo(1L);
    }
    // 응답 DTO를 위한 클래스
    private static class ApiResponse {
        private String status;
        private Object data;

        public ApiResponse(String status, Object data) {
            this.status = status;
            this.data = data;
        }

        public String getStatus() {
            return status;
        }

        public Object getData() {
            return data;
        }
    }
}
