package SEproject.service;

import SEproject.domain.Epic;
import SEproject.dto.NewEpicDTO;
import SEproject.repository.EpicRepository;
import SEproject.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EpicService {
    private final EpicRepository epicRepository;
    private final IssueRepository issueRepository;

    @Autowired
    public EpicService(EpicRepository epicRepository, IssueRepository issueRepository) {
        this.epicRepository = epicRepository;
        this.issueRepository = issueRepository;
    }

    public Epic createEpic(NewEpicDTO newEpicDTO, Long projectId) {
        return epicRepository.save(newEpicDTO, projectId);
    }

    public Map<String, Long> epicProgressStatus(Long epicId) {
        Epic epic = epicRepository.findById(epicId);
        List<Long> issueIds = epic.getIssueIds();

        Long completedIssues = 0L;
        for(int i = 0; i < issueIds.size(); i++) {
            Long issueId = issueIds.get(i);
            if(issueRepository.findById(issueId).getIscompleted()) {
                completedIssues++;
            }
        }

        Map<String, Long> result = new HashMap<>();
        result.put("totalIssues", Long.valueOf(issueIds.size()));
        result.put("completedIssues", completedIssues);

        return result;
    }
}