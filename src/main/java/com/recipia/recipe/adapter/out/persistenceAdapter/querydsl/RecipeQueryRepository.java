package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.recipia.recipe.adapter.in.web.dto.request.SubCategoryDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeDetailViewResponseDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeMainListResponseDto;
import com.recipia.recipe.adapter.out.persistence.entity.NutritionalInfoEntity;
import com.recipia.recipe.adapter.out.persistence.entity.QNutritionalInfoEntity;
import com.recipia.recipe.adapter.out.persistence.entity.QRecipeEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.recipia.recipe.adapter.out.persistence.entity.QBookmarkEntity.bookmarkEntity;
import static com.recipia.recipe.adapter.out.persistence.entity.QNicknameEntity.nicknameEntity;
import static com.recipia.recipe.adapter.out.persistence.entity.QRecipeCategoryMapEntity.recipeCategoryMapEntity;
import static com.recipia.recipe.adapter.out.persistence.entity.QRecipeEntity.recipeEntity;
import static com.recipia.recipe.adapter.out.persistence.entity.QRecipeFileEntity.recipeFileEntity;
import static com.recipia.recipe.adapter.out.persistence.entity.QRecipeLikeEntity.recipeLikeEntity;


@RequiredArgsConstructor
@Repository
public class RecipeQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * [레시피 전체 목록 조회]
     * fetch join을 사용하면 관련된 엔티티들이 메모리에 모두 로드되어서, 데이터베이스 레벨에서의 페이징이 아닌 메모리 레벨에서의 페이징이 발생할 수 있다.
     * 이는 대량의 데이터를 처리할 때 메모리 사용량이 증가하고, 성능 저하를 초래할 수 있다. 그래서 여기서는 fetchJoin을 사용하지 않는다.
     */
    public Page<RecipeMainListResponseDto> getAllRecipeList(Long memberId, Pageable pageable, String sortType) {

        // 북마크 여부 서브쿼리
        JPQLQuery<Boolean> bookmarkSubQuery = JPAExpressions
                .select(bookmarkEntity.count().gt(0L))
                .from(bookmarkEntity)
                .where(bookmarkEntity.memberId.eq(memberId), bookmarkEntity.recipeEntity.id.eq(recipeEntity.id));

        // 닉네임 엔티티에서 닉네임 조회 서브쿼리
        JPQLQuery<String> nicknameSubQuery = JPAExpressions
                .select(nicknameEntity.nickname)
                .from(nicknameEntity)
                .where(nicknameEntity.memberId.eq(memberId));

        // sort 이전 메인 쿼리 추출 (레시피 기본 정보 및 북마크 여부 조회)
        JPAQuery<RecipeMainListResponseDto> query = queryFactory
                .select(Projections.constructor(RecipeMainListResponseDto.class, //subCategory 주의가 필요 (일단 null로 들어가고 아래에서 데이터를 추가해줌)
                        recipeEntity.id,
                        recipeEntity.recipeName,
                        ExpressionUtils.as(nicknameSubQuery, "nickname"),
                        ExpressionUtils.as(bookmarkSubQuery, "isBookmarked")
                ))
                .from(recipeEntity)
                .where(recipeEntity.delYn.eq("N")); // 삭제여부 검증 필수

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
     * 서브 카테고리 정보를 dto로 받아온다.
     * N+1을 방지하기 위해 대 카테고리의 정보는 받아오지 않는다.
     */
    public List<SubCategoryDto> findSubCategoryDtoListForRecipeId(Long recipeId) {
        return queryFactory
                .select(Projections.constructor(SubCategoryDto.class,
                        recipeCategoryMapEntity.subCategoryEntity.id,
                        recipeCategoryMapEntity.subCategoryEntity.subCategoryNm
                ))
                .from(recipeCategoryMapEntity)
                .where(recipeCategoryMapEntity.recipeEntity.id.eq(recipeId))
                .fetch();
    }

    /**
     * 레시피 단건에 대한 정보를 상세조회
     */
    public Optional<RecipeDetailViewResponseDto> getRecipeDetailView(Long recipeId, Long currentMemberId) {

        // 북마크 여부 서브쿼리
        JPQLQuery<Boolean> bookmarkSubQuery = JPAExpressions
                .select(bookmarkEntity.count().gt(0L))
                .from(bookmarkEntity)
                .where(bookmarkEntity.memberId.eq(currentMemberId), bookmarkEntity.recipeEntity.id.eq(recipeEntity.id));

        // 닉네임 엔티티에서 닉네임 조회 서브쿼리
        JPQLQuery<String> nicknameSubQuery = JPAExpressions
                .select(nicknameEntity.nickname)
                .from(nicknameEntity)
                .where(nicknameEntity.memberId.eq(currentMemberId));


        // todo: RecipeLikeEntity의 id값 보내기 (없으면 NuLL)
        JPQLQuery<Long> recipeLikeSubQuery = JPAExpressions
                .select(recipeLikeEntity.id)
                .from(recipeLikeEntity)
                .where(recipeEntity.id.eq(recipeLikeEntity.recipeEntity.id));

        // 레시피 상세조회
        return Optional.ofNullable(queryFactory
                .select(Projections.constructor(RecipeDetailViewResponseDto.class,
                        recipeEntity.id,
                        recipeEntity.recipeName,
                        recipeEntity.recipeDesc,
                        recipeEntity.timeTaken,
                        recipeEntity.ingredient,
                        recipeEntity.hashtag,
                        ExpressionUtils.as(nicknameSubQuery, "nickname"),
                        recipeEntity.delYn,
                        ExpressionUtils.as(bookmarkSubQuery, "isBookmarked"),
                        ExpressionUtils.as(recipeLikeSubQuery, "recipeLikeId")
                ))
                .from(recipeEntity)
                .where(recipeEntity.id.eq(recipeId), recipeEntity.delYn.eq("N"))
                .fetchOne());
    }

    /**
     * 레시피 업데이트
     * 이름, 내용, 재료, 해시태그를 업데이트 한다.
     */
    public Long updateRecipe(RecipeEntity entity) {

        return queryFactory.update(recipeEntity)
                .where(recipeEntity.id.eq(entity.getId()))
                .set(recipeEntity.recipeName, entity.getRecipeName())
                .set(recipeEntity.recipeDesc, entity.getRecipeDesc())
                .set(recipeEntity.ingredient, entity.getIngredient())
                .set(recipeEntity.hashtag, entity.getHashtag())
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

    /**
     * 레시피와 연관된 파일을 soft delete 처리한다.
     * 업데이트된 엔티티의 개수를 반환한다.
     */
    public Long softDeleteRecipeFilesByRecipeId(Long recipeId) {
        return queryFactory.update(recipeFileEntity)
                .where(recipeFileEntity.recipeEntity.id.eq(recipeId))
                .set(recipeFileEntity.delYn, "Y")
                .execute();
    }

    /**
     * 레시피를 soft delete 처리한다.
     */
    public Long softDeleteRecipeByRecipeId(Long recipeId) {
        return queryFactory.update(recipeEntity)
                .where(recipeEntity.id.eq(recipeId))
                .set(recipeEntity.delYn, "Y")
                .execute();
    }

    /**
     * 레시피 내부의 좋아요 개수를 업데이트 한다.
     */
    public Long updateLikesInDatabase(Long recipeId, Integer likeCount) {
        return queryFactory.update(recipeEntity)
                .where(recipeEntity.id.eq(recipeId))
                .set(recipeEntity.likeCount, likeCount)
                .execute();
    }

}
