package shop.yesaladin.auth.service.inter;


import shop.yesaladin.security.dto.AuthorizationMetaResponseDto;

/**
 * 인가 처리 관련 기능을 가지는 서비스 클래스입니다.
 *
 * @author 김홍대
 * @since 1.0
 */
public interface AuthorizationService {

    /**
     * 토큰을 파싱하여 반환합니다.
     * @param token JWT 토큰
     * @return 파싱된 토큰 값
     */
    AuthorizationMetaResponseDto authorization(String token);
}
