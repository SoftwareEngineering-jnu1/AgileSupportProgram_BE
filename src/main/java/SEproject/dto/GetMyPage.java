package SEproject.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class GetMyPage {
    private Map<String, Map<String, Boolean>> projectAndEpic;
    // Long : 에픽 아이디, String : 스프린트 이름
    private Map<Long, String> sprintRetrospective = new HashMap<>();
    private String username;
    private String position;
    private String companyOrSchool;
    private String contactInfo;
}