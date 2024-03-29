package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<RecipeEntity, Long> {

    Optional<RecipeEntity> findByIdAndDelYn(Long id, String delYn);

    Optional<RecipeEntity> findByIdAndMemberIdAndDelYn(Long id, Long memberId, String delYn);

    Long countByMemberIdAndDelYn(Long memberId, String DelYn);

    List<RecipeEntity> findAllByMemberId(Long memberId);
}
