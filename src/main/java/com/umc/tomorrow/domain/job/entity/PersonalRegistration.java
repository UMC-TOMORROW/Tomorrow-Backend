package com.umc.tomorrow.domain.job.entity;

import com.umc.tomorrow.global.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class PersonalRegistration extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column( precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(nullable = false, length = 20)
    private String contact;

    @Column(columnDefinition = "TEXT")
    private String registrationPurpose;

    @Column(length = 50)
    private String address;

    //연관관계
    @OneToOne(mappedBy = "personalRegistration")
    private Job job;
}
