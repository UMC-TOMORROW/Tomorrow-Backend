package com.umc.tomorrow.domain.job.entity;

import com.umc.tomorrow.global.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WorkEnvironment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean canWorkSitting;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean canWorkStanding;

    //물건운반으로 통일
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean canCarryObjects;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean canMoveActively;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean canCommunicate;

    //연관관계
    @OneToOne(mappedBy = "workEnvironment")
    private Job job;
}
