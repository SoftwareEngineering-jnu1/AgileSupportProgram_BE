package SEproject.service;

import SEproject.domain.Epic;
import SEproject.domain.Member;
import SEproject.domain.Project;
import SEproject.domain.SprintRetrospective;
import SEproject.dto.*;
import SEproject.repository.EpicRepository;
import SEproject.repository.MemberRepository;
import SEproject.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final EpicRepository epicRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository, ProjectRepository projectRepository, EpicRepository epicRepository) {
        this.memberRepository = memberRepository;
        this.projectRepository = projectRepository;
        this.epicRepository = epicRepository;
    }

    public Member createMember(NewMemberDTO newMemberDTO) {
        return memberRepository.save(newMemberDTO);
    }

    public Member login(LoginMemberDTO loginMemberDTO) {
        return memberRepository.findByEmailId(loginMemberDTO.getEmailId())
                .filter(m -> m.getPassword().equals(loginMemberDTO.getPassword()))
                .orElse(null);
    }

    public Map<String, Map<String, Long>> projectList(Long memberId) {
        // 해당 Member의 ProjectId 추출
        List<Long> projectIds = memberRepository.findById(memberId).getProjectIds();
        Map<String, Map<String, Long>> result = new HashMap<>();

        // 프로젝트 별 - 완료한 에픽/ 전체 에픽 수
        for(int i = 0; i < projectIds.size(); i++) {
            Long projectId = projectIds.get(i);
            Map<String, Long> epicRatio = new HashMap<>();

            // 전체 에픽 수 구하기
            List<Long> epicsId = projectRepository.findById(projectId).getEpicsId();
            epicRatio.put("totalEpics", Long.valueOf(epicsId.size()));

            // 완료한 에픽 수 구하기
            Long completedEpics = 0L;
            for(int j = 0; j < epicsId.size(); j++) {
                if(epicRepository.findById(epicsId.get(j)).getIsCompleted()) {
                    completedEpics++;
                }
            }
            epicRatio.put("completedEpics", completedEpics);
            epicRatio.put("projectId", projectId);

            Long totalMember = 0L;
            List<Long> membersId = projectRepository.findById(projectId).getMembersId();
            epicRatio.put("totalMember", Long.valueOf(membersId.size()));


            result.put(projectRepository.findById(projectId).getProjectName(), epicRatio);
        }

        return result;
    }

    public GetMyPage getMyPage(Long memberId) {
        Member member = memberRepository.findById(memberId);
        GetMyPage result = new GetMyPage();

        // 프로젝트마다 완료한 에픽/미완료한 에픽 표시
        Map<String, Map<String, Boolean>> projectAndEpic = new HashMap<>();
        for(Long projectId : member.getProjectIds()) {
            Project project = projectRepository.findById(projectId);

            // 프로젝트별 에픽 추출
            Map<String, Boolean> epics = new HashMap<>();
            for(Long epicid : project.getEpicsId()) {
                Epic epic = epicRepository.findById(epicid);
                epics.put(epic.getTitle(), epic.getIsCompleted());

                result.getSprintRetrospective().put(epic.getId(), epic.getSprintName());
            }

            projectAndEpic.put(project.getProjectName(), epics);
        }
        result.setProjectAndEpic(projectAndEpic);

        // 기타 회원 정보 삽입
        result.setUsername(member.getUsername());
        result.setPosition(member.getPosition());
        result.setContactInfo(member.getContactInfo());
        result.setCompanyOrSchool(member.getCompanyOrSchool());

        return result;
    }

    public Member editMember(EditMemberDTO editMemberDTO, Long memberId) {
        Member member = memberRepository.findById(memberId);

        // 회원 정보 수정
        member.setCompanyOrSchool(editMemberDTO.getCompanyOrSchool());
        member.setContactInfo(editMemberDTO.getContactInfo());
        member.setPosition(editMemberDTO.getPosition());

        return member;
    }

    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId);
    }

    public GetSprintRetrospectiveDTO getsprintRetrospective(Long memberId, Long epicId) {
        Member member = memberRepository.findById(memberId);

        // 멤버가 보유한 스프린트 회고 추출
        SprintRetrospective sprintRetrospective = member.getSprintRetrospectives().get(epicId);

        // 멤버가 보유한 스프린트 회고를 DTO에 담음
        GetSprintRetrospectiveDTO getSprintRetrospectiveDTO = new GetSprintRetrospectiveDTO();
        getSprintRetrospectiveDTO.setContinueAction(sprintRetrospective.getContinueAction());
        getSprintRetrospectiveDTO.setStop(sprintRetrospective.getStop());
        getSprintRetrospectiveDTO.setStart(sprintRetrospective.getStart());

        return getSprintRetrospectiveDTO;
    }
}
