package SEproject.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Issue {
    private Long id;
    private Long epicId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String mainMemberName;
    private String progressStatus;
    private Boolean iscompleted = false;
}