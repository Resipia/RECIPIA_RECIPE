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

    @Column(name = "sub_category_nm", nullable = false)
    private String subCategoryNm;

    @Column(name = "del_yn", nullable = false)
    private String delYn;

    @Builder
    private SubCategoryEntity(Long id, String subCategoryNm, String delYn) {
        this.id = id;
        this.subCategoryNm = subCategoryNm;
        this.delYn = delYn;
    }

    public static SubCategoryEntity of(Long id, String subCategoryNm, String delYn) {
        return new SubCategoryEntity(id, subCategoryNm, delYn);
    }

    public static SubCategoryEntity of(String subCategoryNm, String delYn) {
        return new SubCategoryEntity(null, subCategoryNm, delYn);
    }


    public static SubCategoryEntity of(Long id) {
        return new SubCategoryEntity(id, null, null);
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
