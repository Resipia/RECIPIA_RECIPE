package com.recipia.recipe.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 대댓글 도메인 객체
 */
@NoArgsConstructor
@Getter
public class SubComment {

    private Long id;
    private Long parentCommentId;
    private Long memberId;
    private String subCommentText;
    private String delYn;

    @Builder
    private SubComment(Long id, Long parentCommentId, Long memberId, String subCommentText, String delYn) {
        this.id = id;
        this.parentCommentId = parentCommentId;
        this.memberId = memberId;
        this.subCommentText = subCommentText;
        this.delYn = delYn;
    }

    public static SubComment of(Long id, Long parentCommentId, Long memberId, String subCommentText, String delYn) {
        return new SubComment(id, parentCommentId, memberId, subCommentText, delYn);
    }

    /**
     * 대댓글 요청용 도메인 생성
     */
    public static SubComment of(Long parentCommentId, Long memberId, String subCommentText, String delYn) {
        return new SubComment(null, parentCommentId, memberId, subCommentText, delYn);
    }

}
