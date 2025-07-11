package com.umc.tomorrow.domain.job.entity;


import com.umc.tomorrow.global.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WorkDays extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean MON;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean TUE;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean WED;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean THU;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean FRI;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean SAT;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean SUN;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDayNegotiable;

    //연관관계
    @OneToOne(mappedBy = "workDays")
    private Job job;
}
