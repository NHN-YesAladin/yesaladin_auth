package shop.yesaladin.auth.service.impl;

import static shop.yesaladin.auth.util.AuthUtil.ACCESS_TOKEN;
import static shop.yesaladin.auth.util.AuthUtil.PRINCIPALS;
import static shop.yesaladin.auth.util.AuthUtil.REFRESH_TOKEN;
import static shop.yesaladin.auth.util.AuthUtil.USER_ID;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import shop.yesaladin.auth.service.inter.AuthenticationService;

/**
 * 로그아웃, 토큰 재발급 처리 관련 기능을 가지는 서비스 클래스의 구현체 입니다.
 *
 * @author 송학현
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLoginId(String memberUuid) {
        return Objects.requireNonNull(redisTemplate.opsForHash()
                        .get(memberUuid, USER_ID.getValue()))
                .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPrincipals(String memberUuid) {
        return Objects.requireNonNull(redisTemplate.opsForHash()
                        .get(memberUuid, PRINCIPALS.getValue()))
                .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doReissue(String memberUuid, String accessToken) {
        redisTemplate.opsForHash().delete(memberUuid, ACCESS_TOKEN.getValue());
        redisTemplate.opsForHash()
                .put(memberUuid, ACCESS_TOKEN.getValue(), accessToken);
    }

    /**
     * {@inheritDoc}
     */
    public void doLogout(String memberUuid) {
        redisTemplate.opsForHash().delete(memberUuid, REFRESH_TOKEN.getValue());
        redisTemplate.opsForHash().delete(memberUuid, ACCESS_TOKEN.getValue());
        redisTemplate.opsForHash().delete(memberUuid, PRINCIPALS.getValue());
        redisTemplate.opsForHash().delete(memberUuid, USER_ID.getValue());
    }
}
