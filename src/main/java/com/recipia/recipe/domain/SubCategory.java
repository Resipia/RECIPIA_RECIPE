package com.recipia.recipe.domain;


import com.recipia.recipe.adapter.out.persistence.entity.CategoryEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SubCategory {

    private Long id;

    @Builder
    private SubCategory(Long id) {
        this.id = id;
    }

    public static SubCategory of(Long id) {
        return new SubCategory(id);
    }

}

