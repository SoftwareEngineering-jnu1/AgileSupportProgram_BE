package SEproject.controller;

import SEproject.domain.Member;
import SEproject.dto.*;
import SEproject.service.MemberService;
import SEproject.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("SE/join")
    public ResponseEntity<?> join(@RequestBody NewMemberDTO memberJoinDTO) {
        // NewMemberDTO 검증
        if(memberJoinDTO.getEmailId() == null || memberJoinDTO.getPassword() == null || memberJoinDTO.getUsername() == null) {
            return ResponseEntity.badRequest().body(new ApiResponse("fail", "Invalid join data"));
        } else if(memberJoinDTO.getEmailId().equals(" ") || memberJoinDTO.getPassword().equals(" ") || memberJoinDTO.getUsername().equals(" ")) {
            return ResponseEntity.badRequest().body(new ApiResponse("fail", "Invalid join data"));
        } else if(memberJoinDTO.getEmailId().isEmpty() || memberJoinDTO.getPassword().isEmpty() || memberJoinDTO.getUsername().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("fail", "Invalid join data"));
        }

        // 회원 가입 수행
        memberService.createMember(memberJoinDTO);
        return ResponseEntity.ok(new ApiResponse("success", "Join successfully"));
    }

    @PostMapping("SE/login")
    public ResponseEntity<?> login(@RequestBody LoginMemberDTO loginMemberDTO, HttpServletRequest request) {
        if(loginMemberDTO.getEmailId() == null || loginMemberDTO.getPassword() == null) {
            return ResponseEntity.badRequest().body(new ApiResponse("fail", "Invalid login data"));
        } else if(loginMemberDTO.getEmailId().equals(" ") || loginMemberDTO.getPassword().equals(" ")) {
            return ResponseEntity.badRequest().body(new ApiResponse("fail", "Invalid login data"));
        } else if(loginMemberDTO.getEmailId().isEmpty() || loginMemberDTO.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("fail", "Invalid login data"));
        }

        // 로그인 수행
        Member loginMember = memberService.login(loginMemberDTO);

        // 로그인 실패 처리
        if(loginMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Login failed"));
        }

        // 로그인 성공, 세션이 있으면 세션을 반환하고 없으면 신규 세선 생성
        HttpSession session = request.getSession();
        // 첫 번째 매개변수 : 키(문자열), 두 번째 매개변수 : 저장할 데이터 객체
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return ResponseEntity.ok(new ApiResponse("success", "Login successful"));
    }

    @PostMapping("SE/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // getSession의 기본값은 true이므로 false를 명시해야 함(의미없는 세션을 생성하지 않기 위함)
        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok(new ApiResponse("success", "Logout successful"));
    }

    @GetMapping("SE/projects/{memberId}")
    public ResponseEntity<?> projectList(@PathVariable("memberId") Long memberId, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        // 멤버의 프로젝트 리스트 조회
        Map<String, Map<String, Long>> projectList = memberService.projectList(memberId);
        return ResponseEntity.ok(new ApiResponse("success", projectList));
    }

    @GetMapping("SE/members/{memberId}")
    public ResponseEntity<?> getMyPage(@PathVariable Long memberId, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        // 마이페이지 조회
        GetMyPage myPage = memberService.getMyPage(memberId);
        return ResponseEntity.ok(new ApiResponse("success", myPage));
    }

    @PostMapping("SE/members/{memberId}/edit")
    public ResponseEntity<?> editMember(@RequestBody EditMemberDTO editMemberDTO, @PathVariable Long memberId, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        // 멤버 수정
        Member eidtMember = memberService.editMember(editMemberDTO, memberId);
        return ResponseEntity.ok(new ApiResponse("success", eidtMember));
    }

    @GetMapping("SE/members/{memberId}/edit")
    public ResponseEntity<?> getMember(@PathVariable Long memberId, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        Member member = memberService.getMember(memberId);
        return ResponseEntity.ok(new ApiResponse("success", member));
    }

    @GetMapping("SE/members/{memberId}/{epicId}")
    public ResponseEntity<?> getsprintRetrospective(@PathVariable Long memberId, @PathVariable Long epicId, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        GetSprintRetrospective getSprintRetrospective = memberService.getsprintRetrospective(memberId, epicId);
        return ResponseEntity.ok(new ApiResponse("success", getSprintRetrospective));
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