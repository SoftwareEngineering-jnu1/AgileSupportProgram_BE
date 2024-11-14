package SEproject.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    private List<SprintRetrospective> sprintRetrospectives = new ArrayList<>();
}