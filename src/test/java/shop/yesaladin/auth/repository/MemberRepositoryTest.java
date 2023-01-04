package shop.yesaladin.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import shop.yesaladin.auth.entity.Member;
import shop.yesaladin.auth.entity.MemberGenderCode;
import shop.yesaladin.auth.entity.MemberGrade;
import shop.yesaladin.auth.entity.Role;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void test() throws Exception {
        //given
        MemberGrade grade = MemberGrade.builder()
                .id(1)
                .name("플래티넘")
                .baseGivenPoint(2000L)
                .baseOrderAmount(200000L)
                .build();

        MemberGenderCode male = MemberGenderCode.builder()
                .id(1)
                .gender("MALE")
                .build();

        Role role = Role.builder()
                .id(1)
                .name("ROLE_MEMBER")
                .build();

        Member member = Member.builder()
                .nickname("Ramos")
                .name("Ramos")
                .loginId("test")
                .password("password")
                .birthYear(1996)
                .birthMonth(1)
                .birthDay(19)
                .email("test@test.com")
                .signUpDate(LocalDateTime.now())
                .isBlocked(false)
                .point(0L)
                .memberGrade(grade)
                .memberGenderCode(male)
                .roles(List.of(role))
                .build();

        //when
        Member savedMember = memberRepository.save(member);

        //then
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getMemberGrade().getName()).isEqualTo("플래티넘");
        assertThat(savedMember.getRoles().get(0).getName()).isEqualTo("ROLE_MEMBER");
    }
}