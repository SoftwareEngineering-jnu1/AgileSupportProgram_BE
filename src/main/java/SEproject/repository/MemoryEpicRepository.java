package SEproject.repository;

import SEproject.domain.Epic;
import SEproject.domain.Issue;
import SEproject.domain.Project;
import SEproject.dto.NewEpicDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryEpicRepository implements EpicRepository {
    public static ConcurrentHashMap<Long, Epic> store = new ConcurrentHashMap<>();
    public static AtomicLong sequence = new AtomicLong();

    public final MemoryProjectRepository memoryProjectRepository;

    @Autowired
    public MemoryEpicRepository(MemoryProjectRepository memoryProjectRepository) {
        this.memoryProjectRepository = memoryProjectRepository;
    }

    @Override
    public Epic save(NewEpicDTO newEpicDTO, Long projectId) {
        Epic savedepic = new Epic();
        savedepic.setId(sequence.incrementAndGet());
        savedepic.setProjectId(projectId);
        savedepic.setTitle(newEpicDTO.getTitle());
        savedepic.setStartDate(newEpicDTO.getStartDate());
        savedepic.setEndDate(newEpicDTO.getEndDate());
        memoryProjectRepository.addEpic(projectId, savedepic.getId());
        store.put(savedepic.getId(), savedepic);

        return savedepic;
    }

    @Override
    public Epic findById(Long id) {
        return store.get(id);
    }

    @Override
    public Optional<Epic> findByTitle(String name) {
        return findAll().stream()
                .filter(m -> m.getTitle().equals(name))
                .findFirst();
    }

    @Override
    public List<Epic> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Map<Long, Issue> findIssueIds(Long epicId) {
        return store.get(epicId).getIssues();
    }
}
