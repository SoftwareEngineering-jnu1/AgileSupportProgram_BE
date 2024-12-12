package SEproject.service;

import SEproject.domain.Issue;
import SEproject.dto.EditIssueDTO;
import SEproject.dto.NewIssueDTO;
import SEproject.repository.EpicRepository;
import SEproject.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class IssueService {
    private final IssueRepository issueRepository;
    private final EpicService epicService;
    private final EpicRepository epicRepository;

    @Autowired
    public IssueService(IssueRepository issueRepository, EpicService epicService, EpicRepository epicRepository) {
        this.issueRepository = issueRepository;
        this.epicService = epicService;
        this.epicRepository = epicRepository;
    }

    public Issue createIssue(NewIssueDTO newIssueDTO, Long epicId) {
        Issue save = issueRepository.save(newIssueDTO, epicId);

        epicService.epicProgressStatus(epicId);

        return save;
    }

    public EditIssueDTO correctionIssue(NewIssueDTO newIssueDTO, Long epicId , Long issueId) {
        if(epicService.editKanbanboard(epicId, issueId, newIssueDTO.getProgressStatus()) == null) {
            return null;
        }

        Issue result = issueRepository.edit(newIssueDTO, issueId);
        Map<String, Long> epicProgressStatus = epicService.epicProgressStatus(epicId);

        EditIssueDTO editIssueDTO = new EditIssueDTO();
        editIssueDTO.setIssue(result);
        editIssueDTO.setEpicProgressStatus(epicProgressStatus);

        return editIssueDTO;
    }

    public Issue checkIssue(Long issueId) {
        return issueRepository.findById(issueId);
    }
}