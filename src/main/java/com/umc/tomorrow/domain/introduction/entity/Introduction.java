package com.umc.tomorrow.domain.introduction.entity;

import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.global.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Introduction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false, length = 100)
    private String content;

    //연관관계
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", unique = true)
    private Resume resume;

}
