package com.recipia.recipe.application.port.out;

import com.recipia.recipe.adapter.in.web.dto.response.RecipeListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MyPagePort {

    Long getTargetMemberIdRecipeCount(Long targetMemberId);

    List<Long> getTargetMemberRecipeIds(Long targetMemberId);

    List<RecipeListResponseDto> getTargetMemberHighRecipeList(Long targetMemberId, List<Long> highRecipeIds);

    Page<RecipeListResponseDto> getTargetMemberRecipeList(Long targetMemberId, Pageable pageable, String sortType);

    Page<RecipeListResponseDto> getAllMyBookmarkList(Pageable pageable);

    Page<RecipeListResponseDto> getAllMyLikeList(Pageable pageable);

}
