package com.recipia.recipe.domain;

import com.recipia.recipe.domain.auditingfield.CreateDateTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Ingredient extends CreateDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id", nullable = false)
    private Long id;

    @Column(name = "ingredient_nm", nullable = false)
    private String ingredientName;

    @Column(name = "del_yn", nullable = false)
    private String delYn;

    private Ingredient(String ingredientName, String delYn) {
        this.ingredientName = ingredientName;
        this.delYn = delYn;
    }

    public static Ingredient of(String ingredientName, String delYn) {
        return new Ingredient(ingredientName, delYn);
    }
}
