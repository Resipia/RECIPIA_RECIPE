package com.recipia.recipe.hexagonal.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RecipeFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_file_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipeEntity;

    @Column(name = "file_order", nullable = false)
    private Integer file_order;

    @Column(name = "flpth", nullable = false)
    private String flpth;

    @Column(name = "origin_file_nm", nullable = false)
    private String originFileNm;

    @Column(name = "strd_file_nm", nullable = false)
    private String strdFileNm;

    @Column(name = "file_extsn", nullable = false)
    private String fileExtsn;

    @Column(name = "file_size", nullable = false)
    private Integer fileSize;

    @Column(name = "del_yn", nullable = false)
    private String delYn;

    @Builder
    private RecipeFileEntity(Long id, RecipeEntity recipeEntity, Integer file_order, String flpth, String originFileNm, String strdFileNm, String fileExtsn, Integer fileSize, String delYn) {
        this.id = id;
        this.recipeEntity = recipeEntity;
        this.file_order = file_order;
        this.flpth = flpth;
        this.originFileNm = originFileNm;
        this.strdFileNm = strdFileNm;
        this.fileExtsn = fileExtsn;
        this.fileSize = fileSize;
        this.delYn = delYn;
    }

    public static RecipeFileEntity of(Long id, RecipeEntity recipeEntity, Integer file_order, String flpth, String originFileNm, String strdFileNm, String fileExtsn, Integer fileSize, String delYn) {
        return new RecipeFileEntity(id, recipeEntity, file_order, flpth, originFileNm, strdFileNm, fileExtsn, fileSize, delYn);
    }

    public static RecipeFileEntity of(RecipeEntity recipeEntity, Integer file_order, String flpth, String originFileNm, String strdFileNm, String fileExtsn, Integer fileSize, String delYn) {
        return new RecipeFileEntity(null, recipeEntity, file_order, flpth, originFileNm, strdFileNm, fileExtsn, fileSize, delYn);
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
