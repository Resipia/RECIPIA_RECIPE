package com.recipia.recipe.adapter.out.persistenceAdapter;

import com.recipia.recipe.adapter.out.persistence.entity.NicknameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NicknameRepository extends JpaRepository<NicknameEntity, Long> {

    Optional<NicknameEntity> findByMemberId(Long memberId);

}
