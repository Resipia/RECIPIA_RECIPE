package com.recipia.recipe.event.sqseventlistener;


import brave.Span;
import brave.Tracer;
import com.recipia.recipe.aws.AwsSqsListenerService;
import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.event.springevent.NicknameChangeEvent;
import com.recipia.recipe.feign.MemberFeignClient;
import com.recipia.recipe.feign.dto.NicknameDto;
import com.recipia.recipe.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        NicknameDto nicknameDto;
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(feignRequestSpan)) {
            nicknameDto = memberFeignClient.getNickname(memberId);
            // Feign 요청 후 응답 처리
            if (nicknameDto != null) {
                // NicknameDto 처리 로직
                List<Recipe> recipeList = recipeRepository.findRecipeByMemberIdAndDelYn(memberId, "N");
                if (!recipeList.isEmpty()) {
                    recipeList.forEach(recipe -> recipe.changeNickname(nicknameDto.nickname()));
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