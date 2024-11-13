package SEproject.controller;

import SEproject.domain.Epic;
import SEproject.domain.Issue;
import SEproject.dto.EditEpicDTO;
import SEproject.dto.NewEpicDTO;
import SEproject.dto.NewSprintDTO;
import SEproject.service.EpicService;
import SEproject.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public String settingSprint(@RequestBody NewSprintDTO newSprintDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        if(epicService.settingSprint(newSprintDTO) != null) {
            return "success";
        } else {
            return "fail";
        }
    }
}
