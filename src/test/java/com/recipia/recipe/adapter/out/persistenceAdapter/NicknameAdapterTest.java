package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.adapter.out.persistenceAdapter.querydsl.NicknameQuerydslRepository;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import com.recipia.recipe.config.TotalTestSupport;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[통합] 닉네임 Adapter 테스트")
class NicknameAdapterTest extends TotalTestSupport {

    @Autowired
    private NicknameAdapter sut;
    @Autowired
    private NicknameQuerydslRepository nicknameQuerydslRepository;
    @Autowired
    private NicknameRepository nicknameRepository;
    @Autowired
    private EntityManager entityManager;


    @DisplayName("[happy] 유저가 닉네임을 변경하면 닉네임 엔티티 내부의 유저 닉네임도 변경된다.")
    @Transactional
    @Test
    public void updateNicknames() {
        //given
        NicknameDto nicknameDto = NicknameDto.of(1L, "changedNickname");

        //when
        Long updatedCount = sut.updateNicknames(nicknameDto);

        //then
        String nickname = nicknameRepository.findByMemberId(nicknameDto.memberId()).get().getNickname();
        assertThat(nickname).isEqualTo(nicknameDto.nickname());
        assertThat(updatedCount).isNotNull();
        assertThat(updatedCount).isGreaterThan(0);
    }

    @DisplayName("[bad] 존재하지 않는 유저(memberId)가 닉네임 변경을 시도하면 예외가 발생한다.")
    @Test
    void updateNicknamesFail() {
        //given
        NicknameDto nicknameDto = NicknameDto.of(100L, "NotValidNickname");

        //when & then
        assertThatThrownBy(() -> sut.updateNicknames(nicknameDto))
                .isInstanceOf(RecipeApplicationException.class)
                .hasMessageContaining("유저를 찾을 수 없습니다.")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }


}