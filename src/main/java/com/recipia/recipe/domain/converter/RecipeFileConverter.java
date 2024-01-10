package com.recipia.recipe.domain.converter;

import com.recipia.recipe.adapter.in.web.dto.request.RecipeFileDto;
import com.recipia.recipe.adapter.out.persistence.entity.RecipeFileEntity;
import com.recipia.recipe.domain.RecipeFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RecipeFileConverter {

    /**
     * 엔티티를 도메인 객체로 변환
     */
    public RecipeFile entityToDomain(RecipeFileEntity entity) {
        return RecipeFile.of(
                entity.getFileOrder(),
                entity.getStoredFilePath(),
                entity.getObjectUrl(),
                entity.getOriginFileNm(),
                entity.getStoredFileNm(),
                entity.getFileExtension(),
                entity.getFileSize(),
                entity.getDelYn()
        );
    }

    /**
     * 도메인 객체를 dto로 변환한다.
     */
    public RecipeFileDto domainToDto(RecipeFile domain) {
        return RecipeFileDto.of(
                domain.getFileOrder(),
                domain.getStoredFilePath(),
                domain.getObjectUrl(),
                domain.getOriginFileNm(),
                domain.getStoredFileNm(),
                domain.getFileExtension(),
                domain.getFileSize(),
                domain.getDelYn()
        );
    }
}
