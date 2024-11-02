package SEproject.repository;

import SEproject.domain.Epic;
import SEproject.domain.Project;
import SEproject.dto.NewProjectDTO;
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
    public Project save(NewProjectDTO newProjectDTO) {
        Project newproject = new Project();
        newproject.setId(sequence.incrementAndGet());
        newproject.setProjectName(newProjectDTO.getProjectName());
        newproject.setMembersId(new ArrayList<>(newProjectDTO.getMembersId()));

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
    public void addEpic(Long projectId, Long epicId) {
        store.get(projectId).getEpicsId().add(epicId);
    }


    @Override
    public List<Long> findMemoIds(Long projectId) {
        return store.get(projectId).getMemosId();
    }

    @Override
    public void addMemo(Long projectId, Long memoId) {
        store.get(projectId).getMemosId().add(memoId);
    }
}
