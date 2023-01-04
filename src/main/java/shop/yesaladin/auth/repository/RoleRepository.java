package shop.yesaladin.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.yesaladin.auth.entity.Role;

/**
 * 회원의 권한 테이블에 JPA로 접근 가능한 클래스입니다.
 *
 * @author : 송학현
 * @since : 1.0
 */
public interface RoleRepository extends JpaRepository<Role, Integer> {

}
