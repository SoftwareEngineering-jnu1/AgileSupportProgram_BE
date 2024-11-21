package SEproject.controller;

import static org.junit.jupiter.api.Assertions.*;
import SEproject.domain.Epic;
import SEproject.domain.SprintRetrospective;
import SEproject.dto.*;
import SEproject.repository.SprintRetrospectiveRepository;
import SEproject.repository.memoryrepository.MemoryEpicRepository;
import SEproject.repository.memoryrepository.MemoryMemberRepository;
import SEproject.repository.memoryrepository.MemoryProjectRepository;
import SEproject.service.EpicService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EpicControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EpicService epicService;

    @Autowired
    private MemoryEpicRepository epicRepository;

    @Autowired
    private MemoryProjectRepository projectRepository;

    @Autowired
    private MemoryMemberRepository memberRepository;

    @Autowired
    private SprintRetrospectiveRepository sprintRetrospectiveRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // 초기화
        MemoryEpicRepository.store.clear();
        MemoryEpicRepository.sequence.set(0L);
        MemoryProjectRepository.store.clear();
        MemoryProjectRepository.sequence.set(0L);
        MemoryMemberRepository.store.clear();
        MemoryMemberRepository.sequence.set(0L);

        // 멤버 데이터 추가
        NewMemberDTO memberDTO = new NewMemberDTO();
        memberDTO.setUsername("Test User");
        memberDTO.setEmailId("test@example.com");
        memberDTO.setPassword("password");
        memberRepository.save(memberDTO);

        // 프로젝트 데이터 추가
        NewProjectDTO projectDTO = new NewProjectDTO();
        projectDTO.setProjectName("Test Project");
        projectDTO.setMembersEmailId(List.of("test@example.com"));
        projectRepository.save(projectDTO);
    }

    @Test
    void testCreateEpic() throws Exception {
        // 1. 프로젝트 ID 가져오기
        Long projectId = projectRepository.findAll().get(0).getId();

        // 2. 에픽 생성 요청 데이터 준비
        NewEpicDTO newEpicDTO = new NewEpicDTO();
        newEpicDTO.setTitle("Test Epic");
        newEpicDTO.setStartDate(LocalDate.now());
        newEpicDTO.setEndDate(LocalDate.now().plusDays(7));

        // 3. 에픽 생성 API 호출
        mockMvc.perform(post("/SE/project/" + projectId + "/addepic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEpicDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Epic"));

        // 4. 저장된 에픽 확인
        Epic createdEpic = epicRepository.findAll().stream()
                .filter(epic -> "Test Epic".equals(epic.getTitle()))
                .findFirst()
                .orElse(null);

        assertThat(createdEpic).isNotNull();
        assertThat(createdEpic.getTitle()).isEqualTo("Test Epic");
    }

    @Test
    void testEditEpic() throws Exception {
        // 1. 프로젝트 및 에픽 생성
        Long projectId = projectRepository.findAll().get(0).getId();
        NewEpicDTO newEpicDTO = new NewEpicDTO();
        newEpicDTO.setTitle("Original Epic");
        newEpicDTO.setStartDate(LocalDate.now());
        newEpicDTO.setEndDate(LocalDate.now().plusDays(7));
        Epic epic = epicService.createEpic(newEpicDTO, projectId);

        // 2. 에픽 수정 요청 데이터 준비
        EditEpicDTO editEpicDTO = new EditEpicDTO();
        editEpicDTO.setTitle("Updated Epic");
        editEpicDTO.setStartDate(LocalDate.now().minusDays(1));
        editEpicDTO.setEndDate(LocalDate.now().plusDays(10));

        // 3. 에픽 수정 API 호출
        mockMvc.perform(post("/SE/project/" + projectId + "/" + epic.getId() + "/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editEpicDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Epic"));

        // 4. 수정된 에픽 확인
        Epic updatedEpic = epicRepository.findById(epic.getId());
        assertThat(updatedEpic).isNotNull();
        assertThat(updatedEpic.getTitle()).isEqualTo("Updated Epic");
        assertThat(updatedEpic.getStartDate()).isEqualTo(LocalDate.now().minusDays(1));
        assertThat(updatedEpic.getEndDate()).isEqualTo(LocalDate.now().plusDays(10));
    }

    @Test
    void testGetKanbanBoard() throws Exception {
        // 1. 프로젝트 및 에픽 생성
        Long projectId = projectRepository.findAll().get(0).getId();
        NewEpicDTO newEpicDTO = new NewEpicDTO();
        newEpicDTO.setTitle("Kanban Epic");
        newEpicDTO.setStartDate(LocalDate.now());
        newEpicDTO.setEndDate(LocalDate.now().plusDays(7));
        Epic epic = epicService.createEpic(newEpicDTO, projectId);

        // 2. 칸반 보드 API 호출
        mockMvc.perform(get("/SE/project/" + projectId + "/kanbanboard/" + epic.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sprintName").value(epic.getSprintName()))
                .andExpect(jsonPath("$.projectName").value("Test Project"));
    }

    @Test
    void testSubmitRetrospective() throws Exception {
        // 1. 프로젝트 및 에픽 생성
        Long projectId = projectRepository.findAll().get(0).getId();
        NewEpicDTO newEpicDTO = new NewEpicDTO();
        newEpicDTO.setTitle("Retrospective Epic");
        newEpicDTO.setStartDate(LocalDate.now());
        newEpicDTO.setEndDate(LocalDate.now().plusDays(7));
        Epic epic = epicService.createEpic(newEpicDTO, projectId);

        // 2. 스프린트 회고 데이터 생성 및 저장
        SprintRetrospective sprintRetrospective = new SprintRetrospective();
        sprintRetrospective.setEpicId(epic.getId());
        sprintRetrospective.setSprintName("Sprint 1");
        sprintRetrospective.setMemberIds(projectRepository.findById(projectId).getMembersId());
        sprintRetrospective.setTotalMemberCount((long) sprintRetrospective.getMemberIds().size());
        sprintRetrospectiveRepository.save(sprintRetrospective);

        // 3. 회고 제출 요청 데이터 준비
        SubmitRetrospectiveDTO retrospectiveDTO = new SubmitRetrospectiveDTO();
        retrospectiveDTO.setStart("Start doing TDD");
        retrospectiveDTO.setStop("Stop ignoring tests");
        retrospectiveDTO.setContinue("Continue refactoring");

        // 4. 회고 제출 API 호출
        mockMvc.perform(post("/SE/project/" + projectId + "/kanbanboard/" + epic.getId() + "/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(retrospectiveDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));

        // 5. 저장된 회고 확인
        SprintRetrospective retrievedRetrospective = sprintRetrospectiveRepository.findByEpicId(epic.getId());
        assertThat(retrievedRetrospective).isNotNull();
        assertThat(retrievedRetrospective.getStart()).contains("Start doing TDD");
        assertThat(retrievedRetrospective.getStop()).contains("Stop ignoring tests");
        assertThat(retrievedRetrospective.getContinue()).contains("Continue refactoring");
    }

}
