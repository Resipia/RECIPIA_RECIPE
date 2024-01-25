package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.BookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Long> {
    Long deleteAllByRecipeEntity_Id(Long recipeId);

    List<BookmarkEntity> findAllByRecipeEntity_Id(Long recipeId);
}
