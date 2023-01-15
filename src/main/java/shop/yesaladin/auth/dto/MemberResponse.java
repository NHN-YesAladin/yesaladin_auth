package shop.yesaladin.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    private Long id;
    private String name;
    private String nickname;
    private String loginId;
    private String email;
    private String password;
    // TODO: Shop 서버의 API 구현 추가와 함께 List 타입으로 바꿔야 함.
    private String role;
}
