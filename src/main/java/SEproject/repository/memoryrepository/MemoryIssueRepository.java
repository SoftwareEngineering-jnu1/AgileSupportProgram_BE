package SEproject.repository.memoryrepository;

import SEproject.domain.Issue;
import SEproject.dto.NewIssueDTO;
import SEproject.repository.EpicRepository;
import SEproject.repository.IssueRepository;
import SEproject.repository.MemberRepository;
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
    public final MemberRepository memberRepository;
    public final EpicRepository epicRepository;

    @Autowired
    public MemoryIssueRepository(MemberRepository memberRepository, EpicRepository epicRepository) {
        this.memberRepository = memberRepository;
        this.epicRepository = epicRepository;
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
        epicRepository.findById(epicId).getIssueIds().add(issue.getId());

        store.put(issue.getId(), issue);

        return issue;
    }

    @Override
    public Issue edit(NewIssueDTO newIssueDTO, Long issueId) {
        Issue editIssue = store.get(issueId);
        editIssue.setTitle(newIssueDTO.getTitle());
        editIssue.setStartDate(newIssueDTO.getStartDate());
        editIssue.setEndDate(newIssueDTO.getEndDate());
        editIssue.setProgressStatus(newIssueDTO.getProgressStatus());
        if(memberRepository.findByUsername(newIssueDTO.getMainMemberName()).isPresent()) {
            editIssue.setMainMemberName(newIssueDTO.getMainMemberName());
        } else {
            editIssue.setMainMemberName(null);
        }
        if(editIssue.getProgressStatus().equals("Done")) {
            editIssue.setIscompleted(true);
        }

        return editIssue;
    }
}