package SEproject.controller;

import SEproject.domain.Epic;
import SEproject.domain.Member;
import SEproject.domain.Project;
import SEproject.dto.NewEpicDTO;
import SEproject.dto.NewMemberDTO;
import SEproject.dto.NewProjectDTO;
import SEproject.dto.TimelineEpicDTO;
import SEproject.repository.EpicRepository;
import SEproject.repository.memoryrepository.MemoryMemberRepository;
import SEproject.repository.memoryrepository.MemoryProjectRepository;
import SEproject.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private MemoryProjectRepository projectRepository;
    @Autowired
    private MemoryMemberRepository memberRepository;
    @Autowired
    private EpicRepository epicRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // 초기화: 테스트 데이터 정리
        MemoryProjectRepository.store.clear();
        MemoryProjectRepository.sequence.set(0L);
        MemoryMemberRepository.store.clear();
        MemoryMemberRepository.sequence.set(0L);

        // 테스트 멤버 데이터 추가
        NewMemberDTO newMemberDTO = new NewMemberDTO();
        newMemberDTO.setUsername("Test User");
        newMemberDTO.setEmailId("test@example.com");
        newMemberDTO.setPassword("password");
        memberRepository.save(newMemberDTO); // NewMemberDTO를 사용해 저장
    }

    @Test
    void testCreateAndFetchProject() throws Exception {
        // 1. 프로젝트 생성 요청 데이터 준비
        NewProjectDTO newProjectDTO = new NewProjectDTO();
        newProjectDTO.setProjectName("Integration Test Project");
        newProjectDTO.setMembersEmailId(List.of("test@example.com"));

        // 2. 프로젝트 생성 API 호출
        mockMvc.perform(post("/SE/project/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProjectDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName").value("Integration Test Project"));

        // 3. 생성된 프로젝트 확인
        Project createdProject = projectRepository.findAll().stream()
                .filter(project -> "Integration Test Project".equals(project.getProjectName()))
                .findFirst()
                .orElse(null);

        assertThat(createdProject).isNotNull();
        assertThat(createdProject.getProjectName()).isEqualTo("Integration Test Project");

        // 4. 프로젝트 타임라인 API 호출
        mockMvc.perform(get("/SE/project/" + createdProject.getId() + "/timeline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['Integration Test Project']").exists());
    }

    @Test
    void testProjectTimelineAndKanbanBoard() throws Exception {
        // 1. 프로젝트 및 에픽 추가 설정
        NewProjectDTO newProjectDTO = new NewProjectDTO();
        newProjectDTO.setProjectName("Timeline Test Project");
        newProjectDTO.setMembersEmailId(List.of("test@example.com"));

        // 프로젝트 생성
        Project createdProject = projectService.createProject(newProjectDTO);

        // 에픽 추가
        NewEpicDTO newEpicDTO = new NewEpicDTO();
        newEpicDTO.setTitle("Dummy Epic Title");
        newEpicDTO.setStartDate(LocalDate.now());
        newEpicDTO.setEndDate(LocalDate.now().plusDays(7));
        epicRepository.save(newEpicDTO, 1L); // 에픽 저장

        projectRepository.addEpic(createdProject.getId(), 1L); // 프로젝트에 에픽 추가

        // 2. 타임라인 API 호출 및 검증
        mockMvc.perform(get("/SE/project/" + createdProject.getId() + "/timeline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['Timeline Test Project']").exists())
                .andExpect(jsonPath("$['Timeline Test Project'][0].epicTitle").value("Dummy Epic Title"));

        // 3. 칸반보드 API 호출 및 검증
        mockMvc.perform(get("/SE/project/" + createdProject.getId() + "/kanbanboard/newsprint"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Dummy Epic Title"));
    }

    @Test
    void testProjectServiceLogic() {
        // 1. 프로젝트 생성
        NewProjectDTO newProjectDTO = new NewProjectDTO();
        newProjectDTO.setProjectName("Service Logic Test Project");
        newProjectDTO.setMembersEmailId(List.of("test@example.com"));

        Project createdProject = projectService.createProject(newProjectDTO);

        // 2. 타임라인 데이터 확인
        Map<String, List<TimelineEpicDTO>> timeline = projectService.getTimeline(createdProject.getId());
        assertThat(timeline).isNotNull();
        assertThat(timeline).containsKey("Service Logic Test Project");

        // 3. 에픽 데이터 확인
        List<String> epics = projectService.getEpics(createdProject.getId());
        assertThat(epics).isNotNull();
        assertThat(epics).isEmpty(); // 에픽 추가 전
    }
}

