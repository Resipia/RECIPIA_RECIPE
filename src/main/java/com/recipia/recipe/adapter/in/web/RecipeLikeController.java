package com.recipia.recipe.adapter.in.web;

import com.recipia.recipe.adapter.in.web.dto.request.RecipeLikeRequestDto;
import com.recipia.recipe.adapter.in.web.dto.response.ResponseDto;
import com.recipia.recipe.application.port.in.RecipeLikeUseCase;
import com.recipia.recipe.domain.RecipeLike;
import com.recipia.recipe.domain.converter.RecipeLikeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 좋아요 컨트롤러
 */
@RequestMapping("/recipe")
@RequiredArgsConstructor
@RestController
public class RecipeLikeController {

    private final RecipeLikeUseCase recipeLikeUseCase;
    private final RecipeLikeConverter recipeLikeConverter;

    /**
     * 유저가 레시피에 좋아요를 누르면 동작한다. (상세보기에서만 가능)
     * 좋아요가 되어있지 않다면 카운트가 올라가고 좋아요가 등록된다.
     * 근데 이미 좋아요가 되어있을때 이 컨트롤러를 호출하면 카운트가 줄어들고 좋아요가 삭제된다.
     */
    @PostMapping("/like")
    public ResponseEntity<ResponseDto<Long>> recipeLike(
            RecipeLikeRequestDto likeRequestDto
    ) {
        // recipeId를 통해 좋아요 도메인 객체를 생성한다.
        RecipeLike domain = recipeLikeConverter.dtoToDomain(likeRequestDto);
        Long savedCount = recipeLikeUseCase.recipeLikeProcess(domain);
        return ResponseEntity.ok(ResponseDto.success(savedCount));
    }

}
