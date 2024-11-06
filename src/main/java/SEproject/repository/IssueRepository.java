package SEproject.repository;

import SEproject.domain.Issue;
import SEproject.dto.NewIssueDTO;

import java.util.List;

public interface IssueRepository {
    public Issue findById(Long id);
    public List<Issue> findAll();
    public Issue save(NewIssueDTO newIssueDTO, Long epicId);
}
