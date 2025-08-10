package com.umc.tomorrow.domain.member.entity;

import com.umc.tomorrow.domain.application.entity.Application;
import com.umc.tomorrow.domain.job.entity.BusinessVerification;
import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.member.enums.Gender;
import com.umc.tomorrow.domain.member.enums.Provider;
import com.umc.tomorrow.domain.member.enums.UserStatus;
import com.umc.tomorrow.domain.preferences.entity.Preference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class) // auditing 활성화
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, unique = true)
    private String email;

    @Column(length = 10, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 255)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private UserStatus status;

    private LocalDateTime inactiveAt;

    private Boolean isOnboarded;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private Provider provider;

    @Column(length = 255, nullable = false)
    private String providerUserId;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private Long resumeId;

    @Column(length = 512)
    private String refreshToken;

    /** 사용자명(로그인 ID 또는 소셜 ID) */
    @Column(length = 50, unique = true, nullable = false)
    private String username;

    //연관관계
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Job> jobs = new ArrayList<>(); // 내가 등록한 일자리 목록


    // 사업자 등록 테이블과 1대1 연결
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "business_verification_id", unique = true)
    private BusinessVerification businessVerification;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Preference preference;

    //JUnit테스트용
    public User(String name, String email, Provider provider, String providerUserId, String username) {
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.username = username;
    }


}
