package SEproject.dto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TimelineEpicDTO {
    private String epicTitle;
    private LocalDate epicStartDate;
    private LocalDate epicEndDate;
    private Map<String, Long> epicProgressStatus = new HashMap<>();
    private List<TimelineIssueDTO> issues = new ArrayList<>();
}