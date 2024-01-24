package com.recipia.recipe.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 마이페이지 도메인 객체
 */
@NoArgsConstructor
@Getter
public class MyPage {

    private Long recipeCount;

    private MyPage(Long recipeCount) {
        this.recipeCount = recipeCount;
    }

    public static MyPage of(Long recipeCount) {
        return new MyPage(recipeCount);
    }
}
