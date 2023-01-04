package shop.yesaladin.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.yesaladin.auth.entity.MemberGenderCode;

/**
 * 회원 성별 코드 테이블에 JPA로 접근 가능한 테이블 입니다.
 *
 * @author : 송학현
 * @since : 1.0
 */
public interface MemberGenderCodeRepository extends JpaRepository<MemberGenderCode, Integer> {

}
