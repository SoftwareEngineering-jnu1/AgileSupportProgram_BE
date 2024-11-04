package SEproject.repository;

import SEproject.domain.Epic;
import SEproject.dto.NewEpicDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
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
    public Epic findById(Long id) {
        return store.get(id);
    }

    @Override
    public List<Epic> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Epic save(NewEpicDTO newEpicDTO, Long projectId) {
        Epic epic = new Epic();
        epic.setId(sequence.incrementAndGet());
        epic.setProjectId(projectId);
        epic.setTitle(newEpicDTO.getTitle());
        epic.setStartDate(newEpicDTO.getStartDate());
        epic.setEndDate(newEpicDTO.getEndDate());
        memoryProjectRepository.addEpic(projectId, epic.getId());
        store.put(epic.getId(), epic);

        return epic;
    }
}
