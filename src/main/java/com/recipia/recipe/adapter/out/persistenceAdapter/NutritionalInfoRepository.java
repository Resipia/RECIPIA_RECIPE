package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.NutritionalInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NutritionalInfoRepository extends JpaRepository<NutritionalInfoEntity, Long> {
    Optional<NutritionalInfoEntity> findByRecipe_Id(Long recipeId);
}
