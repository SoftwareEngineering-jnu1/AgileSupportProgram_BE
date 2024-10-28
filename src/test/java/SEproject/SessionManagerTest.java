package SEproject;

import SEproject.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SessionManagerTest {
    SessionManager sessionManager = new SessionManager();
    @Test
    void sessionTest() {
        /**
         *  세션 생성 - 현재 HttpServletRequest, HttpServletResponse 객체를 직접 사용할 수 없으므로 테스트에서 비슷한 역할을
         *  수행해주는 가짜 MockHttpServletRequest, MockHttpServletResponse를 사용함
         */
        MockHttpServletResponse response = new MockHttpServletResponse();
        Member member = new Member();
        sessionManager.createSession(member, response);

        // 요청에 응답 쿠키 저장 - 추후 세션 조회와 세션 만료를 테스트 하기 위해 아래 코드 작성
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(response.getCookies());

        // 세션 조회
        Object result = sessionManager.getSession(request);
        assertThat(result).isEqualTo(member);

        // 세션 만료
        sessionManager.expire(request);
        Object expired = sessionManager.getSession(request);
        assertThat(expired).isNull();
    }
}