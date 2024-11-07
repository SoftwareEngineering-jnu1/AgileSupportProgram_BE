package SEproject.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Epic {
    private Long id;
    private Long projectId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCompleted = false;
    private List<Long> issueIds = new ArrayList<>();
    private Map<Long, Long> dependency = new HashMap<>();
}