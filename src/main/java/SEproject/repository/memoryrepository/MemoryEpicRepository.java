package SEproject.repository.memoryrepository;

import SEproject.domain.Epic;
import SEproject.dto.EditEpicDTO;
import SEproject.dto.NewEpicDTO;
import SEproject.repository.EpicRepository;
import SEproject.repository.IssueRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

        if(!editEpicDTO.getDependency().isEmpty()) {
            editEpic.getDependency().put(0L, issueRepository.findByTitle(editEpicDTO.getDependency().get(0L)).get().getId());
            editEpic.getDependency().put(1L, issueRepository.findByTitle(editEpicDTO.getDependency().get(1L)).get().getId());
        }

        return editEpicDTO;
    }

    @Override
    public Optional<Epic> findByTitle(String title) {
        return findAll().stream()
                .filter(m -> m.getTitle().equals(title))
                .findFirst();
    }

    @PostConstruct
    public void init() {
        NewEpicDTO newEpicDTO1 = new NewEpicDTO();
        newEpicDTO1.setTitle("테스트 에픽 이름 1(프로젝트 1에 속한 에픽)");
        newEpicDTO1.setStartDate(LocalDate.of(2024, 11, 30));
        newEpicDTO1.setEndDate(LocalDate.of(2024, 12, 10));

        NewEpicDTO newEpicDTO2 = new NewEpicDTO();
        newEpicDTO2.setTitle("테스트 에픽 이름 2(프로젝트 1에 속한 에픽)");
        newEpicDTO2.setStartDate(LocalDate.of(2024, 12, 11));
        newEpicDTO2.setEndDate(LocalDate.of(2024, 12, 20));

        NewEpicDTO newEpicDTO3 = new NewEpicDTO();
        newEpicDTO3.setTitle("테스트 에픽 이름 3(프로젝트 2에 속한 에픽)");
        newEpicDTO3.setStartDate(LocalDate.of(2024, 11, 30));
        newEpicDTO3.setEndDate(LocalDate.of(2024, 12, 10));

        save(newEpicDTO1, 1L);
        save(newEpicDTO2, 1L);
        save(newEpicDTO3, 2L);
    }
}
