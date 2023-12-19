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

    // 레시피 엔티티는 재료를 string으로 가지고 있고 mongoDB에서는 모든 쟤료정보를 저장해서 검색에 사용한다.
    @Column(name = "ingredient", nullable = false)
    private String ingredient;

    @Column(name = "hashtag", nullable = false)
    private String hashtag;

    @Column(name = "nutritional_info", nullable = false)
    private String nutritionalInfo;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "del_yn", nullable = false)
    private String delYn;

    @Builder
    private RecipeEntity(Long id, Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, String nutritionalInfo, String nickname, String delYn) {
        this.id = id;
        this.memberId = memberId;
        this.recipeName = recipeName;
        this.recipeDesc = recipeDesc;
        this.timeTaken = timeTaken;
        this.ingredient = ingredient;
        this.hashtag = hashtag;
        this.nutritionalInfo = nutritionalInfo;
        this.nickname = nickname;
        this.delYn = delYn;
    }

    public static RecipeEntity of(Long id, Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, String nutritionalInfo, String nickname, String delYn) {
        return new RecipeEntity(id, memberId, recipeName, recipeDesc, timeTaken, ingredient, hashtag, nutritionalInfo, nickname, delYn);
    }

    public static RecipeEntity of(Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, String nutritionalInfo, String nickname, String delYn) {
        return new RecipeEntity(null, memberId, recipeName, recipeDesc, timeTaken, ingredient, hashtag, nutritionalInfo, nickname, delYn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeEntity that)) return false;
        return this.id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}