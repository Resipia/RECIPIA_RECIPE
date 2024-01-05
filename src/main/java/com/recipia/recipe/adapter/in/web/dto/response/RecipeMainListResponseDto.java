package com.recipia.recipe.adapter.in.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 메인 페이지의 레시피 조회를 할때 데이터를 전달할 객체
 */
@AllArgsConstructor
@Data
public class RecipeMainListResponseDto {
    private Long id;
    private String recipeName;
    private String nickname;
    private String subCategory;
    private boolean isBookmarked;
    //제목, 닉넴, 카테고리(list or string), 북마크 여부, 조회수, order by (조회수 높고낮음, 등록날짜 높고낮음)

}
