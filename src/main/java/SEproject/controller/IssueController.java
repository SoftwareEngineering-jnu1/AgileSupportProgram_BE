package SEproject.controller;

import SEproject.domain.Issue;
import SEproject.dto.EditIssueDTO;
import SEproject.dto.NewIssueDTO;
import SEproject.service.IssueService;
import SEproject.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class IssueController {
    private final IssueService issueService;

    @Autowired
    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping("SE/project/{projectId}/{epicId}/addissue")
    public ResponseEntity<?> createIssue(@RequestBody NewIssueDTO newIssueDTO, @PathVariable Long epicId, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        Issue issue = issueService.createIssue(newIssueDTO, epicId);
        return ResponseEntity.ok(new ApiResponse("success", issue));
    }

    @PostMapping("SE/project/{projectId}/{epicId}/{issueId}/edit")
    public ResponseEntity<?> correctionIssue(@RequestBody NewIssueDTO newIssueDTO, @PathVariable Long epicId , @PathVariable Long issueId, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        EditIssueDTO editIssueDTO = issueService.correctionIssue(newIssueDTO, epicId, issueId);
        if(editIssueDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("fail", "의존관계 위반"));
        }

        return ResponseEntity.ok(new ApiResponse("success", editIssueDTO));
    }

    @GetMapping("SE/project/{projectId}/{epicId}/{issueId}/edit")
    public ResponseEntity<?> checkIssue(@PathVariable Long issueId, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        Issue issue = issueService.checkIssue(issueId);
        return ResponseEntity.ok(new ApiResponse("success", issue));
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
