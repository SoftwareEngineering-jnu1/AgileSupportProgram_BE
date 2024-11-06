package SEproject.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Project {
    private Long id;
    private String projectName;
    private List<Long> membersId = new ArrayList<>();
    private List<Long> epicsId = new ArrayList<>();
}