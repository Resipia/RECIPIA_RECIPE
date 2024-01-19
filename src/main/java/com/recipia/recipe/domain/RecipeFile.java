package com.recipia.recipe.domain;

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
    private Recipe recipe;          // 어떤 레시피와 연관있는지
    private Integer fileOrder;      // 파일 정렬 조건
    private String storedFilePath;  // 저장된 파일 경로 url
    private String objectUrl;       // 저장된 객체 url (사진 바로 볼수있음)
    private String originFileNm;    // 원본 파일 이름
    private String storedFileNm;    // 저장된 파일 이름
    private String fileExtension;   // 파일 확장자 (jpg, jpeg, png)
    private Integer fileSize;       // 파일 크기
    private String delYn;           // 삭제 여부
    private String preUrl;          // S3에 접근할 수 있는 임시 pre-signed-url


    @Builder
    public RecipeFile(Long id, Recipe recipe, Integer fileOrder, String storedFilePath, String objectUrl, String originFileNm, String storedFileNm, String fileExtension, Integer fileSize, String delYn, String preUrl) {
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
        this.preUrl = preUrl;
    }

    public static RecipeFile of(Long id, Recipe recipe, Integer fileOrder, String storedFilePath, String objectUrl, String originFileNm, String storedFileNm, String fileExtension, Integer fileSize, String delYn, String preUrl) {
        return new RecipeFile(id, recipe, fileOrder, storedFilePath, objectUrl, originFileNm, storedFileNm, fileExtension, fileSize, delYn, preUrl);
    }

    /**
     * ImageS3Service 클래스에서 사용
     * MultipartFile로 이미지 저장에 필요한 값을 꺼내서 도메인으로 변환
     */
    public static RecipeFile of(Recipe recipe, Integer fileOrder, String storedFilePath, String objectUrl, String originFileNm, String storedFileNm, String fileExtension, Integer fileSize, String delYn) {
        return new RecipeFile(null, recipe, fileOrder, storedFilePath, objectUrl, originFileNm, storedFileNm, fileExtension, fileSize, delYn, null);
    }


    /**
     * 어댑터에서 파일을 조회할때 EntityToDomain 컨버터를 호출하면 이 메서드가 사용된다.
     */
    public static RecipeFile of(Integer fileOrder, String storedFilePath, String objectUrl, String originFileNm, String storedFileNm, String fileExtension, Integer fileSize, String delYn, String preUrl) {
        return new RecipeFile(null, null, fileOrder, storedFilePath, objectUrl, originFileNm, storedFileNm, fileExtension, fileSize, delYn, preUrl);
    }

    /**
     * 레시피 조회에서 사용되는 of 메서드
     */
    public static RecipeFile of(Long id, Integer fileOrder, String preUrl) {
        return new RecipeFile(id, null, fileOrder, null, null, null, null, null, null, null, preUrl);
    }
}
