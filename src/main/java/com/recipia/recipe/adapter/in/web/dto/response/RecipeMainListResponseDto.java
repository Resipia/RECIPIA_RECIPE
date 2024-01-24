package com.recipia.recipe.adapter.in.web.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 메인 페이지의 레시피 조회를 할때 데이터를 전달할 객체
 */
@NoArgsConstructor
@Data
public class RecipeMainListResponseDto {
    private Long id;
    private String recipeName;                  // 제목
    private String nickname;                    // 닉네임
    private Long bookmarkId;                    // 북마크 id
    private List<String> subCategoryList;       // 서브 카테고리 목록
    private String thumbnailFullPath;           // 썸네일 이미지 저장경로
    private String thumbnailPreUrl;             // 썸네일 이미지 pre-signed-url

    private RecipeMainListResponseDto(Long id, String recipeName, String nickname, Long bookmarkId, List<String> subCategoryList, String thumbnailFullPath, String thumbnailPreUrl) {
        this.id = id;
        this.recipeName = recipeName;
        this.nickname = nickname;
        this.bookmarkId = bookmarkId;
        this.subCategoryList = subCategoryList;
        this.thumbnailFullPath = thumbnailFullPath;
        this.thumbnailPreUrl = thumbnailPreUrl;
    }

//    public RecipeMainListResponseDto(Long id, String recipeName, String nickname, Long bookmarkId, String thumbnailFullPath, String thumbnailPreUrl) {
//        this.id = id;
//        this.recipeName = recipeName;
//        this.nickname = nickname;
//        this.bookmarkId = bookmarkId;
//        this.thumbnailFullPath = thumbnailFullPath;
//        this.thumbnailPreUrl = thumbnailPreUrl;
//    }

    public static RecipeMainListResponseDto of(Long id, String recipeName, String nickname, Long bookmarkId, String thumbnailFullPath, String thumbnailPreUrl) {
        return new RecipeMainListResponseDto(id, recipeName, nickname, bookmarkId, null, thumbnailFullPath, thumbnailPreUrl);
    }

    public static RecipeMainListResponseDto of(Long id, String recipeName, String nickname, Long bookmarkId, List<String> subCategoryList, String thumbnailFullPath, String thumbnailPreUrl) {
        return new RecipeMainListResponseDto(id, recipeName, nickname, bookmarkId, subCategoryList, thumbnailFullPath, thumbnailPreUrl);
    }

    public static RecipeMainListResponseDto of(String recipeName, String nickname, Long bookmarkId, String thumbnailFullPath, String thumbnailPreUrl) {
        return new RecipeMainListResponseDto(null, recipeName, nickname, bookmarkId, null, thumbnailFullPath, thumbnailPreUrl);
    }

}
