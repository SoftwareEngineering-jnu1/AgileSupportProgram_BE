package SEproject.repository.memoryrepository;

import SEproject.domain.Memo;
import SEproject.dto.NewMemoDTO;
import SEproject.repository.MemoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryMemoRepository implements MemoRepository {
    public static ConcurrentHashMap<Long, Memo> store = new ConcurrentHashMap<>();
    public static AtomicLong sequence = new AtomicLong();

    @Override
    public Memo findById(Long id) {
        return store.get(id);
    }

    @Override
    public List<Memo> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Memo save(NewMemoDTO memoDTO, Long projectId, LocalDate editDate) {
        Memo memo = new Memo();
        memo.setId(sequence.incrementAndGet());
        memo.setProjectId(projectId);
        memo.setContent(memoDTO.getContent());
        memo.setTitle(memoDTO.getTitle());
        memo.setEditDate(editDate);
        memo.setCreateDate(editDate);

        store.put(memo.getId(), memo);

        return memo;
    }

    @Override
    public Memo edit(NewMemoDTO memoDTO, Long memoId, LocalDate editDate) {
        Memo editMemo = store.get(memoId);
        editMemo.setContent(memoDTO.getContent());
        editMemo.setTitle(memoDTO.getTitle());
        editMemo.setEditDate(editDate);

        return editMemo;
    }
}
