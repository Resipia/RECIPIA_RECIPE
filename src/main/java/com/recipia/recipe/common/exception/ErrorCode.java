package com.recipia.recipe.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 커스텀한 에러코드를 작성한다.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 10000. 로그인 관련 에러 (JWT 포함)
    USER_NOT_FOUND(404, 10001, "유저를 찾을 수 없습니다."),
    INVALID_JWT(401, 10002, "토큰이 유효하지 않습니다."),
    EXPIRED_JWT(401, 10003, "JWT 토큰이 만료되었습니다."),
    MISSING_JWT(401, 10004, "JWT 토큰이 누락되었습니다."),

    // 20000. 레시피 에러
    REQUIRED_MEMBER_ID(404, 20001, "회원 ID는 필수 항목입니다."),
    REQUIRED_RECIPE_NAME(404, 20002, "레시피 이름은 필수 항목입니다."),
    REQUIRED_RECIPE_DESCRIPTION(404, 20003, "레시피 설명은 필수 항목입니다."),
    REQUIRED_MEMBER_NICKNAME(404, 20004, "닉네임은 필수 항목입니다."),
    REQUIRED_RECIPE_DELETE_YN(404, 20005, "삭제 여부는 필수 항목입니다."),
    INVALID_INGREDIENTS(404, 20006, "재료가 유효하지 않습니다."),
    INVALID_HASHTAGS(404, 20007, "해시태그가 유효하지 않습니다."),
    CATEGORY_NOT_FOUND(404, 20008, "존재하지 않는 카테고리입니다."),
    RECIPE_FILE_SAVE_ERROR(500, 20009, "데이터 베이스에 파일을 저장하던중 예외가 발생했습니다."),
    RECIPE_IS_NOT_MINE(404, 20010, "레시피 수정/삭제를 시도한 유저가 주인이 아닙니다."),

    // 30000. 댓글/대댓글 관련 에러
    COMMENT_NOT_FOUND(404, 30001, "댓글이 존재하지 않습니다."),
    COMMENT_IS_NOT_MINE(404, 30002, "요청자가 작성한 댓글이 아닙니다."),
    SUB_COMMENT_IS_NOT_MINE(404, 30003, "요청자가 작성한 대댓글이 아닙니다."),

    // 40000. S3 관련 에러
    S3_UPLOAD_ERROR(500, 40001, "AWS S3 서비스 에러"),
    S3_UPLOAD_FILE_NOT_FOUND(404, 40002, "업로드할 파일이 존재하지 않습니다."),
    INVALID_FILE_TYPE(404, 40003, "S3에 업로드 할 수 없는 파일 타입입니다."),

    // 50000. 북마크/좋아요 관련 에러
    RECIPE_NOT_FOUND(404, 50001, "레시피가 존재하지 않습니다."),
    INVALID_INPUT(404, 50002, "잘못된 입력입니다."),
    BOOKMARK_NOT_FOUND(404, 50003, "북마크를 찾을 수 없습니다."),
    NOT_FOUND_LIKE(404, 50011, "좋아요가 존재하지 않습니다."),

    // 60000. 영양소, 카테고리, 서브카테고리 관련 에러
    NUTRITIONAL_INFO_NOT_FOUND(404, 60001, "업데이트 하려는 영양소 정보가 존재하지 않습니다."),
    SUB_CATEGORY_IS_NULL_OR_EMPTY(404, 60002, "카테고리는 null이거나 공백이어서는 안됩니다."),
    SUB_CATEGORY_NOT_EXIST(404, 60003, "존재하지 않는 서브 카테고리입니다."),

    // 70000. RDB, 몽고 DB 검색 관련 에러
    REQUIRED_FIELD_NULL(404, 70001, "필수 컬럼값이 존재하지 않습니다."),
    FILE_NOT_FOUND(404, 70002, "파일 경로에 파일이 존재하지 않습니다."),
    FILE_DUPLICATED(404, 70003, "중복된 파일입니다."),
    DB_ERROR(500, 70004, "DB ERROR"),
    MONGO_DB_UPDATED_FAIL(404, 70005, "몽고DB에 데이터 저장을 실패했습니다."),

    CONDITION_NOT_FOUND(404, 70011, "검색 조건을 찾을 수 없습니다."),
    INVALID_SEARCH_CONDITION(404, 70012, "검색 조건이 유효하지 않습니다."),
    SEARCH_WORD_NECESSARY(404, 70013, "검색 단어 입력은 필수입니다."),

    // 80000. 공통 에러
    IO_ERROR(404, 80001, "INPUT/OUTPUT ERROR"),
    BAD_REQUEST(400, 80002, "잘못된 요청"),
    FILTER_USERNAME_PASSWORD_AUTHENTICATION_TOKEN(404, 80003, "filter attemptAuthentication 인증 에러"),
    EVENT_NOT_FOUND(404, 80004, "이벤트 저장소에 해당 이벤트를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(500, 80005, "서버 내부 오류"),
    NULL_POINTER_EXCEPTION(500, 80006, "Null 참조 오류"),
    ILLEGAL_ARGUMENT_EXCEPTION(400, 80007, "부적절한 인자 오류"),

    // 90000. 외부 서비스 에러
    AWS_SNS_CLIENT(500, 90001, "SNS 발행 에러"),
    NO_TRACE_ID_IN_MESSAGE(404, 90002, "message에 traceid가 없습니다."),
    RECIPE_SERVICE_ERROR(500, 90003, "RECIPE 서비스 에러"),
    WRIGGLE_SERVICE_ERROR(500, 90004, "WRIGGLE 서비스 에러"),
    CHAT_SERVICE_ERROR(500, 90005, "CHAT 서비스 에러"),
    REDIS_RECIPE_ID_NOT_FOUND(404, 90006, "레디스 내부에서 레시피id를 찾을 수 없습니다."),
    REDIS_ERROR_OCCUR(404, 90007, "레디스에서 오류가 발생했습니다.")

    ;


    private final int status;  // HTTP 상태 코드
    private final int code;    // 내부적으로 사용할 숫자 코드
    private final String message;  // 로깅을 위한 메시지

}

