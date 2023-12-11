package com.recipia.recipe.hexagonal.adapter.out.persistence;


import com.recipia.recipe.hexagonal.adapter.out.persistence.auditingfield.UpdateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CtgryEntity extends UpdateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ctgry_id", nullable = false)
    private Long id;

    @Column(name = "upp_ctgry_id", nullable = false)
    private Long uppCtgryId;

    @Column(name = "ctgry_nm", nullable = false)
    private String ctgryName;

    @Column(name = "lvl_no", nullable = false)
    private Integer levelNo;

    @Column(name = "sort_no", nullable = false)
    private Integer sortNo;

    @Column(name = "del_yn", nullable = false)
    private String delYn;

    @ToString.Exclude
    @OneToMany(mappedBy = "ctgryEntity")
    private List<RecipeCtgryMapEntity> recipeCtgryMapEntityList = new ArrayList<>();

    private CtgryEntity(Long uppCtgryId, String ctgryName, Integer levelNo, Integer sortNo, String delYn) {
        this.uppCtgryId = uppCtgryId;
        this.ctgryName = ctgryName;
        this.levelNo = levelNo;
        this.sortNo = sortNo;
        this.delYn = delYn;
    }

    public static CtgryEntity of(Long uppCtgryId, String ctgryName, Integer levelNo, Integer sortNo, String delYn) {
        return new CtgryEntity(uppCtgryId, ctgryName, levelNo, sortNo, delYn);
    }
}
