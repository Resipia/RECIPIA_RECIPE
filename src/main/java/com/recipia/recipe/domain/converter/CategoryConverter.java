package com.recipia.recipe.domain.converter;

import com.recipia.recipe.adapter.in.web.dto.request.SubCategoryDto;
import com.recipia.recipe.domain.SubCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CategoryConverter {

    /**
     * 서브 카테고리 dto를 domain 객체로 변환한다.
     */
    public SubCategory dtoToDomain(SubCategoryDto dto) {
        return SubCategory.of(dto.getId(), dto.getSubCategoryNm());
    }

    /**
     * 도메인을 dto로 변환한다.
     */
    public SubCategoryDto domainToDto(SubCategory domain) {
        return SubCategoryDto.of(domain.getId(), domain.getSubCategoryNm());
    }

}
