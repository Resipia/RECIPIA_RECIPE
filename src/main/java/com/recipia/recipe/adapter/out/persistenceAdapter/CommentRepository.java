package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    Optional<CommentEntity> findByIdAndMemberIdAndDelYn(Long id, Long memberId, String delYn);

    Optional<CommentEntity> findByIdAndDelYn(Long id, String delYn);
}
