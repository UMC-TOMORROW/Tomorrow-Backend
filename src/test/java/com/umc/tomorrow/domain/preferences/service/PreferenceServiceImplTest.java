package com.umc.tomorrow.domain.preferences.service;

import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.exception.MemberException;
import com.umc.tomorrow.domain.member.exception.code.MemberErrorStatus;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.domain.preferences.converter.PreferenceConverter;
import com.umc.tomorrow.domain.preferences.dto.PreferencesDTO;
import com.umc.tomorrow.domain.preferences.entity.Preference;
import com.umc.tomorrow.domain.preferences.entity.PreferenceType;
import com.umc.tomorrow.domain.preferences.exception.PreferenceException;
import com.umc.tomorrow.domain.preferences.exception.code.PreferenceErrorStatus;
import com.umc.tomorrow.domain.preferences.repository.PreferenceRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
import org.junit.jupiter.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("PreferenceServiceImpl 희망 조건 저장/수정 단위 테스트")
@ExtendWith(MockitoExtension.class)
class PreferenceServiceImplTest {

    @InjectMocks
    private PreferenceServiceImpl service;

    @Mock
    private PreferenceRepository preferenceRepository;

    @Mock
    private UserRepository userRepository;

    private final Long userId = 1L;
    private final Long otherUserId = 99L;

    private User user;
    private User otherUser;

    @BeforeEach
    void setUp() {
        user = User.builder().id(userId).build();
        otherUser = User.builder().id(otherUserId).build();
    }

    // ===== savePreferences =====
    @Test
    @DisplayName("savePreferences: 새로운 희망 조건이 정상적으로 저장됨")
    void savePreferences_success() {
        // given
        Set<PreferenceType> preferenceTypes = new HashSet<>();
        preferenceTypes.add(PreferenceType.SIT);
        preferenceTypes.add(PreferenceType.HUMAN);
        
        PreferencesDTO requestDto = PreferencesDTO.builder()
                .preferences(preferenceTypes)
                .build();
        
        Preference savedPreference = Preference.builder()
                .id(1L)
                .user(user)
                .preferences(preferenceTypes)
                .build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(preferenceRepository.save(any(Preference.class))).thenReturn(savedPreference);
        
        // when
        PreferencesDTO result = service.savePreferences(userId, requestDto);
        
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(preferenceTypes, result.getPreferences()),
                () -> assertEquals(2, result.getPreferences().size()),
                () -> assertTrue(result.getPreferences().contains(PreferenceType.SIT)),
                () -> assertTrue(result.getPreferences().contains(PreferenceType.HUMAN))
        );
        
        verify(userRepository).findById(userId);
        verify(preferenceRepository).save(any(Preference.class));
    }

    @Test
    @DisplayName("savePreferences: 사용자를 찾을 수 없음 -> _NOT_FOUND")
    void savePreferences_userNotFound_throwsException() {
        // given
        Set<PreferenceType> preferenceTypes = new HashSet<>();
        preferenceTypes.add(PreferenceType.STAND);
        
        PreferencesDTO requestDto = PreferencesDTO.builder()
                .preferences(preferenceTypes)
                .build();
        
        when(userRepository.findById(otherUserId)).thenReturn(Optional.empty());
        
        // when & then
        RestApiException exception = assertThrows(RestApiException.class, 
                () -> service.savePreferences(otherUserId, requestDto));
        
        assertEquals(GlobalErrorStatus._NOT_FOUND.getCode(), exception.getErrorCode().getCode());
        verify(userRepository).findById(otherUserId);
        verify(preferenceRepository, never()).save(any());
    }

    // ===== updatePreferences =====
    @Test
    @DisplayName("updatePreferences: 기존 희망 조건이 정상적으로 수정됨")
    void updatePreferences_success() {
        // given
        Set<PreferenceType> originalPreferences = new HashSet<>();
        originalPreferences.add(PreferenceType.SIT);
        
        Set<PreferenceType> newPreferences = new HashSet<>();
        newPreferences.add(PreferenceType.STAND);
        newPreferences.add(PreferenceType.DELIVERY);
        
        PreferencesDTO requestDto = PreferencesDTO.builder()
                .preferences(newPreferences)
                .build();
        
        Preference existingPreference = Preference.builder()
                .id(1L)
                .user(user)
                .preferences(originalPreferences)
                .build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(preferenceRepository.findByUser(user)).thenReturn(Optional.of(existingPreference));
        when(preferenceRepository.save(any(Preference.class))).thenReturn(existingPreference);
        
        // when
        PreferencesDTO result = service.updatePreferences(userId, requestDto);
        
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(newPreferences, result.getPreferences()),
                () -> assertEquals(2, result.getPreferences().size()),
                () -> assertTrue(result.getPreferences().contains(PreferenceType.STAND)),
                () -> assertTrue(result.getPreferences().contains(PreferenceType.DELIVERY))
        );
        
        verify(userRepository).findById(userId);
        verify(preferenceRepository).findByUser(user);
        verify(preferenceRepository).save(existingPreference);
    }

    @Test
    @DisplayName("updatePreferences: 사용자를 찾을 수 없음 -> MEMBER_NOT_FOUND")
    void updatePreferences_userNotFound_throwsException() {
        // given
        Set<PreferenceType> preferenceTypes = new HashSet<>();
        preferenceTypes.add(PreferenceType.PHYSICAL);
        
        PreferencesDTO requestDto = PreferencesDTO.builder()
                .preferences(preferenceTypes)
                .build();
        
        when(userRepository.findById(otherUserId)).thenReturn(Optional.empty());
        
        // when & then
        MemberException exception = assertThrows(MemberException.class, 
                () -> service.updatePreferences(otherUserId, requestDto));
        
        assertEquals(MemberErrorStatus.MEMBER_NOT_FOUND.getCode(), exception.getErrorCode().getCode());
        verify(userRepository).findById(otherUserId);
        verify(preferenceRepository, never()).findByUser(any());
        verify(preferenceRepository, never()).save(any());
    }

    @Test
    @DisplayName("updatePreferences: 희망 조건을 찾을 수 없음 -> PREFERENCE_NOT_FOUND")
    void updatePreferences_preferenceNotFound_throwsException() {
        // given
        Set<PreferenceType> preferenceTypes = new HashSet<>();
        preferenceTypes.add(PreferenceType.HUMAN);
        
        PreferencesDTO requestDto = PreferencesDTO.builder()
                .preferences(preferenceTypes)
                .build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(preferenceRepository.findByUser(user)).thenReturn(Optional.empty());
        
        // when & then
        PreferenceException exception = assertThrows(PreferenceException.class, 
                () -> service.updatePreferences(userId, requestDto));
        
        assertEquals(PreferenceErrorStatus.PREFERENCE_NOT_FOUND.getCode(), exception.getErrorCode().getCode());
        verify(userRepository).findById(userId);
        verify(preferenceRepository).findByUser(user);
        verify(preferenceRepository, never()).save(any());
    }

    @Test
    @DisplayName("savePreferences: 빈 Set으로도 정상 저장됨")
    void savePreferences_emptySet_success() {
        // given
        Set<PreferenceType> emptyPreferences = new HashSet<>();
        
        PreferencesDTO requestDto = PreferencesDTO.builder()
                .preferences(emptyPreferences)
                .build();
        
        Preference savedPreference = Preference.builder()
                .id(1L)
                .user(user)
                .preferences(emptyPreferences)
                .build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(preferenceRepository.save(any(Preference.class))).thenReturn(savedPreference);
        
        // when
        PreferencesDTO result = service.savePreferences(userId, requestDto);
        
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.getPreferences().isEmpty()),
                () -> assertEquals(0, result.getPreferences().size())
        );
        
        verify(userRepository).findById(userId);
        verify(preferenceRepository).save(any(Preference.class));
    }

    @Test
    @DisplayName("savePreferences: 모든 PreferenceType 값으로 저장 가능")
    void savePreferences_allPreferenceTypes_success() {
        // given
        Set<PreferenceType> allPreferenceTypes = new HashSet<>();
        allPreferenceTypes.add(PreferenceType.SIT);
        allPreferenceTypes.add(PreferenceType.STAND);
        allPreferenceTypes.add(PreferenceType.DELIVERY);
        allPreferenceTypes.add(PreferenceType.PHYSICAL);
        allPreferenceTypes.add(PreferenceType.HUMAN);
        
        PreferencesDTO requestDto = PreferencesDTO.builder()
                .preferences(allPreferenceTypes)
                .build();
        
        Preference savedPreference = Preference.builder()
                .id(1L)
                .user(user)
                .preferences(allPreferenceTypes)
                .build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(preferenceRepository.save(any(Preference.class))).thenReturn(savedPreference);
        
        // when
        PreferencesDTO result = service.savePreferences(userId, requestDto);
        
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(5, result.getPreferences().size()),
                () -> assertTrue(result.getPreferences().contains(PreferenceType.SIT)),
                () -> assertTrue(result.getPreferences().contains(PreferenceType.STAND)),
                () -> assertTrue(result.getPreferences().contains(PreferenceType.DELIVERY)),
                () -> assertTrue(result.getPreferences().contains(PreferenceType.PHYSICAL)),
                () -> assertTrue(result.getPreferences().contains(PreferenceType.HUMAN))
        );
        
        verify(userRepository).findById(userId);
        verify(preferenceRepository).save(any(Preference.class));
    }
}
