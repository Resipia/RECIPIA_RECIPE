package com.recipia.recipe.adapter.out.persistenceAdapter.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.recipia.recipe.adapter.out.persistence.entity.QNicknameEntity.nicknameEntity;

@RequiredArgsConstructor
@Repository
public class NicknameQuerydslRepository {

    private final JPAQueryFactory queryFactory;


    /**
     * [UPDATE] 유저가 변경한 닉네임을 닉네임 엔티티에서 변경시켜준다.
     */
    public Long updateNicknames(NicknameDto nicknameDto) {
        return queryFactory
                .update(nicknameEntity)
                .set(nicknameEntity.nickname, nicknameDto.nickname())
                .where(nicknameEntity.memberId.eq(nicknameDto.memberId()))
                .execute();
    }

    /**
     * [DELETE] memberId에 해당하는 닉네임을 삭제한다.
     */
    public void deleteNickname(Long memberId) {
        queryFactory
                .delete(nicknameEntity)
                .where(nicknameEntity.memberId.eq(memberId))
                .execute();
    }
}
