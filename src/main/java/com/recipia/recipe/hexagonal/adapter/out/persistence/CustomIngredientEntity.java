package com.recipia.recipe.hexagonal.adapter.out.persistence;

import com.recipia.recipe.hexagonal.adapter.out.persistence.auditingfield.CreateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.*;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CustomIngredientEntity extends CreateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_ingredient_id", nullable = false)
    private Long id;

    @Column(name = "ingredient_nm", nullable = false)
    private String ingredientName;

    private CustomIngredientEntity(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public static CustomIngredientEntity of(String ingredientName) {
        return new CustomIngredientEntity(ingredientName);
    }

}
