package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.SubCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubCommentRepository extends JpaRepository<SubCommentEntity, Long> {
    Optional<SubCommentEntity> findByIdAndMemberIdAndDelYn(Long id, Long memberId, String delYn);
    List<SubCommentEntity> findAllByCommentEntity_Id(Long commentId);

    List<SubCommentEntity> findAllByMemberId(Long memberId);
}
