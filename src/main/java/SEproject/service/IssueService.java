package SEproject.service;

import SEproject.domain.Issue;
import SEproject.dto.NewIssueDTO;
import SEproject.repository.memoryrepository.MemoryIssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IssueService {
    private final MemoryIssueRepository memoryIssueRepository;

    @Autowired
    public IssueService(MemoryIssueRepository memoryIssueRepository) {
        this.memoryIssueRepository = memoryIssueRepository;
    }

    public Issue createIssue(NewIssueDTO newIssueDTO, Long epicId) {
        return memoryIssueRepository.save(newIssueDTO, epicId);
    }
}
