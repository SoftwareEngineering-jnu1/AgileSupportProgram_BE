package SEproject.controller;

import SEproject.domain.Issue;
import SEproject.dto.EditIssueDTO;
import SEproject.dto.NewIssueDTO;
import SEproject.service.IssueService;
import SEproject.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class IssueController {
    private final IssueService issueService;

    @Autowired
    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping("SE/project/{projectId}/{epicId}/addissue")
    public Issue createIssue(@RequestBody NewIssueDTO newIssueDTO, @PathVariable Long epicId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        return issueService.createIssue(newIssueDTO, epicId);
    }

    @PostMapping("SE/project/{projectId}/{epicId}/{issueId}/edit")
    public EditIssueDTO correctionIssue(@RequestBody NewIssueDTO newIssueDTO, @PathVariable Long epicId , @PathVariable Long issueId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        return issueService.correctionIssue(newIssueDTO, epicId ,issueId);
    }

    @GetMapping("SE/project/{projectId}/{epicId}/{issueId}/edit")
    public Issue checkIssue(@PathVariable Long issueId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        return issueService.checkIssue(issueId);
    }
}
