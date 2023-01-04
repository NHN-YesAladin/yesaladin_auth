package shop.yesaladin.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.yesaladin.auth.entity.MemberGenderCode;

public interface MemberGenderCodeRepository extends JpaRepository<MemberGenderCode, Integer> {

}
