package shop.yesaladin.auth.service;

import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import shop.yesaladin.auth.dto.MemberResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    @Value("${yesaladin.shop}")
    private String shopUrl;

    private final RestTemplate restTemplate;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        ResponseEntity<MemberResponse> response = restTemplate.getForEntity(
                shopUrl + "/v1/members/login/{loginId}",
                MemberResponse.class,
                loginId
        );

        MemberResponse member = response.getBody();

        log.info("UserDetailsServiceImpl, member={}", response.getBody());

        if (Objects.isNull(member)) {
            throw new UsernameNotFoundException(loginId);
        }

        User user = new User(
                member.getLoginId(),
                member.getPassword(),
                member.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
        );

        log.info("user={}", user);
        return user;
    }
}
