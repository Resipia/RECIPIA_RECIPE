package com.recipia.recipe.adapter.in.listener.springevent;


import brave.Span;
import brave.Tracer;
import com.recipia.recipe.adapter.out.feign.MemberFeignClient;
import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.application.port.in.FeignClientUseCase;
import com.recipia.recipe.common.event.NicknameChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * SqsListenerService에서 Feign 요청을 보내는 스프링 이벤트가 발행되면 동작한다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RequestFeignListener {

    private final MemberFeignClient memberFeignClient;
    private final FeignClientUseCase feignClientUseCase;
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

            // Feign 요청으로 받은 dto가 존재하면 레시피 엔티티 내부의 유저 닉네임 변경
            if (nicknameDto != null) {
                feignClientUseCase.updateRecipesNicknames(nicknameDto);
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