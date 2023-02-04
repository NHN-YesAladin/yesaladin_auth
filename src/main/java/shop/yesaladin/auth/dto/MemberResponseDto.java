package shop.yesaladin.auth.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Shop 서버에 회원 정보를 요청 시 결과를 받아오기 위한 DTO 입니다.
 *
 * @author 송학현
 * @since 1.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {

    private Long id;
    private String name;
    private String nickname;
    private String loginId;
    private String email;
    private String password;
    private List<String> roles;
}
