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
import SEproject.domain.Memo;
import SEproject.domain.Member;
import SEproject.dto.NewMemoDTO;
import SEproject.dto.NewProjectDTO;
import SEproject.repository.memoryrepository.MemoryMemoRepository;
import SEproject.repository.memoryrepository.MemoryMemberRepository;
import SEproject.repository.memoryrepository.MemoryProjectRepository;
import SEproject.web.SessionConst;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemoryMemberRepository memoryMemberRepository;

    @Autowired
    private MemoryProjectRepository memoryProjectRepository;

    @Autowired
    private MemoryMemoRepository memoryMemoRepository;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        memoryProjectRepository.clear();
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

        // 로그인 세션 생성
        Member loginMember = memoryMemberRepository.findByEmailId("user1@example.com").orElse(null);
        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
    }

    @Test
    void 메모생성_정상_케이스() throws Exception {
        // 요청 데이터 준비
        Map<String, Object> newMemoDTO = new HashMap<>();
        newMemoDTO.put("title", "Test Memo");
        newMemoDTO.put("content", "This is a test memo content.");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(newMemoDTO);

        // 프로젝트 ID 조회
        Long projectId = memoryProjectRepository.findAll().get(0).getId();

        // API 호출 및 검증
        mockMvc.perform(post("/SE/project/" + projectId + "/memo/newmemo")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.title").value("Test Memo"))
                .andExpect(jsonPath("$.data.content").value("This is a test memo content."));

        // 저장소 확인
        assertTrue(memoryMemoRepository.findAll().stream()
                .anyMatch(memo -> "Test Memo".equals(memo.getTitle())));
    }

    @Test
    void 메모수정_정상_케이스() throws Exception {
        // Memo 데이터 설정
        NewMemoDTO newMemoDTO = new NewMemoDTO();
        newMemoDTO.setTitle("Initial Memo");
        newMemoDTO.setContent("Initial content");
        Long projectId = memoryProjectRepository.findAll().get(0).getId();
        Memo savedMemo = memoryMemoRepository.save(newMemoDTO, projectId, LocalDate.now());

        // 수정 데이터 준비
        Map<String, Object> editMemoDTO = new HashMap<>();
        editMemoDTO.put("title", "Updated Memo");
        editMemoDTO.put("content", "Updated content");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(editMemoDTO);

        // API 호출 및 검증
        mockMvc.perform(post("/SE/project/" + projectId + "/memo/" + savedMemo.getId())
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.title").value("Updated Memo"))
                .andExpect(jsonPath("$.data.content").value("Updated content"));

        // 저장소 확인
        Memo updatedMemo = memoryMemoRepository.findById(savedMemo.getId());
        assertEquals("Updated Memo", updatedMemo.getTitle());
        assertEquals("Updated content", updatedMemo.getContent());
    }

    @Test
    void 메모목록조회_정상_케이스() throws Exception {
        // Memo 데이터 설정
        NewMemoDTO memo1 = new NewMemoDTO();
        memo1.setTitle("Memo 1");
        memo1.setContent("Content 1");
        Long projectId = memoryProjectRepository.findAll().get(0).getId();
        memoryMemoRepository.save(memo1, projectId, LocalDate.now());

        NewMemoDTO memo2 = new NewMemoDTO();
        memo2.setTitle("Memo 2");
        memo2.setContent("Content 2");
        memoryMemoRepository.save(memo2, projectId, LocalDate.now());

        // 저장소에 데이터 확인 (디버깅용)
        List<Memo> storedMemos = memoryMemoRepository.findAll();
        assertTrue(storedMemos.stream().anyMatch(memo -> memo.getTitle().equals("Memo 1")));
        assertTrue(storedMemos.stream().anyMatch(memo -> memo.getTitle().equals("Memo 2")));

        // API 호출
        MvcResult result = mockMvc.perform(get("/SE/project/" + projectId + "/memo")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        // 응답 데이터 파싱 및 검증
        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = result.getResponse().getContentAsString();

        Map<String, Object> responseData = objectMapper.readValue(responseBody, Map.class);
        List<Map<String, Object>> memoList = (List<Map<String, Object>>) ((Map<String, Object>) responseData.get("data")).get("Test Project");

        // 제목 검증
        List<String> titles = memoList.stream()
                .map(memo -> memo.get("title").toString())
                .toList();
        assertTrue(titles.contains("Memo 1"), "Memo 1 should be in the response");
        assertTrue(titles.contains("Memo 2"), "Memo 2 should be in the response");
    }

}
