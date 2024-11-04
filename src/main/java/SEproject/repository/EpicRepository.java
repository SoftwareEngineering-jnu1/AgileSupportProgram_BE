package SEproject.repository;

import SEproject.domain.Epic;
import SEproject.dto.NewEpicDTO;

import java.util.List;

public interface EpicRepository {
    public Epic findById(Long id);
    public List<Epic> findAll();
    public Epic save(NewEpicDTO newEpicDTO, Long projectId);
}
