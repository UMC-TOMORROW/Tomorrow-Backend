package com.umc.tomorrow.domain.member.entity;

import com.umc.tomorrow.domain.job.entity.Job;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false, unique = true)
    private String email;

    @Column(length = 10, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private Gender gender;

    @Column(length = 20, nullable = false)
    private String phoneNumber;

    @Column(length = 255)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private Status status;

    private LocalDateTime inactiveAt;

    @Column(nullable = false)
    private Boolean isOnboarded;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private Provider provider;

    @Column(length = 10, nullable = false)
    private String providerUserId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Long resumeId;

    //연관관계
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Job> jobs = new ArrayList<>(); // 내가 등록한 일자리 목록

    public enum Gender {
        MALE, FEMALE
    }

    public enum Status {
        ACTIVE, INACTIVE
    }

    public enum Provider {
        KAKAO, NAVER, GOOGLE
    }
}