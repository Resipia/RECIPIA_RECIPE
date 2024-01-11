package com.recipia.recipe.adapter.out.persistence.entity;

import com.recipia.recipe.adapter.out.persistence.entity.auditingfield.UpdateDateTimeForEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "recipe")
public class RecipeEntity extends UpdateDateTimeForEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id", nullable = false)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "recipe_nm", nullable = false)
    private String recipeName;

    @Column(name = "recipe_desc")
    private String recipeDesc;

    @Column(name = "time_taken")
    private Integer timeTaken;

    // 레시피 엔티티는 재료를 string으로 가지고 있고 mongoDB에서는 모든 쟤료정보를 저장해서 검색에 사용한다.
    @Column(name = "ingredient", nullable = false)
    private String ingredient;

    @Column(name = "hashtag", nullable = false)
    private String hashtag;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @OneToMany(mappedBy = "recipeEntity", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    @ToString.Exclude
    private List<RecipeFileEntity> recipeFileList = new ArrayList<>();

    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @Column(name = "del_yn", nullable = false)
    private String delYn;


    @Builder
    private RecipeEntity(Long id, Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, String nickname, List<RecipeFileEntity> recipeFileList, Integer likeCount, String delYn) {
        this.id = id;
        this.memberId = memberId;
        this.recipeName = recipeName;
        this.recipeDesc = recipeDesc;
        this.timeTaken = timeTaken;
        this.ingredient = ingredient;
        this.hashtag = hashtag;
        this.nickname = nickname;
        this.recipeFileList = recipeFileList;
        this.likeCount = likeCount;
        this.delYn = delYn;
    }

    public static RecipeEntity of(Long id, Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, String nickname, Integer likeCount, String delYn) {
        return new RecipeEntity(id, memberId, recipeName, recipeDesc, timeTaken, ingredient, hashtag, nickname, Collections.emptyList(), likeCount, delYn);
    }

    public static RecipeEntity of(Long memberId, String recipeName, String recipeDesc, Integer timeTaken, String ingredient, String hashtag, String nickname, Integer likeCount, String delYn) {
        return new RecipeEntity(null, memberId, recipeName, recipeDesc, timeTaken, ingredient, hashtag, nickname, Collections.emptyList(), likeCount, delYn);
    }


    public static RecipeEntity of(Long id) {
        return new RecipeEntity(id, null, null, null, null, null, null, null, Collections.emptyList(), null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeEntity that)) return false;
        return this.id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }


//    // 수정할 때 삭제한 이미지의 url을 전부 제거
//    public void changeRecipe(RecipeModifyRequestDto dto) {
//        this.recipeName=dto.getPostTitle();
//        this.recipeDesc=dto.getContent();
//        List<String> urls = dto.getImageUrlListForDelete();
//
//        // 파일 삭제 삭제
//        this.recipeFileList.removeIf(
//                e -> urls.contains(e.getStoredImagePath())
//        );
//    }
}