package com.recipia.recipe.adapter.out.persistence.entity;

import com.recipia.recipe.adapter.out.persistence.entity.auditingfield.CreateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "category_sub_map")
public class CategorySubMapEntity extends CreateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_sub_map_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity categoryEntity;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id", nullable = false)
    private SubCategoryEntity subCategoryEntity;

    @Builder
    private CategorySubMapEntity(Long id, CategoryEntity categoryEntity, SubCategoryEntity subCategoryEntity) {
        this.id = id;
        this.categoryEntity = categoryEntity;
        this.subCategoryEntity = subCategoryEntity;
    }

    public static CategorySubMapEntity of(Long id, CategoryEntity categoryEntity, SubCategoryEntity subCategoryEntity) {
        return new CategorySubMapEntity(id, categoryEntity, subCategoryEntity);
    }

    public static CategorySubMapEntity of(CategoryEntity categoryEntity, SubCategoryEntity subCategoryEntity) {
        return new CategorySubMapEntity(null, categoryEntity, subCategoryEntity);
    }

    public static CategorySubMapEntity of(Long id) {
        return new CategorySubMapEntity(id, null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategorySubMapEntity that)) return false;
        return this.id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
