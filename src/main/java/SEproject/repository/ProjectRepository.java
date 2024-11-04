package SEproject.repository;

import SEproject.domain.Project;
import SEproject.dto.NewProjectDTO;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    public Project findById(Long id);
    public List<Project> findAll();
    public Project save(NewProjectDTO newProjectDTO);
    public Optional<Project> findByName(String name);
    public List<Long> findMemberIds(Long projectId);
    public void addEpic(Long projectId, Long epicId);

}
