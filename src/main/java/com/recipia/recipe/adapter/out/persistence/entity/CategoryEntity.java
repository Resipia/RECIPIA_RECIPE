package com.recipia.recipe.adapter.out.persistence.entity;


import com.recipia.recipe.adapter.out.persistence.entity.auditingfield.UpdateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "category")
public class CategoryEntity extends UpdateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Long id;

    @Column(name = "category_nm", nullable = false)
    private String categoryNm;

    @Column(name = "sort_no", nullable = false)
    private Integer sortNo;

    @Column(name = "del_yn", nullable = false)
    private String delYn;

    @Builder
    private CategoryEntity(Long id, String categoryNm, Integer sortNo, String delYn) {
        this.id = id;
        this.categoryNm = categoryNm;
        this.sortNo = sortNo;
        this.delYn = delYn;
    }

    public static CategoryEntity of(Long id, String categoryNm, Integer sortNo, String delYn) {
        return new CategoryEntity(id, categoryNm, sortNo, delYn);
    }

    public static CategoryEntity of(String categoryNm, Integer sortNo, String delYn) {
        return new CategoryEntity(null, categoryNm, sortNo, delYn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryEntity that)) return false;
        return this.id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
