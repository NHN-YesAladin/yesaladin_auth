package shop.yesaladin.auth.advice;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.yesaladin.auth.exception.InvalidAuthorizationHeaderException;
import shop.yesaladin.auth.exception.InvalidTokenException;
import shop.yesaladin.common.dto.ResponseDto;

/**
 * 예외 처리를 위한 Controller Advice 입니다.
 *
 * @author 송학현
 * @since 1.0
 */
@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(InvalidAuthorizationHeaderException.class)
    public ResponseEntity<ResponseDto<Object>> handleInvalidAuthorizationHeaderException(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ResponseDto.builder()
                        .success(false)
                        .status(HttpStatus.UNAUTHORIZED)
                        .errorMessages(List.of(e.getMessage()))
                        .build()
        );
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ResponseDto<Object>> handleInvalidTokenException(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ResponseDto.builder()
                        .success(false)
                        .status(HttpStatus.FORBIDDEN)
                        .errorMessages(List.of())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Object>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ResponseDto.builder()
                        .success(false)
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .errorMessages(List.of(e.getMessage()))
                        .build()
        );
    }
}
