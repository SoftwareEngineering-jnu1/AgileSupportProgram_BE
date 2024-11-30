package SEproject.repository.memoryrepository;

import SEproject.domain.Project;
import SEproject.dto.NewMemberDTO;
import SEproject.dto.NewProjectDTO;
import SEproject.repository.ProjectRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryProjectRepository implements ProjectRepository {
    public static ConcurrentHashMap<Long, Project> store = new ConcurrentHashMap<>();
    public static AtomicLong sequence = new AtomicLong();
    public final MemoryMemberRepository memoryMemberRepository;

    @Autowired
    public MemoryProjectRepository(MemoryMemberRepository memoryMemberRepository) {
        this.memoryMemberRepository = memoryMemberRepository;
    }

    @Override
    public Project findById(Long id) {
        return store.get(id);
    }

    @Override
    public List<Project> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Project save(NewProjectDTO newProjectDTO) {
        Project project = new Project();
        project.setId(sequence.incrementAndGet());
        project.setProjectName(newProjectDTO.getProjectName());
        List<Long> memberIds = new ArrayList<>();
        List<String> membersEmailId = newProjectDTO.getMembersEmailId();
        for(int i = 0; i < membersEmailId.size(); i++) {
            memberIds.add(memoryMemberRepository.findByEmailId(membersEmailId.get(i)).get().getId());
            Long memberid = memoryMemberRepository.findByEmailId(membersEmailId.get(i)).get().getId();
            memoryMemberRepository.findById(memberid).getProjectIds().add(project.getId());
        }
        project.setMembersId(memberIds);
        store.put(project.getId(), project);

        return project;
    }

    @Override
    public void addEpic(Long projectId, Long epicId) {
        store.get(projectId).getEpicsId().add(epicId);
    }

    // 테스트용 데이터
    @PostConstruct
    public void init() {
        NewProjectDTO newProjectDTO1 = new NewProjectDTO();
        newProjectDTO1.setProjectName("테스트 프로젝트 이름 1");
        List<String> membersEmailId1 = new ArrayList<>();
        membersEmailId1.add("emailId1@gmail.com");
        membersEmailId1.add("emailId2@gmail.com");
        membersEmailId1.add("emailId3@gmail.com");
        membersEmailId1.add("emailId4@gmail.com");
        newProjectDTO1.setMembersEmailId(membersEmailId1);

        NewProjectDTO newProjectDTO2 = new NewProjectDTO();
        newProjectDTO2.setProjectName("테스트 프로젝트 이름 2");
        List<String> membersEmailId2 = new ArrayList<>();
        membersEmailId2.add("emailId1@gmail.com");
        membersEmailId2.add("emailId2@gmail.com");
        membersEmailId2.add("emailId3@gmail.com");
        newProjectDTO2.setMembersEmailId(membersEmailId2);

        NewProjectDTO newProjectDTO3 = new NewProjectDTO();
        newProjectDTO3.setProjectName("테스트 프로젝트 이름 3");
        List<String> membersEmailId3 = new ArrayList<>();
        membersEmailId3.add("emailId1@gmail.com");
        newProjectDTO3.setMembersEmailId(membersEmailId3);

        save(newProjectDTO1);
        save(newProjectDTO2);
        save(newProjectDTO3);
    }

    public void clear() {
        store.clear();
    }
}
