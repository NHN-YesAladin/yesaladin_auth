package shop.yesaladin.auth.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String invalidToken) {
        super("Invalid token. " + invalidToken);
    }
}
