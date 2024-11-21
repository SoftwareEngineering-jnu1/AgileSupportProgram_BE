package SEproject.controller;

import SEproject.domain.Epic;
import SEproject.domain.SprintRetrospective;
import SEproject.dto.*;
import SEproject.service.EpicService;
import SEproject.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EpicController {
    private final EpicService epicService;

    @Autowired
    public EpicController(EpicService epicService) {
        this.epicService = epicService;
    }

    @PostMapping("SE/project/{projectId}/addepic")
    public ResponseEntity<?> createEpic(@RequestBody NewEpicDTO newEpicDTO, @PathVariable Long projectId, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        Epic epic = epicService.createEpic(newEpicDTO, projectId);
        return ResponseEntity.ok(new ApiResponse("success", epic));
    }

    @PostMapping("SE/project/{projectId}/{epicId}/edit")
    public ResponseEntity<?> correctionEpic(@RequestBody EditEpicDTO editEpicDTO, @PathVariable Long epicId, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        EditEpicDTO editEpicDTO1 = epicService.correctionEpic(editEpicDTO, epicId);
        return ResponseEntity.ok(new ApiResponse("success", editEpicDTO1));
    }

    @GetMapping("SE/project/{projectId}/{epicId}/edit")
    public ResponseEntity<?> checkEpic(@PathVariable Long epicId, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        EditEpicDTO editEpicDTO = epicService.checkEpic(epicId);
        return ResponseEntity.ok(new ApiResponse("success", editEpicDTO));
    }

    @PostMapping("SE/project/{projectId}/kanbanboard/newsprint")
    public ResponseEntity<?> settingSprint(@RequestBody NewSprintDTO newSprintDTO, @PathVariable Long projectId,HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        Map<String, String> result = new HashMap<>();

        if(epicService.settingSprint(newSprintDTO, projectId) != null) {
            result.put("epicId", epicService.settingSprint(newSprintDTO, projectId));
            return ResponseEntity.ok(new ApiResponse("success", result));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse("fail", "존재하지 않는 에픽 title을 넘겨주었음"));
        }
    }

    @GetMapping("SE/project/{projectId}/kanbanboard/{epicId}")
    public ResponseEntity<?> getKanbanboard(@PathVariable Long projectId, @PathVariable Long epicId, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        KanbanboardDTO kanbanboard = epicService.getKanbanboard(projectId, epicId);
        return ResponseEntity.ok(new ApiResponse("success", kanbanboard));
    }

    @PostMapping("SE/project/{projectId}/kanbanboard/{epicId}/{issueId}")
    public ResponseEntity<?> editKanbanboard(@RequestBody KanbanboardEditIssueDTO kanbanboardEditIssueDTO, @PathVariable Long epicId ,@PathVariable Long issueId, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        KanbanboardEditIssueDTO kanbanboardEditIssueDTO1 = epicService.editKanbanboard(epicId, issueId, kanbanboardEditIssueDTO.getProgressStatus());
        return ResponseEntity.ok(new ApiResponse("success", kanbanboardEditIssueDTO1));
    }

    @PostMapping("SE/project/{projectId}/kanbanboard/{epicId}/review")
    public ResponseEntity<?> submitRetrospective(@RequestBody SubmitRetrospectiveDTO submitRetrospectiveDTO, @PathVariable Long projectId, @PathVariable Long epicId, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        SprintRetrospective sprintRetrospective = epicService.submitRetrospective(submitRetrospectiveDTO, projectId, epicId);
        return ResponseEntity.ok(new ApiResponse("success", sprintRetrospective));
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
