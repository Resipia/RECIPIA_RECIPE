package com.recipia.recipe.adapter.in.web;

import com.recipia.recipe.adapter.in.web.dto.request.MyPageRequestDto;
import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeListResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.ResponseDto;
import com.recipia.recipe.application.port.in.MyPageUseCase;
import com.recipia.recipe.domain.MyPage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 마이페이지 컨트롤러
 */
@RequestMapping("/recipe")
@RequiredArgsConstructor
@RestController
public class MyPageController {

    private final MyPageUseCase myPageUseCase;

    /**
     * 마이페이지에서 targetMemberId가 작성한 레시피 갯수 가져오기
     */
    @PostMapping("/mypage/recipeCnt")
    public ResponseEntity<ResponseDto<Long>> getRecipeCount(@Valid @RequestBody MyPageRequestDto dto) {
        MyPage myPage = myPageUseCase.getRecipeCount(dto.getTargetMemberId());
        return ResponseEntity.ok(
                ResponseDto.success(myPage.getRecipeCount())
        );
    }

    /**
     * 마이페이지에서 targetMemberId가 작성한 레시피 중 조회수 높은 레시피 최대 5개 가져오기
     */
    @PostMapping("/mypage/highRecipe")
    public ResponseEntity<ResponseDto<List<RecipeListResponseDto>>> getHighRecipe(@Valid @RequestBody MyPageRequestDto dto) {
        List<RecipeListResponseDto> result = myPageUseCase.getTargetMemberRecipeHigh(dto.getTargetMemberId());
        return ResponseEntity.ok(
                ResponseDto.success(result)
        );
    }

    /**
     * 마이페이지에서 targetMember가 작성한 레시피 조회
     * page와 size는 각각 '현재 페이지'와 '페이지 당 항목 수'를 의미한다.
     */
    @GetMapping("/mypage/targetMemberRecipeList")
    public ResponseEntity<PagingResponseDto<RecipeListResponseDto>> getAllTargetMemberRecipeList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortType", defaultValue = "new") String sortType,
            @RequestParam(value = "targetMemberId") Long targetMemberId

    ) {
        PagingResponseDto<RecipeListResponseDto> allTargetRecipeList = myPageUseCase.getTargetMemberRecipeList(page, size, sortType, targetMemberId);
        return ResponseEntity.ok(allTargetRecipeList);
    }

    /**
     * 마이페이지에서 내가 북마크한 레시피 조회
     * page와 size는 각각 '현재 페이지'와 '페이지 당 항목 수'를 의미한다.
     */
    @GetMapping("/mypage/myBookmarkRecipeList")
    public ResponseEntity<PagingResponseDto<RecipeListResponseDto>> getAllMyBookmarkRecipeList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        PagingResponseDto<RecipeListResponseDto> allMyBookmarkRecipeList = myPageUseCase.getAllMyBookmarkList(page, size);
        return ResponseEntity.ok(allMyBookmarkRecipeList);
    }

}
