package shop.yesaladin.auth.exception;

/**
 * JWT Token이 유효하지 않은 경우 발생하는 예외 입니다.
 *
 * @author 김홍대
 * @since 1.0
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String invalidToken) {
        super("Invalid token. " + invalidToken);
    }
}
