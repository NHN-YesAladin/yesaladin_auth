package shop.yesaladin.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import shop.yesaladin.auth.entity.MemberGenderCode;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class MemberGenderCodeRepositoryTest {

    @Autowired
    MemberGenderCodeRepository repository;

    @Test
    void entityMappingTest() throws Exception {
        //given
        int id = 1;
        String male = "MALE";

        MemberGenderCode gender = MemberGenderCode.builder()
                .id(id)
                .gender(male)
                .build();

        //when
        MemberGenderCode savedGender = repository.save(gender);

        //then
        assertThat(savedGender.getId()).isEqualTo(id);
        assertThat(savedGender.getGender()).isEqualTo(male);
        assertThat(repository.findById(id)).isPresent();
    }

}