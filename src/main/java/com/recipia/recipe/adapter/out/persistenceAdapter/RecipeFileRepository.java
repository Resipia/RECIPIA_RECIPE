package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.RecipeFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeFileRepository extends JpaRepository<RecipeFileEntity, Long> {

    void deleteByRecipeEntityId(Long updatedRecipeId);

    @Query("SELECT rf FROM RecipeFileEntity rf WHERE rf.recipeEntity.id = :recipeId")
    List<RecipeFileEntity> findByRecipeId(@Param("recipeId") Long recipeId);

}
