package SEproject.controller;

import SEproject.domain.Epic;
import SEproject.dto.*;
import SEproject.service.EpicService;
import SEproject.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Epic createEpic(@RequestBody NewEpicDTO newEpicDTO, @PathVariable Long projectId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        return epicService.createEpic(newEpicDTO, projectId);
    }

    @PostMapping("SE/project/{projectId}/{epicId}/edit")
    public EditEpicDTO correctionEpic(@RequestBody EditEpicDTO editEpicDTO, @PathVariable Long epicId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        return epicService.correctionEpic(editEpicDTO, epicId);
    }

    @GetMapping("SE/project/{projectId}/{epicId}/edit")
    public EditEpicDTO checkEpic(@PathVariable Long epicId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        return epicService.checkEpic(epicId);
    }

    @PostMapping("SE/project/{projectId}/kanbanboard/newsprint")
    public Map<String, String> settingSprint(@RequestBody NewSprintDTO newSprintDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        Map<String, String> result = new HashMap<>();

        if(epicService.settingSprint(newSprintDTO) != null) {
            result.put("epicId", epicService.settingSprint(newSprintDTO));
            return result;
        } else {
            result.put("epicId", null);
            return result;
        }
    }

    @GetMapping("SE/project/{projectId}/kanbanboard/{epicId}")
    public KanbanboardDTO getKanbanboard(@PathVariable Long projectId, @PathVariable Long epicId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        return epicService.getKanbanboard(projectId, epicId);
    }

    @PostMapping("SE/project/{projectId}/kanbanboard/{epicId}/{issueId}")
    public KanbanboardEditIssueDTO editKanbanboard(@RequestBody KanbanboardEditIssueDTO kanbanboardEditIssueDTO, @PathVariable Long issueId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        return epicService.editKanbanboard(issueId, kanbanboardEditIssueDTO.getProgressStatus());
    }
}
