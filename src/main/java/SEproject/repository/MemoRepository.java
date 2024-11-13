package SEproject.repository;

import SEproject.domain.Issue;
import SEproject.domain.Memo;
import SEproject.dto.NewIssueDTO;
import SEproject.dto.NewMemoDTO;

import java.time.LocalDate;
import java.util.List;

public interface MemoRepository {
    public Memo findById(Long id);
    public List<Memo> findAll();
    public Memo save(NewMemoDTO memoDTO, Long projectId, LocalDate editDate);
    public Memo edit(NewMemoDTO memoDTO, Long memoId, LocalDate editDate);
}
