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
        NewMemberDTO newMemberDTO1 = new NewMemberDTO();
        newMemberDTO1.setEmailId("emailId1@gmail.com");
        newMemberDTO1.setUsername("곽명길");
        newMemberDTO1.setPassword("123456");

        NewMemberDTO newMemberDTO2 = new NewMemberDTO();
        newMemberDTO2.setEmailId("emailId2@gmail.com");
        newMemberDTO2.setUsername("구동민");
        newMemberDTO2.setPassword("123456");

        NewMemberDTO newMemberDTO3 = new NewMemberDTO();
        newMemberDTO3.setEmailId("emailId3@gmail.com");
        newMemberDTO3.setUsername("강호정");
        newMemberDTO3.setPassword("123456");

        NewMemberDTO newMemberDTO4 = new NewMemberDTO();
        newMemberDTO4.setEmailId("emailId4@gmail.com");
        newMemberDTO4.setUsername("기은빈");
        newMemberDTO4.setPassword("123456");

        save(newMemberDTO1);
        save(newMemberDTO2);
        save(newMemberDTO3);
        save(newMemberDTO4);
    }

    public void clear() {
        store.clear();
    }
}
