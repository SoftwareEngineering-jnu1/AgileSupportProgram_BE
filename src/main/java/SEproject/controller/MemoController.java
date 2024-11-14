package SEproject.controller;

import SEproject.domain.Memo;
import SEproject.dto.NewMemoDTO;
import SEproject.service.MemoService;
import SEproject.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class MemoController {
    private final MemoService memoService;

    @Autowired
    public MemoController(MemoService memoService) {
        this.memoService = memoService;
    }

    @PostMapping("SE/project/{projectId}/memo/newmemo")
    public Memo createMemo(@RequestBody NewMemoDTO memoDTO, @PathVariable Long projectId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        return memoService.createMemo(memoDTO, projectId);
    }

    @GetMapping("SE/project/{projectId}/memo")
    public Map<String, List<Memo>> getMemos(@PathVariable Long projectId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        return memoService.getMemos(projectId);
    }

    @GetMapping("SE/project/{projectId}/memo/{memoId}")
    public Memo getMemo(@PathVariable Long memoId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        return memoService.getMemo(memoId);
    }

    @PostMapping("SE/project/{projectId}/memo/{memoId}")
    public Memo correctionMemo(@RequestBody NewMemoDTO memoDTO, @PathVariable Long memoId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        return memoService.correctionMemo(memoDTO, memoId);
    }
}
