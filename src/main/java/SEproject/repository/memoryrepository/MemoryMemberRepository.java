package SEproject.repository.memoryrepository;

import SEproject.domain.Member;
import SEproject.dto.NewMemberDTO;
import SEproject.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryMemberRepository implements MemberRepository {
    public static ConcurrentHashMap<Long, Member> store = new ConcurrentHashMap<>();
    public static AtomicLong sequence = new AtomicLong();

    @Override
    public Member findById(Long id) {
        return store.get(id);
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Member save(NewMemberDTO newMemberDTO) {
        Member member = new Member();
        member.setId(sequence.incrementAndGet());
        member.setPassword(newMemberDTO.getPassword());
        member.setUsername(newMemberDTO.getUsername());
        member.setEmailId(newMemberDTO.getEmailId());
        store.put(member.getId(), member);

        return member;
    }

    @Override
    public Optional<Member> findByEmailId(String emailId) {
        // 아래 코드는 Optional<Member>를 리턴하므로 메서드의 리턴 타입을 Optional<Member>로 지정함
        return findAll().stream()
                .filter(m -> m.getEmailId().equals(emailId))
                .findFirst();
    }

    @Override
    public Optional<Member> findByUsername(String username) {
        return findAll().stream()
                .filter(m -> m.getUsername().equals(username))
                .findFirst();
    }

    // 테스트용 데이터
    @PostConstruct
    public void init() {
        Member member = new Member();
        member.setId(sequence.incrementAndGet());
        member.setPassword("rhkraudrlf1!");
        member.setUsername("곽명길");
        member.setEmailId("gmg010217@gmail.com");
        store.put(member.getId(), member);
    }
}
