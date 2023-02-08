package shop.yesaladin.auth.service.inter;

/**
 * 로그아웃, 토큰 재발급 기능을 가지는 서비스 인터페이스 입니다.
 *
 * @author 송학현
 * @since 1.0
 */
public interface AuthenticationService {

    /**
     * 회원의 loginId를 Redis에서 가져오기 위한 기능 입니다.
     *
     * @param memberUuid 로그인 한 사용자가 가진 유일한 식별 값
     * @return Redis에 접근해 가져온 사용자의 loginId
     * @author 송학현
     * @since 1.0
     */
    String getLoginId(String memberUuid);

    /**
     * 회원의 권한 정보를 Redis에서 가져오기 위한 기능 입니다.
     *
     * @param memberUuid 로그인 한 사용자가 가진 유일한 식별 값
     * @return Redis에 접근해 가져온 사용자의 권한 정보
     * @author 송학현
     * @since 1.0
     */
    String getPrincipals(String memberUuid);

    /**
     * JWT 토큰 재발급 이후 처리를 위한 기능입니다. Redis에 접근해 해당 유저의 토큰 정보를 갱신 합니다.
     *
     * @param memberUuid 로그인 한 사용자가 가진 유일한 식별 값
     * @param reissuedToken 재발급 된 accessToken
     * @author 송학현
     * @since 1.0
     */
    void doReissue(String memberUuid, String reissuedToken);

    /**
     * 로그아웃 처리를 위한 기능입니다. Redis에 접근해 해당 유저의 정보를 삭제 합니다.
     *
     * @param memberUuid 로그인 한 사용자가 가진 유일한 식별 값
     * @author 송학현
     * @since 1.0
     */
    void doLogout(String memberUuid);
}
