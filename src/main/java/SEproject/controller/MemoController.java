package SEproject.controller;

import SEproject.domain.Memo;
import SEproject.dto.NewMemoDTO;
import SEproject.service.MemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MemoController {
    private final MemoService memoService;

    @Autowired
    public MemoController(MemoService memoService) {
        this.memoService = memoService;
    }

    @PostMapping("SE/project/{projectId}/memo/newmemo")
    public Memo createMemo(@RequestBody NewMemoDTO memoDTO, @PathVariable Long projectId) {
        return memoService.createMemo(memoDTO, projectId);
    }

    @GetMapping("SE/project/{projectId}/memo")
    public List<Memo> getMemos(@PathVariable Long projectId) {
        return memoService.getMemos(projectId);
    }

    @GetMapping("SE/project/{projectId}/memo/{memoId}")
    public Memo getMemo(@PathVariable Long memoId) {
        return memoService.getMemo(memoId);
    }

    @PostMapping("SE/project/{projectId}/memo/{memoId}")
    public Memo correctionMemo(@RequestBody NewMemoDTO memoDTO, @PathVariable Long memoId) {
        return memoService.correctionMemo(memoDTO, memoId);
    }
}
