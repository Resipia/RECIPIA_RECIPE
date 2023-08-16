package com.recipia.recipe.domain;

import com.recipia.recipe.domain.auditingfield.CreateDateTime;
import jakarta.persistence.*;
import lombok.*;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CustomIngredient extends CreateDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_ingredient_id", nullable = false)
    private Long id;

    @Column(name = "ingredient_nm", nullable = false)
    private String ingredientName;

    private CustomIngredient(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public static CustomIngredient of(String ingredientName) {
        return new CustomIngredient(ingredientName);
    }

}
