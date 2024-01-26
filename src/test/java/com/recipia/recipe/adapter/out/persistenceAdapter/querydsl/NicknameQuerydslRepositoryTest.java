package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.adapter.out.persistence.entity.NicknameEntity;
import com.recipia.recipe.adapter.out.persistenceAdapter.NicknameRepository;
import com.recipia.recipe.config.TotalTestSupport;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("[통합] 닉네임 querydsl 테스트")
class NicknameQuerydslRepositoryTest extends TotalTestSupport {

    @Autowired
    private NicknameQuerydslRepository sut;
    @Autowired
    private NicknameRepository nicknameRepository;
    @Autowired
    private EntityManager entityManager;

    @DisplayName("[happy] 닉네임이 성공적으로 업데이트된다.")
    @Test
    void updateNicknamesTest() {
        //given
        NicknameDto nicknameDto = new NicknameDto(1L, "새로운 닉네임");

        //when
        sut.updateNicknames(nicknameDto);

        //then
        NicknameEntity updatedNickname = nicknameRepository.findByMemberId(nicknameDto.memberId()).get();
        assertThat(updatedNickname.getNickname()).isEqualTo("새로운 닉네임");
    }

    @DisplayName("[happy] memberId에 해당하는 닉네임을 성공적으로 삭제한다.")
    @Test
    void deleteNickname() {
        // given
        Long memberId = 1L;
        // when
        sut.deleteNickname(memberId);
        // then
        Optional<NicknameEntity> optionalNicknameEntity = nicknameRepository.findByMemberId(memberId);
        assertTrue(optionalNicknameEntity.isEmpty());
    }

}