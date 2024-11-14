package SEproject.repository.memoryrepository;

import SEproject.domain.SprintRetrospective;
import SEproject.dto.NewProjectDTO;
import SEproject.repository.SprintRetrospectiveRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemorySprintRetrospective implements SprintRetrospectiveRepository {
    public static ConcurrentHashMap<Long, SprintRetrospective> store = new ConcurrentHashMap<>();
    public static AtomicLong sequence = new AtomicLong();

    @Override
    public SprintRetrospective findById(Long id) {
        return store.get(id);
    }

    @Override
    public List<SprintRetrospective> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public SprintRetrospective save(SprintRetrospective sprintRetrospective) {
        SprintRetrospective savedSprintRetrospective = new SprintRetrospective();
        savedSprintRetrospective.setId(sequence.incrementAndGet());
        savedSprintRetrospective.setEpicId(sprintRetrospective.getEpicId());
        savedSprintRetrospective.setSprintName(sprintRetrospective.getSprintName());
        savedSprintRetrospective.setMemberIds(sprintRetrospective.getMemberIds());
        savedSprintRetrospective.setTotalMemberCount(sprintRetrospective.getTotalMemberCount());

        store.put(savedSprintRetrospective.getId(), savedSprintRetrospective);

        return savedSprintRetrospective;
    }

    @Override
    public SprintRetrospective findByEpicId(Long epicId) {
        for (SprintRetrospective sprintRetrospective : store.values()) {
            if (sprintRetrospective.getEpicId().equals(epicId)) {
                return sprintRetrospective;
            }
        }

        return null;
    }
}