package SEproject.service;

import SEproject.domain.Epic;
import SEproject.dto.EditEpicDTO;
import SEproject.dto.NewEpicDTO;
import SEproject.repository.EpicRepository;
import SEproject.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public EditEpicDTO correctionEpic(EditEpicDTO editEpicDTO, Long epicId) {
        return epicRepository.edit(editEpicDTO, epicId);
    }

    public EditEpicDTO checkEpic(Long epicId) {
        EditEpicDTO editEpicDTO = new EditEpicDTO();
        Epic epic = epicRepository.findById(epicId);

        editEpicDTO.setStartDate(epic.getStartDate());
        editEpicDTO.setEndDate(epic.getEndDate());
        editEpicDTO.setTitle(epic.getTitle());

        List<Long> issueIds = epic.getIssueIds();
        List<String> issueTitles = new ArrayList<>();
        for(int i = 0; i < issueIds.size(); i++) {
            issueTitles.add(issueRepository.findById(issueIds.get(i)).getTitle());
        }
        editEpicDTO.getSubIssueTitle().addAll(issueTitles);

        for(int i = 0; i < epic.getDependency().size(); i++) {
            editEpicDTO.getDependency().put(Long.valueOf(i), issueRepository.findById(epic.getDependency().get(Long.valueOf(i))).getTitle());
        }
        editEpicDTO.setEpicProgressStatus(this.epicProgressStatus(epicId));

        return editEpicDTO;
    }
}