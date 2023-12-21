package com.recipia.recipe.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiErrorCodeEnum {

    // 2.0 recipe
    RECIPE_NOT_FOUNT(404, "2001", "레시피가 존재하지 않습니다."),
    NOT_SUPPORTED_FILE_FORMAT(404, "2002", "지원하지 않는 데이터 포맷입니다."),

    // 2.1 category
    CATEGORY_NOT_FOUND(404, "2101", "카테고리가 존재하지 않습니다."),

    // 2.2 ingredient



    // COMMON
    // 8.0 DB
    REQUIRED_FIELD_NULL(404, "8001", "필수 컬럼값이 존재하지 않습니다."),
    FILE_NOT_FOUND(404, "8002", "파일 경로에 파일이 존재하지 않습니다."),
    FILE_DUPLICATED(404, "8003", "중복된 파일입니다."),
    DB_ERROR(500, "8004", "DB ERROR"),

    // 9.0 EXTERNAL
    MEMBER_SERVICE_ERROR(500, "9001", "MEMBER 서비스 에러"),
    YORI_ZORI_SERVICE_ERROR(500, "9003", "YORIZORI 서비스 에러"),
    CHAT_SERVICE_ERROR(500, "9004", "CHAT 서비스 에러")

    ;



    private int status;
    private String code;
    private String message;

}
