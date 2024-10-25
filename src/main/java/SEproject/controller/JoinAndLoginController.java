package SEproject.controller;

import SEproject.domain.Member;
import SEproject.dto.MemberJoinDTO;
import SEproject.dto.MemberLoginDTO;
import SEproject.service.JoinAndLoginService;
import jakarta.annotation.PostConstruct;
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
    public Map<String, String> login(@RequestBody MemberLoginDTO memberLoginDTO) {
        System.out.println("memberLoginDTO.getPassword() = " + memberLoginDTO.getPassword());
        System.out.println("memberLoginDTO.getEmail() = " + memberLoginDTO.getEmail());
        if(memberLoginDTO.getEmail() == null || memberLoginDTO.getPassword() == null) {
            return responseError;
        } else if(memberLoginDTO.getEmail().equals(" ") || memberLoginDTO.getPassword().equals(" ")) {
            return responseError;
        } else if(memberLoginDTO.getEmail().isEmpty() || memberLoginDTO.getPassword().isEmpty()) {
            return responseError;
        }

        Member loginMember = joinAndLoginService.login(memberLoginDTO.getEmail(), memberLoginDTO.getPassword());

        if(loginMember == null) {
            return responseError;
        }

        return responseSuccess;
    }
}
