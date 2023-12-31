package com.recipia.recipe.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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
        return buildErrorResponse(e.getErrorCode(), e.getMessage());
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
