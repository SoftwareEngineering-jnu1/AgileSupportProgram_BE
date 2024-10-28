package SEproject.repository;

import SEproject.domain.Member;
import SEproject.dto.MemberJoinDTO;
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
    public Member save(MemberJoinDTO memberJoinDTO) {
        Member member = new Member();
        member.setId(sequence.incrementAndGet());
        member.setPassword(memberJoinDTO.getPassword());
        member.setUsername(memberJoinDTO.getUsername());
        member.setEmailId(memberJoinDTO.getEmailId());
        store.put(member.getId(), member);
        return member;
    }

    @Override
    public Member findById(Long id) {
       return store.get(id);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        // 아래 코드는 Optional<Member>를 리턴하므로 메서드의 리턴 타입을 Optional<Member>로 지정함
        return findAll().stream()
                .filter(m -> m.getEmailId().equals(email))
                .findFirst();
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public void clearStore() {
        store.clear();
    }

//로그인&로그아웃&회원가입 테스트용 데이터
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
