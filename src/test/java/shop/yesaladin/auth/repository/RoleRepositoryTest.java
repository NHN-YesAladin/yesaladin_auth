package shop.yesaladin.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import shop.yesaladin.auth.entity.Role;

/**
 * Some description here
 *
 * @author : 송학현
 * @since : 1.0
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class RoleRepositoryTest {

    @Autowired
    RoleRepository repository;

    @Test
    void entityMappingTest() throws Exception {
        //given
        int id = 1;
        String roleName = "ROLE_MEMBER";

        Role role = Role.builder()
                .id(id)
                .name(roleName)
                .build();

        //when
        Role savedRole = repository.save(role);

        //then
        assertThat(savedRole.getId()).isEqualTo(id);
        assertThat(savedRole.getName()).isEqualTo(roleName);
        assertThat(repository.findById(id)).isPresent();
    }
}