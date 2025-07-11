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

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean canLiftHeavyObjects;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean canLiftLightObjects;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean canMoveActively;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean canCommunicate;

    //연관관계
    @OneToOne(mappedBy = "workEnvironment")
    private Job job;
}
