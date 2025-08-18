/**
 * 회원 정보 서비스
 * - 회원 정보 수정 등 비즈니스 로직 처리
 *
 * 작성자: 정여진
 * 생성일: 2025-07-10
 */
package com.umc.tomorrow.domain.member.service;

import com.umc.tomorrow.domain.member.dto.UserResponseDTO;
import com.umc.tomorrow.domain.member.dto.UserUpdateDTO;
import com.umc.tomorrow.domain.member.dto.request.DeactivateUserRequest;
import com.umc.tomorrow.domain.member.dto.response.DeactivateUserResponse;
import com.umc.tomorrow.domain.member.dto.response.GetUserTypeResponse;
import com.umc.tomorrow.domain.member.dto.response.RecoverUserResponse;
import com.umc.tomorrow.domain.member.dto.response.UpdateUserTypeResponse;
import com.umc.tomorrow.domain.member.entity.MemberType;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.enums.UserStatus;
import com.umc.tomorrow.domain.member.exception.MemberException;
import com.umc.tomorrow.domain.member.exception.code.MemberErrorStatus;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.domain.member.dto.UserResponseConverter;
import com.umc.tomorrow.global.common.exception.RestApiException;
import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
import com.umc.tomorrow.domain.resume.repository.ResumeRepository;
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.domain.introduction.entity.Introduction;
import com.umc.tomorrow.global.infrastructure.s3.S3Uploader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MemberService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final S3Uploader fileUploadService;

    @Autowired
    public MemberService(UserRepository userRepository, ResumeRepository resumeRepository, S3Uploader fileUploadService) {
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.fileUploadService = fileUploadService;
    }

    /**
     * 회원 정보 수정
     * @param userId 현재 로그인한 회원의 ID
     * @param userUpdateDTO 수정할 정보가 담긴 DTO
     * @return 수정된 회원 정보 DTO
     */
    @Transactional
    public UserResponseDTO updateUser(Long userId, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));
        UserResponseConverter.updateEntity(user, userUpdateDTO);
        userRepository.save(user);
        return UserResponseConverter.toResponseDTO(user);
    }

    /**
     * User 엔티티를 UserResponseDTO로 변환
     */
    public UserResponseDTO toDTO(User user) {
        return UserResponseConverter.toResponseDTO(user);
    }

    /**
     * 회원 탈퇴 처리 (DELETED 상태로 변경)
     * 14일 이내 복구 가능
     */
    @Transactional
    public DeactivateUserResponse deactivateUser(Long userId, DeactivateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

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
            throw new RestApiException(MemberErrorStatus.NOT_DELETED_USER);
        }

        if (user.getInactiveAt().plusDays(14).isBefore(LocalDateTime.now())) {
            throw new RestApiException(MemberErrorStatus.INVALID_RECOVERY_PERIOD);
        }

        user.setStatus(UserStatus.ACTIVE);
        user.setInactiveAt(null);

        return RecoverUserResponse.builder()
                .status(user.getStatus().name())
                .recoveredAt(LocalDateTime.now())
                .build();
    }

    /**
     * 사용자의 resumeId가 null인 경우 Resume 엔티티에서 찾아서 업데이트
     * 이력서가 없는 경우 기본 이력서를 생성
     */
    @Transactional
    public void updateResumeIdIfNull(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));
        
        if (user.getResumeId() == null) {
            // 사용자의 Resume을 찾아서 resumeId 업데이트
            Optional<Resume> existingResume = resumeRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
            
            if (existingResume.isPresent()) {
                // 기존 이력서가 있는 경우 resumeId 업데이트
                user.setResumeId(existingResume.get().getId());
                userRepository.save(user);
            } else {
                // 이력서가 없는 경우 기본 이력서 생성
                createDefaultResumeForUser(user);
            }
        }
    }
    
    /**
     * 사용자에게 기본 이력서 생성 및 resumeId 할당
     */
    private void createDefaultResumeForUser(User user) {
        // 기본 이력서 생성
        Resume defaultResume = Resume.builder()
                .user(user)
                .build();
        
        // 기본 자기소개 생성
        Introduction defaultIntroduction = Introduction.builder()
                .content("안녕하세요! 저는 " + user.getName() + "입니다.")
                .resume(defaultResume)
                .build();
        
        defaultResume.setIntroduction(defaultIntroduction);
        
        // 이력서 저장
        Resume savedResume = resumeRepository.save(defaultResume);
        
        // 사용자의 resumeId 업데이트
        user.setResumeId(savedResume.getId());
        userRepository.save(user);
    }

    //유저 구인자인지 구직자인지 설정
    @Transactional
    public UpdateUserTypeResponse updateMemberType(Long userId, MemberType memberType) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

        user.setMemberType(memberType);

        return UpdateUserTypeResponse.builder()
                .userId(user.getId())
                .build();
    }

    //유저의 구인자 or 구직자 타입 조회
    public GetUserTypeResponse getMemberType(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

        return GetUserTypeResponse.builder()
                .memberType(user.getMemberType())
                .build();

    }

    /**
     * 프로필 이미지 업로드
     * 
     * @param userId 사용자 ID
     * @param imageFile 업로드할 이미지 파일
     * @return 업로드된 이미지 URL
     */
    @Transactional
    public String uploadProfileImage(Long userId, MultipartFile imageFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));
        
        // 기존 이미지가 있다면 삭제
        if (user.getProfileImageUrl() != null) {
            fileUploadService.delete(user.getProfileImageUrl());
        }
        
        // 새 이미지 업로드
        String imageUrl = fileUploadService.upload(imageFile, "profile/" + userId);
        
        // 사용자 정보 업데이트
        user.setProfileImageUrl(imageUrl);
        userRepository.save(user);
        
        return imageUrl;
    }

    /**
     * 프로필 이미지 삭제
     *
     * @param userId 사용자 ID
     * @return
     */
    @Transactional
    public boolean deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

        if (user.getProfileImageUrl() == null) {
            return false; // 삭제할 이미지 없음
        }

        // S3에서 이미지 삭제
        fileUploadService.delete(user.getProfileImageUrl());

        // 사용자 정보에서 이미지 URL 제거
        user.setProfileImageUrl(null);
        userRepository.save(user);
        return false;
    }
} 