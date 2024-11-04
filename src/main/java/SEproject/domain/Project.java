package SEproject.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// 프로젝트에 대한 모든 정보를 보유하고 있는 도메인
@Getter
@Setter
public class Project {
    private Long id;
    private String projectName;
    private List<Long> membersId = new ArrayList<>();
    private List<Long> epicsId = new ArrayList<>();
}