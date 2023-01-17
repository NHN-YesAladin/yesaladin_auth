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

    @Value("${front}")
    private String frontUrl;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        log.info("Failure Handler called");
        // TODO: url 정보 수정
        response.sendRedirect(frontUrl + "/members/login");
    }
}
