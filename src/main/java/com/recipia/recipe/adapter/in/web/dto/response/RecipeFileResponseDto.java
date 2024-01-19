package com.recipia.recipe.adapter.in.web.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 레시피 상세보기에 담겨있는 레시피 파일 응답 dto
 */
@NoArgsConstructor
@Data
public class RecipeFileResponseDto {
    private Long id;                // 파일 id
    private Integer fileOrder;      // 파일 정렬 순서
    private String preUrl;          // s3에서 임시로 발급한 pre-signed-url

    private RecipeFileResponseDto(Long id, Integer fileOrder, String preUrl) {
        this.id = id;
        this.fileOrder = fileOrder;
        this.preUrl = preUrl;
    }

    public static RecipeFileResponseDto of(Long id, Integer fileOrder, String preUrl) {
        return new RecipeFileResponseDto(id, fileOrder, preUrl);
    }
}
