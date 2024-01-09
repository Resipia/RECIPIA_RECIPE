package com.recipia.recipe.adapter.in.web.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubCategoryDto {

    private Long id;
    private String subCategoryNm;

    /**
     * querydsl에서 projection으로 생성자를 만들때 private면 오류가 발생해서 public으로 해준다.
     */
    @Builder
    public SubCategoryDto(Long id, String subCategoryNm) {
        this.id = id;
        this.subCategoryNm = subCategoryNm;
    }

    public static SubCategoryDto of(Long id, String subCategoryNm) {
        return new SubCategoryDto(id, subCategoryNm);
    }

    public static SubCategoryDto of(Long id) {
        return new SubCategoryDto(id,null);
    }


}
