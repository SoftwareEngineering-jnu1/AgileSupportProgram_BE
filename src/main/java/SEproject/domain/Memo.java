package SEproject.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Memo {
    private Long id;
    private String title;
    private StringBuffer content = new StringBuffer();
}
