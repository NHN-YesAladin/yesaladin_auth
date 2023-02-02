package shop.yesaladin.auth.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static shop.yesaladin.auth.util.AuthUtil.ACCESS_TOKEN;
import static shop.yesaladin.auth.util.AuthUtil.PRINCIPALS;
import static shop.yesaladin.auth.util.AuthUtil.REFRESH_TOKEN;
import static shop.yesaladin.auth.util.AuthUtil.USER_ID;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.yesaladin.auth.dto.LogoutRequestDto;
import shop.yesaladin.auth.dto.TokenReissueResponseDto;
import shop.yesaladin.auth.jwt.JwtTokenProvider;
import shop.yesaladin.common.dto.ResponseDto;

/**
 * 토큰 재발급, 로그아웃을 위한 컨트롤러 클래스 입니다.
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

    private static final String UUID_HEADER = "UUID_HEADER";

    /**
     * JWT 토큰 재발급을 위한 기능입니다.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return 응답 결과를 담은 DTO 입니다.
     * @throws IOException IO 예외
     * @author 송학현
     * @since 1.0
     */
    @PostMapping("/reissue")
    public ResponseDto<Void> reissue(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String accessToken = request.getHeader(AUTHORIZATION);
        String memberUuid = request.getHeader("UUID");

        if (Objects.isNull(accessToken) || Objects.isNull(memberUuid) || !accessToken.startsWith(
                "Bearer ") || tokenProvider.isValidToken(accessToken.substring(7))) {
            throw new IllegalArgumentException("Header 정보가 없거나 유효하지 않은 토큰입니다.");
        }

        if (!redisTemplate.opsForHash().keys(memberUuid).isEmpty()) {
            String loginId = redisTemplate.opsForHash()
                    .get(memberUuid, USER_ID.getValue())
                    .toString();
            String principals = redisTemplate.opsForHash()
                    .get(memberUuid, PRINCIPALS.getValue())
                    .toString();
            log.info("loginId={}", loginId);
            log.info("roles={}", principals);

            List<String> roles = splitRoles(principals);

            TokenReissueResponseDto tokenReissueResponseDto = tokenProvider.tokenReissue(loginId, roles);

            redisTemplate.opsForHash().delete(memberUuid, ACCESS_TOKEN.getValue());
            redisTemplate.opsForHash().delete(memberUuid, REFRESH_TOKEN.getValue());

            redisTemplate.opsForHash()
                    .put(memberUuid, REFRESH_TOKEN.getValue(), tokenReissueResponseDto.getRefreshToken());
            redisTemplate.opsForHash()
                    .put(memberUuid, ACCESS_TOKEN.getValue(), tokenReissueResponseDto.getAccessToken());

            response.addHeader(AUTHORIZATION, tokenReissueResponseDto.getAccessToken());
            response.addHeader(UUID_HEADER, memberUuid);
            return ResponseDto.<Void>builder()
                    .success(true)
                    .status(HttpStatus.OK)
                    .build();
        }

        log.error("uuid 없음");
        return ResponseDto.<Void>builder()
                .success(false)
                .status(HttpStatus.BAD_REQUEST)
                .errorMessages(List.of("이미 로그아웃 된 사용자 입니다."))
                .build();
    }

    private List<String> splitRoles(String principals) {
        return Arrays.asList(principals.replaceAll("[\\[\\]]", "").split(", "));
    }

    /**
     * Front 서버의 logout 요청을 받아 Redis에 저장된 해당 login 된 user의 정보를 삭제하기 위한 기능입니다.
     *
     * @param request Front 서버에서 Logout 요청에 대한 정보를 담은 DTO
     * @return 응답 결과를 담은 DTO 입니다.
     * @author : 송학현
     * @since : 1.0
     */
    @PostMapping("/logout")
    public ResponseDto<Void> logout(
            @RequestBody LogoutRequestDto request,
            @RequestHeader(name = "Authorization") String accessToken
    ) {
        String uuid = request.getKey();
        log.info("uuid={}", uuid);

        if (Objects.isNull(accessToken) || Objects.isNull(uuid) || !accessToken.startsWith(
                "Bearer ") || tokenProvider.isValidToken(accessToken.substring(7))) {
            throw new IllegalArgumentException("Header 정보가 없거나 유효하지 않은 토큰입니다.");
        }

        if (!redisTemplate.opsForHash().keys(uuid).isEmpty()) {
            redisTemplate.opsForHash().delete(uuid, REFRESH_TOKEN.getValue());
            redisTemplate.opsForHash().delete(uuid, ACCESS_TOKEN.getValue());
            redisTemplate.opsForHash().delete(uuid, PRINCIPALS.getValue());
            redisTemplate.opsForHash().delete(uuid, USER_ID.getValue());

            return ResponseDto.<Void>builder()
                    .success(true)
                    .status(HttpStatus.OK)
                    .build();
        }

        return ResponseDto.<Void>builder()
                .success(false)
                .status(HttpStatus.BAD_REQUEST)
                .errorMessages(List.of("이미 로그아웃 된 사용자 입니다."))
                .build();
    }
}
