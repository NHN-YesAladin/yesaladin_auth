package shop.yesaladin.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security의 설정 Bean 등록 클래스입니다.
 *
 * @author : 송학현
 * @since : 1.0
 */
@Configuration
public class SecurityConfiguration {

    /**
     * 초기 설정으로 disable을 두었습니다. (기능 추가에 따라 수정 예정)
     *
     * @param http http의 filter 등록을 위한 객체 입니다.
     * @return Bean으로 등록한 SecurityFilterChain 입니다.
     * @throws Exception
     * @author : 송학현
     * @since : 1.0
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll();
        http.formLogin().disable();
        http.logout().disable();
        http.csrf().disable();

        return http.build();
    }

    /**
     * PasswordEncoder를 빈으로 등록하기 위한 메소드 입니다.
     *
     * @return 회원가입 시 password를 encoding 하기 위해 등록한 Bean 입니다.
     * @author : 송학현
     * @since : 1.0
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
