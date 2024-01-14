package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.RecipeLikeCntEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeLikeCountRepository extends JpaRepository<RecipeLikeCntEntity, Long> {
    Optional<RecipeLikeCntEntity> findByRecipeEntityId(Long recipeId);
}
