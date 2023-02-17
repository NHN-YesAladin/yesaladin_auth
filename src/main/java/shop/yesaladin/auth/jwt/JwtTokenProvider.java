package shop.yesaladin.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * JWT Token을 생성하기 위한 Provider 입니다.
 *
 * @author 송학현
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final long ACCESS_TOKEN_EXPIRE_TIME = Duration.ofHours(1).toMillis();
    private static final long REFRESH_TOKEN_EXPIRE_TIME = Duration.ofDays(7).toMillis();

    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * JWT를 생성하기 위해 HMAC-SHA 알고리즘으로 JWT에 서명할 키를 생성합니다.
     *
     * @param secretKey JWT를 생성하기 위해 사용하는 secretKey 입니다.
     * @return 인코딩 된 secretKey를 기반으로 HMAC-SHA 알고리즘으로 생성한 Key를 반환합니다.
     * @author 송학현
     * @since 1.0
     */
    private Key getSecretKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * JWT 토큰을 발급 하는 기능 입니다.
     *
     * @param loginId         회원의 loginId 입니다.
     * @param roles           회원의 권한 목록입니다.
     * @param tokenExpireTime 토큰의 유효 시간입니다.
     * @return 발급한 JWT 토큰을 반환합니다.
     * @author 송학현
     * @since 1.0
     */
    public String createToken(String loginId, List<String> roles, long tokenExpireTime) {
        Claims claims = Jwts.claims().setSubject(loginId);
        claims.put("roles", roles);
        Date date = new Date();

        return Jwts.builder()
                .setClaims(claims) // 발행 유저 정보 저장
                .setIssuedAt(date) // 발행 시간 저장
                .setExpiration(new Date(date.getTime() + tokenExpireTime)) // 토큰 유효시간 설정
                .signWith(getSecretKey(secretKey), SignatureAlgorithm.HS512) // 해싱 알고리즘, 키 설정
                .compact();
    }

    /**
     * accessToken을 발급하는 기능입니다.
     *
     * @param loginId 회원의 loginId 입니다.
     * @param roles   회원의 권한 목록입니다.
     * @return JWT 토큰으로 발급한 accessToken을 반환합니다.
     * @author 송학현
     * @since 1.0
     */
    public String createAccessToken(
            String loginId,
            List<String> roles
    ) {
        return createToken(loginId, roles, ACCESS_TOKEN_EXPIRE_TIME);
    }

    /**
     * refreshToken을 발급하는 기능입니다.
     *
     * @param loginId 회원의 loginId 입니다.
     * @param roles   회원의 권한 목록입니다.
     * @return JWT 토큰으로 발급한 refreshToken 반환합니다.
     * @author 송학현
     * @since 1.0
     */
    public String createRefreshToken(
            String loginId,
            List<String> roles
    ) {
        return createToken(loginId, roles, REFRESH_TOKEN_EXPIRE_TIME);
    }

    /**
     * JWT 토큰을 파싱하여 payload에 들어있는 회원의 loginId를 반환하는 기능입니다.
     *
     * @param token JWT 토큰입니다.
     * @return payload에 들어있는 회원의 loginId
     * @author 송학현
     * @since 1.0
     */
    public String extractLoginId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * secretKey를 기반 으로 JWT 토큰이 유효한 지 검증 하는 기능 입니다.
     *
     * @param token JWT 토큰입니다.
     * @return 토큰이 유효한 토큰 인지 판별한 결과
     * @author 송학현
     * @since 1.0
     */
    public boolean isValidToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey(secretKey))
                    .build()
                    .parseClaimsJws(token);
            log.info("token : {}", claimsJws);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * JWT 토큰의 만료 시간을 추출하기 위한 기능 입니다.
     *
     * @param token JWT 토큰입니다.
     * @return 토큰의 만료 시간을 LocalDateTime으로 변환한 결과
     * @author 송학현
     * @since 1.0
     */
    public Date extractExpiredTime(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    /**
     * JWT 토큰을 재발급 하는 기능입니다.
     *
     * @param loginId Token을 재발급 하기 위해 필요한 회원의 loginId 입니다.
     * @param roles   Token을 재발급 하기 위해 필요한 회원의 권한 리스트 입니다.
     * @return 재발급한 accessToken
     * @author 송학현
     * @since 1.0
     */
    public String tokenReissue(String loginId, List<String> roles) {
        log.info("loginId={}", loginId);
        log.info("roles={}", roles);

        String accessToken = createAccessToken(loginId, roles);

        log.info("Auth Server === Token Reissued, accessToken={}", accessToken);

        return accessToken;
    }

    /**
     * JWT 토큰의 payload에 저장된 loginId를 바탕 으로 유저 정보가 담긴 인증 객체를 반환 하기 위한 기능 입니다.
     *
     * @param token JWT 토큰입니다.
     * @return token을 기반으로 조회한 사용자의 인증 객체를 반환합니다.
     * @author 송학현
     * @since 1.0
     */
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(extractLoginId(token));
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );
    }
}
