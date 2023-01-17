package shop.yesaladin.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import shop.yesaladin.auth.dto.LoginRequest;
import shop.yesaladin.auth.jwt.JwtTokenProvider;

/**
 * JWT 토큰 인증을 위해 UsernamePasswordAuthenticationFilter를 대체하여 custom한 filter 입니다.
 *
 * @author : 송학현
 * @since : 1.0
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Front Server에서 login을 시도하면 발생하는 기능 입니다.
     * 사용자가 입력한 loginId와 password를 기반으로 UsernamePasswordAuthenticationToken을 발급하고
     * authenticationManager에게 인가를 위임합니다.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return authenticationManager에게 인가를 위임하여 반환된 결과입니다.
     * @throws AuthenticationException Spring Security에서 발생하는 예외 입니다.
     *
     * @author : 송학현
     * @since : 1.0
     */
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response
    ) throws AuthenticationException {
        ObjectMapper mapper = new ObjectMapper();
        LoginRequest loginRequest = mapper.readValue(request.getInputStream(), LoginRequest.class);

        log.info("loginId={}", loginRequest.getLoginId());
        log.info("password={}", loginRequest.getPassword());

        // username과 password를 이용해 Authentication 타입의 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getLoginId(),
                loginRequest.getPassword()
        );

        log.info("authenticationToken.getName={}", authenticationToken.getName());
        log.info("authenticationToken.getAuthorities={}", authenticationToken.getAuthorities());
        log.info(
                "authenticationToken.getPrincipal={}",
                authenticationToken.getPrincipal().toString()
        );
        log.info("authenticationToken.getDetails={}", authenticationToken.getDetails());

        return authenticationManager.authenticate(authenticationToken);
    }

    /**
     * 인증 성공 시 동작하는 후처리 메소드 입니다.
     * JWT 토큰을 발급하고 Redis에 저장 및 HTTP Header Authorization 필드에 accessToken을 담아 반환합니다.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param chain FilterChain
     * @param auth 인증 객체 입니다.
     * @throws IOException
     * @throws ServletException
     *
     * @author : 송학현
     * @since : 1.0
     */
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication auth
    ) throws IOException, ServletException {
        log.info("auth={}", auth);
        log.info("Granted Authorities={}", auth.getAuthorities());
        String token = jwtTokenProvider.createAccessToken(
                auth.getName(),
                auth
        );
        // refresh token 발급 후 redis에 집어 넣기
        String refreshToken = jwtTokenProvider.createRefreshToken(
                auth.getName(),
                auth
        );

//        String memberUuid = UUID.randomUUID().toString();

        redisTemplate.opsForHash().put(auth.getName(), "REFRESH_TOKEN", refreshToken);
//        redisTemplate.opsForHash().put(memberUuid, auth.getName(), auth.getName());

        response.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + token);

        log.info("accessToken={}", token);
        log.info("refreshToken={}", refreshToken);
    }
}
