package shop.yesaladin.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.yesaladin.auth.entity.MemberGrade;

public interface MemberGradeRepository extends JpaRepository<MemberGrade, Integer> {

}
