package SEproject.repository;

import SEproject.domain.Member;
import SEproject.dto.NewMemberDTO;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    public Member findById(Long id);
    public List<Member> findAll();
    public Member save(NewMemberDTO newMemberDTO);
    public Optional<Member> findByEmailId(String emailId);
    public Optional<Member> findByUsername(String username);
}