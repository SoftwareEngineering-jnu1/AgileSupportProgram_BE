package SEproject.repository;

import SEproject.domain.Project;
import SEproject.dto.NewProjectDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryProjectRepository implements ProjectRepository{
    public static ConcurrentHashMap<Long, Project> store = new ConcurrentHashMap<>();
    public static AtomicLong sequence = new AtomicLong();

    @Override
    public Project findById(Long id) {
        return store.get(id);
    }

    @Override
    public List<Project> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Project save(NewProjectDTO newProjectDTO) {
        Project project = new Project();
        project.setId(sequence.incrementAndGet());
        project.setProjectName(newProjectDTO.getProjectName());
        project.setMembersId(new ArrayList<>(newProjectDTO.getMembersId()));
        store.put(project.getId(), project);

        return project;
    }

    @Override
    public void addEpic(Long projectId, Long epicId) {
        store.get(projectId).getEpicsId().add(epicId);
    }

    @Override
    public Optional<Project> findByName(String name) {
        return findAll().stream()
                .filter(m -> m.getProjectName().equals(name))
                .findFirst();
    }

    @Override
    public List<Long> findMemberIds(Long projectId) {
        return store.get(projectId).getMembersId();
    }

    // 테스트용 데이터
    @PostConstruct
    public void init() {
        Project newproject = new Project();
        newproject.setId(sequence.incrementAndGet());
        newproject.setProjectName("testProject");
        List<Long> membersId = new ArrayList<>();
        membersId.add(1L);
        membersId.add(2L);
        newproject.setMembersId(membersId);

        store.put(newproject.getId(), newproject);
    }
}
