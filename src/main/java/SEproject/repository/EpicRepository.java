package SEproject.repository;

import SEproject.domain.Epic;
import SEproject.domain.Issue;
import SEproject.dto.NewEpicDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EpicRepository {
    public Epic save(NewEpicDTO newEpicDTO, Long projectId);
    public Epic findById(Long id);
    public Optional<Epic> findByTitle(String name);
    public List<Epic> findAll();

    public Map<Long, Issue> findIssueIds(Long epicId);
}
