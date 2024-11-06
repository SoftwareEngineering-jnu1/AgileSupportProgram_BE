package SEproject.repository.memoryrepository;

import SEproject.domain.Issue;
import SEproject.dto.NewIssueDTO;
import SEproject.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryIssueRepository implements IssueRepository {
    public static ConcurrentHashMap<Long, Issue> store = new ConcurrentHashMap<>();
    public static AtomicLong sequence = new AtomicLong();
    public MemoryMemberRepository memberRepository;

    @Autowired
    public MemoryIssueRepository(MemoryMemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public Issue findById(Long id) {
        return store.get(id);
    }

    @Override
    public List<Issue> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Issue save(NewIssueDTO newIssueDTO, Long epicId) {
        Issue issue = new Issue();
        issue.setId(sequence.incrementAndGet());
        issue.setEpicId(epicId);
        issue.setTitle(newIssueDTO.getTitle());
        issue.setStartDate(newIssueDTO.getStartDate());
        issue.setEndDate(newIssueDTO.getEndDate());
        if(memberRepository.findByUsername(newIssueDTO.getMainMemberName()).isPresent()) {
            issue.setMainMemberName(newIssueDTO.getMainMemberName());
        } else {
            issue.setMainMemberName(null);
        }
        issue.setProgressStatus(newIssueDTO.getProgressStatus());

        store.put(issue.getId(), issue);

        return issue;
    }
}
