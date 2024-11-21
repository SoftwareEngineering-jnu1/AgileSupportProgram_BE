package SEproject.controller;

import SEproject.domain.Project;
import SEproject.dto.NewProjectDTO;
import SEproject.dto.TimelineEpicDTO;
import SEproject.service.ProjectService;
import SEproject.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> createProject(@RequestBody NewProjectDTO newProjectDTO, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        Project project = projectService.createProject(newProjectDTO);
        return ResponseEntity.ok(new ApiResponse("success", project));
    }

    @GetMapping("SE/project/{projectId}/timeline")
    public ResponseEntity<?> getTimeline(@PathVariable Long projectId, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        Map<String, List<TimelineEpicDTO>> timeline = projectService.getTimeline(projectId);
        return ResponseEntity.ok(new ApiResponse("success", timeline));
    }

    @GetMapping("SE/project/{projectId}/kanbanboard/newsprint")
    public ResponseEntity<?> getEpics(@PathVariable Long projectId, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        List<String> epics = projectService.getEpics(projectId);
        return ResponseEntity.ok(new ApiResponse("success", epics));
    }

    // 세션 처리
    private boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(SessionConst.LOGIN_MEMBER) != null;
    }

    // 응답 DTO를 위한 클래스
    private static class ApiResponse {
        private String status;
        private Object data;

        public ApiResponse(String status, Object data) {
            this.status = status;
            this.data = data;
        }

        public String getStatus() {
            return status;
        }

        public Object getData() {
            return data;
        }
    }
}
