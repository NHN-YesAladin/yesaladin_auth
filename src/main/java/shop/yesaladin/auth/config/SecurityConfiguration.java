package shop.yesaladin.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import shop.yesaladin.auth.filter.JwtAuthenticationFilter;
import shop.yesaladin.auth.jwt.JwtFailureHandler;
import shop.yesaladin.auth.jwt.JwtTokenProvider;

/**
 * Spring Security의 설정 Bean 등록 클래스입니다.
 *
 * @author 송학현
 * @since 1.0
 */
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Security Filter Chain 설정을 위한 Bean을 등록합니다.
     *
     * @param http http의 filter 등록을 위한 객체 입니다.
     * @return Bean으로 등록한 SecurityFilterChain 입니다.
     * @throws Exception Spring Security의 메소드에서 발생하는 예외 입니다.
     *
     * @author 송학현
     * @since 1.0
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll();
        http.formLogin().disable();
        http.logout().disable();
        http.csrf().disable();
        http.addFilter(jwtAuthenticationFilter());

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.headers().frameOptions().sameOrigin();

        return http.build();
    }

    /**
     * PasswordEncoder를 빈으로 등록하기 위한 메소드 입니다.
     *
     * @return 회원가입 시 password를 encoding 하기 위해 등록한 Bean 입니다.
     *
     * @author 송학현
     * @since 1.0
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Jwt 인증을 위해 UsernamePasswordAuthenticaitonFilter를 custom한 Filter의 설정을 위한 기능입니다.
     *
     * @return UsernamePasswordAuthenticationFilter를 custom한 Filter를 반환합니다.
     * @throws Exception Spring Security에서 발생하는 예외입니다.
     *
     * @author 송학현
     * @since 1.0
     */
    private JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                authenticationManager(null),
                jwtTokenProvider,
                redisTemplate
        );

        jwtAuthenticationFilter.setFilterProcessesUrl("/auth/login");
        jwtAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler());

        return jwtAuthenticationFilter;
    }

    /**
     * 인증을 관리하는 AuthenticationManager 를 반환합니다.
     *
     * @param configuration 인증 구성
     * @return 인증 정보를 관리하는 AuthenticationManager를 반환합니다.
     * @throws Exception getAuthenticationManager()에서 발생하는 예외
     *
     * @author 송학현
     * @since 1.0
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * 인증 실패시 동작하는 핸들러를 Bean으로 등록합니다.
     *
     * @return 인증 실패에 대한 Handler 입니다.
     *
     * @author 송학현
     * @since 1.0
     */
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new JwtFailureHandler();
    }
}
