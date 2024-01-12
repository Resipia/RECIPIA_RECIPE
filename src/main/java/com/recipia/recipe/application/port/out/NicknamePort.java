package com.recipia.recipe.application.port.out;

import com.recipia.recipe.adapter.out.feign.dto.NicknameDto;

public interface NicknamePort {
    Long updateNicknames(NicknameDto nicknameDto);

}
