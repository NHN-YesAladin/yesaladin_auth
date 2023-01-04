package shop.yesaladin.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.yesaladin.auth.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
