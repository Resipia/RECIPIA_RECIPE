package com.recipia.recipe.adapter.in.web;

import com.recipia.recipe.adapter.in.web.dto.request.CommentDeleteRequestDto;
import com.recipia.recipe.adapter.in.web.dto.request.CommentRegistRequestDto;
import com.recipia.recipe.adapter.in.web.dto.request.CommentUpdateRequestDto;
import com.recipia.recipe.adapter.in.web.dto.response.ResponseDto;
import com.recipia.recipe.application.port.in.CommentUseCase;
import com.recipia.recipe.common.utils.SecurityUtil;
import com.recipia.recipe.domain.converter.CommentConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 댓글 컨트롤러
 */
@RequestMapping("/recipe")
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentUseCase commentUseCase;
    private final CommentConverter commentConverter;
    private final SecurityUtil securityUtil;

    /**
     * 댓글 등록
     */
    @PostMapping("/regist/comment")
    public ResponseEntity<ResponseDto<Long>> registComment(@Valid @RequestBody CommentRegistRequestDto requestDto) {
        Long createdCommentId = commentUseCase.createComment(commentConverter.registRequestDtoToDomain(requestDto));
        return ResponseEntity.ok(
                ResponseDto.success(createdCommentId)
        );
    }

    /**
     * 댓글 수정
     */
    @PostMapping("/update/comment")
    public ResponseEntity<ResponseDto<Void>> updateComment(@Valid @RequestBody CommentUpdateRequestDto requestDto) {
        commentUseCase.updateComment(commentConverter.updateRequestDtoToDomain(requestDto));
        return ResponseEntity.ok(
                ResponseDto.success()
        );
    }

    /**
     * 댓글 삭제
     */
    @PostMapping("/delete/comment")
    public ResponseEntity<ResponseDto<Void>> softDeleteComment(@Valid @RequestBody CommentDeleteRequestDto requestDto) {
        commentUseCase.softDeleteComment(commentConverter.deleteRequestDtoToDomain(requestDto));
        return ResponseEntity.ok(
                ResponseDto.success()
        );
    }

}
