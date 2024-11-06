package SEproject.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class NewIssueDTO {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String mainMemberName;
    private String progressStatus;
}