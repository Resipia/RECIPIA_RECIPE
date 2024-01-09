package com.recipia.recipe.adapter.in.web.dto.request;

import com.recipia.recipe.domain.Recipe;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class RecipeFileDto {

    private Long id;
    private Recipe recipe; // 어떤 레시피와 연관있는지
    private Integer fileOrder; // 파일 정렬 조건
    private String storedFilePath; // 저장된 파일 경로 url
    private String objectUrl; // 저장된 객체 url (사진 바로 볼수있음)
    private String originFileNm;    // 원본 파일 이름
    private String storedFileNm;  // 저장된 파일 이름
    private String fileExtension; // 파일 확장자 (jpg, jpeg, png)
    private Integer fileSize; // 파일 크기
    private String delYn; // 삭제 여부

    @Builder
    private RecipeFileDto(Long id, Recipe recipe, Integer fileOrder, String storedFilePath, String objectUrl, String originFileNm, String storedFileNm, String fileExtension, Integer fileSize, String delYn) {
        this.id = id;
        this.recipe = recipe;
        this.fileOrder = fileOrder;
        this.storedFilePath = storedFilePath;
        this.objectUrl = objectUrl;
        this.originFileNm = originFileNm;
        this.storedFileNm = storedFileNm;
        this.fileExtension = fileExtension;
        this.fileSize = fileSize;
        this.delYn = delYn;
    }

    public static RecipeFileDto of(Long id, Recipe recipe, Integer fileOrder, String storedFilePath, String objectUrl, String originFileNm, String storedFileNm, String fileExtension, Integer fileSize, String delYn) {
        return new RecipeFileDto(id, recipe, fileOrder, storedFilePath, objectUrl, originFileNm, storedFileNm, fileExtension, fileSize, delYn);
    }

    public static RecipeFileDto of(Integer fileOrder, String storedFilePath, String objectUrl, String originFileNm, String storedFileNm, String fileExtension, Integer fileSize, String delYn) {
        return new RecipeFileDto(null, null, fileOrder, storedFilePath, objectUrl, originFileNm, storedFileNm, fileExtension, fileSize, delYn);
    }

}
