package com.recipia.recipe.hexagonal.adapter.out.persistence;

import com.recipia.recipe.hexagonal.adapter.out.persistence.auditingfield.UpdateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RecipeEntity extends UpdateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id", nullable = false)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "recipe_nm", nullable = false)
    private String recipeName;

    @Column(name = "recipe_desc")
    private String recipeDesc;

    @Column(name = "time_taken")
    private Integer timeTaken;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "del_yn", nullable = false)
    private String delYn;

    @ToString.Exclude
    @OneToMany(mappedBy = "recipeEntity")
    private List<RecipeStepEntity> recipeStepEntityList = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "recipeEntity")
    private List<RecipeCtgryMapEntity> recipeCtgryMapEntityList = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "recipeEntity")
    private List<StarRateEntity> starRateEntityList = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "recipeEntity")
    private List<RecipeHistLogEntity> recipeHistLogEntityList = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "recipeEntity")
    private List<IngredientRecipeMapEntity> ingredientList = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "recipeEntity")
    private List<RecipeViewCntEntity> recipeViewCntEntityList = new ArrayList<>();

    @Builder
    private RecipeEntity(Long id, Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String nickname, String delYn) {
        this.id = id;
        this.memberId = memberId;
        this.recipeName = recipeName;
        this.recipeDesc = recipeDesc;
        this.timeTaken = timeTaken;
        this.nickname = nickname;
        this.delYn = delYn;
    }

    public static RecipeEntity of(Long id, Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String nickname, String delYn) {
        return new RecipeEntity(id, memberId, recipeName, recipeDesc, timeTaken, nickname, delYn);
    }

    public static RecipeEntity of(Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String nickname, String delYn) {
        return new RecipeEntity(null, memberId, recipeName, recipeDesc, timeTaken, nickname, delYn);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeEntity recipeEntity)) return false;
        return this.id != null && Objects.equals(getId(), recipeEntity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

}