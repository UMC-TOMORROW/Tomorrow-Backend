package com.umc.tomorrow.domain.career.entity;

import com.umc.tomorrow.domain.career.enums.WorkPeriodType;
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.global.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Career extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int workedYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkPeriodType workedPeriod;

    //연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    public void update(String company, String description, int workedYear, WorkPeriodType workedPeriod) {
        this.company = company;
        this.description = description;
        this.workedYear = workedYear;
        this.workedPeriod = workedPeriod;
    }
}
