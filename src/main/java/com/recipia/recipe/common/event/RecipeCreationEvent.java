package com.recipia.recipe.common.event;

import java.util.List;

/**
 * 사용자가 레시피를 저장하면 이 스프링 이벤트가 발행된다.
 * 동작: 해시태그와 재료를 MongoDB에 저장해서 추천검색에서 사용한다.
 * 김치, 밥, 콩, 감자 -> 이런식으로 데이터가 들어올텐데 이것을 각각의 구독자에서 처리해서 mongo에 저장하도록 한다.
 */
public record RecipeCreationEvent(
        String ingredients,
        String hashtags
) {
}
