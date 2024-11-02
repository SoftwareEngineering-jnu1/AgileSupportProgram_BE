package SEproject.repository;

import SEproject.domain.Epic;
import SEproject.domain.Member;
import SEproject.domain.Memo;
import SEproject.domain.Project;
import SEproject.dto.NewProjectDTO;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    public Project save(NewProjectDTO newProjectDTO);
    public Project findById(Long id);
    public Optional<Project> findByName(String name);
    public List<Project> findAll();

    public List<Long> findMemberIds(Long projectId);

    public List<Long> findEpicIds(Long projectId);
    public void addEpic(Long projectId, Long epicId);

    public List<Long> findMemoIds(Long projectId);
    public void addMemo(Long projectId, Long memoId);
}
