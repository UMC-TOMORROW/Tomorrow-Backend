//package com.umc.tomorrow.domain.career.service;
//
//import com.umc.tomorrow.domain.career.dto.request.CareerCreateRequestDTO;
//import com.umc.tomorrow.domain.career.dto.request.CareerUpdateRequestDTO;
//import com.umc.tomorrow.domain.career.dto.response.CareerCreateResponseDTO;
//import com.umc.tomorrow.domain.career.dto.response.CareerGetResponseDTO;
//import com.umc.tomorrow.domain.career.entity.Career;
//import com.umc.tomorrow.domain.career.enums.WorkPeriodType;
//import com.umc.tomorrow.domain.career.repository.CareerRepository;
//import com.umc.tomorrow.domain.career.service.conmmand.CareerCommandServiceImpl;
//import com.umc.tomorrow.domain.career.service.query.CareerQueryServiceImpl;
//import com.umc.tomorrow.domain.member.entity.User;
//import com.umc.tomorrow.domain.member.repository.UserRepository;
//import com.umc.tomorrow.domain.resume.entity.Resume;
//import com.umc.tomorrow.domain.resume.repository.ResumeRepository;
//import com.umc.tomorrow.global.common.exception.RestApiException;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.ArrayList;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@DisplayName("CareerService 단위 테스트 (CRUD 성공/실패 종합)")
//@ExtendWith(MockitoExtension.class)
//class CareerServiceTest {
//
//    @InjectMocks
//    private CareerCommandServiceImpl careerCommandService;
//
//    @InjectMocks
//    private CareerQueryServiceImpl careerQueryService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private CareerRepository careerRepository;
//
//    @Mock
//    private ResumeRepository resumeRepository;
//
//    // ===================== Command: save =====================
//
//    @Test
//    @DisplayName("경력 추가 성공")
//    void saveCareer_성공() {
//        // given
//        Long userId = 1L;
//        Long resumeId = 2L;
//        CareerCreateRequestDTO req = CareerCreateRequestDTO.builder()
//                .company("삼성전자")
//                .description("백엔드 개발")
//                .workedYear(2024)
//                .workedPeriod(WorkPeriodType.MORE_THAN_THREE_YEARS)
//                .build();
//
//        User user = User.builder().id(userId).build();
//        Resume resume = Resume.builder()
//                .id(resumeId)
//                .user(user)
//                .career(new ArrayList<>()) // 리스트 초기화 중요
//                .build();
//
//        Career saved = Career.builder().id(10L).resume(resume).build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
//        when(careerRepository.save(any(Career.class))).thenReturn(saved);
//
//        // when
//        CareerCreateResponseDTO result = careerCommandService.saveCareer(userId, resumeId, req);
//
//        // then
//        assertAll(
//                () -> assertNotNull(result),
//                () -> assertEquals(10L, result.getCareerId())
//        );
//    }
//
//    @Test
//    @DisplayName("경력 추가 실패: 유저 없음 → FORBIDDEN")
//    void saveCareer_실패_유저없음() {
//        // given
//        Long userId = 1L;
//        Long resumeId = 2L;
//        CareerCreateRequestDTO req = CareerCreateRequestDTO.builder()
//                .company("회사")
//                .description("설명")
//                .workedYear(2024)
//                .workedPeriod(WorkPeriodType.SHORT_TERM)
//                .build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.empty());
//
//        // when & then //실패는 when, then 한번에 처리
//        assertThrows(RestApiException.class,
//                () -> careerCommandService.saveCareer(userId, resumeId, req));
//    }
//
//    @Test
//    @DisplayName("경력 추가 실패: 이력서 없음 → NOT_FOUND")
//    void saveCareer_실패_이력서없음() {
//        // given
//        Long userId = 1L;
//        Long resumeId = 2L;
//        CareerCreateRequestDTO req = CareerCreateRequestDTO.builder()
//                .company("회사")
//                .description("설명")
//                .workedYear(2024)
//                .workedPeriod(WorkPeriodType.SHORT_TERM)
//                .build();
//
//        User user = User.builder().id(userId).build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(resumeRepository.findById(resumeId)).thenReturn(Optional.empty());
//
//        // when & then
//        assertThrows(RestApiException.class,
//                () -> careerCommandService.saveCareer(userId, resumeId, req));
//    }
//
//    @Test
//    @DisplayName("경력 추가 실패: 이력서 소유자 불일치 → FORBIDDEN")
//    void saveCareer_실패_권한없음() {
//        // given
//        Long userId = 1L;
//        Long resumeId = 2L;
//        CareerCreateRequestDTO req = CareerCreateRequestDTO.builder()
//                .company("회사")
//                .description("설명")
//                .workedYear(2024)
//                .workedPeriod(WorkPeriodType.SHORT_TERM)
//                .build();
//
//        User me = User.builder().id(userId).build();
//        User other = User.builder().id(99L).build();
//        Resume resume = Resume.builder().id(resumeId).user(other).build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(me));
//        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
//
//        // when & then
//        assertThrows(RestApiException.class,
//                () -> careerCommandService.saveCareer(userId, resumeId, req));
//    }
//
//    // ===================== Command: update =====================
//
//    @Test
//    @DisplayName("경력 수정 성공")
//    void updateCareer_성공() {
//        // given
//        Long userId = 1L;
//        Long resumeId = 2L;
//        Long careerId = 3L;
//
//        CareerUpdateRequestDTO req = CareerUpdateRequestDTO.builder()
//                .company("네이버")
//                .description("프론트엔드 개발")
//                .workedYear(2023)
//                .workedPeriod(WorkPeriodType.ONE_TO_TWO_YEARS)
//                .build();
//
//        User user = User.builder().id(userId).build();
//        Resume resume = Resume.builder().id(resumeId).user(user).build();
//        Career career = Career.builder()
//                .id(careerId)
//                .resume(resume)
//                .company("구회사")
//                .description("구직무")
//                .workedYear(2020)
//                .workedPeriod(WorkPeriodType.SHORT_TERM)
//                .build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
//        when(careerRepository.findById(careerId)).thenReturn(Optional.of(career));
//
//        // when
//        careerCommandService.updateCareer(userId, resumeId, careerId, req);
//
//        // then
//        assertAll(
//                () -> assertEquals("네이버", career.getCompany()),
//                () -> assertEquals("프론트엔드 개발", career.getDescription()),
//                () -> assertEquals(2023, career.getWorkedYear()),
//                () -> assertEquals(WorkPeriodType.ONE_TO_TWO_YEARS, career.getWorkedPeriod())
//        );
//    }
//
//    @Test
//    @DisplayName("경력 수정 실패: 경력 없음 → CAREER_DELETE_NOT_FOUND")
//    void updateCareer_실패_경력없음() {
//        // given
//        Long userId = 1L;
//        Long resumeId = 2L;
//        Long careerId = 3L;
//
//        CareerUpdateRequestDTO req = CareerUpdateRequestDTO.builder()
//                .company("회사")
//                .description("설명")
//                .workedYear(2024)
//                .workedPeriod(WorkPeriodType.SHORT_TERM)
//                .build();
//
//        User user = User.builder().id(userId).build();
//        Resume resume = Resume.builder().id(resumeId).user(user).build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
//        when(careerRepository.findById(careerId)).thenReturn(Optional.empty());
//
//        // when & then
//        assertThrows(RestApiException.class,
//                () -> careerCommandService.updateCareer(userId, resumeId, careerId, req));
//    }
//
//    @Test
//    @DisplayName("경력 수정 실패: 이력서-경력 불일치 → FORBIDDEN")
//    void updateCareer_실패_이력서경력불일치() {
//        // given
//        Long userId = 1L;
//        Long resumeId = 2L;           // 요청 이력서
//        Long actualResumeId = 7L;     // 경력의 실제 이력서
//        Long careerId = 3L;
//
//        CareerUpdateRequestDTO req = CareerUpdateRequestDTO.builder()
//                .company("회사")
//                .description("설명")
//                .workedYear(2024)
//                .workedPeriod(WorkPeriodType.SHORT_TERM)
//                .build();
//
//        User user = User.builder().id(userId).build();
//        Resume requestResume = Resume.builder().id(resumeId).user(user).build();
//        Resume otherResume = Resume.builder().id(actualResumeId).user(user).build();
//
//        Career career = Career.builder().id(careerId).resume(otherResume).build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(requestResume));
//        when(careerRepository.findById(careerId)).thenReturn(Optional.of(career));
//
//        // when & then
//        assertThrows(RestApiException.class,
//                () -> careerCommandService.updateCareer(userId, resumeId, careerId, req));
//    }
//
//    // ===================== Command: delete =====================
//
//    @Test
//    @DisplayName("경력 삭제 성공")
//    void deleteCareer_성공() {
//        // given
//        Long userId = 1L;
//        Long resumeId = 2L;
//        Long careerId = 3L;
//
//        User user = User.builder().id(userId).build();
//        Resume resume = Resume.builder().id(resumeId).user(user).build();
//        Career career = Career.builder().id(careerId).resume(resume).build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
//        when(careerRepository.findById(careerId)).thenReturn(Optional.of(career));
//
//        // when
//        careerCommandService.deleteCareer(userId, resumeId, careerId);
//
//        // then
//        verify(careerRepository, times(1)).delete(career);
//    }
//
//    @Test
//    @DisplayName("경력 삭제 실패: 이력서 소유자 불일치 → FORBIDDEN")
//    void deleteCareer_실패_권한없음() {
//        // given
//        Long userId = 1L;
//        Long resumeId = 2L;
//        Long careerId = 3L;
//
//        User me = User.builder().id(userId).build();
//        User other = User.builder().id(100L).build();
//        Resume resume = Resume.builder().id(resumeId).user(other).build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(me));
//        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
//
//        // when & then
//        assertThrows(RestApiException.class,
//                () -> careerCommandService.deleteCareer(userId, resumeId, careerId));
//    }
//
//    // ===================== Query: get =====================
//
//    @Test
//    @DisplayName("경력 조회 성공")
//    void getCareer_성공() {
//        // given
//        Long userId = 1L;
//        Long resumeId = 2L;
//        Long careerId = 3L;
//
//        User user = User.builder().id(userId).build();
//        Resume resume = Resume.builder().id(resumeId).user(user).build();
//        Career career = Career.builder()
//                .id(careerId)
//                .resume(resume)
//                .company("삼성전자")
//                .description("백엔드 개발")
//                .workedYear(2024)
//                .workedPeriod(WorkPeriodType.MORE_THAN_THREE_YEARS)
//                .build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
//        when(careerRepository.findById(careerId)).thenReturn(Optional.of(career));
//
//        // when
//        CareerGetResponseDTO result = careerQueryService.getCareer(userId, resumeId, careerId);
//
//        // then
//        assertAll(
//                () -> assertNotNull(result),
//                () -> assertEquals("삼성전자", result.getCompany()),
//                () -> assertEquals(2024, result.getWorkedYear())
//        );
//    }
//
//    @Test
//    @DisplayName("경력 조회 실패: 유저 없음 → FORBIDDEN")
//    void getCareer_실패_유저없음() {
//        // given
//        Long userId = 1L;
//        Long resumeId = 2L;
//        Long careerId = 3L;
//
//        when(userRepository.findById(userId)).thenReturn(Optional.empty());
//
//        // when & then
//        assertThrows(RestApiException.class,
//                () -> careerQueryService.getCareer(userId, resumeId, careerId));
//    }
//
//    @Test
//    @DisplayName("경력 조회 실패: 이력서 없음 → NOT_FOUND")
//    void getCareer_실패_이력서없음() {
//        // given
//        Long userId = 1L;
//        Long resumeId = 2L;
//        Long careerId = 3L;
//
//        User user = User.builder().id(userId).build();
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(resumeRepository.findById(resumeId)).thenReturn(Optional.empty());
//
//        // when & then
//        assertThrows(RestApiException.class,
//                () -> careerQueryService.getCareer(userId, resumeId, careerId));
//    }
//
//    @Test
//    @DisplayName("경력 조회 실패: 이력서 소유자 불일치 → FORBIDDEN")
//    void getCareer_실패_권한없음() {
//        // given
//        Long userId = 1L;
//        Long resumeId = 2L;
//        Long careerId = 3L;
//
//        User me = User.builder().id(userId).build();
//        User other = User.builder().id(9L).build();
//        Resume resume = Resume.builder().id(resumeId).user(other).build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(me));
//        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
//
//        // when & then
//        assertThrows(RestApiException.class,
//                () -> careerQueryService.getCareer(userId, resumeId, careerId));
//    }
//
//    @Test
//    @DisplayName("경력 조회 실패: 경력 없음 → NOT_FOUND")
//    void getCareer_실패_경력없음() {
//        // given
//        Long userId = 1L;
//        Long resumeId = 2L;
//        Long careerId = 3L;
//
//        User user = User.builder().id(userId).build();
//        Resume resume = Resume.builder().id(resumeId).user(user).build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
//        when(careerRepository.findById(careerId)).thenReturn(Optional.empty());
//
//        // when & then
//        assertThrows(RestApiException.class,
//                () -> careerQueryService.getCareer(userId, resumeId, careerId));
//    }
//
//    @Test
//    @DisplayName("경력 조회 실패: 이력서-경력 불일치 → FORBIDDEN")
//    void getCareer_실패_이력서경력불일치() {
//        // given
//        Long userId = 1L;
//        Long resumeId = 2L;        // 요청 이력서
//        Long careerId = 3L;
//
//        User user = User.builder().id(userId).build();
//        Resume requestResume = Resume.builder().id(resumeId).user(user).build();
//        Resume otherResume = Resume.builder().id(99L).user(user).build();
//
//        Career career = Career.builder().id(careerId).resume(otherResume).build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(requestResume));
//        when(careerRepository.findById(careerId)).thenReturn(Optional.of(career));
//
//        // when & then
//        assertThrows(RestApiException.class,
//                () -> careerQueryService.getCareer(userId, resumeId, careerId));
//    }
//}
