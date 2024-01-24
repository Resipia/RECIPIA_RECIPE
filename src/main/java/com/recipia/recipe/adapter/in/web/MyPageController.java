package com.recipia.recipe.adapter.in.web;

import com.recipia.recipe.adapter.in.web.dto.response.ResponseDto;
import com.recipia.recipe.application.port.in.MyPageUseCase;
import com.recipia.recipe.common.utils.SecurityUtil;
import com.recipia.recipe.domain.MyPage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 마이페이지 컨트롤러
 */
@RequestMapping("/recipe")
@RequiredArgsConstructor
@RestController
public class MyPageController {

    private final MyPageUseCase myPageUseCase;
    private final SecurityUtil securityUtil;

    /**
     * 마이페이지에서 내가 작성한 레시피 갯수 가져오기
     */
    @PostMapping("/mypage/recipeCnt")
    public ResponseEntity<ResponseDto<Long>> getRecipeCount() {
        Long memberId = securityUtil.getCurrentMemberId();
        MyPage myPage = myPageUseCase.getRecipeCount(memberId);
        return ResponseEntity.ok(
                ResponseDto.success(myPage.getRecipeCount())
        );
    }

}
