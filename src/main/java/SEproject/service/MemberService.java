package SEproject.service;

import SEproject.domain.Member;
import SEproject.dto.LoginMemberDTO;
import SEproject.dto.NewMemberDTO;
import SEproject.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member createMember(NewMemberDTO newMemberDTO) {
        return memberRepository.save(newMemberDTO);
    }

    public Member login(LoginMemberDTO loginMemberDTO) {
        return memberRepository.findByEmailId(loginMemberDTO.getEmailId())
                .filter(m -> m.getPassword().equals(loginMemberDTO.getPassword()))
                .orElse(null);
    }
}
