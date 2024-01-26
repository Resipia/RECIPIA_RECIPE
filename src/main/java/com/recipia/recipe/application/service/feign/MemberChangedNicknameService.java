package com.recipia.recipe.application.service.feign;

import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;
import com.recipia.recipe.application.port.in.NicknameUseCase;
import com.recipia.recipe.application.port.out.NicknamePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberChangedNicknameService implements NicknameUseCase {

    private final NicknamePort nicknamePort;

    /**
     * FeignClient로부터 받아온 변경된 닉네임을 닉네임 엔티티에 적용시키는 로직
     * nicknameDto는 Feign에서 받아온 응답이다.
     */
    @Override
    public Long updateNickname(NicknameDto nicknameDto) {
        return nicknamePort.updateNickname(nicknameDto);
    }

    /**
     * FeignClient로부터 받아온 변경된 닉네임을 닉네임 엔티티에 적용시키는 로직
     * nicknameDto는 Feign에서 받아온 응답이다.
     */
    @Override
    public Long saveNickname(NicknameDto nicknameDto) {
        return nicknamePort.saveNickname(nicknameDto);
    }


}
