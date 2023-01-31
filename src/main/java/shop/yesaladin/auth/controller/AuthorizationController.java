package shop.yesaladin.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.yesaladin.auth.exception.InvalidTokenException;
import shop.yesaladin.auth.service.inter.AuthorizationService;
import shop.yesaladin.common.dto.ResponseDto;
import shop.yesaladin.security.dto.AuthorizationMetaResponseDto;

/**
 * 인가 처리를 담당하는 컨트롤러 클래스입니다.
 *
 * @author 김홍대
 * @since 1.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/authorizations")
public class AuthorizationController {

    private static final String BEARER_PREFIX = "Bearer ";
    private final AuthorizationService authorizationService;

    @GetMapping(headers = "Authorization")
    public ResponseDto<AuthorizationMetaResponseDto> authorization(@RequestHeader(name = "Authorization") String authorization) {
        AuthorizationMetaResponseDto authorizationMeta = authorizationService.authorization(
                removeTokenPrefix(authorization));

        return ResponseDto.<AuthorizationMetaResponseDto>builder()
                .success(true)
                .status(HttpStatus.OK)
                .data(authorizationMeta)
                .build();
    }

    private String removeTokenPrefix(String tokenWithPrefix) {
        if (!tokenWithPrefix.startsWith(BEARER_PREFIX)) {
            throw new InvalidTokenException(tokenWithPrefix);
        }
        return tokenWithPrefix.substring(BEARER_PREFIX.length());
    }
}
