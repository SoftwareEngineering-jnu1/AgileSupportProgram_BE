package SEproject.service;

import SEproject.domain.Issue;
import SEproject.dto.NewIssueDTO;
import SEproject.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IssueService {
    private final IssueRepository issueRepository;

    @Autowired
    public IssueService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    public Issue createIssue(NewIssueDTO newIssueDTO, Long epicId) {
        return issueRepository.save(newIssueDTO, epicId);
    }
}
