package com.recipia.recipe.adapter.in.web.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.core.pagination.sync.PaginatedResponsesIterator;

import java.time.LocalDateTime;

/**
 * 레시피 상세 조회할때 댓글 목록 데이터를 담을 응답 객체
 */
@NoArgsConstructor
@Data
public class CommentListResponseDto {
    private Long id;                        // comment pk
    private Long memberId;                  // 작성자 member id
    private String nickname;                // 작성자 닉네임
    private String commentValue;            // 댓글 내용
    private String createDate;       // 댓글 작성 날짜
    private boolean isUpdated;              // 댓글 수정 여부
    private Long subCommentCount;           // 대댓글 갯수

    public CommentListResponseDto(Long id, Long memberId, String nickname, String commentValue, String createDate, boolean isUpdated, Long subCommentCount) {
        this.id = id;
        this.memberId = memberId;
        this.nickname = nickname;
        this.commentValue = commentValue;
        this.createDate = createDate;
        this.isUpdated = isUpdated;
        this.subCommentCount = subCommentCount;
    }

    public static CommentListResponseDto of(Long id, Long memberId, String nickname, String commentValue, String createDate, boolean isUpdated, Long subCommentCount) {
        return new CommentListResponseDto(id, memberId, nickname, commentValue, createDate, isUpdated, subCommentCount);
    }
}
