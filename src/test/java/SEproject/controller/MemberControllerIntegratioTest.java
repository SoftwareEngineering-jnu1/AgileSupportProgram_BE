package SEproject.controller;

import SEproject.domain.Member;
import SEproject.domain.Project;
import SEproject.dto.LoginMemberDTO;
import SEproject.dto.NewMemberDTO;
import SEproject.dto.NewProjectDTO;
import SEproject.repository.ProjectRepository;
import SEproject.repository.memoryrepository.MemoryMemberRepository;
import SEproject.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemoryMemberRepository memberRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ObjectMapper objectMapper; // JSON 직렬화/역직렬화

    @BeforeEach
    void setUp() {
        // 테스트 시작 전 초기화
        memberRepository.store.clear();
        memberRepository.sequence.set(0L);
    }

    @Test
    void testMemberJoinAndLoginFlow() throws Exception {
        // 1. 회원 가입 요청 데이터 준비
        NewMemberDTO newMemberDTO = new NewMemberDTO();
        newMemberDTO.setUsername("TestUser");
        newMemberDTO.setEmailId("test@example.com");
        newMemberDTO.setPassword("password");

        // 2. 회원 가입 API 호출
        mockMvc.perform(post("/SE/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMemberDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusS").value("success"));

        // 3. Repository에서 저장된 회원 확인
        Member savedMember = memberRepository.findByEmailId("test@example.com").orElse(null);
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getUsername()).isEqualTo("TestUser");

        // 4. 로그인 요청 데이터 준비
        LoginMemberDTO loginMemberDTO = new LoginMemberDTO();
        loginMemberDTO.setEmailId("test@example.com");
        loginMemberDTO.setPassword("password");

        // 5. 로그인 API 호출
        mockMvc.perform(post("/SE/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginMemberDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusS").value("success"));

        // 6. 로그인 실패 테스트
        loginMemberDTO.setPassword("wrongPassword");
        mockMvc.perform(post("/SE/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginMemberDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusF").value("fail"));
    }

    @Test
    void testProjectList() throws Exception {
        // 1. 회원 가입 및 이메일 등록
        NewMemberDTO newMemberDTO = new NewMemberDTO();
        newMemberDTO.setUsername("TestUser");
        newMemberDTO.setEmailId("test@example.com");
        newMemberDTO.setPassword("password");

        Member savedMember = memberService.createMember(newMemberDTO);

        // 2. 프로젝트 생성 요청 데이터 준비
        NewProjectDTO newProjectDTO = new NewProjectDTO();
        newProjectDTO.setProjectName("Test Project");
        newProjectDTO.getMembersEmailId().add("test@example.com"); // Member 이메일 추가
        projectRepository.save(newProjectDTO); // ProjectRepository에 저장

        // 5. API 호출 및 검증
        mockMvc.perform(get("/SE/projects/" + savedMember.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['Test Project'].totalEpics").value(0)) // 에픽 개수 검증
                .andExpect(jsonPath("$['Test Project'].completedEpics").value(0)) // 완료된 에픽 개수 검증
                .andExpect(jsonPath("$.length()").value(1)); // 프로젝트 개수 검증
    }

    @Test
    void testLogoutFlow() throws Exception {
        // 1. 회원 가입 요청 데이터 준비
        NewMemberDTO newMemberDTO = new NewMemberDTO();
        newMemberDTO.setUsername("TestUser");
        newMemberDTO.setEmailId("test@example.com");
        newMemberDTO.setPassword("password");

        // 2. 회원 가입 API 호출
        mockMvc.perform(post("/SE/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMemberDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusS").value("success"));

        // 3. 로그인 요청 데이터 준비
        LoginMemberDTO loginMemberDTO = new LoginMemberDTO();
        loginMemberDTO.setEmailId("test@example.com");
        loginMemberDTO.setPassword("password");

        // 4. 로그인 API 호출
        mockMvc.perform(post("/SE/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginMemberDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusS").value("success"));

        // 5. 로그아웃 API 호출
        mockMvc.perform(post("/SE/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusS").value("success"));
    }

}
