package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.config.TotalTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[통합] 마이페이지 Adapter 테스트")
class MyPageAdapterTest extends TotalTestSupport {

    @Autowired
    private MyPageAdapter sut;


    @DisplayName("[happy] 유효한 targetMemberId가 들어왔을때 이 사용자가 작성한 레시피 갯수를 반환한다.")
    @Test
    void getRecipeCountSuccess() {
        // given
        Long targetMemberId = 2L;
        // when
        Long myRecipeCount = sut.getTargetMemberIdRecipeCount(targetMemberId);
        // then
        assertThat(myRecipeCount).isEqualTo(0L);
    }


}