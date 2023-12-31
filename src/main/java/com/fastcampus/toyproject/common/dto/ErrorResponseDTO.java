package com.fastcampus.toyproject.common.dto;

import static com.fastcampus.toyproject.common.exception.DefaultExceptionCode.BAD_REQUEST;
import static com.fastcampus.toyproject.common.exception.DefaultExceptionCode.INTERNAL_SERVER_ERROR;

import com.fastcampus.toyproject.common.exception.ExceptionCode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Getter
@RequiredArgsConstructor
public class ErrorResponseDTO {

    private final HttpStatus status;
    private final String code;
    private final String msg;

    public static ErrorResponseDTO error(ExceptionCode exceptionCode) {
        return new ErrorResponseDTO(
                exceptionCode.getStatus(),
                exceptionCode.getCode(),
                exceptionCode.getMsg()
        );
    }
}
