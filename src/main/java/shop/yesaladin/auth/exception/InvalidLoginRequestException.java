package shop.yesaladin.auth.exception;

public class InvalidLoginRequestException extends RuntimeException {

    private static final String MESSAGE = "입력한 유저 정보가 잘못 되었습니다.";

    public InvalidLoginRequestException() {
        super(MESSAGE);
    }
}
