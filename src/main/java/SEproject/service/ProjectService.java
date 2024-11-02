package SEproject.service;

import SEproject.domain.Project;
import SEproject.dto.NewProjectDTO;
import SEproject.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project createProject(NewProjectDTO newProjectDTO) {
        return projectRepository.save(newProjectDTO);
    }
}
