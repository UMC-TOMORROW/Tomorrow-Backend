package com.umc.tomorrow.domain.introduction.service.command;

import com.umc.tomorrow.domain.introduction.converter.IntroductionConverter;
import com.umc.tomorrow.domain.introduction.dto.request.IntroductionCreateRequestDTO;
import com.umc.tomorrow.domain.introduction.dto.request.IntroductionUpdateRequestDTO;
import com.umc.tomorrow.domain.introduction.dto.response.GetIntroductionResponseDTO;
import com.umc.tomorrow.domain.introduction.dto.response.IntroductionResponseDTO;
import com.umc.tomorrow.domain.introduction.entity.Introduction;
import com.umc.tomorrow.domain.introduction.exception.code.IntroductionStatus;
import com.umc.tomorrow.domain.introduction.repository.IntroductionRepository;
import com.umc.tomorrow.domain.introduction.service.query.IntroductionQueryServiceImpl;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.domain.resume.repository.ResumeRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("IntroductionCommandService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class IntroductionServiceTest {

    @InjectMocks
    private IntroductionCommandServiceImpl introductionCommandService;

    @Mock
    private IntroductionRepository introductionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ResumeRepository resumeRepository;
    @Mock
    private IntroductionConverter introductionConverter;

    @InjectMocks
    private IntroductionQueryServiceImpl introductionQueryService;

    /*
       자기소개 추가
   */
    @Test
    @DisplayName("자기소개 추가 성공: 소유자 이력서에 자기소개가 저장된다")
    void saveIntroduction_성공() {
        // given
        Long userId = 1L;
        Long resumeId = 10L;
        IntroductionCreateRequestDTO dto = IntroductionCreateRequestDTO.builder()
                .content("저의 꿈은 개발자입니다.")
                .build();

        User owner = User.builder().id(userId).build();
        Resume resume = Resume.builder().id(resumeId).user(owner).build();

        Introduction savedIntro = Introduction.builder()
                .content(dto.getContent())
                .resume(resume)
                .build();

        IntroductionResponseDTO resp = new IntroductionResponseDTO(100L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
        when(introductionRepository.save(any(Introduction.class))).thenReturn(savedIntro);
        when(introductionConverter.toResponseDTO(any(Introduction.class))).thenReturn(resp);

        // when
        IntroductionResponseDTO result = introductionCommandService.saveIntroduction(userId, resumeId, dto);

        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(100L, result.getIntroductionId()),
                () -> assertNotNull(resume.getIntroduction())
        );
    }

    @Test
    @DisplayName("자기소개 추가 실패: 다른 유저의 이력서면 FORBIDDEN")
    void saveIntroduction_실패_권한없음() {
        // given
        Long userId = 1L;
        Long resumeId = 10L;
        IntroductionCreateRequestDTO dto = IntroductionCreateRequestDTO.builder()
                .content("안녕하세요. 한지혜입니다.")
                .build();

        User caller = User.builder().id(userId).build();
        User other = User.builder().id(2L).build();
        Resume resume = Resume.builder().id(resumeId).user(other).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(caller));
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));

        // when & then //실패는 when, then 한번에 처리
        RestApiException ex = assertThrows(RestApiException.class,
                () -> introductionCommandService.saveIntroduction(userId, resumeId, dto)
        );
        assertEquals(IntroductionStatus.INTRODUCTION_FORBIDDEN.getMessage(), ex.getErrorCode().getMessage());
    }

    @Test
    @DisplayName("자기소개 추가 실패: 이력서가 없으면 INTRODUCTION_NOT_FOUND")
    void saveIntroduction_실패_이력서없음() {
        // given
        Long userId = 1L;
        Long resumeId = 10L;
        IntroductionCreateRequestDTO dto = IntroductionCreateRequestDTO.builder()
                .content("내용")
                .build();

        User caller = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(caller));
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.empty());

        // when & then //실패는 when, then 한번에 처리
        RestApiException ex = assertThrows(RestApiException.class,
                () -> introductionCommandService.saveIntroduction(userId, resumeId, dto)
        );
        assertEquals(IntroductionStatus.INTRODUCTION_NOT_FOUND.getMessage(), ex.getErrorCode().getMessage());
    }

    /*
        자기소개 수정
    */
    @Test
    @DisplayName("자기소개 수정 성공: 기존 소개가 새 값으로 변경된다")
    void updateIntroduction_성공() {
        // given
        Long userId = 1L;
        Long resumeId = 10L;
        IntroductionUpdateRequestDTO dto = IntroductionUpdateRequestDTO.builder()
                .content("수정된 자기소개")
                .build();

        User owner = User.builder().id(userId).build();
        Resume resume = Resume.builder().id(resumeId).user(owner).build();
        Introduction intro = Introduction.builder()
                .content("기존 자기소개")
                .resume(resume)
                .build();
        resume.setIntroduction(intro);

        IntroductionResponseDTO resp = new IntroductionResponseDTO(200L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
        when(introductionRepository.save(any(Introduction.class))).thenReturn(intro);
        when(introductionConverter.toResponseDTO(any(Introduction.class))).thenReturn(resp);

        // when
        IntroductionResponseDTO result = introductionCommandService.updateIntroduction(userId, resumeId, dto);

        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(200L, result.getIntroductionId()),
                () -> assertEquals("수정된 자기소개", resume.getIntroduction().getContent())
        );
    }

    @Test
    @DisplayName("자기소개 수정 실패: 자기소개가 없으면 NOT_FOUND")
    void updateIntroduction_실패_자기소개없음() {
        // given
        Long userId = 1L;
        Long resumeId = 10L;
        IntroductionUpdateRequestDTO dto = IntroductionUpdateRequestDTO.builder()
                .content("수정 내용")
                .build();

        User owner = User.builder().id(userId).build();
        Resume resume = Resume.builder().id(resumeId).user(owner).build(); // introduction 없음

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));

        // when & then //실패는 when, then 한번에 처리
        RestApiException ex = assertThrows(RestApiException.class,
                () -> introductionCommandService.updateIntroduction(userId, resumeId, dto)
        );
        assertEquals(GlobalErrorStatus._NOT_FOUND.getCode().getMessage(), ex.getErrorCode().getMessage());
    }

    @Test
    @DisplayName("자기소개 수정 실패: 소유자가 아니면 FORBIDDEN")
    void updateIntroduction_실패_권한없음() {
        // given
        Long userId = 1L;
        Long resumeId = 10L;
        IntroductionUpdateRequestDTO dto = IntroductionUpdateRequestDTO.builder()
                .content("수정 내용")
                .build();

        User caller = User.builder().id(userId).build();
        User other = User.builder().id(2L).build();

        Resume resume = Resume.builder().id(resumeId).user(other).build();
        Introduction intro = Introduction.builder().content("기존").resume(resume).build();
        resume.setIntroduction(intro);

        when(userRepository.findById(userId)).thenReturn(Optional.of(caller));
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));

        // when & then //실패는 when, then 한번에 처리
        RestApiException ex = assertThrows(RestApiException.class,
                () -> introductionCommandService.updateIntroduction(userId, resumeId, dto)
        );
        assertEquals(IntroductionStatus.INTRODUCTION_FORBIDDEN.getMessage(), ex.getErrorCode().getMessage());
    }

    @Test
    @DisplayName("자기소개 조회 성공: 이력서에 연결된 자기소개를 반환한다")
    void getIntroduction_성공() {
        // given
        Long userId = 10L;
        Long resumeId = 10L;

        User user = User.builder().id(userId).build(); // 유저 생성
        Resume resume = Resume.builder()
                .id(resumeId)
                .user(user) // 이력서에 유저 연결
                .build();

        Introduction intro = Introduction.builder()
                .content("안녕하세요 저는 한지혜입니다.")
                .resume(resume)
                .build();
        resume.setIntroduction(intro);

        GetIntroductionResponseDTO resp = GetIntroductionResponseDTO.builder()
                .content(intro.getContent())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user)); // ✅ 유저 모킹
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
        when(introductionConverter.toGetResponseDTO(any(Introduction.class))).thenReturn(resp);

        // when
        GetIntroductionResponseDTO result = introductionQueryService.getIntroduction(userId, resumeId);

        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("안녕하세요 저는 한지혜입니다.", result.getContent())
        );
    }


    @Test
    @DisplayName("자기소개 조회 실패: 이력서가 없으면 INTRODUCTION_NOT_FOUND")
    void getIntroduction_실패_이력서없음() {
        // given
        Long userId = 1L;
        Long resumeId = 10L;

        // 유저는 정상 조회되도록 설정
        User user = User.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // 이력서는 없는 경우
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.empty());

        // when & then //실패는 when, then 한번에 처리
        RestApiException ex = assertThrows(RestApiException.class,
                () -> introductionQueryService.getIntroduction(userId, resumeId)
        );
        assertEquals(IntroductionStatus.INTRODUCTION_NOT_FOUND.getMessage(), ex.getErrorCode().getMessage());
    }


    @Test
    @DisplayName("자기소개 조회 실패: 자기소개가 없으면 NOT_FOUND")
    void getIntroduction_실패_자기소개없음() {
        // given
        Long userId = 1L;
        Long resumeId = 10L;
        Resume resume = Resume.builder()
                .id(resumeId)
                .user(User.builder().id(userId).build())
                .build();

        // 유저와 이력서 mocking
        User user = User.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));

        // when & then
        RestApiException ex = assertThrows(RestApiException.class,
                () -> introductionQueryService.getIntroduction(userId, resumeId)
        );
        assertEquals(GlobalErrorStatus._NOT_FOUND.getMessage(), ex.getErrorCode().getMessage());
    }

}
