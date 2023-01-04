package shop.yesaladin.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.yesaladin.auth.entity.Member;

/**
 * 회원 테이블에 JPA를 통해 접근 가능한 클래스입니다.
 *
 * @author : 송학현
 * @since : 1.0
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

}
