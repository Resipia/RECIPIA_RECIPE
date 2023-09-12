package com.recipia.recipe.domain;

import com.recipia.recipe.domain.auditingfield.UpdateDateTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Recipe extends UpdateDateTime {

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

    @Column(name = "create_username", nullable = false)
    private String createUsername;

    @Column(name = "update_username", nullable = false)
    private String updateUsername;

    @Column(name = "create_nickname", nullable = false)
    private String createNickname;

    @Column(name = "del_yn", nullable = false)
    private String delYn;

    @ToString.Exclude
    @OneToMany(mappedBy = "recipe")
    private List<RecipeStep> recipeStepList = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "recipe")
    private List<RecipeCtgryMap> recipeCtgryMapList = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "recipe")
    private List<StarRate> starRateList = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "recipe")
    private List<RecipeHistLog> recipeHistLogList = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "recipe")
    private List<IngredientRecipeMap> ingredientList = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "recipe")
    private List<RecipeViewCnt> recipeViewCntList = new ArrayList<>();

    private Recipe(Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String createUsername, String updateUsername, String createNickname, String delYn) {
        this.memberId = memberId;
        this.recipeName = recipeName;
        this.recipeDesc = recipeDesc;
        this.timeTaken = timeTaken;
        this.createUsername = createUsername;
        this.updateUsername = updateUsername;
        this.createNickname = createNickname;
        this.delYn = delYn;
    }

    public static Recipe of(Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String createUsername, String updateUsername, String createNickname, String delYn) {
        return new Recipe(memberId, recipeName, recipeDesc, timeTaken, createUsername, updateUsername, createNickname, delYn);
    }
}
