package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeDetailViewDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.adapter.out.persistence.entity.NutritionalInfoEntity;
import com.recipia.recipe.adapter.out.persistence.entity.QNutritionalInfoEntity;
import com.recipia.recipe.adapter.out.persistence.entity.QRecipeEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.recipia.recipe.adapter.out.persistence.entity.QBookmarkEntity.bookmarkEntity;
import static com.recipia.recipe.adapter.out.persistence.entity.QRecipeCategoryMapEntity.recipeCategoryMapEntity;
import static com.recipia.recipe.adapter.out.persistence.entity.QRecipeEntity.recipeEntity;
import static com.recipia.recipe.adapter.out.persistence.entity.QSubCategoryEntity.subCategoryEntity;


@RequiredArgsConstructor
@Repository
public class RecipeQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 유저가 변경한 닉네임을 레시피의 모든 엔티티에서도 변경시켜준다.
     */
    public Optional<Long> updateRecipesNicknames(NicknameDto nicknameDto) {
        return Optional.of(queryFactory
                .update(recipeEntity)
                .set(recipeEntity.nickname, nicknameDto.nickname())
                .where(recipeEntity.memberId.eq(nicknameDto.memberId()))
                .execute());
    }

    /**
     * fetch join을 사용하면 관련된 엔티티들이 메모리에 모두 로드되어서, 데이터베이스 레벨에서의 페이징이 아닌 메모리 레벨에서의 페이징이 발생할 수 있다.
     * 이는 대량의 데이터를 처리할 때 메모리 사용량이 증가하고, 성능 저하를 초래할 수 있다. 그래서 여기서는 fetchJoin을 사용하지 않는다.
     */
    public Page<RecipeMainListResponseDto> getAllRecipeList(Long memberId, Pageable pageable, String sortType) {

        // 북마크 여부 서브쿼리
        JPQLQuery<Boolean> bookmarkSubQuery = JPAExpressions
                .select(bookmarkEntity.count().gt(0L))
                .from(bookmarkEntity)
                .where(bookmarkEntity.memberId.eq(memberId), bookmarkEntity.recipeEntity.id.eq(recipeEntity.id));

        // sort 이전 메인 쿼리 추출 (레시피 기본 정보 및 북마크 여부 조회)
        JPAQuery<RecipeMainListResponseDto> query = queryFactory
                .select(Projections.constructor(RecipeMainListResponseDto.class, //subCategory 주의가 필요 (일단 null로 들어가고 아래에서 데이터를 추가해줌)
                        recipeEntity.id,
                        recipeEntity.recipeName,
                        recipeEntity.nickname,
                        ExpressionUtils.as(bookmarkSubQuery, "isBookmarked")
                ))
                .from(recipeEntity)
                .where(recipeEntity.delYn.eq("N"));

        // 정렬 조건(sortType) 적용
        query = switch (sortType) {
            case "new" -> query.orderBy(recipeEntity.createDateTime.desc());
            case "old" -> query.orderBy(recipeEntity.createDateTime.asc());
//            case "viewH" -> query.orderBy(recipeEntity.viewCount.desc());
//            case "viewL" -> query.orderBy(recipeEntity.viewCount.asc());
            default -> query.orderBy(recipeEntity.createDateTime.desc()); // 기본 정렬 조건
        };

        // sort 이후 메인 쿼리 (레시피 기본 정보 및 북마크 여부 조회) 실행
        List<RecipeMainListResponseDto> resultList = query
                .offset(pageable.getOffset()) // 몇번째 페이지인지(page)
                .limit(pageable.getPageSize()) // 페이지당 보여질 개수(size)
                .fetch();

        // 전체 카운트 (결과가 null일 경우 0으로 세팅하여 NPE 방지)
        Long totalCount = Optional.ofNullable(queryFactory
                        .select(recipeEntity.count())
                        .from(recipeEntity)
                        .where(recipeEntity.delYn.eq("N")) // 삭제 여부는 항상 N인 것만 조회
                        .fetchOne())
                .orElse(0L);

        return new PageImpl<>(resultList, pageable, totalCount);
    }

    /**
     * 매게변수로 받은 recipeId들을 사용하여 연관된 서브 카테고리 값들을 List<Tuple> 형태로 반환한다.
     */
    public List<Tuple> findSubCategoriesForRecipe(List<Long> recipeIdList) {
        return queryFactory
                .select(recipeEntity.id.as("id"), subCategoryEntity.subCategoryNm.as("subCategoryNm"))
                .from(recipeEntity)
                .join(recipeCategoryMapEntity).on(recipeEntity.id.eq(recipeCategoryMapEntity.recipeEntity.id))
                .join(recipeCategoryMapEntity.subCategoryEntity, subCategoryEntity)
                .where(recipeEntity.id.in(recipeIdList), recipeEntity.delYn.eq("N"))
                .fetch();
    }

    /**
     * 매게변수로 받은 recipeId들을 사용하여 연관된 서브 카테고리 값들을 List<Tuple> 형태로 반환한다.
     */
    public List<Tuple> findSubCategoriesForRecipe(Long recipeId) {
        return queryFactory
                .select(recipeEntity.id.as("id"), subCategoryEntity.subCategoryNm.as("subCategoryNm"))
                .from(recipeEntity)
                .join(recipeCategoryMapEntity).on(recipeEntity.id.eq(recipeCategoryMapEntity.recipeEntity.id))
                .join(recipeCategoryMapEntity.subCategoryEntity, subCategoryEntity)
                .where(recipeEntity.id.eq(recipeId), recipeEntity.delYn.eq("N"))
                .fetch();
    }

    /**
     * 단건 레시피를 상세조회
     */
    public Optional<RecipeDetailViewDto> getRecipeDetailView(Long recipeId, Long currentMemberId) {

        // 북마크 여부 서브쿼리
        JPQLQuery<Boolean> bookmarkSubQuery = JPAExpressions
                .select(bookmarkEntity.count().gt(0L))
                .from(bookmarkEntity)
                .where(bookmarkEntity.memberId.eq(currentMemberId), bookmarkEntity.recipeEntity.id.eq(recipeEntity.id));

        // 레시피 상세조회
        return Optional.ofNullable(queryFactory
                .select(Projections.constructor(RecipeDetailViewDto.class,
                        recipeEntity.id,
                        recipeEntity.recipeName,
                        recipeEntity.nickname,
                        recipeEntity.recipeDesc,
                        ExpressionUtils.as(bookmarkSubQuery, "isBookmarked")
                ))
                .from(recipeEntity)
                .where(recipeEntity.id.eq(recipeId))
                .fetchOne());
    }

    /**
     * 레시피 업데이트
     * 이름, 내용, 재료, 해시태그를 업데이트 한다.
     */
    public Long updateRecipe(RecipeEntity recipeEntity) {
        QRecipeEntity qRecipe = QRecipeEntity.recipeEntity;

        return queryFactory.update(qRecipe)
                .where(qRecipe.id.eq(recipeEntity.getId()))
                .set(qRecipe.recipeName, recipeEntity.getRecipeName())
                .set(qRecipe.recipeDesc, recipeEntity.getRecipeDesc())
                .set(qRecipe.ingredient, recipeEntity.getIngredient())
                .set(qRecipe.hashtag, recipeEntity.getHashtag())
                .execute();
    }

    /**
     * 영양소 업데이트
     * 모든 정보를 한번에 업데이트 한다.
     */
    public Long updateNutritionalInfo(NutritionalInfoEntity nutritionalInfoEntity) {
        QNutritionalInfoEntity qNutritionalInfo = QNutritionalInfoEntity.nutritionalInfoEntity;

        return queryFactory.update(qNutritionalInfo)
                .where(qNutritionalInfo.id.eq(nutritionalInfoEntity.getId()))
                .set(qNutritionalInfo.carbohydrates, nutritionalInfoEntity.getCarbohydrates())
                .set(qNutritionalInfo.protein, nutritionalInfoEntity.getProtein())
                .set(qNutritionalInfo.fat, nutritionalInfoEntity.getFat())
                .set(qNutritionalInfo.vitamins, nutritionalInfoEntity.getVitamins())
                .set(qNutritionalInfo.minerals, nutritionalInfoEntity.getMinerals())
                .execute();
    }

}
