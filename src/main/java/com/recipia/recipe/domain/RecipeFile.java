package com.recipia.recipe.domain;

import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 레시피와 연관된 파일 도메인 객체
 */
@NoArgsConstructor
@Getter
@Setter
public class RecipeFile {

    private Long id;
    private RecipeEntity recipeEntity; // 어떤 레시피와 연관있는지
    private Integer file_order; // 파일 정렬 조건
    private String storedFilePath; // 저장된 파일 경로 url
    private String originFileNm;    // 원본 파일 이름
    private String storedFileNm;  // 저장된 파일 이름
    private String fileExtension; // 파일 확장자 (jpg, jpeg, png)
    private Integer fileSize; // 파일 크기
    private String delYn; // 삭제 여부

    @Builder
    private RecipeFile(Long id, RecipeEntity recipeEntity, Integer file_order, String storedFilePath, String originFileNm, String storedFileNm, String fileExtension, Integer fileSize, String delYn) {
        this.id = id;
        this.recipeEntity = recipeEntity;
        this.file_order = file_order;
        this.storedFilePath = storedFilePath;
        this.originFileNm = originFileNm;
        this.storedFileNm = storedFileNm;
        this.fileExtension = fileExtension;
        this.fileSize = fileSize;
        this.delYn = delYn;
    }

    public static RecipeFile of(Long id, RecipeEntity recipeEntity, Integer file_order, String storedFilePath, String originFileNm, String storedFileNm, String fileExtension, Integer fileSize, String delYn) {
        return new RecipeFile(id, recipeEntity, file_order, storedFilePath, originFileNm, storedFileNm, fileExtension, fileSize, delYn);
    }

    public static RecipeFile of(Integer file_order, String originFileNm, String fileExtension, Integer fileSize) {
        return new RecipeFile(null, null, file_order, null, originFileNm, null, fileExtension, fileSize, null);
    }

    /**
     * [레시피 생성 컨버터에서 사용하는 팩토리 메서드]
     * 레시피 생성 요청 dto 내부의 [List<MultipartFile> fileList] 데이터를 stream 으로 도메인으로 변환한다.
     */
    public static RecipeFile of(String originFileNm, String fileExtension, Integer fileSize) {
        return new RecipeFile(null, null, null, null, originFileNm, null, fileExtension, fileSize, null);
    }


}
