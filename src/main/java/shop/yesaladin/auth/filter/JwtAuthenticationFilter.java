package shop.yesaladin.auth.filter;

import static shop.yesaladin.auth.util.AuthUtil.ACCESS_TOKEN;
import static shop.yesaladin.auth.util.AuthUtil.PRINCIPALS;
import static shop.yesaladin.auth.util.AuthUtil.REFRESH_TOKEN;
import static shop.yesaladin.auth.util.AuthUtil.USER_ID;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import shop.yesaladin.auth.dto.LoginRequestDto;
import shop.yesaladin.auth.exception.InvalidLoginRequestException;
import shop.yesaladin.auth.jwt.JwtTokenProvider;

/**
 * JWT 토큰 인증을 위해 UsernamePasswordAuthenticationFilter를 대체하여 custom한 filter 입니다.
 *
 * @author 송학현
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String UUID_HEADER = "UUID_HEADER";
    private static final String X_EXPIRE_HEADER = "X-Expire";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Front Server에서 login을 시도하면 발생하는 기능 입니다. 사용자가 입력한 loginId와 password를 기반으로
     * UsernamePasswordAuthenticationToken을 발급하고 authenticationManager에게 인가를 위임합니다.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return authenticationManager에게 인가를 위임하여 반환된 결과입니다.
     * @throws AuthenticationException Spring Security에서 발생하는 예외 입니다.
     * @author 송학현
     * @since 1.0
     */
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response
    ) throws AuthenticationException {
        ObjectMapper mapper = new ObjectMapper();
        LoginRequestDto loginRequestDto;
        try {
            loginRequestDto = mapper.readValue(request.getInputStream(), LoginRequestDto.class);
            log.info("Auth Server === Attempt Authentication");
            log.info("loginId={}", loginRequestDto.getLoginId());
        } catch (IOException e) {
            throw new InvalidLoginRequestException();
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequestDto.getLoginId(),
                loginRequestDto.getPassword()
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
     * 인증 성공 시 동작 하는 후처리 메소드 입니다.
     * JWT 토큰을 발급 하고 Redis에 저장 및 HTTP Header Authorization 필드에 accessToken을 담아 반환 합니다.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param chain    FilterChain
     * @param auth     인증 객체 입니다.
     * @throws IOException IO 관련 예외
     * @throws ServletException Servlet 에서 발생 하는 예외
     * @author 송학현
     * @since 1.0
     */
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication auth
    ) throws IOException, ServletException {
        log.info("Auth Server === Authentication Success");
        String accessToken = getAccessToken(auth);
        String refreshToken = getRefreshToken(auth);

        long expiredTime = jwtTokenProvider.extractExpiredTime(accessToken).getTime();
        log.info("expiredTime={}", expiredTime);
        String memberUuid = UUID.randomUUID().toString();

        redisTemplate.opsForHash().put(memberUuid, REFRESH_TOKEN.getValue(), refreshToken);
        redisTemplate.opsForHash().put(memberUuid, ACCESS_TOKEN.getValue(), accessToken);
        redisTemplate.opsForHash().put(memberUuid, USER_ID.getValue(), auth.getName());
        redisTemplate.opsForHash().put(memberUuid, PRINCIPALS.getValue(), auth.getAuthorities().toString());

        response.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken);
        response.addHeader(UUID_HEADER, memberUuid);
        response.addHeader(X_EXPIRE_HEADER, String.valueOf(expiredTime));
    }

    /**
     * 인증 객체를 JwtTokenProvider에게 전달하여 AccessToken을 발급합니다.
     *
     * @param auth 인증 객체 입니다.
     * @return JWT 형식의 AccessToken 입니다.
     * @author 송학현
     * @since 1.0
     */
    private String getAccessToken(Authentication auth) {
        return jwtTokenProvider.createAccessToken(
                auth.getName(),
                auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(
                        Collectors.toList())
        );
    }

    /**
     * 인증 객체를 JwtTokenProvider에게 전달하여 RefreshToken을 발급합니다.
     *
     * @param auth 인증 객체 입니다.
     * @return JWT 형식의 RefreshToken 입니다.
     * @author 송학현
     * @since 1.0
     */
    private String getRefreshToken(Authentication auth) {
        return jwtTokenProvider.createRefreshToken(
                auth.getName(),
                auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(
                        Collectors.toList())
        );
    }

    /**
     * 인증 실패 시 동작 하는 후처리 메소드 입니다.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param failed Spring Security의 인증 실패 예외
     * @throws IOException IO 관련 예외
     * @throws ServletException Servlet 관련 예외
     * @author 송학현
     * @since 1.0
     */
    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException failed
    ) throws IOException, ServletException {
        log.error("login failed={}", failed.toString());
        getFailureHandler().onAuthenticationFailure(request, response, failed);
    }
}
