package shop.yesaladin.auth.jwt;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * JWT 인증 로직을 수행할 때 로그인에 실패 하는 경우 발생 하는 handler 입니다.
 *
 * @author 송학현
 * @since 1.0
 */
@Slf4j
public class JwtFailureHandler implements AuthenticationFailureHandler {

    @Value("${yesaladin.front}")
    private String frontUrl;

    /**
     * JWT 인증 로직 으로 login 수행 시 인증에 실패 했을 경우 제어 하기 위한 기능 입니다.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param exception Spring Security에서 인증 실패 시 발생하는 예외 입니다.
     * @throws IOException
     * @throws ServletException
     *
     * @author 송학현
     * @since 1.0
     */
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        log.info("Failure Handler called");
        response.sendRedirect(frontUrl + "/members/login");
    }
}
