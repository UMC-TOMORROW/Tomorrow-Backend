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
import com.umc.tomorrow.domain.member.dto.response.DeactivateUserResponse;
import com.umc.tomorrow.domain.member.dto.response.RecoverUserResponse;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import com.umc.tomorrow.domain.member.exception.MemberStatus;

@Tag(name = "member-controller", description = "회원 관련 API")
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 회원의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe(@AuthenticationPrincipal CustomOAuth2User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        UserDTO userDTO = user.getUserDTO();
        return ResponseEntity.ok(userDTO);
    }

    /**
     * 내 정보 수정
     * 실제로 DB에 회원 정보가 반영되도록 MemberService를 호출
     */
    @Operation(summary = "내 정보 수정", description = "현재 로그인한 회원의 정보를 수정합니다.")
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateMe(@AuthenticationPrincipal CustomOAuth2User user, @RequestBody UserDTO userDTO) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        // 실제 DB에 회원 정보 업데이트
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
        return ResponseEntity.ok(BaseResponse.of(MemberStatus.MEMBER_DEACTIVATED, result));
    }
    @Operation(summary = "회원 복구", description = "14일 내에 탈퇴한 회원만 복구 가능합니다.")
    @PatchMapping("/{memberId}/recover")
    public ResponseEntity<BaseResponse<RecoverUserResponse>> recoverUser(
            @PathVariable("memberId") Long memberId
    ) {
        RecoverUserResponse result = memberService.recoverUser(memberId);
        return ResponseEntity.ok(BaseResponse.of(MemberStatus.MEMBER_RECOVERED, result));
    }
}
