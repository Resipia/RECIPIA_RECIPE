package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.in.web.dto.response.RecipeListResponseDto;
import com.recipia.recipe.adapter.out.persistenceAdapter.querydsl.RecipeQueryRepository;
import com.recipia.recipe.application.port.out.MyPagePort;
import com.recipia.recipe.common.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class MyPageAdapter implements MyPagePort {

    private final RecipeRepository recipeRepository;
    private final RecipeQueryRepository recipeQuerydslRepository;
    private final SecurityUtil securityUtil;


    /**
     * [READ] targetMemberId에 해당하는 회원이 작성한 레시피의 갯수를 반환한다.
     */
    @Override
    public Long getTargetMemberIdRecipeCount(Long targetMemberId) {
        return recipeRepository.countByMemberIdAndDelYn(targetMemberId, "N");
    }

    /**
     * [READ] targetMember가 작성한 recipe id 목록을 반환한다.
     */
    @Override
    public List<Long> getTargetMemberRecipeIds(Long targetMemberId) {
        return recipeQuerydslRepository.findTargetMemberRecipeIds(targetMemberId);
    }

    /**
     * [READ] targetMember가 작성한 recipe중에서 조회수가 높은 5개의 목록을 가져온다.
     */
    @Override
    public List<RecipeListResponseDto> getTargetMemberHighRecipeList(Long targetMemberId, List<Long> highRecipeIds) {
        Long loggedId = securityUtil.getCurrentMemberId();
        List<RecipeListResponseDto> resultList = recipeQuerydslRepository.getTargetMemberHighRecipeList(targetMemberId, loggedId, highRecipeIds);

        Map<Long, RecipeListResponseDto> recipesMap = resultList.stream()
                .collect(Collectors.toMap(RecipeListResponseDto::getId, dto -> dto));

        return highRecipeIds.stream()
                .map(recipesMap::get)
                .collect(Collectors.toList());
    }

    /**
     * [READ] targetMember가 작성한 레시피 목록을 page 객체로 가져온다.
     */
    public Page<RecipeListResponseDto> getTargetMemberRecipeList(Long targetMemberId, Pageable pageable, String sortType) {
        Long loggedId = securityUtil.getCurrentMemberId();
        Page<RecipeListResponseDto> recipeResponseDtoList = recipeQuerydslRepository.getTargetRecipeList(targetMemberId, loggedId, pageable, sortType);

        return recipeResponseDtoList;
    }

    /**
     * [READ] 내가 북마크한 레시피 목록을 page 객체로 가져온다.
     */
    @Override
    public Page<RecipeListResponseDto> getAllMyBookmarkList(Pageable pageable) {
        Long currentMemberId = securityUtil.getCurrentMemberId();

        // 조건에 맞는 모든 레시피 리스트를 가져온다.
        Page<RecipeListResponseDto> recipeResponseDtoList = recipeQuerydslRepository.getAllMyBookmarkList(currentMemberId, pageable);

        return recipeResponseDtoList;
    }

    /**
     * [READ] 내가 좋아요한 레시피 목록을 page 객체로 가져온다.
     */
    @Override
    public Page<RecipeListResponseDto> getAllMyLikeList(Pageable pageable) {
        Long currentMemberId = securityUtil.getCurrentMemberId();

        // 조건에 맞는 모든 레시피 리스트를 가져온다.
        Page<RecipeListResponseDto> recipeResponseDtoList = recipeQuerydslRepository.getAllMyLikeList(currentMemberId, pageable);

        return recipeResponseDtoList;
    }


}
