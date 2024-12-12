package SEproject.service;

import SEproject.domain.Epic;
import SEproject.domain.Issue;
import SEproject.domain.Member;
import SEproject.domain.SprintRetrospective;
import SEproject.dto.*;
import SEproject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.List;

@Service
public class EpicService {
    private final EpicRepository epicRepository;
    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final SprintRetrospectiveRepository sprintRetrospectiveRepository;
    private final MemberRepository memberRepository;

    private final Map<String, String> memberColorMap = new HashMap<>();

    @Autowired
    public EpicService(EpicRepository epicRepository, IssueRepository issueRepository, ProjectRepository projectRepository, SprintRetrospectiveRepository sprintRetrospectiveRepository, MemberRepository memberRepository) {
        this.epicRepository = epicRepository;
        this.issueRepository = issueRepository;
        this.projectRepository = projectRepository;
        this.sprintRetrospectiveRepository = sprintRetrospectiveRepository;
        this.memberRepository = memberRepository;
    }

    public Epic createEpic(NewEpicDTO newEpicDTO, Long projectId) {
        return epicRepository.save(newEpicDTO, projectId);
    }

    public Map<String, Long> epicProgressStatus(Long epicId) {
        Epic epic = epicRepository.findById(epicId);
        List<Long> issueIds = epic.getIssueIds();

        Long completedIssues = 0L;
        for (int i = 0; i < issueIds.size(); i++) {
            Long issueId = issueIds.get(i);
            if (issueRepository.findById(issueId).getIscompleted()) {
                completedIssues++;
            }
        }

        long totalIssue = issueIds.size();
        long completeIssue = completedIssues;
        System.out.println("epicId = " + epicId);
        System.out.println("issueIds = " + issueIds.size());
        System.out.println("completeIssue = " + completeIssue);

        if(completeIssue != 0L && totalIssue == completeIssue) {
            epicRepository.findById(epicId).setIsCompleted(true);
        } else {
            epicRepository.findById(epicId).setIsCompleted(false);
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
        for (int i = 0; i < issueIds.size(); i++) {
            Long issueId = issueIds.get(i);
            Issue issue = issueRepository.findById(issueId);
            editEpicDTO.getSubIssues().add(issue);
        }

        /*
        List<Long> issueIds = epic.getIssueIds();
        List<String> issueTitles = new ArrayList<>();
        for (int i = 0; i < issueIds.size(); i++) {
            issueTitles.add(issueRepository.findById(issueIds.get(i)).getTitle());
        }
        editEpicDTO.getSubIssueTitle().addAll(issueTitles);
         */

        for (int i = 0; i < epic.getDependency().size(); i++) {
            editEpicDTO.getDependency().put(Long.valueOf(i), issueRepository.findById(epic.getDependency().get(Long.valueOf(i))).getTitle());
        }
        editEpicDTO.setEpicProgressStatus(this.epicProgressStatus(epicId));

        return editEpicDTO;
    }

    public String settingSprint(NewSprintDTO newSprintDTO, Long projectId) {
        epicRepository.findByTitle(newSprintDTO.getEpicTitle()).get().setSprintName(newSprintDTO.getSprintName());
        Long epicId = epicRepository.findByTitle(newSprintDTO.getEpicTitle()).get().getId();
        // 스프린트 회고 제작
        SprintRetrospective sprintRetrospective = new SprintRetrospective();
        sprintRetrospective.setEpicId(epicId);
        sprintRetrospective.setSprintName(newSprintDTO.getSprintName());

        List<Long> membersId = projectRepository.findById(projectId).getMembersId();
        sprintRetrospective.setMemberIds(membersId);
        sprintRetrospective.setTotalMemberCount(Long.valueOf(membersId.size()));
        sprintRetrospectiveRepository.save(sprintRetrospective);

        return epicId.toString();
    }

    public KanbanboardDTO getKanbanboard(Long projectId, Long epicId) {
        KanbanboardDTO kanbanboardDTO = new KanbanboardDTO();
        kanbanboardDTO.setProjectName(projectRepository.findById(projectId).getProjectName());
        kanbanboardDTO.setSprintName(epicRepository.findById(epicId).getSprintName());
        kanbanboardDTO.setSprintEndDate(epicRepository.findById(epicId).getEndDate());

        List<Long> issueIds = epicRepository.findById(epicId).getIssueIds();
        List<KanbanboardIssueDTO> kanbanboardIssueDTOs = new ArrayList<>();
        for (int i = 0; i < issueIds.size(); i++) {
            KanbanboardIssueDTO kanbanboardIssueDTO = new KanbanboardIssueDTO();
            kanbanboardIssueDTO.setIssueId(issueIds.get(i));
            kanbanboardIssueDTO.setIssueTitle(issueRepository.findById(issueIds.get(i)).getTitle());
            kanbanboardIssueDTO.setProgressStatus(issueRepository.findById(issueIds.get(i)).getProgressStatus());

            Map<String, String> mainMemberNameAndColor = new HashMap<>();

            // 담당자 별로 색상 지정
            if (memberColorMap.get(issueRepository.findById(issueIds.get(i)).getMainMemberName()) != null) {
                mainMemberNameAndColor.put(issueRepository.findById(issueIds.get(i)).getMainMemberName(), memberColorMap.get(issueRepository.findById(issueIds.get(i)).getMainMemberName()));
            } else {
                memberColorMap.put(issueRepository.findById(issueIds.get(i)).getMainMemberName(), generateRandomColor());
                mainMemberNameAndColor.put(issueRepository.findById(issueIds.get(i)).getMainMemberName(), memberColorMap.get(issueRepository.findById(issueIds.get(i)).getMainMemberName()));
            }

            kanbanboardIssueDTO.setMainMemberNameAndColor(mainMemberNameAndColor);
            kanbanboardIssueDTOs.add(kanbanboardIssueDTO);
        }
        kanbanboardDTO.setKanbanboardIssueDTO(kanbanboardIssueDTOs);

        return kanbanboardDTO;
    }

    private String generateRandomColor() {
        Random random = new Random();
        int red = random.nextInt(255);
        int green = random.nextInt(255);
        int blue = random.nextInt(255);
        return String.format("#%02X%02X%02X", red, green, blue);
    }

    public KanbanboardEditIssueDTO editKanbanboard(Long epicId, Long issueId, String progressStatus) {
        if (progressStatus.equals("Done")) {

            Map<Long, Long> dependency = epicRepository.findById(epicId).getDependency();
            if(!dependency.isEmpty() && dependency.get(1L).equals(issueId)) {
                if(issueRepository.findById(dependency.get(0L)).getProgressStatus().equals("Done")) {
                    epicRepository.findById(epicId).getDependency().remove(0L);
                    epicRepository.findById(epicId).getDependency().remove(1L);
                } else {
                    return null;
                }
            }

            issueRepository.findById(issueId).setProgressStatus(progressStatus);
            issueRepository.findById(issueId).setIscompleted(true);

            Epic epic = epicRepository.findById(epicId);
            Map<String, Long> epicProgressStatus = epicProgressStatus(epicId);
            if (epicProgressStatus.get("totalIssues").equals(epicProgressStatus.get("completedIssues"))) {
                epic.setIsCompleted(true);
            } else {
                epic.setIsCompleted(false);
            }
        } else {
            issueRepository.findById(issueId).setProgressStatus(progressStatus);
            issueRepository.findById(issueId).setIscompleted(false);

            Epic epic = epicRepository.findById(epicId);
            Map<String, Long> epicProgressStatus = epicProgressStatus(epicId);
            if (epicProgressStatus.get("totalIssues").equals(epicProgressStatus.get("completedIssues"))) {
                epic.setIsCompleted(true);
            } else {
                epic.setIsCompleted(false);
            }
        }

        KanbanboardEditIssueDTO editIssueDTO = new KanbanboardEditIssueDTO();
        editIssueDTO.setIssudId(issueId);
        editIssueDTO.setProgressStatus(progressStatus);

        return editIssueDTO;
    }

    public SprintRetrospective submitRetrospective(SubmitRetrospectiveDTO submitRetrospectiveDTO, Long projectId, Long epicId) {
        SprintRetrospective submit = sprintRetrospectiveRepository.findByEpicId(epicId);

        if (submit == null) {
            return new SprintRetrospective();
        }

        submit.getStop().add(submitRetrospectiveDTO.getStop());
        submit.getStart().add(submitRetrospectiveDTO.getStart());
        submit.getContinueAction().add(submitRetrospectiveDTO.getContinueAction());

        Long completeMemberCount = submit.getCompleteMemberCount();
        submit.setCompleteMemberCount(completeMemberCount + 1);

        if (submit.getCompleteMemberCount().equals(submit.getTotalMemberCount())) {
            List<Long> membersId = projectRepository.findById(projectId).getMembersId();
            for (Long memberId : membersId) {
                Member member = memberRepository.findById(memberId);
                member.getSprintRetrospectives().put(epicId, submit);
            }
        }

        return submit;
    }
}