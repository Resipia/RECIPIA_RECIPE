package com.recipia.recipe.application.service;

import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.application.port.in.MyPageUseCase;
import com.recipia.recipe.application.port.out.RecipePort;
import com.recipia.recipe.application.port.out.RedisPort;
import com.recipia.recipe.domain.MyPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 마이페이지 서비스 클래스
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MyPageService implements MyPageUseCase {

    private final RecipePort recipePort;
    private final RedisPort redisPort;
    private final ImageS3Service imageS3Service;

    /**
     * [READ] memberId에 해당하는 회원이 작성한 레시피 갯수를 가져온다.
     */
    @Override
    public MyPage getRecipeCount(Long memberId) {
        Long myRecipeCount = recipePort.getMyRecipeCount(memberId);
        return MyPage.of(myRecipeCount);
    }

    /**
     * [READ] 내가 작성한 레시피 목록을 조회수 높은 순으로 최대 5개 가져온다.
     */
    @Override
    public List<RecipeMainListResponseDto> getMyRecipeHigh(Long memberId) {

        List<Long> myRecipeIds = recipePort.getAllMyRecipeIds(memberId);

        // 내가 작성한 레시피가 존재한다면 redis 에서 조회수 높은순으로 가져오기
        if(myRecipeIds.isEmpty()) {
            return null;
        }
        List<Long> myHighRecipeIds = getMyHighRecipeIds(myRecipeIds);
        List<RecipeMainListResponseDto> databaseResult = recipePort.getMyHighRecipeList(memberId, myHighRecipeIds);

        List<RecipeMainListResponseDto> finalResult = databaseResult.stream().map(dto -> {
            if (dto.getThumbnailFullPath() != null) {
                String preSignedUrl = imageS3Service.generatePreSignedUrl(dto.getThumbnailFullPath(), 60);
                return RecipeMainListResponseDto.of(dto.getId(), dto.getRecipeName(), dto.getNickname(), dto.getBookmarkId(), dto.getSubCategoryList(), null, preSignedUrl);
            }
            return dto;
        }).collect(Collectors.toList());

        return finalResult;
    }

    /**
     * redis에서 파라미터로 받은 id에 해당하는 데이터중 조회수 높은순으로 5개 가져오기
     */
    private List<Long> getMyHighRecipeIds(List<Long> myRecipeIds) {
        Map<Long, Integer> recipeViews = new HashMap<>();

        // 각 레시피 ID에 대한 조회수를 가져와 매핑
        myRecipeIds.forEach(recipeId -> {
            Integer views = redisPort.getViews(recipeId);
            recipeViews.put(recipeId, views);
        });

        // 조회수를 기준으로 내림차순 정렬 후 상위 5개의 레시피 ID를 추출
        return recipeViews.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
