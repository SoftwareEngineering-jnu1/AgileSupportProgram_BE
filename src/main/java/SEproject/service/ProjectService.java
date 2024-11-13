package SEproject.service;

import SEproject.domain.Epic;
import SEproject.domain.Issue;
import SEproject.domain.Project;
import SEproject.dto.NewProjectDTO;
import SEproject.dto.TimelineEpicDTO;
import SEproject.dto.TimelineIssueDTO;
import SEproject.repository.EpicRepository;
import SEproject.repository.IssueRepository;
import SEproject.repository.ProjectRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final EpicRepository epicRepository;
    private final IssueRepository issueRepository;
    private final EpicService epicService;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, EpicRepository epicRepository, IssueRepository issueRepository, EpicService epicService) {
        this.projectRepository = projectRepository;
        this.epicRepository = epicRepository;
        this.issueRepository = issueRepository;
        this.epicService = epicService;
    }

    public Project createProject(NewProjectDTO newProjectDTO) {
        return projectRepository.save(newProjectDTO);
    }

    public Map<String, List<TimelineEpicDTO>> getTimeline(Long projectId) {
        Project project = projectRepository.findById(projectId);
        String projectName = project.getProjectName();

        List<TimelineEpicDTO> result = new ArrayList<>();

        List<Long> epicsId = project.getEpicsId();
        for(int i = 0; i < epicsId.size(); i++) {
            Epic epic = epicRepository.findById(epicsId.get(i));
            result.add(new TimelineEpicDTO());
            result.get(i).setEpicTitle(epic.getTitle());
            result.get(i).setEpicStartDate(epic.getStartDate());
            result.get(i).setEpicEndDate(epic.getEndDate());
            result.get(i).setEpicProgressStatus(epicService.epicProgressStatus(epic.getId()));

            List<Long> issueIds = epic.getIssueIds();
            for(int j = 0; j < issueIds.size(); j++) {
                Issue issue = issueRepository.findById(issueIds.get(j));
                TimelineIssueDTO timelineIssueDTO = new TimelineIssueDTO();
                timelineIssueDTO.setIssueTitle(issue.getTitle());
                timelineIssueDTO.setIssueStartDate(issue.getStartDate());
                timelineIssueDTO.setIssueEndDate(issue.getEndDate());
                if(!epic.getDependency().isEmpty() && (epic.getDependency().get(0L).equals(issue.getId()) || epic.getDependency().get(1L).equals(issue.getId()))) {
                    timelineIssueDTO.setHasDependency(true);
                } else {
                    timelineIssueDTO.setHasDependency(false);
                }

                result.get(i).getIssues().add(timelineIssueDTO);
            }

        }
        Map<String, List<TimelineEpicDTO>> totalResult = new HashMap<>();
        totalResult.put(projectName, result);

        return totalResult;
    }

    public List<String> getEpics(Long projectId) {
        Project project = projectRepository.findById(projectId);
        List<Long> epicsId = project.getEpicsId();

        List<String> result = new ArrayList<>();
        for(int i = 0; i < epicsId.size(); i++) {
            result.add(epicRepository.findById(epicsId.get(i)).getTitle());
        }

        return result;
    }
}