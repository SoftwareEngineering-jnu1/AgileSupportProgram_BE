package SEproject.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class KanbanboardDTO {
    private String projectName;
    private String sprintName;
    private LocalDate sprintEndDate;
    private List<KanbanboardIssueDTO> kanbanboardIssueDTO;
}
