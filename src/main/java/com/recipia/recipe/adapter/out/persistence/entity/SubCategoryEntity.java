package com.recipia.recipe.adapter.out.persistence.entity;


import com.recipia.recipe.adapter.out.persistence.entity.auditingfield.UpdateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "sub_category")
public class SubCategoryEntity extends UpdateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_category_id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity categoryEntity;

    @Column(name = "sub_category_nm", nullable = false)
    private String subCategoryNm;

    @Column(name = "sort_no", nullable = false)
    private Integer sortNo;

    @Column(name = "del_yn", nullable = false)
    private String delYn;

    @Builder
    private SubCategoryEntity(Long id, CategoryEntity categoryEntity, String subCategoryNm, Integer sortNo, String delYn) {
        this.id = id;
        this.categoryEntity = categoryEntity;
        this.subCategoryNm = subCategoryNm;
        this.sortNo = sortNo;
        this.delYn = delYn;
    }

    public static SubCategoryEntity of(Long id, CategoryEntity categoryEntity, String subCategoryNm, Integer sortNo, String delYn) {
        return new SubCategoryEntity(id, categoryEntity, subCategoryNm, sortNo, delYn);
    }

    public static SubCategoryEntity of(CategoryEntity categoryEntity, String subCategoryNm, Integer sortNo, String delYn) {
        return new SubCategoryEntity(null, categoryEntity, subCategoryNm, sortNo, delYn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubCategoryEntity that)) return false;
        return this.id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
