package com.recipia.recipe.adapter.in.web;

import com.recipia.recipe.adapter.in.web.dto.request.BookmarkDto;
import com.recipia.recipe.adapter.in.web.dto.response.ResponseDto;
import com.recipia.recipe.application.port.in.BookmarkUseCase;
import com.recipia.recipe.domain.Bookmark;
import com.recipia.recipe.domain.converter.BookmarkConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/recipe")
@RequiredArgsConstructor
@RestController
public class BookmarkController {

    private final BookmarkConverter converter;
    private final BookmarkUseCase bookmarkUseCase;

    /**
     * 북마크 등록하는 컨트롤러
     * 이 레시피(recipeId)를 북마크한 유저(memberId)가 누구냐?
     */
    @PostMapping("/addBookmark")
    public ResponseEntity<ResponseDto<Long>> addBookmark(@RequestBody BookmarkDto bookmarkDto) {
        // dto to domain
        Bookmark bookmark = converter.requestDtoToDomain(bookmarkDto);

        Long addedBookmark = bookmarkUseCase.addBookmark(bookmark);
        return ResponseEntity.ok(ResponseDto.success(addedBookmark));
    }


    /**
     * 북마크 취소하는 컨트롤러
     */
    @PostMapping("/removeBookmark")
    public ResponseEntity<ResponseDto<?>> removeBookmark() {

//        bookmarkUseCase.removeBookmark();

        return ResponseEntity.ok(ResponseDto.success());
    }
}
