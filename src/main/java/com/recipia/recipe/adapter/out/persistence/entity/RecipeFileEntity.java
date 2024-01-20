package com.recipia.recipe.adapter.out.persistence.entity;

import com.recipia.recipe.adapter.out.persistence.entity.auditingfield.CreateDateTimeForEntity;
import com.recipia.recipe.adapter.out.persistence.entity.auditingfield.UpdateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "recipe_file")
public class RecipeFileEntity extends UpdateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_file_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipeEntity; // 어떤 레시피와 연관있는지

    @Column(name = "file_order", nullable = false)
    private Integer fileOrder; // 파일 정렬 조건

    @Column(name = "flpth", nullable = false)
    private String storedFilePath; // s3에 저장된 파일 경로 url

    @Column(name = "object_url", nullable = false)
    private String objectUrl;      // 저장된 객체 url (사진 바로 볼수있음)

    @Column(name = "origin_file_nm", nullable = false)
    private String originFileNm;    // 원본 파일 이름

    @Column(name = "strd_file_nm", nullable = false)
    private String storedFileNm;  // 저장된 파일 이름

    @Column(name = "file_extsn", nullable = false)
    private String fileExtension; // 파일 확장자 (jpg, jpeg, png)

    @Column(name = "file_size", nullable = false)
    private Integer fileSize; // 파일 크기

    @Column(name = "del_yn", nullable = false)
    private String delYn; // 삭제 여부

    @Builder
    private RecipeFileEntity(Long id, RecipeEntity recipeEntity, Integer fileOrder, String storedFilePath, String objectUrl, String originFileNm, String storedFileNm, String fileExtension, Integer fileSize, String delYn) {
        this.id = id;
        this.recipeEntity = recipeEntity;
        this.fileOrder = fileOrder;
        this.storedFilePath = storedFilePath;
        this.objectUrl = objectUrl;
        this.originFileNm = originFileNm;
        this.storedFileNm = storedFileNm;
        this.fileExtension = fileExtension;
        this.fileSize = fileSize;
        this.delYn = delYn;
    }

    public static RecipeFileEntity of(Long id, RecipeEntity recipeEntity, Integer fileOrder, String storedFilePath, String objectUrl, String originFileNm, String storedFileNm, String fileExtension, Integer fileSize, String delYn) {
        return new RecipeFileEntity(id, recipeEntity, fileOrder, storedFilePath, objectUrl, originFileNm, storedFileNm, fileExtension, fileSize, delYn);
    }

    public static RecipeFileEntity of(RecipeEntity recipeEntity, Integer fileOrder, String storedFilePath, String objectUrl, String originFileNm, String storedFileNm, String fileExtension, Integer fileSize, String delYn) {
        return new RecipeFileEntity(null, recipeEntity, fileOrder, storedFilePath, objectUrl, originFileNm, storedFileNm, fileExtension, fileSize, delYn);
    }

    /**
     * s3 저장을 하기위해 ImageS3Service 에서 사용하는 팩토리 메서드
     */
    public static RecipeFileEntity of(RecipeEntity recipeEntity, String storedFilePath, String objectUrl, String originFileNm, String storedFileNm, String fileExtension, Integer fileSize) {
        return new RecipeFileEntity(null, recipeEntity, null, storedFilePath, objectUrl, originFileNm, storedFileNm, fileExtension, fileSize, null);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeFileEntity that)) return false;
        return this.id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
