package com.recipia.recipe.hexagonal.adapter.out.feign;

import com.recipia.recipe.hexagonal.adapter.out.feign.dto.NicknameDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "member-service", url = "${feign.member_url}")
public interface MemberFeignClient {

    /**
     * 멤버 서버에서 닉네임 변경 이벤트가 정상적으로 발행되어서 Recipe 서버의 SQS가 이 Feign을 호출하면
     * 멤버 서버로부터 Id, Nickname 값을 받아온다.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/feign/member/getNickname")
    NicknameDto getNickname(@RequestParam(name = "memberId") Long memberId);

}
