package SEproject.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class NewProjectDTO {
    private String projectName;
    private List<String> membersEmailId = new ArrayList<>();
}