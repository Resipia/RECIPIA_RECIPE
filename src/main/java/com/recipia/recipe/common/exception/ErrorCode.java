package com.recipia.recipe.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 커스텀한 에러코드를 작성한다.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 로그인 관련 에러
    USER_NOT_FOUND(404, "USER_NOT_FOUND", "유저를 찾을 수 없습니다."),

    // JWT 관련 에러
    INVALID_JWT(401, "INVALID_JWT", "토큰이 유효하지 않습니다."),
    EXPIRED_JWT(401, "EXPIRED_JWT", "JWT 토큰이 만료되었습니다."),
    MISSING_JWT(401, "MISSING_JWT", "JWT 토큰이 누락되었습니다."),

    // RECIPE Create 에러
    REQUIRED_MEMBER_ID(404, "MISSING_MEMBER_ID", "회원 ID는 필수 항목입니다."),
    REQUIRED_RECIPE_NAME(404, "REQUIRED_RECIPE_NAME", "레시피 이름은 필수 항목입니다."),
    REQUIRED_RECIPE_DESCRIPTION(404, "REQUIRED_RECIPE_NAME", "레시피 설명은 필수 항목입니다."),
    REQUIRED_MEMBER_NICKNAME(404, "REQUIRED_RECIPE_NAME", "닉네임은 필수 항목입니다."),
    REQUIRED_RECIPE_DELETE_YN(404, "RECIPE_TIME_SET_ERROR", "삭제 여부는 필수 항목입니다."),
    INVALID_INGREDIENTS(404, "INVALID_INGREDIENTS", "재료가 유효하지 않습니다."),
    INVALID_HASHTAGS(404, "INVALID_HASHTAGS", "해시태그가 유효하지 않습니다."),
    CATEGORY_NOT_FOUND(404, "CATEGORY_NOT_FOUND", "존재하지 않는 카테고리입니다."),
    RECIPE_FILE_SAVE_ERROR(500, "RECIPE_FILE_SAVE_ERROR", "데이터 베이스에 파일을 저장하던중 예외가 발생했습니다."),

    // 댓글 관련 에러
    COMMENT_NOT_FOUND(404, "COMMENT_NOT_FOUND", "댓글이 존재하지 않습니다."),
    COMMENT_IS_NOT_MINE(404, "COMMENT_IS_NOT_MINE", "요청자가 작성한 댓글이 아닙니다."),

    // S3 관련 에러
    S3_UPLOAD_ERROR(500, "S3_UPLOAD_ERROR", "AWS S3 서비스 에러"),
    S3_UPLOAD_FILE_NOT_FOUND(404, "S3_UPLOAD_FILE_NOT_FOUND", "업로드할 파일이 존재하지 않습니다."),
    INVALID_FILE_TYPE(404, "INVALID_FILE_TYPE", "S3에 업로드 할 수 없는 파일 타입입니다."),

    // 북마크 관련 에러
    RECIPE_NOT_FOUND(404, "RECIPE_NOT_FOUND", "레시피가 존재하지 않습니다."),
    INVALID_INPUT(404, "INVALID_INPUT", "잘못된 입력입니다."),
    BOOKMARK_NOT_FOUND(404, "BOOKMARK_NOT_FOUND", "북마크를 찾을 수 없습니다."),

    // 영양소 관련 에러
    NUTRITIONAL_INFO_NOT_FOUND(404, "NUTRITIONAL_INFO_NOT_FOUND", "업데이트 하려는 영양소 정보가 존재하지 않습니다."),

    // 카테고리, 서브카테고리 관련 에러
    SUB_CATEGORY_IS_NULL_OR_EMPTY(404, "SUB_CATEGORY_IS_NULL_OR_EMPTY", "카테고리는 null이거나 공백이어서는 안됩니다."),
    SUB_CATEGORY_NOT_EXIST(404, "SUB_CATEGORY_NOT_EXIST", "존재하지 않는 서브 카테고리입니다."),

    // 검색 관련 에러
    CONDITION_NOT_FOUND(404, "CONDITION_NOT_FOUND", "검색 조건을 찾을 수 없습니다."),
    INVALID_SEARCH_CONDITION(404, "INVALID_SEARCH_CONDITION", "검색 조건이 유효하지 않습니다."),
    SEARCH_WORD_NECESSARY(404, "SEARCH_WORD_NOT_FOUND", "검색 단어 입력은 필수입니다."),

    // DB 관련 에러
    REQUIRED_FIELD_NULL(404, "REQUIRED_FIELD_NULL", "필수 컬럼값이 존재하지 않습니다."),
    FILE_NOT_FOUND(404, "FILE_NOT_FOUND", "파일 경로에 파일이 존재하지 않습니다."),
    FILE_DUPLICATED(404, "FILE_DUPLICATED", "중복된 파일입니다."),
    DB_ERROR(500, "DB_ERROR", "DB ERROR"),
    MONGO_DB_UPDATED_FAIL(404, "MONGO_DB_UPDATED_FAIL", "몽고DB에 데이터 저장을 실패했습니다."),

    // Zipkin 관련 에러
    NO_TRACE_ID_IN_MESSAGE(404, "NO_TRACE_ID_IN_MESSAGE", "message에 traceid가 없습니다."),

    // 공통 에러
    IO_ERROR(404, "IO_ERROR", "INPUT/OUTPUT ERROR"),
    BAD_REQUEST(400, "BAD_REQUEST", "잘못된 요청"),
    FILTER_USERNAME_PASSWORD_AUTHENTICATION_TOKEN(404, "FILTER_USERNAME_PASSWORD_AUTHENTICATION_TOKEN", "filter attemptAuthentication 인증 에러"),
    EVENT_NOT_FOUND(404, "EVENT_NOT_FOUND", "이벤트 저장소에 해당 이벤트를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 내부 오류"),
    NULL_POINTER_EXCEPTION(500, "NULL_POINTER_EXCEPTION", "Null 참조 오류"),
    ILLEGAL_ARGUMENT_EXCEPTION(400, "ILLEGAL_ARGUMENT_EXCEPTION", "부적절한 인자 오류"),

    AWS_SNS_CLIENT(500, "AWS_SNS_CLIENT", "SNS 발행 에러"),

    // 외부 서비스 에러
    RECIPE_SERVICE_ERROR(500, "RECIPE_SERVICE_ERROR", "RECIPE 서비스 에러"),
    WRIGGLE_SERVICE_ERROR(500, "WRIGGLE_SERVICE_ERROR", "WRIGGLE 서비스 에러"),
    CHAT_SERVICE_ERROR(500, "CHAT_SERVICE_ERROR", "CHAT 서비스 에러"),

    ;


    private final int status;
    private final String code;
    private final String message;

}

