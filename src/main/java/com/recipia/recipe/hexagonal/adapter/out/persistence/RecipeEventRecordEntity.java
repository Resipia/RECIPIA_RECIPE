package com.recipia.recipe.hexagonal.adapter.out.persistence;

import com.recipia.recipe.hexagonal.adapter.out.persistence.auditingfield.CreateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RecipeEventRecordEntity extends CreateDateTimeForEntity {

    @Id
    @GeneratedValue
    @Column(name = "recipe_event_record_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipeEntity;

    @Column(name = "sns_topic", nullable = false)
    private String sns_topic;

    @Column(name = "event_type", nullable = false)
    private String event_type;

    @Column(name = "attribute", nullable = false)
    private String attribute;

    @Column(name = "trace_id", nullable = false)
    private String trace_id;

    @Column(name = "published", nullable = false)
    private Boolean published;

    @Column(name = "published_at", nullable = false)
    private LocalDateTime published_at;

    @Builder
    private RecipeEventRecordEntity(Long id, RecipeEntity recipeEntity, String sns_topic, String event_type, String attribute, String trace_id, Boolean published, LocalDateTime published_at) {
        this.id = id;
        this.recipeEntity = recipeEntity;
        this.sns_topic = sns_topic;
        this.event_type = event_type;
        this.attribute = attribute;
        this.trace_id = trace_id;
        this.published = published;
        this.published_at = published_at;
    }

    public static RecipeEventRecordEntity of(Long id, RecipeEntity recipeEntity, String sns_topic, String event_type, String attribute, String trace_id, Boolean published, LocalDateTime published_at) {
        return new RecipeEventRecordEntity(id, recipeEntity, sns_topic, event_type, attribute, trace_id, published, published_at);
    }

    public static RecipeEventRecordEntity of(RecipeEntity recipeEntity, String sns_topic, String event_type, String attribute, String trace_id, Boolean published, LocalDateTime published_at) {
        return new RecipeEventRecordEntity(null, recipeEntity, sns_topic, event_type, attribute, trace_id, published, published_at);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeEventRecordEntity that)) return false;
        return this.id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }


}
