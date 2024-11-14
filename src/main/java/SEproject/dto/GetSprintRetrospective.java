package SEproject.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GetSprintRetrospective {
    private List<String> stop;
    private List<String> start;
    private List<String> Continue;
}
