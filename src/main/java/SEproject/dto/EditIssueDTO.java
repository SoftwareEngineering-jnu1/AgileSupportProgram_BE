package SEproject.dto;

import SEproject.domain.Issue;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class EditIssueDTO {
    private Issue issue;
    private Map<String, Long> epicProgressStatus;
}