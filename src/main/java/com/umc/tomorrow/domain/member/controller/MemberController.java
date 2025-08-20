/**
 * 회원 관련 API 컨트롤러
 * - GET /api/v1/members/me : 내 정보 조회
 * - PUT /api/v1/members/me : 내 정보 수정
 *
 * 작성자: 정여진
 * 생성일: 2025-07-10
 */
package com.umc.tomorrow.domain.member.controller;

import com.umc.tomorrow.domain.member.dto.UserResponseDTO;
import com.umc.tomorrow.domain.member.dto.UserUpdateDTO;
import com.umc.tomorrow.domain.member.dto.request.DeactivateUserRequest;
import com.umc.tomorrow.domain.member.dto.request.UpdateMemberTypeRequestDTO;
import com.umc.tomorrow.domain.member.dto.response.DeactivateUserResponse;
import com.umc.tomorrow.domain.member.dto.response.GetUserTypeResponse;
import com.umc.tomorrow.domain.member.dto.response.RecoverUserResponse;
import com.umc.tomorrow.domain.member.dto.response.UpdateUserTypeResponse;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import com.umc.tomorrow.domain.member.exception.code.MemberErrorStatus;
import com.umc.tomorrow.domain.member.exception.MemberException;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.domain.member.dto.UserResponseConverter;
import com.umc.tomorrow.domain.member.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<UserResponseDTO> getMe(@AuthenticationPrincipal CustomOAuth2User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        
        Long userId = user.getUserResponseDTO().getId();

        memberService.updateResumeIdIfNull(userId);

        User updatedUser = userRepository.findById(userId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));
        
        UserResponseDTO updatedUserDTO = UserResponseConverter.toResponseDTO(updatedUser);
        return ResponseEntity.ok(updatedUserDTO);
    }

    @Operation(summary = "내 정보 수정", description = "현재 로그인한 회원의 정보를 수정합니다.")
    @PutMapping(value="/me", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateMeJson(
            @AuthenticationPrincipal CustomOAuth2User user,
            @Valid @RequestBody UserUpdateDTO dto
    ){
        var updated = memberService.updateUser(user.getUserResponseDTO().getId(), dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "프로필 사진 업로드/수정", description = "현재 로그인한 회원의 프로필 사진을 업로드하거나 수정합니다.")
    @PutMapping(value="/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfileImage(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestPart("imageFile") MultipartFile image
    ){
        String url = memberService.uploadProfileImage(user.getUserResponseDTO().getId(), image);
        return ResponseEntity.ok(Map.of("profileImageUrl", url));
    }

    @Operation(summary = "프로필 사진 조회", description = "현재 로그인한 회원의 프로필 사진 URL을 조회합니다.")
    @GetMapping("/me/profile-image")
    public ResponseEntity<?> getProfileImage(@AuthenticationPrincipal CustomOAuth2User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        
        Long userId = user.getUserResponseDTO().getId();
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));
        
        return ResponseEntity.ok(Map.of("profileImageUrl", currentUser.getProfileImageUrl()));
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
        UpdateUserTypeResponse updated = memberService.updateMemberType(user.getUserResponseDTO().getId(), request.getMemberType());
        return ResponseEntity.ok(BaseResponse.onSuccess(updated));
    }

    @Operation(
            summary = "내 역할 조회",
            description = "현재 로그인한 회원의 역할(EMPLOYER | JOB_SEEKER)을 조회합니다."
    )
    @GetMapping("/member-type")
    public ResponseEntity<BaseResponse<GetUserTypeResponse>> getMemberType(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomOAuth2User user) {

        GetUserTypeResponse getUserTypeResponse = memberService.getMemberType(user.getUserResponseDTO().getId());
        return ResponseEntity.ok(BaseResponse.onSuccess(getUserTypeResponse));
    }

    @Operation(
            summary = "프로필 이미지 삭제",
            description = "프로필 이미지만 삭제합니다."
    )
    @DeleteMapping("/me/profile-image")
    public ResponseEntity<String> deleteProfileImage(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomOAuth2User user) {

        if (user == null) {
            return ResponseEntity.status(401).body("인증되지 않은 사용자입니다.");
        }

        try {
            boolean deleted = memberService.deleteProfileImage(user.getUserResponseDTO().getId());
            if (!deleted) {
                return ResponseEntity.badRequest().body("삭제할 이미지가 없습니다.");
            }
            return ResponseEntity.ok("프로필 이미지가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("프로필 이미지 삭제에 실패했습니다: " + e.getMessage());
        }
    }


    // 이 컨트롤러 안에 지역 예외처리(검증 실패 400 JSON)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleManve(MethodArgumentNotValidException ex) {
        var details = ex.getBindingResult().getFieldErrors().stream().map(fe -> {
            Object rej = fe.getRejectedValue();
            String s = rej == null ? null : rej.toString();
            if (s != null && s.length() > 200) s = s.substring(0,200) + "...";
            return Map.of("field", fe.getField(), "reason", fe.getDefaultMessage(), "rejectedValue", s);
        }).toList();
        return ResponseEntity.badRequest().body(Map.of(
                "code","VALIDATION_ERROR",
                "message","입력값이 유효하지 않습니다.",
                "errors", details
        ));
    }

}
