package com.recipia.recipe.adapter.in.web.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 메인 페이지의 레시피 조회를 할때 데이터를 전달할 객체
 */
@NoArgsConstructor
@Data
public class RecipeListResponseDto {
    private Long id;
    private String recipeName;                  // 제목
    private String nickname;                    // 닉네임
    private Long bookmarkId;                    // 북마크 id
    private List<String> subCategoryList;       // 서브 카테고리 목록
    private String thumbnailFullPath;           // 썸네일 이미지 저장경로
    private String thumbnailPreUrl;             // 썸네일 이미지 pre-signed-url
    private String createDate;                  // 레시피 생성 날짜

    // querydsl select문에서 Projections.constructor 사용하기 위함
    public RecipeListResponseDto(Long id, String recipeName, String nickname, Long bookmarkId, String thumbnailFullPath, String createDate) {
        this.id = id;
        this.recipeName = recipeName;
        this.nickname = nickname;
        this.bookmarkId = bookmarkId;
        this.thumbnailFullPath = thumbnailFullPath;
        this.createDate = createDate;
    }

    // 모든 필드를 매개변수로 받는 생성자
    private RecipeListResponseDto(Long id, String recipeName, String nickname, Long bookmarkId, List<String> subCategoryList, String thumbnailFullPath, String thumbnailPreUrl, String createDate) {
        this.id = id;
        this.recipeName = recipeName;
        this.nickname = nickname;
        this.bookmarkId = bookmarkId;
        this.subCategoryList = subCategoryList;
        this.thumbnailFullPath = thumbnailFullPath;
        this.thumbnailPreUrl = thumbnailPreUrl;
        this.createDate = createDate;
    }


    public static RecipeListResponseDto of(Long id, String recipeName, String nickname, Long bookmarkId, String thumbnailFullPath, String thumbnailPreUrl, String createDate) {
        return new RecipeListResponseDto(id, recipeName, nickname, bookmarkId, null, thumbnailFullPath, thumbnailPreUrl, createDate);
    }

    public static RecipeListResponseDto of(Long id, String recipeName, String nickname, Long bookmarkId, List<String> subCategoryList, String thumbnailFullPath, String thumbnailPreUrl, String createDate) {
        return new RecipeListResponseDto(id, recipeName, nickname, bookmarkId, subCategoryList, thumbnailFullPath, thumbnailPreUrl, createDate);
    }

    public static RecipeListResponseDto of(String recipeName, String nickname, Long bookmarkId, String thumbnailFullPath, String thumbnailPreUrl, String createDate) {
        return new RecipeListResponseDto(null, recipeName, nickname, bookmarkId, null, thumbnailFullPath, thumbnailPreUrl, createDate);
    }

}
