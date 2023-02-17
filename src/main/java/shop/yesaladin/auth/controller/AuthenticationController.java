package shop.yesaladin.auth.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static shop.yesaladin.auth.util.AuthUtil.REFRESH_TOKEN;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
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
import shop.yesaladin.auth.exception.InvalidAuthorizationHeaderException;
import shop.yesaladin.auth.jwt.JwtTokenProvider;
import shop.yesaladin.auth.service.inter.AuthenticationService;
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
    private final AuthenticationService authenticationService;

    private static final String UUID_HEADER = "UUID_HEADER";
    private static final String X_EXPIRE_HEADER = "X-Expire";

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

        log.info("Auth Server === Token Reissue Called");

        if (isValidHeader(accessToken, memberUuid)) {
            throw new InvalidAuthorizationHeaderException();
        }

        if (!isValidKey(memberUuid)) {
            log.error("uuid 없음");
            return ResponseDto.<Void>builder()
                    .success(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .errorMessages(List.of("이미 로그아웃 된 사용자 입니다."))
                    .build();
        }

        if (!isValidRefreshToken(memberUuid)) {
            return ResponseDto.<Void>builder()
                    .success(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .errorMessages(List.of("RefreshToken이 만료 되었습니다."))
                    .build();
        }

        String loginId = authenticationService.getLoginId(memberUuid);
        String principals = authenticationService.getPrincipals(memberUuid);
        log.info("loginId={}", loginId);
        log.info("roles={}", principals);

        List<String> roles = extractUserRoles(principals);

        String reissuedToken = tokenProvider.tokenReissue(loginId, roles);

        authenticationService.doReissue(memberUuid, reissuedToken);

        long expiredTime = tokenProvider.extractExpiredTime(reissuedToken).getTime();

        response.addHeader(AUTHORIZATION, reissuedToken);
        response.addHeader(UUID_HEADER, memberUuid);
        response.addHeader(X_EXPIRE_HEADER, String.valueOf(expiredTime));
        return ResponseDto.<Void>builder()
                .success(true)
                .status(HttpStatus.OK)
                .build();
    }

    private boolean isValidRefreshToken(String memberUuid) {
        String refreshToken = Objects.requireNonNull(redisTemplate.opsForHash()
                        .get(memberUuid, REFRESH_TOKEN.getValue()))
                        .toString();

        long expiredTime = tokenProvider.extractExpiredTime(refreshToken).getTime();

        long now = new Date().getTime();
        return (expiredTime - (now / 1000)) > 0;
    }

    /**
     * 로그아웃, 토큰 재발급 처리에 앞서 식별 키가 Redis에 유효한 지 판단 하기 위한 기능 입니다.
     *
     * @param memberUuid 로그인 된 회원이 갖고 있는 유일한 식별 키 입니다.
     * @return Redis에 접근 하기 전 key 값이 유효한지에 대한 결과 입니다.
     * @author 송학현
     * @since 1.0
     */
    private boolean isValidKey(String memberUuid) {
        return !redisTemplate.opsForHash().keys(memberUuid).isEmpty();
    }

    /**
     * Redis에 String 형태로 저장되어 있는 회원의 권한 정보를 정제하여 List로 추출하기 위한 기능 입니다.
     *
     * @param principals Redis에 저장되어 있던 회원의 권한 정보입니다.
     * @return 권한 정보를 List로 추출한 결과 입니다.
     * @author 송학현
     * @since 1.0
     */
    private List<String> extractUserRoles(String principals) {
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
        log.info("Auth Server === Logout called");

        if (isValidHeader(accessToken, uuid)) {
            throw new InvalidAuthorizationHeaderException();
        }

        if (isValidKey(uuid)) {
            authenticationService.doLogout(uuid);

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

    /**
     * 토큰 재발급, 로그아웃 요청에 대해 유효한 Request Header 정보를 갖고 있는지 판단하는 기능입니다.
     *
     * @param accessToken Authorization Header에 들어있는 JWT 토큰 입니다.
     * @param memberUuid  로그인 된 회원이 갖고 있는 유일한 식별 키 입니다.
     * @return Request Header가 유효한지에 대한 결과 입니다.
     * @author 송학현
     * @since 1.0
     */
    private boolean isValidHeader(String accessToken, String memberUuid) {
        return Objects.isNull(accessToken) || Objects.isNull(memberUuid) || !accessToken.startsWith(
                "Bearer ") || !tokenProvider.isValidToken(accessToken.substring(7));
    }
}
