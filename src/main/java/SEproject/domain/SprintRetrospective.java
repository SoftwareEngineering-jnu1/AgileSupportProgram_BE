package SEproject.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SprintRetrospective {
    private Long id;
    private Long epicId;
    private String sprintName;
    private List<String> stop = new ArrayList<>();
    private List<String> start = new ArrayList<>();
    private List<String> continueAction = new ArrayList<>();
    private List<Long> memberIds = new ArrayList<>();
    private Long completeMemberCount = 0L;
    private Long totalMemberCount;
}