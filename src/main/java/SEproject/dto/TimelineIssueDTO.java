package SEproject.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TimelineIssueDTO {
    private Long issueId;
    private String issueTitle;
    private LocalDate issueStartDate;
    private LocalDate issueEndDate;
    private boolean hasDependency;
}
