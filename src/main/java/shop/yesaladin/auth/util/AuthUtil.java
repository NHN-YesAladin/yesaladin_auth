package shop.yesaladin.auth.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthUtil {
    ACCESS_TOKEN("ACCESS_TOKEN"),
    REFRESH_TOKEN("REFRESH_TOKEN"),
    PRINCIPALS("PRINCIPALS"),
    USER_ID("USER_ID");

    private final String value;
}
