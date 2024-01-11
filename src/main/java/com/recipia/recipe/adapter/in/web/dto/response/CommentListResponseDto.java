package com.recipia.recipe.adapter.in.web.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 레시피 상세 조회할때 댓글 목록 데이터를 담을 응답 객체
 */
@NoArgsConstructor
@Data
public class CommentListResponseDto {
    private Long id;                // comment pk
    private Long memberId;          // 작성자 member id
    private String nickname;        // 작성자 닉네임
    private String commentValue;    // 댓글 내용
    private String createDate;      // 댓글 작성 날짜
    private boolean isUpdated;      // 댓글 수정 여부

    private CommentListResponseDto(Long id, Long memberId, String nickname, String commentValue, String createDate, boolean isUpdated) {
        this.id = id;
        this.memberId = memberId;
        this.nickname = nickname;
        this.commentValue = commentValue;
        this.createDate = createDate;
        this.isUpdated = isUpdated;
    }

    public static CommentListResponseDto of(Long id, Long memberId, String nickname, String commentValue, String createDate, boolean isUpdated) {
        return new CommentListResponseDto(id, memberId, nickname, commentValue, createDate, isUpdated);
    }
}
