package SEproject.service;

import SEproject.domain.Memo;
import SEproject.dto.NewMemoDTO;
import SEproject.repository.MemoRepository;
import SEproject.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MemoService {
    private final MemoRepository memoRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public MemoService(MemoRepository memoRepository, ProjectRepository projectRepository) {
        this.memoRepository = memoRepository;
        this.projectRepository = projectRepository;
    }

    public Memo createMemo(NewMemoDTO memoDTO, Long projectId) {
        Memo result = memoRepository.save(memoDTO, projectId, LocalDate.now());

        return result;
    }

    public Map<String, List<Memo>> getMemos(Long projectId) {
        List<Memo> memoList = new ArrayList<>();
        List<Memo> allmemo = memoRepository.findAll();

        for(int i = 0; i < allmemo.size(); i++){
            if(projectId.equals(allmemo.get(i).getProjectId())) {
                memoList.add(allmemo.get(i));
            }
        }

        String projectName = projectRepository.findById(projectId).getProjectName();
        Map<String, List<Memo>> memoMap = new HashMap<>();
        memoMap.put(projectName, memoList);

        return memoMap;
    }

    public Memo getMemo(Long memoId) {
        return memoRepository.findById(memoId);
    }

    public Memo correctionMemo(NewMemoDTO memoDTO, Long memoId) {
        return memoRepository.edit(memoDTO, memoId, LocalDate.now());
    }
}