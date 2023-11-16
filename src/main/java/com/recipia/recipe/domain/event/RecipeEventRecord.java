package com.recipia.recipe.domain.event;

import com.recipia.recipe.domain.Recipe;
import com.recipia.recipe.domain.auditingfield.CreateDateTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/** 회원 이벤트 기록 */
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class RecipeEventRecord extends CreateDateTime {

    // 회원 이벤트 기록 Pk
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_event_record_id", nullable = false)
    private Long id;

    // 회원 pk
    @ToString.Exclude
    @JoinColumn(name = "recipe_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Recipe recipe;

    // sns topic 명
    @Column(name = "sns_topic", nullable = false)
    private String snsTopic;

    // Spring event 이벤트 객체
    @Column(name = "event_type", nullable = false)
    private String eventType;

    // sns 메시지 내용 (json 형태)
    @Column(name = "attribute", nullable = false)
    private String attribute;

    // sns 발행 여부
    @Column(name = "published", nullable = false)
    private boolean published;

    // sqs 에서 메시지 받은 시점
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    private RecipeEventRecord(Recipe recipe, String snsTopic, String eventType, String attribute, boolean published, LocalDateTime publishedAt) {
        this.recipe = recipe;
        this.snsTopic = snsTopic;
        this.eventType = eventType;
        this.attribute = attribute;
        this.published = published;
        this.publishedAt = publishedAt;
    }

    // 생성자 factory method of 선언
    public static RecipeEventRecord of(Recipe recipe, String snsTopic, String eventType, String attribute, boolean published, LocalDateTime publishedAt) {
        return new RecipeEventRecord(recipe, snsTopic, eventType, attribute, published, publishedAt);
    }


}
