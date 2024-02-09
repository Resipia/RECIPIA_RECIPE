package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.RecipeViewCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeViewCountRepository extends JpaRepository<RecipeViewCountEntity, Long> {
    Optional<RecipeViewCountEntity> findByRecipeEntityId(Long recipeId);
}