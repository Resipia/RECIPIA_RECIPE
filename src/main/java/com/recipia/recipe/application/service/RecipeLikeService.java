package com.recipia.recipe.application.service;

import com.recipia.recipe.application.port.in.RecipeLikeUseCase;
import com.recipia.recipe.application.port.out.RecipeLikePort;
import com.recipia.recipe.application.port.out.RedisPort;
import com.recipia.recipe.domain.RecipeLike;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 좋아요 서비스
 */
@Transactional
@RequiredArgsConstructor
@Service
public class RecipeLikeService implements RecipeLikeUseCase {

    private final RedisPort redisPort;
    private final RecipeLikePort recipeLikePort;

    /**
     * [CREATE/DELETE]
     * RDB에 좋아요를 기록하고 Redis 카운트를 증가시킨다.
     */
    public Long recipeLikeProcess(RecipeLike domain) {
        Long recipeLikeId = domain.getId();

        if (recipeLikeId == null) {
            // 좋아요 저장 및 레디스 카운트 증가
            Long savedId = recipeLikePort.saveLike(domain);
            redisPort.incrementLikeCount(domain.getRecipe().getId());
            return savedId;
        } else {
            // 좋아요를 삭제 후 카운트 감소
            recipeLikePort.deleteRecipeLike(domain);
            redisPort.decreaseLikeCount(domain.getRecipe().getId());
            return 0L;
        }
    }

}
