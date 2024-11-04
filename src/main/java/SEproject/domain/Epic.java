package SEproject.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Epic {
    private Long id;
    private Long projectId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
}