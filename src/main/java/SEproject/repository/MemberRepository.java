package SEproject.repository;

import SEproject.domain.Member;
import SEproject.dto.MemberJoinDTO;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    public Member save(MemberJoinDTO memberJoinDTO);
    public Member findById(Long id);
    public Optional<Member> findByEmail(String email);
    public List<Member> findAll();
}
