package com.recipia.recipe.hexagonal.adapter.out.persistence;

import com.recipia.recipe.hexagonal.adapter.out.persistence.auditingfield.UpdateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RecipeFileEntity extends UpdateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_file_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_step_id", nullable = false)
    private RecipeStepEntity recipeStepEntity;

    @Column(name = "file_order", nullable = false)
    private Integer fileOrder;

    @Column(name = "flpth", nullable = false)
    private String filePath;

    @Column(name = "origin_file_nm", nullable = false)
    private String originFileName;

    @Column(name = "strd_file_nm", nullable = false)
    private String storedFileName;

    @Column(name = "file_extsn", nullable = false)
    private String fileExtension;

    @Column(name = "file_size", nullable = false)
    private Integer fileSize;

    @Column(name = "del_yn", nullable = false)
    private String delYn;

    private RecipeFileEntity(RecipeStepEntity recipeStepEntity, Integer fileOrder, String filePath, String originFileName, String storedFileName, String fileExtension, Integer fileSize, String delYn) {
        this.recipeStepEntity = recipeStepEntity;
        this.fileOrder = fileOrder;
        this.filePath = filePath;
        this.originFileName = originFileName;
        this.storedFileName = storedFileName;
        this.fileExtension = fileExtension;
        this.fileSize = fileSize;
        this.delYn = delYn;
    }

    public static RecipeFileEntity of(RecipeStepEntity recipeStepEntity, Integer fileOrder, String filePath, String originFileName, String storedFileName, String fileExtension, Integer fileSize, String delYn) {
        return new RecipeFileEntity(recipeStepEntity, fileOrder, filePath, originFileName, storedFileName, fileExtension, fileSize, delYn);
    }
}
