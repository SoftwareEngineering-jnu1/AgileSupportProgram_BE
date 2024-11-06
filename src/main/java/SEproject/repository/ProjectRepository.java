package SEproject.repository;

import SEproject.domain.Project;
import SEproject.dto.NewProjectDTO;

import java.util.List;

public interface ProjectRepository {
    public Project findById(Long id);
    public List<Project> findAll();
    public Project save(NewProjectDTO newProjectDTO);
    public void addEpic(Long projectId, Long epicId);
}
