package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.RecipeCategoryMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeCategoryMapRepository extends JpaRepository<RecipeCategoryMapEntity, Long> {

    void deleteByRecipeEntityId(Long id);

    List<RecipeCategoryMapEntity> findByRecipeEntityId(Long id);
}
