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
    // 시스템 내부에서 멤버를 구별하기 위한 ID
    private Long id;
    // 사용자가 직접 입력하는 email ID
    @NotBlank
    private String emailId;
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    // 회원이 속한 프로젝트 Id
    private List<Long> projectIds = new ArrayList<>();
    // 하나의 스프린트가 종료된 후 받는 스프린트 회고
    private Map<Long, SprintRetrospective> sprintRetrospectives = new HashMap<>();
}