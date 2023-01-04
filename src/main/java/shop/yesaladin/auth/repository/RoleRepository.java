package shop.yesaladin.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.yesaladin.auth.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

}
