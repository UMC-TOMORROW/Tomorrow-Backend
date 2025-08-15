/**
 * 회원 관련 API 컨트롤러
 * - GET /api/v1/members/me : 내 정보 조회
 * - PUT /api/v1/members/me : 내 정보 수정
 *
 * 작성자: 정여진
 * 생성일: 2025-07-10
 */
package com.umc.tomorrow.domain.member.controller;

import com.umc.tomorrow.domain.member.dto.UserDTO;
import com.umc.tomorrow.domain.member.dto.request.DeactivateUserRequest;
import com.umc.tomorrow.domain.member.dto.request.UpdateMemberTypeRequestDTO;
import com.umc.tomorrow.domain.member.dto.response.DeactivateUserResponse;
import com.umc.tomorrow.domain.member.dto.response.GetUserTypeResponse;
import com.umc.tomorrow.domain.member.dto.response.RecoverUserResponse;
import com.umc.tomorrow.domain.member.dto.response.UpdateUserTypeResponse;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import com.umc.tomorrow.domain.member.exception.code.MemberErrorStatus;
import com.umc.tomorrow.domain.member.exception.MemberException;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.domain.member.dto.UserConverter;
import com.umc.tomorrow.domain.member.entity.User;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "member-controller", description = "회원 관련 API")
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 회원의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe(@AuthenticationPrincipal CustomOAuth2User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        
        Long userId = user.getUserDTO().getId();

        memberService.updateResumeIdIfNull(userId);

        User updatedUser = userRepository.findById(userId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));
        
        UserDTO updatedUserDTO = UserConverter.toDTO(updatedUser);
        return ResponseEntity.ok(updatedUserDTO);
    }

    @Operation(summary = "내 정보 수정", description = "현재 로그인한 회원의 정보를 수정합니다. 이미지 파일도 함께 업로드할 수 있습니다.")
    @PutMapping(value = "/me", consumes = {"application/json", "multipart/form-data"})
    public ResponseEntity<UserDTO> updateMe(
            @AuthenticationPrincipal CustomOAuth2User user, 
            @Valid @RequestPart(value = "userInfo", required = false) UserDTO userDTO,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        
        if (imageFile != null) {
            try {
                String imageUrl = memberService.uploadProfileImage(user.getUserDTO().getId(), imageFile);
                if (userDTO == null) {
                    userDTO = new UserDTO();
                }
                // 이미지 URL을 userDTO에 설정
                userDTO = new UserDTO(
                        userDTO.getId(),
                        userDTO.getRole(),
                        userDTO.getUsername(),
                        userDTO.getEmail(),
                        userDTO.getName(),
                        userDTO.getGender(),
                        userDTO.getPhoneNumber(),
                        userDTO.getAddress(),
                        userDTO.getStatus(),
                        userDTO.getInactiveAt(),
                        userDTO.getIsOnboarded(),
                        userDTO.getProvider(),
                        userDTO.getProviderUserId(),
                        userDTO.getResumeId(),
                        imageUrl
                );
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        UserDTO updated = memberService.updateUser(user.getUserDTO(), userDTO);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 API입니다. 14일 내로 복구가 가능합니다.")
    @PatchMapping("/{memberId}/deactivate")
    public ResponseEntity<BaseResponse<DeactivateUserResponse>> deactivateUser(
            @PathVariable("memberId") Long memberId,
            @RequestBody DeactivateUserRequest request
    ) {
        DeactivateUserResponse result = memberService.deactivateUser(memberId, request);
        return ResponseEntity.ok(BaseResponse.of(MemberErrorStatus.MEMBER_DEACTIVATED, result));
    }
    @Operation(summary = "회원 복구", description = "14일 내에 탈퇴한 회원만 복구 가능합니다.")
    @PatchMapping("/{memberId}/recover")
    public ResponseEntity<BaseResponse<RecoverUserResponse>> recoverUser(
            @PathVariable("memberId") Long memberId
    ) {
        RecoverUserResponse result = memberService.recoverUser(memberId);
        return ResponseEntity.ok(BaseResponse.of(MemberErrorStatus.MEMBER_RECOVERED, result));
    }

    @Operation(
            summary = "내 역할(구인자/구직자) 설정",
            description = "현재 로그인한 회원의 역할을 설정/변경합니다. (EMPLOYER | JOB_SEEKER)"
    )
    @PatchMapping("/member-type")
    public ResponseEntity<BaseResponse<UpdateUserTypeResponse>> updateMyMemberType(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody UpdateMemberTypeRequestDTO request
    ){
        UpdateUserTypeResponse updated = memberService.updateMemberType(user.getUserDTO().getId(), request.getMemberType());
        return ResponseEntity.ok(BaseResponse.onSuccess(updated));
    }

    @Operation(
            summary = "내 역할 조회",
            description = "현재 로그인한 회원의 역할(EMPLOYER | JOB_SEEKER)을 조회합니다."
    )
    @GetMapping("/member-type")
    public ResponseEntity<BaseResponse<GetUserTypeResponse>> getMemberType(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomOAuth2User user) {

        GetUserTypeResponse getUserTypeResponse = memberService.getMemberType(user.getUserDTO().getId());
        return ResponseEntity.ok(BaseResponse.onSuccess(getUserTypeResponse));
    }

    @Operation(
            summary = "프로필 이미지 삭제",
            description = "프로필 이미지만 삭제합니다."
    )
    @DeleteMapping("/profile-image")
    public ResponseEntity<String> deleteProfileImage(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomOAuth2User user) {
        
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            memberService.deleteProfileImage(user.getUserDTO().getId());
            return ResponseEntity.ok("프로필 이미지가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("프로필 이미지 삭제에 실패했습니다: " + e.getMessage());
        }
    }

}
