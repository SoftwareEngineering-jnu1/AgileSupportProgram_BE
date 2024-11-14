package SEproject.repository.memoryrepository;

import SEproject.domain.Epic;
import SEproject.dto.EditEpicDTO;
import SEproject.dto.NewEpicDTO;
import SEproject.repository.EpicRepository;
import SEproject.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryEpicRepository implements EpicRepository {
    public static ConcurrentHashMap<Long, Epic> store = new ConcurrentHashMap<>();
    public static AtomicLong sequence = new AtomicLong();

    public final MemoryProjectRepository memoryProjectRepository;
    public final IssueRepository issueRepository;

    @Autowired
    public MemoryEpicRepository(MemoryProjectRepository memoryProjectRepository, IssueRepository issueRepository) {
        this.memoryProjectRepository = memoryProjectRepository;
        this.issueRepository = issueRepository;
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

    @Override
    public EditEpicDTO edit(EditEpicDTO editEpicDTO, Long epicId) {
        Epic editEpic = store.get(epicId);
        editEpic.setStartDate(editEpicDTO.getStartDate());
        editEpic.setEndDate(editEpicDTO.getEndDate());
        editEpic.setTitle(editEpicDTO.getTitle());
        for(int i = 0; i < editEpicDTO.getDependency().size(); i++) {
            Long dependencyIssueId = issueRepository.findByTitle(editEpicDTO.getDependency().get(i)).get().getId();
            editEpic.getDependency().put(Long.valueOf(i), dependencyIssueId);
        }

        return editEpicDTO;
    }

    @Override
    public Optional<Epic> findByTitle(String title) {
        return findAll().stream()
                .filter(m -> m.getTitle().equals(title))
                .findFirst();
    }
}
