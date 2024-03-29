package com.recipia.recipe.adapter.in.web;

import com.recipia.recipe.adapter.in.web.dto.request.*;
import com.recipia.recipe.adapter.in.web.dto.response.CommentListResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.ResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.SubCommentListResponseDto;
import com.recipia.recipe.application.port.in.CommentUseCase;
import com.recipia.recipe.application.port.in.SubCommentUseCase;
import com.recipia.recipe.domain.converter.CommentConverter;
import com.recipia.recipe.domain.converter.SubCommentConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 댓글/대댓글 컨트롤러
 */
@RequestMapping("/recipe")
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentUseCase commentUseCase;
    private final SubCommentUseCase subCommentUseCase;
    private final CommentConverter commentConverter;
    private final SubCommentConverter subCommentConverter;

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

    /**
     * 레시피 상세조회에서 recipeId에 해당하는 댓글 목록 조회
     */
    @GetMapping("/getAllCommentList")
    public ResponseEntity<PagingResponseDto<CommentListResponseDto>> getAllCommentList(
            @RequestParam(value = "recipeId") Long recipeId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortType", defaultValue = "new") String sortType
    ) {
        PagingResponseDto<CommentListResponseDto> commentList = commentUseCase.getCommentList(recipeId, page, size, sortType);
        return ResponseEntity.ok(commentList);
    }

    /**
     * 대댓글 등록
     */
    @PostMapping("/regist/subComment")
    public ResponseEntity<ResponseDto<Long>> registSubComment(@Valid @RequestBody SubCommentRegistRequestDto dto) {
        Long createdSubCommentId = subCommentUseCase.createSubComment(subCommentConverter.registRequestDtoToDomain(dto));
        return ResponseEntity.ok(
                ResponseDto.success(createdSubCommentId)
        );
    }

    /**
     * 대댓글 수정
     */
    @PostMapping("/update/subComment")
    public ResponseEntity<ResponseDto<Void>> updateSubComment(@Valid @RequestBody SubCommentUpdateRequestDto dto) {
        subCommentUseCase.updateSubComment(subCommentConverter.updateRequestDtoToDomain(dto));
        return ResponseEntity.ok(
                ResponseDto.success()
        );
    }

    /**
     * 대댓글 삭제
     */
    @PostMapping("/delete/subComment")
    public ResponseEntity<ResponseDto<Void>> softDeleteSubComment(@Valid @RequestBody SubCommentDeleteRequestDto requestDto) {
        subCommentUseCase.deleteSubComment(subCommentConverter.deleteRequestDtoToDomain(requestDto));
        return ResponseEntity.ok(
                ResponseDto.success()
        );
    }

    /**
     * 부모 댓글에서 대댓글에 해당하는 대댓글 목록 조회
     */
    @GetMapping("/getAllSubCommentList")
    public ResponseEntity<PagingResponseDto<SubCommentListResponseDto>> getAllSubCommentList(
            @RequestParam(value = "parentCommentId") Long parentCommentId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        PagingResponseDto<SubCommentListResponseDto> commentList = subCommentUseCase.getSubCommentList(parentCommentId, page, size);
        return ResponseEntity.ok(commentList);
    }

}
