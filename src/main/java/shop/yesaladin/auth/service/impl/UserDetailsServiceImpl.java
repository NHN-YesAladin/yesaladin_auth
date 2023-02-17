package shop.yesaladin.auth.service.impl;

import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import shop.yesaladin.auth.dto.MemberResponseDto;
import shop.yesaladin.common.dto.ResponseDto;

/**
 * 인증 관련 비즈니스 로직을 수행하기 위해 UserDetailsService를 구현한 클래스 입니다.
 *
 * @author 송학현
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    @Value("${yesaladin.shop}")
    private String shopUrl;

    private final RestTemplate restTemplate;

    /**
     * Spring Security 를 사용 하여 로그인 진행 시 loginId를 통해 사용자를 찾는 기능 입니다.
     *
     * @param loginId 로그인을 시도할 때 사용자가 입력한 loginId
     * @return 찾은 사용자를 기반으로 생성된 UserDetails
     * @throws UsernameNotFoundException 해당 loginId와 일치하는 사용자가 존재하지 않는 경우 발생하는 예외
     * @author 송학현
     * @since 1.0
     */
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        ResponseEntity<ResponseDto<MemberResponseDto>> response = restTemplate.exchange(
                shopUrl + "/v1/members/login/" + loginId,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<>() {
                }
        );
        log.info("response={}", response);

        MemberResponseDto member = response.getBody().getData();

        log.info("UserDetailsServiceImpl, member={}", member.getLoginId());

        if (Objects.isNull(member)) {
            throw new UsernameNotFoundException(loginId);
        }

        User user = new User(
                member.getLoginId(),
                member.getPassword(),
                member.getRoles()
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );

        log.info("user={}", user);
        return user;
    }
}
