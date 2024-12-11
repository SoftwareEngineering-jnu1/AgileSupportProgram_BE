package SEproject.dto;

import SEproject.domain.Issue;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class EditEpicDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private String title;
    private List<String> subIssueTitle = new ArrayList<>();
    private Map<Long, String> dependency = new HashMap<>();
    private Map<String, Long> epicProgressStatus = new HashMap<>();
    private List<Issue> subIssues = new ArrayList<>();
}