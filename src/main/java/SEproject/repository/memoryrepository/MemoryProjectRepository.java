package SEproject.repository.memoryrepository;

import SEproject.domain.Project;
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
        Project newproject = new Project();
        newproject.setId(sequence.incrementAndGet());
        newproject.setProjectName("testProject");
        List<Long> membersId = new ArrayList<>();
        membersId.add(1L);
        newproject.setMembersId(membersId);

        store.put(newproject.getId(), newproject);
    }
}
