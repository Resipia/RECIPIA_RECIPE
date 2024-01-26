package com.recipia.recipe.application.service;

import com.recipia.recipe.adapter.in.web.dto.response.PagingResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeListResponseDto;
import com.recipia.recipe.application.port.in.MyPageUseCase;
import com.recipia.recipe.application.port.out.MyPagePort;
import com.recipia.recipe.application.port.out.RedisPort;
import com.recipia.recipe.domain.MyPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private final MyPagePort MyPagePort;
    private final RedisPort redisPort;
    private final ImageS3Service imageS3Service;

    /**
     * [READ] targetMemberId에 해당하는 회원이 작성한 레시피 갯수를 가져온다.
     */
    @Override
    public MyPage getRecipeCount(Long targetMemberId) {
        Long recipeCount = MyPagePort.getTargetMemberIdRecipeCount(targetMemberId);
        return MyPage.of(recipeCount);
    }

    /**
     * [READ] targetMemberId가 작성한 레시피 목록을 조회수 높은 순으로 최대 5개 가져온다.
     */
    @Override
    public List<RecipeListResponseDto> getTargetMemberRecipeHigh(Long targetMemberId) {

        List<Long> recipeIds = MyPagePort.getTargetMemberRecipeIds(targetMemberId);

        // targetMember가 작성한 레시피가 존재하지 않는다면 그냥 return null
        if (recipeIds.isEmpty()) {
            return null;
        }

        // targetMember가 작성한 레시피가 존재한다면 redis 에서 조회수 높은순으로 가져오기
        List<Long> highRecipeIds = getTargetMemberHighRecipeIds(recipeIds);

        // 레시피 조회수 top5에 해당하는 레시피 목록 가져오기
        List<RecipeListResponseDto> databaseResult = MyPagePort.getTargetMemberHighRecipeList(targetMemberId, highRecipeIds);

        // 가져온 레시피중에서 썸네일이 존재하는 레시피가 있다면 preUrl을 세팅해준다.
        List<RecipeListResponseDto> finalResult = setPreUrls(databaseResult);
        return finalResult;
    }


    /**
     * [READ] targetMember가 작성한 모든 레시피 목록을 가져온다
     */
    @Override
    public PagingResponseDto<RecipeListResponseDto> getTargetMemberRecipeList(int page, int size, String sortType, Long targetMemberId) {
        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size);

        // 데이터를 받아온다.
        Page<RecipeListResponseDto> databaseResult = MyPagePort.getTargetMemberRecipeList(targetMemberId, pageable, sortType);

        // 받아온 데이터를 꺼내서 응답 dto에 값을 세팅해 준다.
        List<RecipeListResponseDto> beforeContent = databaseResult.getContent();
        // 썸네일 데이터를 preUrl로 세팅해준다.
        List<RecipeListResponseDto> finalResult = setPreUrls(beforeContent);
        // 총 갯수를 세팅해준다.
        Long totalCount = databaseResult.getTotalElements();
        return PagingResponseDto.of(finalResult, totalCount);
    }

    /**
     * [READ] 내가 북마크한 레시피 목록을 가져온다.
     */
    @Override
    public PagingResponseDto<RecipeListResponseDto> getAllMyBookmarkList(int page, int size) {
        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size);

        // 데이터를 받아온다.
        Page<RecipeListResponseDto> databaseResult = MyPagePort.getAllMyBookmarkList(pageable);
        // 받아온 데이터를 꺼내서 응답 dto에 값을 세팅해 준다.
        List<RecipeListResponseDto> beforeContent = databaseResult.getContent();
        // 썸네일 데이터를 preUrl로 세팅해준다.
        List<RecipeListResponseDto> finalResult = setPreUrls(beforeContent);
        // 총 갯수를 세팅해준다.
        Long totalCount = databaseResult.getTotalElements();
        return PagingResponseDto.of(finalResult, totalCount);
    }

    /**
     * [READ] 내가 좋아요한 레시피 목록을 가져온다.
     */
    @Override
    public PagingResponseDto<RecipeListResponseDto> getAllMyLikeList(int page, int size) {
        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size);

        // 데이터를 받아온다.
        Page<RecipeListResponseDto> databaseResult = MyPagePort.getAllMyLikeList(pageable);

        // 받아온 데이터를 꺼내서 응답 dto에 값을 세팅해 준다.
        List<RecipeListResponseDto> beforeContent = databaseResult.getContent();
        // 썸네일 데이터를 preUrl로 세팅해준다.
        List<RecipeListResponseDto> finalResult = setPreUrls(beforeContent);
        // 총 갯수를 세팅해준다.
        Long totalCount = databaseResult.getTotalElements();
        return PagingResponseDto.of(finalResult, totalCount);
    }

    /**
     * [Extract Method]
     * redis에서 파라미터로 받은 id에 해당하는 데이터중 조회수 높은순으로 5개 가져오기
     */
    private List<Long> getTargetMemberHighRecipeIds(List<Long> myRecipeIds) {
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

    /**
     * [Extract Method]
     * DB에서 응답받은 데이터중에서 썸네일이 있는 경우, pre-signed-url을 생성해서 세팅한다.
     */
    private List<RecipeListResponseDto> setPreUrls(List<RecipeListResponseDto> databaseResult) {
        return databaseResult.stream().map(dto -> {
            // 만약 저장된 썸네일이 있다면, S3 pre-signed-url을 생성한다.
            if (dto.getThumbnailFullPath() != null) {
                String preSignedUrl = imageS3Service.generatePreSignedUrl(dto.getThumbnailFullPath(), 60);
                // 생성된 pre-signed-url을 세팅해주고 반환한다.
                return RecipeListResponseDto.of(dto.getId(), dto.getRecipeName(), dto.getNickname(), dto.getBookmarkId(), dto.getSubCategoryList(), null, preSignedUrl, dto.getCreateDate());
            }
            // 만약 저장된 썸네일이 없다면 기존 데이터를 반환한다.
            return dto;
        }).collect(Collectors.toList());
    }
}
