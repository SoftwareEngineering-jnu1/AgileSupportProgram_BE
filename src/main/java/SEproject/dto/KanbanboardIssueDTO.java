package SEproject.dto;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.Map;

@Getter
@Setter
public class KanbanboardIssueDTO {
    private Long issueId;
    private String issueTitle;
    private Map<String, String> mainMemberNameAndColor;
    private String progressStatus;
}