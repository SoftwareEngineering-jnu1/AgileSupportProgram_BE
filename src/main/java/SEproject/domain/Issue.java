package SEproject.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Issue {
    private Long id;
    private String todo;
    private Long epicId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long mainMemberId;
    private String progressStatus;
}
