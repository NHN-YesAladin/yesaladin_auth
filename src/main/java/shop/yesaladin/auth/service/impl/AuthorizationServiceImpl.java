package shop.yesaladin.auth.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import shop.yesaladin.auth.jwt.JwtTokenProvider;
import shop.yesaladin.auth.service.inter.AuthorizationService;
import shop.yesaladin.security.dto.AuthorizationMetaResponseDto;

/**
 * 인가 처리 관련 기능을 가지는 서비스 클래스의 구현체 입니다.
 *
 * @author 김홍대
 * @since 1.0
 */
@RequiredArgsConstructor
@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private final JwtTokenProvider tokenProvider;

    /**
     * {@inheritDoc}
     */
    public AuthorizationMetaResponseDto authorization(String token) {
        tokenProvider.isValidToken(token);
        Authentication authentication = tokenProvider.getAuthentication(token);

        List<String> authority = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new AuthorizationMetaResponseDto(authentication.getName(), authority);
    }
}
