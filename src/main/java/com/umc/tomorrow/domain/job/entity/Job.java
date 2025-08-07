package com.umc.tomorrow.domain.job.entity;

import com.umc.tomorrow.domain.job.enums.*;
import com.umc.tomorrow.domain.application.entity.Application;
import com.umc.tomorrow.domain.jobbookmark.entity.JobBookmark;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.global.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_job_work_env", columnNames = "work_environment_id"),
                @UniqueConstraint(name = "uk_job_personal_reg", columnNames = "personal_registration_id"),
                @UniqueConstraint(name = "uk_job_work_days", columnNames = "work_days_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobCategory jobCategory;

    @Enumerated(EnumType.STRING)
    private WorkPeriod workPeriod;

    @Builder.Default
    @Column(columnDefinition = "BOOLEAN DEFAULT false", nullable = false) //false라면 workPeriod를 필수로 입력 받아야함
    private Boolean isPeriodNegotiable = false;

    private LocalTime workStart;

    private LocalTime workEnd;

    @Builder.Default
    @Column(columnDefinition = "BOOLEAN DEFAULT false") //false라면 workStart, workEnd를 필수로 입력 받아야함
    private Boolean isTimeNegotiable = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @Column(nullable = false)
    private Integer salary;

    @Lob
    private String jobDescription;

    private String jobImageUrl;

    @Column(length = 100)
    private String companyName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PostStatus status = PostStatus.OPEN;

    @Column(nullable = false)
    private Integer recruitmentLimit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrantType registrantType;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Lob
    private String preferredQualifications;

    @Column(precision = 10, scale = 7, nullable = false)
    private BigDecimal latitude; //위도

    @Column(precision = 10, scale = 7, nullable = false)
    private BigDecimal longitude; //경도

    @Column(length = 100)
    private String location;//위도, 경도를 받아서 location에 저장

    @Builder.Default
    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean alwaysHiring = false;

    //personalRegistration와 1:1관계
    @OneToOne(cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "personal_registration_id", nullable = true,
            foreignKey = @ForeignKey(name = "fk_job_personal_registration"))
    private PersonalRegistration personalRegistration;

    //workDays와 1:1관계
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "work_days_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_job_work_days"))
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
    
    // 지원서와 1:N 관계
    @Builder.Default
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();

    // 찜과 1:N 관계
    @Builder.Default
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobBookmark> jobBookmarks = new ArrayList<>();

    public void updateStatus(PostStatus newStatus) {
        this.status = newStatus;
    }
}
