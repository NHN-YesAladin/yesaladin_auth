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

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

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
