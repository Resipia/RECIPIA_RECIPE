package com.recipia.recipe.hexagonal.adapter.out.persistenceAdapter;

import com.recipia.recipe.hexagonal.adapter.out.persistence.RecipeEventRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeEventRecordRepository extends JpaRepository<RecipeEventRecordEntity, Long> {

}
