package com.recipia.recipe.adapter.in.listener.springevent;

import brave.Span;
import brave.Tracer;
import com.recipia.recipe.application.port.in.MemberWithdrawUseCase;
import com.recipia.recipe.application.port.in.MyPageUseCase;
import com.recipia.recipe.common.event.MemberWithdrawEvent;
import com.recipia.recipe.domain.MyPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 탈퇴 스프링 이벤트 리스너 클래스
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class MemberWithdrawListener {

    private final MyPageUseCase myPageUseCase;
    private final MemberWithdrawUseCase memberWithdrawUseCase;
    private final Tracer tracer;

    /**
     * 회원 탈퇴시 관련 레시피 데이터 전부 삭제하는 리스터
     */
    @Transactional
    @EventListener
    public void deleteMemberRecipe(MemberWithdrawEvent event) {
        Span span = tracer.nextSpan().name("[RECIPE] delete member's recipes").start();

        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            memberWithdrawUseCase.deleteRecipeByMemberId(event.memberId());
        }catch (Exception e) {
            // 에러 태그 추가
            span.tag("error", e.toString());

            // 에러 로깅
            log.error("delete member recipe error: ", e);
        } finally {
            span.finish();
        }



    }

}
