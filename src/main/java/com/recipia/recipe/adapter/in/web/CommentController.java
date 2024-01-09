package com.recipia.recipe.adapter.in.web;

import com.recipia.recipe.adapter.in.web.dto.request.CommentRequestDto;
import com.recipia.recipe.adapter.in.web.dto.response.ResponseDto;
import com.recipia.recipe.application.port.in.CommentUseCase;
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

    @PostMapping("/regist/comment")
    public ResponseEntity<ResponseDto<Long>> registComment(@Valid @RequestBody CommentRequestDto requestDto) {
        Long createdCommentId = commentUseCase.createComment(commentConverter.dtoToDomain(requestDto));
        return ResponseEntity.ok(
                ResponseDto.success(createdCommentId)
        );
    }

}
