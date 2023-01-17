package shop.yesaladin.auth.jwt;

import static java.util.stream.Collectors.toList;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final long ACCESS_TOKEN_EXPIRE_TIME = Duration.ofMinutes(30).toSeconds(); // 30 minutes
    private static final long REFRESH_TOKEN_EXPIRE_TIME = Duration.ofDays(7).toSeconds(); // 7 days

    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    private Key getSecretKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(String loginId, Authentication roles, long tokenExpireTime) {

        Claims claims = Jwts.claims().setSubject(loginId);
        claims.put("roles",
                roles.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(toList()));
        Date date = new Date();

        return Jwts.builder()
                .setClaims(claims) // 발행 유저 정보 저장
                .setIssuedAt(date) // 발행 시간 저장
                .setExpiration(new Date(date.getTime() + tokenExpireTime)) // 토큰 유효시간 설정
                .signWith(getSecretKey(secretKey), SignatureAlgorithm.HS512) // 해싱 알고리즘, 키 설정
                .compact();
    }

    public String createAccessToken(
            String loginId,
            Authentication auth
    ) {
        return createToken(loginId, auth, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String createRefreshToken(
            String loginId,
            Authentication auth
    ) {
        return createToken(loginId, auth, REFRESH_TOKEN_EXPIRE_TIME);
    }

    public String extractLoginId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isValidToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey(secretKey))
                    .build()
                    .parseClaimsJws(token);
            log.debug("token : {}", claimsJws);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(extractLoginId(token));
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );
    }
}
