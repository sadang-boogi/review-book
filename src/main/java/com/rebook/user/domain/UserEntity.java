package com.rebook.user.domain;

import com.rebook.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "password")
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", columnDefinition = "varchar(255)")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", columnDefinition = "varchar(255)")
    private SocialType socialType;

    private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

    @Builder
    public UserEntity(Long id, String nickname, String email, String password, Role role, SocialType socialType, String socialId) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.socialType = socialType;
        this.socialId = socialId;
    }

    public UserEntity update(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
        return this;
    }
}
