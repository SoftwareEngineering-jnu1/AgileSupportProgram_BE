package SEproject.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Epic {
    private Long id;
    private Long projectId;
    private Long sprintId;
    // Long -> 우선순위 설정을 위해 도입
    private Map<Long, Issue> issues = new HashMap<>();
    // 이슈 완료 여부
    private Map<Boolean, Issue> issuesComplete = new HashMap<>();
    private List<Long> membersId = new ArrayList<>();
}
