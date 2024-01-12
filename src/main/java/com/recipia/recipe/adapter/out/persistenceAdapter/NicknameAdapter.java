package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.adapter.out.persistenceAdapter.querydsl.NicknameQuerydslRepository;
import com.recipia.recipe.application.port.out.NicknamePort;
import com.recipia.recipe.common.exception.ErrorCode;
import com.recipia.recipe.common.exception.RecipeApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 외부(DB)와의 연결을 관리한다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class NicknameAdapter implements NicknamePort {

    private final NicknameQuerydslRepository nicknameQuerydslRepository;

    /**
     * [UPDATE] - 닉네임 테이블의 닉네임 컬럼 변경
     * MSA 프로젝트라서 레시피 DB에는 닉네임 테이블이 존재한다.
     * 닉네임 테이블에는 [memberId, nickname] 컬럼이 존재한다.
     * 유저가 닉네임을 변경하면 SNS로 이벤트가 발행되고 SQSListener가 동작해서 이 메서드가 호출된다.
     * 여기서는 memberId로 유저의 닉네임 컬럼을 변경한다.
     */
    @Override
    public Long updateNicknames(NicknameDto nicknameDto) {
        Long updateCount = nicknameQuerydslRepository.updateNicknames(nicknameDto);

        if (updateCount <= 0) {
            throw new RecipeApplicationException(ErrorCode.USER_NOT_FOUND);
        }
        log.info("Updated {} nickname entity with new nickname '{}' for memberId {}", updateCount, nicknameDto.nickname(), nicknameDto.memberId());
        return updateCount;
    }
}
