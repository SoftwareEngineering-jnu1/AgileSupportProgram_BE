package SEproject.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Member {
    private Long id;
    private String emailId;
    private String username;
    private String password;
}