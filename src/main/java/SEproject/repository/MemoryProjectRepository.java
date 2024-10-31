package SEproject.repository;

import SEproject.domain.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MemoryProjectRepository implements ProjectRepository{
    public static ConcurrentHashMap<Long, Project> store = new ConcurrentHashMap<>();
    public static AtomicLong sequence = new AtomicLong();

    @Override
    public Project save(Project project) {
        Project newproject = new Project();
        newproject.setId(sequence.incrementAndGet());
        newproject.setProjectName(project.getProjectName());
        newproject.setMembersId(new ArrayList<>(project.getMembersId()));

        store.put(newproject.getId(), newproject);

        return newproject;
    }

    @Override
    public Project findById(Long id) {
        return store.get(id);
    }

    @Override
    public Optional<Project> findByName(String name) {
        return findAll().stream()
                .filter(m -> m.getProjectName().equals(name))
                .findFirst();
    }

    @Override
    public List<Project> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Long> findMemberIds(Long projectId) {
        return store.get(projectId).getMembersId();
    }

    @Override
    public List<Long> findEpicIds(Long projectId) {
        return store.get(projectId).getEpicsId();
    }

    @Override
    public List<Long> findMemoIds(Long projectId) {
        return store.get(projectId).getMemosId();
    }
}
