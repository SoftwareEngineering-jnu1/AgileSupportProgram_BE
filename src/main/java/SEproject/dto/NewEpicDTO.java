package SEproject.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class NewEpicDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private String title;
}
