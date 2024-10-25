package SEproject.service;

import SEproject.domain.Member;
import SEproject.dto.MemberJoinDTO;
import SEproject.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JoinAndLoginService {
    private final MemberRepository memberRepository;

    @Autowired
    public JoinAndLoginService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member createMember(MemberJoinDTO memberJoinDTO) {
        return memberRepository.save(memberJoinDTO);
    }

    public Member login(String email, String password) {
        Member result = memberRepository.findByEmail(email)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
        return result;
    }
}
