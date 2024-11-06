package SEproject.repository;

import SEproject.domain.Project;


import SEproject.dto.NewProjectDTO;
import SEproject.repository.memoryrepository.MemoryProjectRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class MemoryProjectRepositoryTest {
    MemoryProjectRepository projectRepository = new MemoryProjectRepository();

    @BeforeEach
    public void beforeEach() {
        NewProjectDTO newProjectDTO1 = new NewProjectDTO();
        newProjectDTO1.setProjectName("testproject1");
        List<Long> memberIds1 = new ArrayList<>();
        memberIds1.add(1L);
        newProjectDTO1.setMembersId(memberIds1);

        NewProjectDTO newProjectDTO2 = new NewProjectDTO();
        newProjectDTO2.setProjectName("testproject2");
        List<Long> memberIds2 = new ArrayList<>();
        memberIds2.add(2L);
        newProjectDTO2.setMembersId(memberIds2);

        Project savedProject1 = projectRepository.save(newProjectDTO1);
        Project savedProject2 = projectRepository.save(newProjectDTO2);
    }

    @Test
    void save() {
    }

    @Test
    void findById() {
        // when
        Project savedProject = projectRepository.findById(1L);
        // then
        Assertions.assertThat(savedProject.getId()).isEqualTo(1L);
    }

    @Test
    void findByName() {
        // when
        Optional<Project> savedProject = projectRepository.findByName("testproject1");
        // then
        Assertions.assertThat(savedProject.get().getProjectName()).isEqualTo("testproject1");
    }

    @Test
    void findAll() {
        // when
        List<Project> allProject = projectRepository.findAll();
        // then
        Assertions.assertThat(allProject).hasSize(2);
    }

    @Test
    void findMemberIds() {
        // when
        List<Long> savedmemberId1 = projectRepository.findMemberIds(1L);
        List<Long> savedmemberId2 = projectRepository.findMemberIds(2L);
        // then
        Assertions.assertThat(savedmemberId1).hasSize(1);
        Assertions.assertThat(savedmemberId1.get(0)).isEqualTo(1L);

        Assertions.assertThat(savedmemberId2).hasSize(1);
        Assertions.assertThat(savedmemberId2.get(0)).isEqualTo(2L);
    }

    @Test
    void findEpicIds() {
    }

    @Test
    void findMemoIds() {
    }
}