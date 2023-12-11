package com.recipia.recipe.dto;

import com.recipia.recipe.hexagonal.adapter.out.persistence.RecipeFileEntity;

import java.time.LocalDateTime;

/**
 * DTO for {@link RecipeFileEntity}
 */
public record RecipeFileDto(
        Long id,
        Integer fileOrder,
        String filePath,
        String originFileName,
        String storedFileName,
        String fileExtension,
        Integer fileSize,
        String delYn,
        LocalDateTime createDateTime,
        LocalDateTime updateDateTime
) {

    /**
     * 전체 생성자 factory method 선전
     */
    public static RecipeFileDto of(Long id, Integer fileOrder, String filePath, String originFileName, String storedFileName, String fileExtension, Integer fileSize, String delYn, LocalDateTime createDateTime, LocalDateTime updateDateTime) {
        return new RecipeFileDto(id, fileOrder, filePath, originFileName, storedFileName, fileExtension, fileSize, delYn, createDateTime, updateDateTime);
    }

    /**
     * entity를 dto로 변환하는 factory method 선언
     */
    public RecipeFileDto fromEntity(RecipeFileEntity entity) {
        return of(
                entity.getId(),
                entity.getFileOrder(),
                entity.getFilePath(),
                entity.getOriginFileName(),
                entity.getStoredFileName(),
                entity.getFileExtension(),
                entity.getFileSize(),
                entity.getDelYn(),
                entity.getCreateDateTime(),
                entity.getUpdateDateTime()
        );

    }
}