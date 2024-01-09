package com.recipia.recipe.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SubCategory {

    private Long id;
    private String subCategoryNm;

    @Builder
    private SubCategory(Long id, String subCategoryNm) {
        this.id = id;
        this.subCategoryNm = subCategoryNm;
    }

    public static SubCategory of(Long id, String subCategoryNm) {
        return new SubCategory(id, subCategoryNm);
    }

    public static SubCategory of(String subCategoryNm) {
        return new SubCategory(null, subCategoryNm);
    }

    public static SubCategory of(Long id) {
        return new SubCategory(id, null);
    }


}

