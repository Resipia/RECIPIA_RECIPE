package com.recipia.recipe.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "nickname")
public class NicknameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nickname_id", nullable = false)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Builder
    private NicknameEntity(Long id, Long memberId, String nickname) {
        this.id = id;
        this.memberId = memberId;
        this.nickname = nickname;
    }

    public static NicknameEntity of(Long id, Long memberId, String nickname) {
        return new NicknameEntity(id, memberId, nickname);
    }

    public static NicknameEntity of(Long memberId, String nickname) {
        return new NicknameEntity(null, memberId, nickname);
    }


}
