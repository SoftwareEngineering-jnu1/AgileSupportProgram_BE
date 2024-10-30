package SEproject.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Sprint {
    private Long id;
    private Boolean isActive = false;
    private String sprintName;
    private LocalDate sprintStart;
    private LocalDate sprintEnd;
    private Long epidId;

    // 스프린트 회고 관련 필드, Boolean : 스프린트에 참여한 모든 Member가 스프린트 회고를 작성하였는지 판별
    private Map<Boolean, StringBuffer> sprintRetrospectivesStop = new HashMap<>();
    private Map<Boolean, StringBuffer> sprintRetrospectivesStart = new HashMap<>();
    private Map<Boolean, StringBuffer> sprintRetrospectivesContinue = new HashMap<>();
}