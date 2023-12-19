package com.recipia.recipe.hexagonal.adapter.out.persistence;

import com.recipia.recipe.hexagonal.adapter.out.persistence.auditingfield.CreateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RecipeHistLogEntity extends CreateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_hist_log_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipeEntity;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "related_entity_nm", nullable = false)
    private String relatedEntityNm;

    @Column(name = "related_entity_id")
    private Long relatedEntityId;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "event_type")
    private String eventType;

    @Builder
    private RecipeHistLogEntity(Long id, RecipeEntity recipeEntity, Long memberId, String relatedEntityNm, Long relatedEntityId, String action, String eventType) {
        this.id = id;
        this.recipeEntity = recipeEntity;
        this.memberId = memberId;
        this.relatedEntityNm = relatedEntityNm;
        this.relatedEntityId = relatedEntityId;
        this.action = action;
        this.eventType = eventType;
    }

    public static RecipeHistLogEntity of(Long id, RecipeEntity recipeEntity, Long memberId, String relatedEntityNm, Long relatedEntityId, String action, String eventType) {
        return new RecipeHistLogEntity(id, recipeEntity, memberId, relatedEntityNm, relatedEntityId, action, eventType);
    }

    public static RecipeHistLogEntity of(RecipeEntity recipeEntity, Long memberId, String relatedEntityNm, Long relatedEntityId, String action, String eventType) {
        return new RecipeHistLogEntity(null, recipeEntity, memberId, relatedEntityNm, relatedEntityId, action, eventType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeHistLogEntity that)) return false;
        return this.id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
