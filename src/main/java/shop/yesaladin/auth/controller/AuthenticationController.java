package shop.yesaladin.auth.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static shop.yesaladin.auth.util.AuthUtil.ACCESS_TOKEN;
import static shop.yesaladin.auth.util.AuthUtil.PRINCIPALS;
import static shop.yesaladin.auth.util.AuthUtil.REFRESH_TOKEN;
import static shop.yesaladin.auth.util.AuthUtil.USER_ID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.yesaladin.auth.dto.LogoutRequestDto;
import shop.yesaladin.auth.jwt.JwtTokenProvider;

/**
 * 인증 처리를 위한 컨트롤러 클래스 입니다.
 *
 * @author 송학현
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping
public class AuthenticationController {

    private final JwtTokenProvider tokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    // TODO: 토큰 재발급 구현
    @PostMapping("/reissue")
    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = request.getHeader(AUTHORIZATION).substring(7);
        String memberUuid = request.getHeader("UUID");

        String loginId = redisTemplate.opsForValue().get(memberUuid).toString();

        tokenProvider.tokenReissue(accessToken);

        response.addHeader(AUTHORIZATION, "ok");
    }

    /**
     * Front 서버의 logout 요청을 받아 Redis에 저장된 해당 login 된 user의 정보를 삭제하기 위한 기능입니다.
     *
     * @param request Front 서버에서 Logout 요청에 대한 정보를 담은 DTO
     * @author : 송학현
     * @since : 1.0
     */
    @PostMapping("/logout")
    public void logout(@RequestBody LogoutRequestDto request) {
        String uuid = request.getKey();
        log.info("uuid={}", uuid);

        redisTemplate.opsForHash().delete(uuid, REFRESH_TOKEN.getValue());
        redisTemplate.opsForHash().delete(uuid, ACCESS_TOKEN.getValue());
        redisTemplate.opsForHash().delete(uuid, PRINCIPALS.getValue());
        redisTemplate.opsForHash().delete(uuid, USER_ID.getValue());
    }
}
