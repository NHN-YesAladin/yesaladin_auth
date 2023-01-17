package shop.yesaladin.auth.jwt;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Slf4j
public class JwtFailureHandler implements AuthenticationFailureHandler {

    @Value("${gatewayUrl}")
    private String gatewayUrl;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        log.info("Failure Handler called");
        // TODO: /members/login으로 변경 예정
        response.sendRedirect(gatewayUrl + "/web/members/login");
    }
}
