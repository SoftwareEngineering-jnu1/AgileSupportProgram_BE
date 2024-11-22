package SEproject.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetSprintRetrospectiveDTO {
    private List<String> stop;
    private List<String> start;
    private List<String> continueAction;
}
