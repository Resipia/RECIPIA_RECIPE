package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.recipia.recipe.adapter.in.web.dto.request.SubCategoryDto;
import com.recipia.recipe.adapter.in.web.dto.response.RecipeListResponseDto;
import com.recipia.recipe.adapter.out.persistence.entity.NutritionalInfoEntity;
import com.recipia.recipe.adapter.out.persistence.entity.QNutritionalInfoEntity;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import com.recipia.recipe.domain.Recipe;
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
import static com.recipia.recipe.adapter.out.persistence.entity.QRecipeLikeCntEntity.recipeLikeCntEntity;
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
    public Page<RecipeListResponseDto> getAllRecipeList(Long memberId, Pageable pageable, String sortType, List<Long> subCategoryList) {

        // 북마크 id 가져오는 서브쿼리
        JPQLQuery<Long> bookmarkSubQuery = JPAExpressions
                .select(bookmarkEntity.id)
                .from(bookmarkEntity)
                .where(bookmarkEntity.memberId.eq(memberId), bookmarkEntity.recipeEntity.id.eq(recipeEntity.id));

        // 닉네임 엔티티에서 닉네임 조회 서브쿼리
        JPQLQuery<String> nicknameSubQuery = JPAExpressions
                .select(nicknameEntity.nickname)
                .from(nicknameEntity)
                .where(nicknameEntity.memberId.eq(recipeEntity.memberId));

        // where 조건 생성
        BooleanExpression whereCondition = recipeEntity.delYn.eq("N");      // 삭제 여부는 필수
        // 서브 카테고리는 옵션값
        if (subCategoryList != null && !subCategoryList.isEmpty()) {
            whereCondition = whereCondition.and(recipeEntity.id.in(
                    JPAExpressions.select(recipeCategoryMapEntity.recipeEntity.id)
                            .from(recipeCategoryMapEntity)
                            .where(recipeCategoryMapEntity.subCategoryEntity.id.in(subCategoryList))
            ));
        }

        // sort 이전 메인 쿼리 추출 (레시피 기본 정보 및 북마크 여부 조회)
        JPAQuery<RecipeListResponseDto> query = queryFactory
                .select(Projections.fields(RecipeListResponseDto.class, //subCategory 주의가 필요 (일단 null로 들어가고 아래에서 데이터를 추가해줌)
                        recipeEntity.id,
                        recipeEntity.recipeName,
                        ExpressionUtils.as(nicknameSubQuery, "nickname"),
                        ExpressionUtils.as(bookmarkSubQuery, "bookmarkId"),
                        ExpressionUtils.as(JPAExpressions
                                .select(recipeFileEntity.storedFilePath)
                                .from(recipeFileEntity)
                                .where(recipeFileEntity.id.eq(
                                        JPAExpressions
                                                .select(recipeFileEntity.id.min())
                                                .from(recipeFileEntity)
                                                .where(recipeFileEntity.recipeEntity.id.eq(recipeEntity.id), recipeFileEntity.delYn.eq("N"))
                                )), "thumbnailFullPath")
                ))
                .from(recipeEntity)
                .where(whereCondition);

        // 정렬 조건(sortType) 적용
        query = switch (sortType) {
            case "new" -> query.orderBy(recipeEntity.createDateTime.desc());
            case "old" -> query.orderBy(recipeEntity.createDateTime.asc());
//            case "viewH" -> query.orderBy(recipeEntity.viewCount.desc());
//            case "viewL" -> query.orderBy(recipeEntity.viewCount.asc());
            default -> query.orderBy(recipeEntity.createDateTime.desc()); // 기본 정렬 조건
        };

        // sort 이후 메인 쿼리 (레시피 기본 정보 및 북마크 여부 조회) 실행
        List<RecipeListResponseDto> resultList = query
                .offset(pageable.getOffset()) // 몇번째 페이지인지(page)
                .limit(pageable.getPageSize()) // 페이지당 보여질 개수(size)
                .fetch();

        // 메인 쿼리 종료 후, 레시피와 맵핑된 서브 카테고리 이름 조회 쿼리 실행 후 세팅
        resultList.forEach(dto -> {
            List<String> subCategories = queryFactory
                    .select(recipeCategoryMapEntity.subCategoryEntity.subCategoryNm)
                    .from(recipeCategoryMapEntity)
                    .where(recipeCategoryMapEntity.recipeEntity.id.eq(dto.getId()))
                    .fetch();
            dto.setSubCategoryList(subCategories);
        });

        // 전체 카운트 (결과가 null일 경우 0으로 세팅하여 NPE 방지)
        Long totalCount = Optional.ofNullable(queryFactory
                        .select(recipeEntity.count())
                        .from(recipeEntity)
                        .where(whereCondition)
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
    public Optional<Recipe> getRecipeDetailView(Long recipeId, Long currentMemberId) {

        // 북마크 id 서브쿼리
        JPQLQuery<Long> bookmarkSubQuery = JPAExpressions
                .select(bookmarkEntity.id)
                .from(bookmarkEntity)
                .where(bookmarkEntity.memberId.eq(currentMemberId), bookmarkEntity.recipeEntity.id.eq(recipeEntity.id));

        // 닉네임 엔티티에서 닉네임 조회 서브쿼리
        JPQLQuery<String> nicknameSubQuery = JPAExpressions
                .select(nicknameEntity.nickname)
                .from(nicknameEntity)
                .where(nicknameEntity.memberId.eq(recipeEntity.memberId));

        // 좋아요 id 서브쿼리
        JPQLQuery<Long> recipeLikeSubQuery = JPAExpressions
                .select(recipeLikeEntity.id)
                .from(recipeLikeEntity)
                .where(recipeLikeEntity.memberId.eq(currentMemberId), recipeEntity.id.eq(recipeLikeEntity.recipeEntity.id));

        // 레시피 상세조회
        return Optional.ofNullable(queryFactory
                .select(Projections.fields(Recipe.class,
                        recipeEntity.id,
                        recipeEntity.memberId,
                        recipeEntity.recipeName,
                        recipeEntity.recipeDesc,
                        recipeEntity.timeTaken,
                        recipeEntity.ingredient,
                        recipeEntity.hashtag,
                        ExpressionUtils.as(nicknameSubQuery, "nickname"),
                        ExpressionUtils.as(bookmarkSubQuery, "bookmarkId"),
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
     * 레시피를 soft delete 처리한다.
     */
    public Long softDeleteRecipeByRecipeId(Long recipeId) {
        return queryFactory.update(recipeEntity)
                .where(recipeEntity.id.eq(recipeId))
                .set(recipeEntity.delYn, "Y")
                .execute();
    }

    /**
     * 레시피 좋아요 개수를 저장하는 테이블에 내용을 업데이트 한다.
     */
    public Long updateLikesInDatabase(Long recipeId, Integer likeCount) {
        return queryFactory.update(recipeLikeCntEntity)
                .where(recipeLikeCntEntity.id.eq(recipeId))
                .set(recipeLikeCntEntity.likeCount, likeCount)
                .execute();
    }

    /**
     * 유저가 삭제 시도한 파일을 soft delete 처리한다.
     */
    public Long softDeleteRecipeFile(Recipe domain, List<Integer> deleteFileOrder) {
        return queryFactory.update(recipeFileEntity)
                .where(
                        recipeFileEntity.recipeEntity.id.eq(domain.getId()),
                        recipeFileEntity.fileOrder.in(deleteFileOrder)
                )
                .set(recipeFileEntity.delYn, "Y")
                .execute();
    }

    /**
     * [READ] targetMember가 작성한 레시피의 id 목록을 반환한다.
     */
    public List<Long> findTargetMemberRecipeIds(Long targetMemberId) {
        return queryFactory.select(recipeEntity.id)
                .from(recipeEntity)
                .where(recipeEntity.memberId.eq(targetMemberId), recipeEntity.delYn.eq("N"))
                .fetch();

    }

    /**
     * [READ] highRecipeIds에 해당하는 레시피 정보를 목록형으로 가져온다.
     */
    public List<RecipeListResponseDto> getTargetMemberHighRecipeList(Long targetMemberId, List<Long> highRecipeIds) {

        // 북마크 id 가져오는 서브쿼리
        JPQLQuery<Long> bookmarkSubQuery = JPAExpressions
                .select(bookmarkEntity.id)
                .from(bookmarkEntity)
                .where(bookmarkEntity.memberId.eq(targetMemberId), bookmarkEntity.recipeEntity.id.eq(recipeEntity.id));

        // 닉네임 엔티티에서 닉네임 조회 서브쿼리
        JPQLQuery<String> nicknameSubQuery = JPAExpressions
                .select(nicknameEntity.nickname)
                .from(nicknameEntity)
                .where(nicknameEntity.memberId.eq(targetMemberId));

        // where 조건 생성
        // 삭제 안된 레시피
        // 내가 작성한 레시피
        BooleanExpression whereCondition = recipeEntity.delYn.eq("N").and(recipeEntity.id.in(highRecipeIds));

        // 메인 쿼리 추출 (레시피 기본 정보 및 북마크 여부 조회)
        List<RecipeListResponseDto> resultList = queryFactory
                .select(Projections.fields(RecipeListResponseDto.class,
                        recipeEntity.id,
                        recipeEntity.recipeName,
                        ExpressionUtils.as(nicknameSubQuery, "nickname"),
                        ExpressionUtils.as(bookmarkSubQuery, "bookmarkId"),
                        ExpressionUtils.as(JPAExpressions
                                .select(recipeFileEntity.storedFilePath)
                                .from(recipeFileEntity)
                                .where(recipeFileEntity.id.eq(
                                        JPAExpressions
                                                .select(recipeFileEntity.id.min())
                                                .from(recipeFileEntity)
                                                .where(recipeFileEntity.recipeEntity.id.eq(recipeEntity.id), recipeFileEntity.delYn.eq("N"))
                                )), "thumbnailFullPath")))
                .from(recipeEntity)
                .where(whereCondition)
                .fetch();


        // 메인 쿼리 종료 후, 레시피와 맵핑된 서브 카테고리 이름 조회 쿼리 실행 후 세팅
        resultList.forEach(dto -> {
            List<String> subCategories = queryFactory
                    .select(recipeCategoryMapEntity.subCategoryEntity.subCategoryNm)
                    .from(recipeCategoryMapEntity)
                    .where(recipeCategoryMapEntity.recipeEntity.id.eq(dto.getId()))
                    .fetch();
            dto.setSubCategoryList(subCategories);
        });

        return resultList;
    }

    /**
     * [READ] targetMember가 작성한 레시피 목록을 Page 객체로 가져온다.
     */
    public Page<RecipeListResponseDto> getTargetRecipeList(Long targetMemberId, Pageable pageable, String sortType) {
        // 북마크 id 가져오는 서브쿼리
        JPQLQuery<Long> bookmarkSubQuery = JPAExpressions
                .select(bookmarkEntity.id)
                .from(bookmarkEntity)
                .where(bookmarkEntity.memberId.eq(targetMemberId), bookmarkEntity.recipeEntity.id.eq(recipeEntity.id));

        // 닉네임 엔티티에서 닉네임 조회 서브쿼리
        JPQLQuery<String> nicknameSubQuery = JPAExpressions
                .select(nicknameEntity.nickname)
                .from(nicknameEntity)
                .where(nicknameEntity.memberId.eq(recipeEntity.memberId));

        // sort 이전 메인 쿼리 추출 (레시피 기본 정보 및 북마크 여부 조회)
        JPAQuery<RecipeListResponseDto> query = queryFactory
                .select(Projections.fields(RecipeListResponseDto.class,
                        recipeEntity.id,
                        recipeEntity.recipeName,
                        ExpressionUtils.as(nicknameSubQuery, "nickname"),
                        ExpressionUtils.as(bookmarkSubQuery, "bookmarkId"),
                        ExpressionUtils.as(JPAExpressions
                                .select(recipeFileEntity.storedFilePath)
                                .from(recipeFileEntity)
                                .where(recipeFileEntity.id.eq(
                                        JPAExpressions
                                                .select(recipeFileEntity.id.min())
                                                .from(recipeFileEntity)
                                                .where(recipeFileEntity.recipeEntity.id.eq(recipeEntity.id), recipeFileEntity.delYn.eq("N"))
                                )), "thumbnailFullPath")
                ))
                .from(recipeEntity)
                .where(recipeEntity.delYn.eq("N"), recipeEntity.memberId.eq(targetMemberId));

        // 정렬 조건(sortType) 적용
        query = switch (sortType) {
            case "new" -> query.orderBy(recipeEntity.createDateTime.desc());
            case "old" -> query.orderBy(recipeEntity.createDateTime.asc());
            default -> query.orderBy(recipeEntity.createDateTime.desc()); // 기본 정렬 조건
        };

        // sort 이후 메인 쿼리 (레시피 기본 정보 및 북마크 여부 조회) 실행
        List<RecipeListResponseDto> resultList = query
                .offset(pageable.getOffset()) // 몇번째 페이지인지(page)
                .limit(pageable.getPageSize()) // 페이지당 보여질 개수(size)
                .fetch();

        // 메인 쿼리 종료 후, 레시피와 맵핑된 서브 카테고리 이름 조회 쿼리 실행 후 세팅
        resultList.forEach(dto -> {
            List<String> subCategories = queryFactory
                    .select(recipeCategoryMapEntity.subCategoryEntity.subCategoryNm)
                    .from(recipeCategoryMapEntity)
                    .where(recipeCategoryMapEntity.recipeEntity.id.eq(dto.getId()))
                    .fetch();
            dto.setSubCategoryList(subCategories);
        });

        // 전체 카운트 (결과가 null일 경우 0으로 세팅하여 NPE 방지)
        Long totalCount = Optional.ofNullable(queryFactory
                        .select(recipeEntity.count())
                        .from(recipeEntity)
                        .where(recipeEntity.delYn.eq("N"), recipeEntity.memberId.eq(targetMemberId))
                        .fetchOne())
                .orElse(0L);

        return new PageImpl<>(resultList, pageable, totalCount);
    }

    /**
     * [READ] 내가 북마크한 레시피 목록을 북마크 등록된 최신순으로 가져온다.
     */
    public Page<RecipeListResponseDto> getAllMyBookmarkList(Long currentMemberId, Pageable pageable) {
        // 북마크 id 가져오는 서브쿼리
        JPQLQuery<Long> bookmarkSubQuery = JPAExpressions
                .select(bookmarkEntity.id)
                .from(bookmarkEntity)
                .where(bookmarkEntity.memberId.eq(currentMemberId), bookmarkEntity.recipeEntity.id.eq(recipeEntity.id));

        // 닉네임 엔티티에서 닉네임 조회 서브쿼리
        JPQLQuery<String> nicknameSubQuery = JPAExpressions
                .select(nicknameEntity.nickname)
                .from(nicknameEntity)
                .where(nicknameEntity.memberId.eq(recipeEntity.memberId));

        // 메인 쿼리
        JPAQuery<RecipeListResponseDto> query = queryFactory
                .select(Projections.fields(RecipeListResponseDto.class,
                        recipeEntity.id,
                        recipeEntity.recipeName,
                        ExpressionUtils.as(nicknameSubQuery, "nickname"),
                        ExpressionUtils.as(bookmarkSubQuery, "bookmarkId"),
                        ExpressionUtils.as(JPAExpressions
                                .select(recipeFileEntity.storedFilePath)
                                .from(recipeFileEntity)
                                .where(recipeFileEntity.id.eq(
                                        JPAExpressions
                                                .select(recipeFileEntity.id.min())
                                                .from(recipeFileEntity)
                                                .where(recipeFileEntity.recipeEntity.id.eq(recipeEntity.id), recipeFileEntity.delYn.eq("N"))
                                )), "thumbnailFullPath")
                ))
                .from(bookmarkEntity)
                .join(bookmarkEntity.recipeEntity, recipeEntity)
                .where(bookmarkEntity.memberId.eq(currentMemberId)
                        .and(recipeEntity.delYn.eq("N")))
                .orderBy(bookmarkEntity.createDateTime.desc());

        // 페이징 적용
        List<RecipeListResponseDto> resultList = query
                .offset(pageable.getOffset()) // 몇번째 페이지인지(page)
                .limit(pageable.getPageSize()) // 페이지당 보여질 개수(size)
                .fetch();

        // 메인 쿼리 종료 후, 레시피와 맵핑된 서브 카테고리 이름 조회 쿼리 실행 후 세팅
        resultList.forEach(dto -> {
            List<String> subCategories = queryFactory
                    .select(recipeCategoryMapEntity.subCategoryEntity.subCategoryNm)
                    .from(recipeCategoryMapEntity)
                    .where(recipeCategoryMapEntity.recipeEntity.id.eq(dto.getId()))
                    .fetch();
            dto.setSubCategoryList(subCategories);
        });

        // 전체 카운트 (결과가 null일 경우 0으로 세팅하여 NPE 방지)
        Long totalCount = Optional.ofNullable(queryFactory
                        .select(bookmarkEntity.count())
                        .from(bookmarkEntity)
                        .where(bookmarkEntity.memberId.eq(currentMemberId))
                        .fetchOne())
                .orElse(0L);

        return new PageImpl<>(resultList, pageable, totalCount);
    }

    /**
     * 내가 좋아요한 레시피 목록을 가져온다.
     */
    public Page<RecipeListResponseDto> getAllMyLikeList(Long currentMemberId, Pageable pageable) {

        // 북마크 id 가져오는 서브쿼리
        JPQLQuery<Long> bookmarkSubQuery = JPAExpressions
                .select(bookmarkEntity.id)
                .from(bookmarkEntity)
                .where(bookmarkEntity.memberId.eq(currentMemberId), bookmarkEntity.recipeEntity.id.eq(recipeEntity.id));

        // 닉네임 엔티티에서 닉네임 조회 서브쿼리
        JPQLQuery<String> nicknameSubQuery = JPAExpressions
                .select(nicknameEntity.nickname)
                .from(nicknameEntity)
                .where(nicknameEntity.memberId.eq(recipeEntity.memberId));

        // 메인 쿼리
        JPAQuery<RecipeListResponseDto> query = queryFactory
                .select(Projections.fields(RecipeListResponseDto.class,
                        recipeEntity.id,
                        recipeEntity.recipeName,
                        ExpressionUtils.as(nicknameSubQuery, "nickname"),
                        ExpressionUtils.as(bookmarkSubQuery, "bookmarkId"),
                        ExpressionUtils.as(JPAExpressions
                                .select(recipeFileEntity.storedFilePath)
                                .from(recipeFileEntity)
                                .where(recipeFileEntity.id.eq(
                                        JPAExpressions
                                                .select(recipeFileEntity.id.min())
                                                .from(recipeFileEntity)
                                                .where(recipeFileEntity.recipeEntity.id.eq(recipeEntity.id), recipeFileEntity.delYn.eq("N"))
                                )), "thumbnailFullPath")
                ))
                .from(recipeLikeEntity)
                .join(recipeLikeEntity.recipeEntity, recipeEntity)
                .where(recipeLikeEntity.memberId.eq(currentMemberId)
                        .and(recipeEntity.delYn.eq("N")))
                .orderBy(recipeLikeEntity.createDateTime.desc());

        // 페이징 적용
        List<RecipeListResponseDto> resultList = query
                .offset(pageable.getOffset()) // 몇번째 페이지인지(page)
                .limit(pageable.getPageSize()) // 페이지당 보여질 개수(size)
                .fetch();

        // 메인 쿼리 종료 후, 레시피와 맵핑된 서브 카테고리 이름 조회 쿼리 실행 후 세팅
        resultList.forEach(dto -> {
            List<String> subCategories = queryFactory
                    .select(recipeCategoryMapEntity.subCategoryEntity.subCategoryNm)
                    .from(recipeCategoryMapEntity)
                    .where(recipeCategoryMapEntity.recipeEntity.id.eq(dto.getId()))
                    .fetch();
            dto.setSubCategoryList(subCategories);
        });

        // 전체 카운트 (결과가 null일 경우 0으로 세팅하여 NPE 방지)
        Long totalCount = Optional.ofNullable(queryFactory
                        .select(recipeLikeEntity.count())
                        .from(recipeLikeEntity)
                        .where(recipeLikeEntity.memberId.eq(currentMemberId))
                        .fetchOne())
                .orElse(0L);

        return new PageImpl<>(resultList, pageable, totalCount);

    }

    /**
     * [DELETE] recipeId에 해당하는 레시피 파일을 soft delete 처리한다.
     */
    public Long softDeleteRecipeFileByRecipeId(Long recipeId) {
        return queryFactory.update(recipeFileEntity)
                .where(recipeFileEntity.recipeEntity.id.eq(recipeId))
                .set(recipeFileEntity.delYn, "Y")
                .execute();
    }
}
