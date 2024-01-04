package com.recipia.recipe.adapter.in.web;

import com.recipia.recipe.adapter.in.web.dto.request.BookmarkRequestDto;
import com.recipia.recipe.adapter.in.web.dto.response.ResponseDto;
import com.recipia.recipe.application.port.in.BookmarkUseCase;
import com.recipia.recipe.domain.Bookmark;
import com.recipia.recipe.domain.converter.BookmarkConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 북마크 컨트롤러
 */
@RequestMapping("/recipe")
@RequiredArgsConstructor
@RestController
public class BookmarkController {

    private final BookmarkConverter converter;
    private final BookmarkUseCase bookmarkUseCase;

    /**
     * 북마크 등록
     */
    @PostMapping("/addBookmark")
    public ResponseEntity<ResponseDto<Long>> addBookmark(@RequestBody BookmarkRequestDto bookmarkRequestDto) {
        // dto to domain
        Bookmark bookmark = converter.requestDtoToDomain(bookmarkRequestDto);

        Long addedBookmark = bookmarkUseCase.addBookmark(bookmark);
        return ResponseEntity.ok(ResponseDto.success(addedBookmark));
    }

    /**
     * 북마크 취소
     */
    @PostMapping("/removeBookmark")
    public ResponseEntity<ResponseDto<Void>> removeBookmark(@RequestParam Long bookmarkId) {
        bookmarkUseCase.removeBookmark(bookmarkId);
        return ResponseEntity.ok(ResponseDto.success());
    }
}
