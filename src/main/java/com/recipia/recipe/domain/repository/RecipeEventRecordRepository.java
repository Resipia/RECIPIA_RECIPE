package com.recipia.recipe.domain.repository;

import com.recipia.recipe.domain.event.RecipeEventRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeEventRecordRepository extends JpaRepository<RecipeEventRecord, Long> {

}
