package SEproject.repository;

import SEproject.domain.Member;
import SEproject.dto.MemberJoinDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

class MemoryMemberRepositoryTest {
    MemoryMemberRepository memoryMemberRepository = new MemoryMemberRepository();

    @BeforeEach
    public void beforeEach() {
        MemberJoinDTO joinDTO1 = new MemberJoinDTO();
        joinDTO1.setEmailId("gmg010217@gmail.com");
        joinDTO1.setPassword("abcdefg");
        joinDTO1.setUsername("곽명길");

        MemberJoinDTO joinDTO2 = new MemberJoinDTO();
        joinDTO2.setEmailId("gmg01021722@gmail.com");
        joinDTO2.setPassword("abcdefg22");
        joinDTO2.setUsername("곽명길22");

        Member savedMember = memoryMemberRepository.save(joinDTO1);
        Member savedMember2 = memoryMemberRepository.save(joinDTO2);
    }

    @Test
    void findById() {
        // when
        Member savedMember = memoryMemberRepository.findById(1L);
        // then
        Assertions.assertThat(savedMember.getId()).isEqualTo(1L);
    }

    @Test
    void findByEmail() {
        // when
        Optional<Member> savedMember = memoryMemberRepository.findByEmail("gmg010217@gmail.com");
        // then
        Assertions.assertThat(savedMember.get().getEmailId()).isEqualTo("gmg010217@gmail.com");
    }

    @Test
    void findAll() {
        // when
        List<Member> allMember = memoryMemberRepository.findAll();
        // then
        Assertions.assertThat(allMember.size()).isEqualTo(2);
        Assertions.assertThat(allMember.get(0).getId()).isEqualTo(1L);
        Assertions.assertThat(allMember.get(1).getId()).isEqualTo(3L);
    }
}