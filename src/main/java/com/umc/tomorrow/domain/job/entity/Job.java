package com.umc.tomorrow.domain.job.entity;

import com.umc.tomorrow.domain.job.enums.PaymentType;
import com.umc.tomorrow.domain.job.enums.RegistrantType;
import com.umc.tomorrow.domain.job.enums.WorkPeriod;
import com.umc.tomorrow.domain.member.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "job",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_job_work_env", columnNames = "work_environment_id"),
                @UniqueConstraint(name = "uk_job_personal_reg", columnNames = "personal_registration_id"),
                @UniqueConstraint(name = "uk_job_work_days", columnNames = "work_days_id"),
                @UniqueConstraint(name = "uk_job_business_verification", columnNames = "business_verification_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(nullable = false, length = 30)
    private String jobCategory;

    @Enumerated(EnumType.STRING)
    private WorkPeriod workPeriod;

    @Column(columnDefinition = "BOOLEAN DEFAULT false", nullable = false) //false라면 workPeriod를 필수로 입력 받아야함
    private Boolean isPeriodNegotiable;

    private LocalDateTime workStart;

    private LocalDateTime workEnd;

    @Column(columnDefinition = "BOOLEAN DEFAULT false") //false라면 workStart, workEnd를 필수로 입력 받아야함
    private Boolean isTimeNegotiable;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    //private PaymentType paymentType;
    private PaymentType paymentType = PaymentType.HOURLY; //테스트용

    @Column(nullable = false)
    private Integer salary;

    @Lob
    private String jobDescription;

    private String jobImageUrl;

    @Column(length = 100)
    private String companyName;

    @Column(nullable = false)
    private Boolean isActive; //공고 활성화 여부

    @Column(nullable = false)
    private Integer recruitmentLimit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    //private RegistrantType registrantType;
    private RegistrantType registrantType = RegistrantType.BUSINESS;//테스트용

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Lob
    private String preferredQualifications;

    @Column(precision = 10, scale = 7, nullable = false)//지도 api추가 후 nullable = false로 변경
    private BigDecimal latitude; //위도

    @Column(precision = 10, scale = 7, nullable = false)//지도 api추가 후 nullable = false로 변경
    private BigDecimal longitude; //경도

    @Column(length = 100)//지도 api추가 후 nullable = false로 변경
    private String location;//위도, 경도를 받아서 address에 저장

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean alwaysHiring;

    //businessVerification와 1:1관계
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "business_verification_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_job_business_verification"))
    private BusinessVerification businessVerification;

    //personalRegistration와 1:1관계
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "personal_registration_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_job_personal_registration"))
    private PersonalRegistration personalRegistration;

    //workDays와 1:1관계
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "work_days_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_job_work_days"))
    //private WorkDays workDays;
    private WorkDays workDays;

    //WorkEnvironment와 1:1관계
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "work_environment_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_job_work_environment"))
    private WorkEnvironment workEnvironment;

    //user와 1:N와 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 등록자
}
