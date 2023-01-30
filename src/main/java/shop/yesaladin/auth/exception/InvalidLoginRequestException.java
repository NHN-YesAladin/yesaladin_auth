package shop.yesaladin.auth.exception;

/**
 * 입력한 로그인 정보가 잘못 된 경우 발생하는 예외
 *
 * @author 송학현
 * @since 1.0
 */
public class InvalidLoginRequestException extends RuntimeException {

    private static final String MESSAGE = "입력한 유저 정보가 잘못 되었습니다.";

    public InvalidLoginRequestException() {
        super(MESSAGE);
    }
}
