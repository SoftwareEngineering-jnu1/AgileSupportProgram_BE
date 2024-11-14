package SEproject.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class Member {
    private Long id;
    private String emailId;
    private String username;
    private String password;
    private List<Long> projectIds = new ArrayList<>();
    private String position;
    private String companyOrSchool;
    private String contactInfo;
    private Map<Long, SprintRetrospective> sprintRetrospectives = new HashMap<>();
}