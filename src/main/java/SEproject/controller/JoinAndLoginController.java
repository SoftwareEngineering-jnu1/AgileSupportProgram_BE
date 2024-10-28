package SEproject.controller;

import SEproject.domain.Member;
import SEproject.dto.MemberJoinDTO;
import SEproject.dto.MemberLoginDTO;
import SEproject.service.JoinAndLoginService;
import SEproject.web.SessionConst;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class JoinAndLoginController {
    private final JoinAndLoginService joinAndLoginService;
    private final Map<String, String> responseSuccess = new HashMap<>();
    private final Map<String, String> responseError = new HashMap<>();

    @Autowired
    public JoinAndLoginController(JoinAndLoginService joinAndLoginService) {
        this.joinAndLoginService = joinAndLoginService;
    }

    @PostConstruct
    public void init() {
        responseSuccess.put("statusS", "success");
        responseError.put("statusF", "fail");
    }

    @PostMapping("SE/join")
    public Map<String, String> join(@RequestBody MemberJoinDTO memberJoinDTO) {
        // 사용자가 올바르게 데이터를 입력하였는지 확인 - null, "", " "은 입력할 수 없음
        if(memberJoinDTO.getEmailId() == null || memberJoinDTO.getPassword() == null || memberJoinDTO.getUsername() == null) {
            return responseError;
        } else if(memberJoinDTO.getEmailId().equals(" ") || memberJoinDTO.getPassword().equals(" ") || memberJoinDTO.getUsername().equals(" ")) {
            return responseError;
        } else if(memberJoinDTO.getEmailId().isEmpty() || memberJoinDTO.getPassword().isEmpty() || memberJoinDTO.getUsername().isEmpty()) {
            return responseError;
        }

        Member createMember = joinAndLoginService.createMember(memberJoinDTO);
        return responseSuccess;
    }

    @PostMapping("SE/login")
    public Map<String, String> login(@RequestBody MemberLoginDTO memberLoginDTO, HttpServletRequest request) {
        if(memberLoginDTO.getEmail() == null || memberLoginDTO.getPassword() == null) {
            return responseError;
        } else if(memberLoginDTO.getEmail().equals(" ") || memberLoginDTO.getPassword().equals(" ")) {
            return responseError;
        } else if(memberLoginDTO.getEmail().isEmpty() || memberLoginDTO.getPassword().isEmpty()) {
            return responseError;
        }

        Member loginMember = joinAndLoginService.login(memberLoginDTO.getEmail(), memberLoginDTO.getPassword());

        // 로그인이 실패할 경우
        if(loginMember == null) {
            return responseError;
        }

        // 로그인 성공 처리, 세션이 있으면 세션을 반환하고 없으면 신규 세선 생성
        HttpSession session = request.getSession();
        // 세션에 로그인 정보 보관 - 첫 번째 매개변수 : 키(문자열), 두 번째 매개변수 : 저장할 데이터 객체
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
}
