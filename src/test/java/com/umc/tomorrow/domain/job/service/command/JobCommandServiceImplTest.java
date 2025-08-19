//package com.umc.tomorrow.domain.job.service.command;
//
//import com.umc.tomorrow.domain.job.converter.JobConverter;
//import com.umc.tomorrow.domain.job.dto.request.BusinessRequestDTO;
//import com.umc.tomorrow.domain.job.dto.request.JobRequestDTO;
//import com.umc.tomorrow.domain.job.dto.request.PersonalRequestDTO;
//import com.umc.tomorrow.domain.job.dto.response.JobCreateResponseDTO;
//import com.umc.tomorrow.domain.job.dto.response.JobStepResponseDTO;
//import com.umc.tomorrow.domain.job.entity.BusinessVerification;
//import com.umc.tomorrow.domain.job.entity.Job;
//import com.umc.tomorrow.domain.job.entity.PersonalRegistration;
//import com.umc.tomorrow.domain.job.enums.RegistrantType;
//import com.umc.tomorrow.domain.job.exception.code.JobErrorStatus;
//import com.umc.tomorrow.domain.job.repository.JobRepository;
//import com.umc.tomorrow.domain.kakaoMap.service.KakaoMapService;
//import com.umc.tomorrow.domain.member.entity.User;
//import com.umc.tomorrow.domain.member.repository.UserRepository;
//import com.umc.tomorrow.domain.preferences.repository.PreferenceRepository;
//import com.umc.tomorrow.domain.review.repository.ReviewRepository;
//import com.umc.tomorrow.global.common.exception.RestApiException;
//import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
//import jakarta.servlet.http.HttpSession;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@DisplayName("JobCommandServiceImpl 등록/상태변경 단위 테스트")
//@ExtendWith(MockitoExtension.class)
//class JobCommandServiceImplTest {
//
//    @InjectMocks
//    private JobCommandServiceImpl service;
//
//    @Mock private JobConverter jobConverter;
//    @Mock private UserRepository userRepository;
//    @Mock private JobRepository jobRepository;
//    @Mock private ReviewRepository reviewRepository;
//    @Mock private PreferenceRepository preferenceRepository;
//    @Mock private KakaoMapService kakaoMapService;
//
//    // ===== saveInitialJobStep =====
//    @Test
//    @DisplayName("초기 단계 저장 성공: 세션에 DTO 저장되고 주소 세팅됨")
//    void saveInitialJobStep_성공() {
//        // given
//        Long userId = 1L;
//        HttpSession session = mock(HttpSession.class);
//        JobRequestDTO req = JobRequestDTO.builder()
//                .title("편의점 알바")
//                .latitude(BigDecimal.valueOf(37.0))
//                .longitude(BigDecimal.valueOf(127.0))
//                .registrantType(RegistrantType.PERSONAL)
//                .build();
//
//        when(kakaoMapService.getAddressFromCoord(BigDecimal.valueOf(37.0), BigDecimal.valueOf(127.0)))
//                .thenReturn("서울 강남구");
//        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
//
//        // when
//        JobStepResponseDTO resp = service.saveInitialJobStep(userId, req, session);
//
//        // then
//        assertAll(
//                () -> assertNotNull(resp),
//                () -> assertEquals(RegistrantType.PERSONAL, resp.getRegistrantType()),
//                () -> assertEquals("job_form_saved", resp.getStep())
//        );
//        verify(session).setAttribute(eq("job_session"), any(JobRequestDTO.class));
//    }
//
//    @Test
//    @DisplayName("초기 단계 저장 실패: 유저 없음 -> _NOT_FOUND")
//    void saveInitialJobStep_실패_유저없음() {
//        // given
//        Long userId = 99L;
//        HttpSession session = mock(HttpSession.class);
//        JobRequestDTO req = JobRequestDTO.builder()
//                .latitude(BigDecimal.ONE)
//                .longitude(BigDecimal.valueOf(2))
//                .registrantType(RegistrantType.PERSONAL)
//                .build();
//
//        when(kakaoMapService.getAddressFromCoord(BigDecimal.ONE, BigDecimal.valueOf(2)))
//                .thenReturn("인천 부평구");
//        when(userRepository.findById(userId)).thenReturn(Optional.empty());
//
//        // when & then
//        assertThrows(RestApiException.class, () -> service.saveInitialJobStep(userId, req, session));
//        verify(session).setAttribute(eq("job_session"), any());
//    }
//
//    // ===== savePersonalRegistration =====
//    @Test
//    @DisplayName("개인 등록 성공: 세션 DTO + 주소 변환 + 저장 + 세션삭제")
//    void savePersonalRegistration_성공() {
//        // given
//        Long userId = 1L;
//        HttpSession session = mock(HttpSession.class);
//
//        JobRequestDTO jobDTO = JobRequestDTO.builder()
//                .title("카페 알바")
//                .latitude(BigDecimal.valueOf(37.5))
//                .longitude(BigDecimal.valueOf(127.1))
//                .registrantType(RegistrantType.PERSONAL)
//                .build();
//
//        PersonalRequestDTO personalDTO = PersonalRequestDTO.builder()
//                .name("홍길동")
//                .latitude(BigDecimal.valueOf(37.6))
//                .longitude(BigDecimal.valueOf(127.2))
//                .build();
//
//        User user = User.builder().id(userId).build();
//        Job job = Job.builder().id(100L).user(user).build();
//        PersonalRegistration pr = PersonalRegistration.builder().build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(session.getAttribute("job_session")).thenReturn(jobDTO);
//        when(kakaoMapService.getAddressFromCoord(BigDecimal.valueOf(37.6), BigDecimal.valueOf(127.2)))
//                .thenReturn("서울 송파구");
//        when(jobConverter.toPersonal(any())).thenReturn(pr);
//        when(jobConverter.toJob(jobDTO)).thenReturn(job);
//        when(jobRepository.save(any(Job.class))).thenReturn(job);
//
//        // when
//        JobCreateResponseDTO resp = service.savePersonalRegistration(userId, personalDTO, session);
//
//        // then
//        assertAll(
//                () -> assertNotNull(resp),
//                () -> assertEquals(100L, resp.getJobId())
//        );
//        verify(session).removeAttribute("job_session");
//    }
//
//    @Test
//    @DisplayName("개인 등록 실패: 세션에 jobDTO 없음 -> JOB_DATA_NOT_FOUND")
//    void savePersonalRegistration_실패_세션없음() {
//        Long userId = 1L;
//        HttpSession session = mock(HttpSession.class);
//        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
//        when(session.getAttribute("job_session")).thenReturn(null);
//
//        RestApiException ex = assertThrows(RestApiException.class,
//                () -> service.savePersonalRegistration(userId, PersonalRequestDTO.builder().build(), session));
//        assertEquals(JobErrorStatus.JOB_DATA_NOT_FOUND.getCode().getCode(), ex.getErrorCode().getCode());
//    }
//
//    @Test
//    @DisplayName("개인 등록 실패: registrantType != PERSONAL -> INVALID_REGISTRANT_TYPE")
//    void savePersonalRegistration_실패_타입불일치() {
//        Long userId = 1L;
//        HttpSession session = mock(HttpSession.class);
//        JobRequestDTO jobDTO = JobRequestDTO.builder().registrantType(RegistrantType.BUSINESS).build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
//        when(session.getAttribute("job_session")).thenReturn(jobDTO);
//
//        RestApiException ex = assertThrows(RestApiException.class,
//                () -> service.savePersonalRegistration(userId, PersonalRequestDTO.builder().build(), session));
//        assertEquals(JobErrorStatus.INVALID_REGISTRANT_TYPE.getCode().getCode(), ex.getErrorCode().getCode());
//    }
//
//    // ===== createJobWithExistingBusiness =====
//    @Test
//    @DisplayName("사업자 등록 보유 사용자: BUSINESS 유형 등록 성공")
//    void createJobWithExistingBusiness_성공() {
//        Long userId = 10L;
//        HttpSession session = mock(HttpSession.class);
//
//        User user = User.builder().id(userId)
//                .businessVerification(BusinessVerification.builder().bizNumber("123").build())
//                .build();
//
//        JobRequestDTO jobDTO = JobRequestDTO.builder()
//                .latitude(BigDecimal.valueOf(37.1))
//                .longitude(BigDecimal.valueOf(127.3))
//                .registrantType(RegistrantType.BUSINESS)
//                .build();
//
//        Job job = Job.builder().id(200L).user(user).build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(session.getAttribute("job_session")).thenReturn(jobDTO);
//        when(kakaoMapService.getAddressFromCoord(BigDecimal.valueOf(37.1), BigDecimal.valueOf(127.3)))
//                .thenReturn("인천 연수구");
//        when(jobConverter.toJob(jobDTO)).thenReturn(job);
//        when(jobRepository.save(any(Job.class))).thenReturn(job);
//
//        JobCreateResponseDTO resp = service.createJobWithExistingBusiness(userId, session);
//
//        assertAll(
//                () -> assertNotNull(resp),
//                () -> assertEquals(200L, resp.getJobId())
//        );
//        verify(session).removeAttribute("job_session");
//    }
//
//    @Test
//    @DisplayName("사업자 등록 보유 사용자 실패: 사업자 정보 없음 -> _BAD_REQUEST")
//    void createJobWithExistingBusiness_실패_사업자없음() {
//        Long userId = 10L;
//        HttpSession session = mock(HttpSession.class);
//        User user = User.builder().id(userId).businessVerification(null).build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
//        RestApiException ex = assertThrows(RestApiException.class,
//                () -> service.createJobWithExistingBusiness(userId, session));
//        assertEquals(GlobalErrorStatus._BAD_REQUEST.getCode().getCode(), ex.getErrorCode().getCode());
//    }
//
//    @Test
//    @DisplayName("사업자 등록 보유 사용자 실패: 세션에 jobDTO 없음 -> JOB_DATA_NOT_FOUND")
//    void createJobWithExistingBusiness_실패_세션없음() {
//        Long userId = 10L;
//        HttpSession session = mock(HttpSession.class);
//        User user = User.builder().id(userId)
//                .businessVerification(BusinessVerification.builder().bizNumber("1").build())
//                .build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(session.getAttribute("job_session")).thenReturn(null);
//
//        RestApiException ex = assertThrows(RestApiException.class,
//                () -> service.createJobWithExistingBusiness(userId, session));
//        assertEquals(JobErrorStatus.JOB_DATA_NOT_FOUND.getCode().getCode(), ex.getErrorCode().getCode());
//    }
//
//    @Test
//    @DisplayName("사업자 등록 보유 사용자 실패: registrantType != BUSINESS -> INVALID_REGISTRANT_TYPE")
//    void createJobWithExistingBusiness_실패_타입불일치() {
//        Long userId = 10L;
//        HttpSession session = mock(HttpSession.class);
//        User user = User.builder().id(userId)
//                .businessVerification(BusinessVerification.builder().bizNumber("1").build())
//                .build();
//        JobRequestDTO jobDTO = JobRequestDTO.builder().registrantType(RegistrantType.PERSONAL).build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(session.getAttribute("job_session")).thenReturn(jobDTO);
//
//        RestApiException ex = assertThrows(RestApiException.class,
//                () -> service.createJobWithExistingBusiness(userId, session));
//        assertEquals(JobErrorStatus.INVALID_REGISTRANT_TYPE.getCode().getCode(), ex.getErrorCode().getCode());
//    }
//
//    // ===== registerBusinessAndCreateJob =====
//    @Test
//    @DisplayName("사업자 미등록 사용자: 사업자 등록 + BUSINESS 유형 등록 성공")
//    void registerBusinessAndCreateJob_성공() {
//        Long userId = 20L;
//        HttpSession session = mock(HttpSession.class);
//
//        User user = User.builder().id(userId).build();
//        BusinessRequestDTO bizReq = BusinessRequestDTO.builder()
//                .bizNumber("555-55-55555").companyName("내일").ownerName("홍길동").build();
//
//        JobRequestDTO jobDTO = JobRequestDTO.builder()
//                .latitude(BigDecimal.valueOf(36.9))
//                .longitude(BigDecimal.valueOf(127.9))
//                .registrantType(RegistrantType.BUSINESS)
//                .build();
//
//        Job job = Job.builder().id(300L).user(user).build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(session.getAttribute("job_session")).thenReturn(jobDTO);
//        when(kakaoMapService.getAddressFromCoord(BigDecimal.valueOf(36.9), BigDecimal.valueOf(127.9)))
//                .thenReturn("부천시");
//        when(jobConverter.toBusiness(bizReq)).thenReturn(BusinessVerification.builder().bizNumber("555").build());
//        when(jobConverter.toJob(jobDTO)).thenReturn(job);
//        when(jobRepository.save(any(Job.class))).thenReturn(job);
//
//        JobCreateResponseDTO resp = service.registerBusinessAndCreateJob(userId, bizReq, session);
//
//        assertAll(
//                () -> assertNotNull(resp),
//                () -> assertEquals(300L, resp.getJobId())
//        );
//        verify(userRepository, times(1)).save(user);
//        verify(session).removeAttribute("job_session");
//    }
//
//    @Test
//    @DisplayName("사업자 미등록 사용자 실패: 세션에 jobDTO 없음 -> JOB_DATA_NOT_FOUND")
//    void registerBusinessAndCreateJob_실패_세션없음() {
//        Long userId = 20L;
//        HttpSession session = mock(HttpSession.class);
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
//        when(session.getAttribute("job_session")).thenReturn(null);
//
//        RestApiException ex = assertThrows(RestApiException.class,
//                () -> service.registerBusinessAndCreateJob(userId, BusinessRequestDTO.builder().build(), session));
//        assertEquals(JobErrorStatus.JOB_DATA_NOT_FOUND.getCode().getCode(), ex.getErrorCode().getCode());
//    }
//
//    @Test
//    @DisplayName("사업자 미등록 사용자 실패: registrantType != BUSINESS -> INVALID_REGISTRANT_TYPE")
//    void registerBusinessAndCreateJob_실패_타입불일치() {
//        Long userId = 20L;
//        HttpSession session = mock(HttpSession.class);
//        JobRequestDTO jobDTO = JobRequestDTO.builder().registrantType(RegistrantType.PERSONAL).build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
//        when(session.getAttribute("job_session")).thenReturn(jobDTO);
//
//        RestApiException ex = assertThrows(RestApiException.class,
//                () -> service.registerBusinessAndCreateJob(userId, BusinessRequestDTO.builder().build(), session));
//        assertEquals(JobErrorStatus.INVALID_REGISTRANT_TYPE.getCode().getCode(), ex.getErrorCode().getCode());
//    }
//
//    // ===== saveBusinessVerification =====
//    @Test
//    @DisplayName("사업자 정보만 저장 성공")
//    void saveBusinessVerification_성공() {
//        Long userId = 30L;
//        User user = User.builder().id(userId).build();
//        BusinessRequestDTO req = BusinessRequestDTO.builder().bizNumber("111").companyName("내일").build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(jobConverter.toBusiness(req)).thenReturn(BusinessVerification.builder().bizNumber("111").build());
//
//        service.saveBusinessVerification(userId, req);
//
//        verify(userRepository).save(user);
//        assertNotNull(user.getBusinessVerification());
//    }
//}
