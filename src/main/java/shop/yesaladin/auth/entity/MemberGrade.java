package shop.yesaladin.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "member_grades")
@Entity
public class MemberGrade {

    @Id
    private Integer id;

    private String name;

    @Column(name = "base_order_amount")
    private Long baseOrderAmount;

    @Column(name = "base_given_point")
    private Long baseGivenPoint;
}
