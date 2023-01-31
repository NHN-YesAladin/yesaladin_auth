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

@RequiredArgsConstructor
@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private final JwtTokenProvider tokenProvider;

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
