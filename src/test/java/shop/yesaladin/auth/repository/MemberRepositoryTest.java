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

/**
 * Some description here
 *
 * @author : 송학현
 * @since : 1.0
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void entityMappingTest() throws Exception {
        //given
        int id = 1;
        String gradeName = "플래티넘";
        long baseGivenPoint = 2000L;
        long baseOrderAmount = 200000L;

        MemberGrade grade = MemberGrade.builder()
                .id(id)
                .name(gradeName)
                .baseGivenPoint(baseGivenPoint)
                .baseOrderAmount(baseOrderAmount)
                .build();

        String gender = "MALE";

        MemberGenderCode male = MemberGenderCode.builder()
                .id(id)
                .gender(gender)
                .build();

        String roleMember = "ROLE_MEMBER";

        Role role = Role.builder()
                .id(id)
                .name(roleMember)
                .build();

        String ramos = "Ramos";
        String password = "password";
        String email = "test@test.com";
        int birthDay = 19;
        int birthMonth = 1;
        int birthYear = 1996;
        long point = 0L;

        Member member = Member.builder()
                .nickname(ramos)
                .name(ramos)
                .loginId(ramos)
                .password(password)
                .birthYear(birthYear)
                .birthMonth(birthMonth)
                .birthDay(birthDay)
                .email(email)
                .signUpDate(LocalDateTime.now())
                .isBlocked(false)
                .point(point)
                .memberGrade(grade)
                .memberGenderCode(male)
                .roles(List.of(role))
                .build();

        //when
        Member savedMember = memberRepository.save(member);

        //then
        int index = 0;

        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getMemberGrade().getName()).isEqualTo(gradeName);
        assertThat(savedMember.getRoles().get(index).getName()).isEqualTo(roleMember);
    }
}