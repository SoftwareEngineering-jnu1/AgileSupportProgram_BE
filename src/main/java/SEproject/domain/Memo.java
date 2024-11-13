package SEproject.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Memo {
    private Long id;
    private Long projectId;
    private String title;
    private String content;
    private LocalDate editDate;
    private LocalDate createDate;
}
