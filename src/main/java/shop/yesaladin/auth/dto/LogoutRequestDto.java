package shop.yesaladin.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 로그아웃 요청을 위한 DTO 입니다.
 *
 * @author 송학현
 * @since 1.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequestDto {

    private String key;
}
