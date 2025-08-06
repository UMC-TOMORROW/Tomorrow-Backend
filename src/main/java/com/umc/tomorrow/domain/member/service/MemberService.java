/**
 * 회원 정보 서비스
 * - 회원 정보 수정 등 비즈니스 로직 처리
 *
 * 작성자: 정여진
 * 생성일: 2025-07-10
 */
package com.umc.tomorrow.domain.member.service;

import com.umc.tomorrow.domain.member.dto.UserDTO;
import com.umc.tomorrow.domain.member.dto.request.DeactivateUserRequest;
import com.umc.tomorrow.domain.member.dto.response.DeactivateUserResponse;
import com.umc.tomorrow.domain.member.dto.response.RecoverUserResponse;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.enums.UserStatus;
import com.umc.tomorrow.domain.member.exception.code.MemberStatus;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.domain.member.dto.UserConverter;
import com.umc.tomorrow.global.common.exception.RestApiException;
import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MemberService {

    private final UserRepository userRepository;

    @Autowired
    public MemberService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 회원 정보 수정
     * @param currentUser 현재 로그인한 회원의 DTO (id 포함)
     * @param userDTO 수정할 정보가 담긴 DTO
     * @return 수정된 회원 정보 DTO
     */
    @Transactional
    public UserDTO updateUser(UserDTO currentUser, UserDTO userDTO) {
        Long userId = currentUser.getId();
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        UserConverter.updateEntity(user, userDTO);
        userRepository.save(user);
        return UserConverter.toDTO(user);
    }

    /**
     * User 엔티티를 UserDTO로 변환
     */
    public UserDTO toDTO(User user) {
        return UserConverter.toDTO(user);
    }

    /**
     * 회원 탈퇴 처리 (DELETED 상태로 변경)
     * 14일 이내 복구 가능
     */
    @Transactional
    public DeactivateUserResponse deactivateUser(Long userId, DeactivateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if (user.getStatus() == UserStatus.DELETED) {
            throw new IllegalStateException("이미 탈퇴한 사용자입니다.");
        }

        // 상태 변경 및 시간 기록
        user.setStatus(UserStatus.DELETED);
        user.setInactiveAt(LocalDateTime.now());

        return DeactivateUserResponse.builder()
                .status(user.getStatus().name())
                .deletedAt(user.getInactiveAt())
                .recoverableUntil(user.getInactiveAt().plusDays(14))
                .build();
    }

    /**
     * 회원 복구 처리 (ACTIVE 상태로 변경)
     * 14일 이내 복구 가능
     */
    @Transactional
    public RecoverUserResponse recoverUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

        if (user.getStatus() != UserStatus.DELETED) {
            throw new RestApiException(MemberStatus.NOT_DELETED_USER);
        }

        if (user.getInactiveAt().plusDays(14).isBefore(LocalDateTime.now())) {
            throw new RestApiException(MemberStatus.INVALID_RECOVERY_PERIOD);
        }

        user.setStatus(UserStatus.ACTIVE);
        user.setInactiveAt(null);

        return RecoverUserResponse.builder()
                .status(user.getStatus().name())
                .recoveredAt(LocalDateTime.now())
                .build();
    }


} 