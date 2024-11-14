package SEproject.repository;

import SEproject.domain.SprintRetrospective;
import SEproject.dto.NewProjectDTO;

import java.util.List;

public interface SprintRetrospectiveRepository {
    public SprintRetrospective findById(Long id);
    public List<SprintRetrospective> findAll();
    public SprintRetrospective save(SprintRetrospective sprintRetrospective);
    public SprintRetrospective findByEpicId(Long epicId);
}