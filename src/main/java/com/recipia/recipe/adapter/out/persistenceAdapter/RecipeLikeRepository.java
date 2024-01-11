package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.RecipeLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeLikeRepository extends JpaRepository<RecipeLikeEntity, Long> {

    boolean existsByIdAndMemberId(Long id, Long memberId);

}
