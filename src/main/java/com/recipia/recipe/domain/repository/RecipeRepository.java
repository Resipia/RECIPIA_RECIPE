package com.recipia.recipe.domain.repository;

import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.dto.member.MemberDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

}
