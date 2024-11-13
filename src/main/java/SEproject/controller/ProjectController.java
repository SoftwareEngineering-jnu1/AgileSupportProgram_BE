package SEproject.controller;

import SEproject.domain.Project;
import SEproject.dto.NewProjectDTO;
import SEproject.dto.TimelineEpicDTO;
import SEproject.service.ProjectService;
import SEproject.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ProjectController {
    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("SE/project/new")
    public Project createProject(@RequestBody NewProjectDTO newProjectDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        return projectService.createProject(newProjectDTO);
    }

    @GetMapping("SE/project/{projectId}/timeline")
    public Map<String, List<TimelineEpicDTO>> getTimeline(@PathVariable Long projectId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        return projectService.getTimeline(projectId);
    }

    @GetMapping("SE/project/{projectId}/kanbanboard/newsprint")
    public List<String> getEpics(@PathVariable Long projectId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        return projectService.getEpics(projectId);
    }
}
