package SEproject.repository;

import SEproject.domain.Member;
import SEproject.dto.MemberJoinDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class MemoryMemberRepositoryTest {
    MemoryMemberRepository memoryMemberRepository = new MemoryMemberRepository();

    @Test
    void save() {
        // given
        MemberJoinDTO joinDTO = new MemberJoinDTO();
        joinDTO.setEmailId("gmg010217@gmail.com");
        joinDTO.setPassword("abcdefg");
        joinDTO.setUsername("곽명길");

        // when
        Member savedMember = memoryMemberRepository.save(joinDTO);

        // then
        Assertions.assertThat(savedMember.getEmailId()).isEqualTo(joinDTO.getEmailId());
        Assertions.assertThat(savedMember.getPassword()).isEqualTo(joinDTO.getPassword());
        Assertions.assertThat(savedMember.getUsername()).isEqualTo(joinDTO.getUsername());
    }

    @Test
    void findById() {
    }

    @Test
    void findByEmail() {
    }

    @Test
    void findAll() {
    }
}