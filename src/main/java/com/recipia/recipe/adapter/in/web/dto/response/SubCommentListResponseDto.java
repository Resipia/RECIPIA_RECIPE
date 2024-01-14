package com.recipia.recipe.adapter.in.web.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 부모 댓글에 해당하는 대댓글 목록 데이터를 담을 응답 객체
 */
@NoArgsConstructor
@Data
public class SubCommentListResponseDto {
    private Long id;                // subComment pk
    private Long parentCommentId;   // 부모 댓글 pk
    private Long memberId;          // 작성자 member id
    private String nickname;        // 작성자 닉네임
    private String subCommentValue;    // 대댓글 내용
    private String createDate;      // 댓글 작성 날짜
    private boolean isUpdated;      // 댓글 수정 여부

    public SubCommentListResponseDto(Long id, Long parentCommentId, Long memberId, String nickname, String subCommentValue, String createDate, boolean isUpdated) {
        this.id = id;
        this.parentCommentId = parentCommentId;
        this.memberId = memberId;
        this.nickname = nickname;
        this.subCommentValue = subCommentValue;
        this.createDate = createDate;
        this.isUpdated = isUpdated;
    }

    public static SubCommentListResponseDto of(Long id, Long parentCommentId, Long memberId, String nickname, String subCommentValue, String createDate, boolean isUpdated) {
        return new SubCommentListResponseDto(id, parentCommentId, memberId, nickname, subCommentValue, createDate, isUpdated);
    }
}
