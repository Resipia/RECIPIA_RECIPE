package com.recipia.recipe.hexagonal.adapter.out.persistenceAdapter;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeEventRecordRepository extends JpaRepository<RecipeEventRecordEntity, Long> {

}
