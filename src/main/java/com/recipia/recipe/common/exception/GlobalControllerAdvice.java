package com.recipia.recipe.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 중앙 집중 예외처리를 위한 GlobalControllerAdvice 선언
 * RestController를 사용중이니 Advice도 @RestControllerAdvice를 사용한다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    /**
     * RecipeApplicationException 처리
     */
    @ExceptionHandler(RecipeApplicationException.class)
    public ResponseEntity<?> handleRecipeApplicationException(RecipeApplicationException e) {
        log.error("RecipeApplicationException occurred", e);
        return buildErrorResponse(e.getErrorCode(), null);
    }

    /**
     * NullPointerException 처리
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException e) {
        log.error("NullPointerException occurred", e);
        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, "Null reference accessed");
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException occurred", e);
        return buildErrorResponse(ErrorCode.BAD_REQUEST, "Invalid argument provided");
    }

    /**
     * @Valid 어노테이션을 사용하고 @NotNull, @NotBlank 등 어노테이션이 달려있는 필수값이 누락된 채로 들어올때 아래 에러를 발생.
     * MethodArgumentNotValidException 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException occurred", e);

        String missingFields = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getField)
                .collect(Collectors.joining(", "));  // 필드를 쉼표와 공백으로 구분하여 하나의 문자열로 합친다.

        return buildErrorResponse(ErrorCode.BAD_REQUEST, missingFields);
    }

    /**
     * 모든 RuntimeException 처리
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException occurred", e);
        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    /**
     * 공통 에러 응답 생성 메서드
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(ErrorCode errorCode, String customMessage) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", errorCode.getStatus());
        errorResponse.put("code", errorCode.getCode());
        errorResponse.put("message", customMessage);

        return ResponseEntity.status(HttpStatus.valueOf(errorCode.getStatus()))
                .body(errorResponse);
    }

}
