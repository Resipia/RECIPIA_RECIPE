package com.recipia.recipe.domain;

import com.recipia.recipe.domain.auditingfield.UpdateDateTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "RECIPE_FILE")
public class RecipeFile extends UpdateDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_file_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_step_id", nullable = false)
    private RecipeStep recipeStep;

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

    private RecipeFile(RecipeStep recipeStep, Integer fileOrder, String filePath, String originFileName, String storedFileName, String fileExtension, Integer fileSize, String delYn) {
        this.recipeStep = recipeStep;
        this.fileOrder = fileOrder;
        this.filePath = filePath;
        this.originFileName = originFileName;
        this.storedFileName = storedFileName;
        this.fileExtension = fileExtension;
        this.fileSize = fileSize;
        this.delYn = delYn;
    }

    public static RecipeFile of(RecipeStep recipeStep, Integer fileOrder, String filePath, String originFileName, String storedFileName, String fileExtension, Integer fileSize, String delYn) {
        return new RecipeFile(recipeStep, fileOrder, filePath, originFileName, storedFileName, fileExtension, fileSize, delYn);
    }
}
