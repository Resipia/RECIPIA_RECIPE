package com.recipia.recipe.hexagonal.adapter.in.listener.springevent;


import brave.Span;
import brave.Tracer;
import com.recipia.recipe.hexagonal.adapter.out.persistence.RecipeEntity;
import com.recipia.recipe.hexagonal.common.event.NicknameChangeEvent;
import com.recipia.recipe.hexagonal.adapter.out.feign.MemberFeignClient;
import com.recipia.recipe.hexagonal.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.hexagonal.adapter.out.persistenceAdapter.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SqsListenerService에서 Feign 요청을 보내는 이벤트를 발행하면 동작
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RequestFeignListener {

    private final MemberFeignClient memberFeignClient;
    private final RecipeRepository recipeRepository;
    private final Tracer tracer;

    /**
     * Feign 클라이언트로 Member서버에 변경된 닉네임을 요청하는 리스너
     */
    @Transactional
    @EventListener
    public void requestMemberChangedNickname(NicknameChangeEvent event) {
        Span feignRequestSpan = tracer.nextSpan().name("[RECIPE] /feign/member/getNickname request").start();
        Long memberId = event.memberId();

        try (Tracer.SpanInScope ws = tracer.withSpanInScope(feignRequestSpan)) {
            NicknameDto nicknameDto = memberFeignClient.getNickname(memberId);

            // Feign 요청 후 응답 처리
            if (nicknameDto != null) {
                // NicknameDto 처리 로직
                List<RecipeEntity> recipeEntityList = recipeRepository.findRecipeByMemberIdAndDelYn(memberId, "N");
                if (!recipeEntityList.isEmpty()) {
                    recipeEntityList.forEach(recipe -> recipe.changeNickname(nicknameDto.nickname()));
                }
            }
        } catch (Exception e) {
            // 에러 태그 추가
            feignRequestSpan.tag("error", e.toString());

            // 에러 로깅
            log.error("Feign request error: ", e);
        } finally {
            feignRequestSpan.finish();
        }
    }

}