package SEproject.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberJoinDTO {
    private String emailId;
    private String username;
    private String password;
}
