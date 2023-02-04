package shop.yesaladin.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * JWT token 재발급 결과를 담은 DTO 입니다.
 *
 * @author 송학현
 * @since 1.0
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TokenReissueResponseDto {

    private String accessToken;
    private String refreshToken;
}
