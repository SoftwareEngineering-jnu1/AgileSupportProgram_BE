package SEproject.controller;

import SEproject.domain.Member;
import SEproject.dto.EditMemberDTO;
import SEproject.dto.GetMyPage;
import SEproject.dto.NewMemberDTO;
import SEproject.dto.LoginMemberDTO;
import SEproject.service.MemberService;
import SEproject.web.SessionConst;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MemberController {
    private final MemberService memberService;
    private final Map<String, String> responseSuccess = new HashMap<>();
    private final Map<String, String> responseError = new HashMap<>();

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostConstruct
    public void init() {
        responseSuccess.put("statusS", "success");
        responseError.put("statusF", "fail");
    }

    @PostMapping("SE/join")
    public Map<String, String> join(@RequestBody NewMemberDTO memberJoinDTO) {
        if(memberJoinDTO.getEmailId() == null || memberJoinDTO.getPassword() == null || memberJoinDTO.getUsername() == null) {
            return responseError;
        } else if(memberJoinDTO.getEmailId().equals(" ") || memberJoinDTO.getPassword().equals(" ") || memberJoinDTO.getUsername().equals(" ")) {
            return responseError;
        } else if(memberJoinDTO.getEmailId().isEmpty() || memberJoinDTO.getPassword().isEmpty() || memberJoinDTO.getUsername().isEmpty()) {
            return responseError;
        }

        memberService.createMember(memberJoinDTO);

        return responseSuccess;
    }

    @PostMapping("SE/login")
    public Map<String, String> login(@RequestBody LoginMemberDTO loginMemberDTO, HttpServletRequest request) {
        if(loginMemberDTO.getEmailId() == null || loginMemberDTO.getPassword() == null) {
            return responseError;
        } else if(loginMemberDTO.getEmailId().equals(" ") || loginMemberDTO.getPassword().equals(" ")) {
            return responseError;
        } else if(loginMemberDTO.getEmailId().isEmpty() || loginMemberDTO.getPassword().isEmpty()) {
            return responseError;
        }

        Member loginMember = memberService.login(loginMemberDTO);

        // 로그인 실패
        if(loginMember == null) {
            return responseError;
        }

        // 로그인 성공, 세션이 있으면 세션을 반환하고 없으면 신규 세선 생성
        HttpSession session = request.getSession();
        // 첫 번째 매개변수 : 키(문자열), 두 번째 매개변수 : 저장할 데이터 객체
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return responseSuccess;
    }

    @PostMapping("SE/logout")
    public Map<String, String> logout(HttpServletRequest request) {
        // getSession의 기본값은 true이므로 false를 명시해야 함(의미없는 세션을 생성하지 않기 위함)
        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
        }

        return responseSuccess;
    }

    @GetMapping("SE/projects/{memberId}")
    public Map<String, Map<String, Long>> projectList(@PathVariable("memberId") Long memberId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        return memberService.projectList(memberId);
    }

    @GetMapping("SE/members/{memberId}")
    public GetMyPage getMyPage(@PathVariable Long memberId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        return memberService.getMyPage(memberId);
    }

    @PostMapping("SE/members/{memberId}/edit")
    public Member editMember(@RequestBody EditMemberDTO editMemberDTO, @PathVariable Long memberId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        return memberService.editMember(editMemberDTO, memberId);
    }

    @GetMapping("SE/members/{memberId}/edit")
    public Member getMember(@PathVariable Long memberId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if(loginMember == null) {
                return null;
            }
        }

        return memberService.getMember(memberId);
    }
}