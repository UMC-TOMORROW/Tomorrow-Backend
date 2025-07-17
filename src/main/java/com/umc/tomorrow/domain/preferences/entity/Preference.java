package com.umc.tomorrow.domain.preferences.entity;

import com.umc.tomorrow.domain.member.entity.User;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Preference Entity
 * - 사용자의 희망 조건(PreferenceType) 목록을 저장
 */
@Entity
public class Preference {
    /** 기본키 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 희망 조건을 소유한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    /**
     * 희망 조건 목록 (EnumSet)
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<PreferenceType> preferences = new HashSet<>();

    // Getter, Setter
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Set<PreferenceType> getPreferences() { return preferences; }
    public void setPreferences(Set<PreferenceType> preferences) { this.preferences = preferences; }
} 