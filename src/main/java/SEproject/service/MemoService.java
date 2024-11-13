package SEproject.service;

import SEproject.domain.Memo;
import SEproject.dto.NewMemoDTO;
import SEproject.repository.MemoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MemoService {
    private final MemoRepository memoRepository;

    @Autowired
    public MemoService(MemoRepository memoRepository) {
        this.memoRepository = memoRepository;
    }

    public Memo createMemo(NewMemoDTO memoDTO, Long projectId) {
        Memo result = memoRepository.save(memoDTO, projectId, LocalDate.now());

        return result;
    }

    public List<Memo> getMemos(Long projectId) {
        List<Memo> memoList = new ArrayList<>();
        List<Memo> allmemo = memoRepository.findAll();

        for(int i = 0; i < allmemo.size(); i++){
            if(projectId.equals(allmemo.get(i).getProjectId())) {
                memoList.add(allmemo.get(i));
            }
        }

        return memoList;
    }

    public Memo getMemo(Long memoId) {
        return memoRepository.findById(memoId);
    }

    public Memo correctionMemo(NewMemoDTO memoDTO, Long memoId) {
        return memoRepository.edit(memoDTO, memoId, LocalDate.now());
    }
}