package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.RecipeFileEntity;
import io.micrometer.core.instrument.config.validate.Validated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeFileRepository extends JpaRepository<RecipeFileEntity, Long> {

    @Query("SELECT rf FROM RecipeFileEntity rf WHERE rf.recipeEntity.id = :recipeId and rf.delYn = 'N'")
    List<RecipeFileEntity> findAllByRecipeId(@Param("recipeId") Long recipeId);

    @Query("SELECT rf FROM RecipeFileEntity rf WHERE rf.recipeEntity.id = :recipeId and rf.delYn = 'Y'")
    List<RecipeFileEntity> findAllSoftDeletedFileList(@Param("recipeId") Long recipeId);

    @Query("SELECT MAX(rfe.fileOrder) FROM RecipeFileEntity rfe WHERE rfe.recipeEntity.id = :recipeId")
    Optional<Integer> findMaxFileOrderByRecipeEntity_Id(@Param("recipeId") Long recipeId);
}



