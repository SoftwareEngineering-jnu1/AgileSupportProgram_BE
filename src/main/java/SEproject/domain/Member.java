package SEproject.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// 회원에 대한 모든 정보를 보유하고 있는 도메인
@Getter @Setter
public class Member {
    private Long id;
    private String emailId;
    private String username;
    private String password;

    private List<Long> projectIds = new ArrayList<>();
    private List<Long> sprintIds = new ArrayList<>();
}