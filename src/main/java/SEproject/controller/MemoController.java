package SEproject.controller;

import SEproject.domain.Memo;
import SEproject.dto.NewMemoDTO;
import SEproject.service.MemoService;
import SEproject.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class MemoController {
    private final MemoService memoService;

    @Autowired
    public MemoController(MemoService memoService) {
        this.memoService = memoService;
    }

    @PostMapping("SE/project/{projectId}/memo/newmemo")
    public ResponseEntity<?> createMemo(@RequestBody NewMemoDTO memoDTO, @PathVariable Long projectId, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        Memo memo = memoService.createMemo(memoDTO, projectId);
        return ResponseEntity.ok(new ApiResponse("success", memo));
    }

    @GetMapping("SE/project/{projectId}/memo")
    public ResponseEntity<?> getMemos(@PathVariable Long projectId, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        Map<String, List<Memo>> memos = memoService.getMemos(projectId);
        return ResponseEntity.ok(new ApiResponse("success", memos));
    }

    @GetMapping("SE/project/{projectId}/memo/{memoId}")
    public ResponseEntity<?> getMemo(@PathVariable Long memoId, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        Memo memo = memoService.getMemo(memoId);
        return ResponseEntity.ok(new ApiResponse("success", memo));
    }

    @PostMapping("SE/project/{projectId}/memo/{memoId}")
    public ResponseEntity<?> correctionMemo(@RequestBody NewMemoDTO memoDTO, @PathVariable Long memoId, HttpServletRequest request) {
        if (!isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("fail", "Unauthorized access"));
        }

        Memo memo = memoService.correctionMemo(memoDTO, memoId);
        return ResponseEntity.ok(new ApiResponse("success", memo));
    }

    // 세션 처리
    private boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(SessionConst.LOGIN_MEMBER) != null;
    }

    // 응답 DTO를 위한 클래스
    private static class ApiResponse {
        private String status;
        private Object data;

        public ApiResponse(String status, Object data) {
            this.status = status;
            this.data = data;
        }

        public String getStatus() {
            return status;
        }

        public Object getData() {
            return data;
        }
    }
}
