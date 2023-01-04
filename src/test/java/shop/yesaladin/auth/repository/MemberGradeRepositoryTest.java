package shop.yesaladin.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import shop.yesaladin.auth.entity.MemberGrade;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class MemberGradeRepositoryTest {

    @Autowired
    MemberGradeRepository repository;

    @Test
    void entityMappingTest() throws Exception {
        //given
        int id = 1;
        String name = "플래티넘";
        long baseGivenPoint = 2000L;
        long baseOrderAmount = 200000L;

        MemberGrade grade = MemberGrade.builder()
                .id(id)
                .name(name)
                .baseGivenPoint(baseGivenPoint)
                .baseOrderAmount(baseOrderAmount)
                .build();

        //when
        MemberGrade savedGrade = repository.save(grade);

        //then
        assertThat(savedGrade).isNotNull();
        assertThat(savedGrade.getName()).isEqualTo(name);
        assertThat(savedGrade.getBaseGivenPoint()).isEqualTo(baseGivenPoint);
        assertThat(savedGrade.getBaseOrderAmount()).isEqualTo(baseOrderAmount);
        assertThat(repository.findById(id)).isPresent();
    }
}