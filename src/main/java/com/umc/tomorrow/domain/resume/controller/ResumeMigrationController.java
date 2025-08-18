package com.umc.tomorrow.domain.resume.controller;

import com.umc.tomorrow.domain.resume.service.ResumeMigrationService;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 이력서 마이그레이션을 위한 컨트롤러
 * - 기존 사용자들의 resumeId 문제 해결
 */
@Slf4j
@Tag(name = "resume-migration-controller", description = "이력서 마이그레이션 API")
@RestController
@RequestMapping("/api/v1/resumes/migration")
@RequiredArgsConstructor
public class ResumeMigrationController {

    private final ResumeMigrationService resumeMigrationService;

    /**
     * 모든 resumeId가 없는 사용자에게 기본 이력서 생성 및 resumeId 할당
     */
    @Operation(summary = "전체 사용자 resumeId 마이그레이션", 
               description = "resumeId가 없는 모든 사용자에게 기본 이력서를 생성하고 resumeId를 할당합니다.")
    @PostMapping("/all")
    public ResponseEntity<BaseResponse<String>> migrateAllUsersWithoutResumeId() {
        try {
            resumeMigrationService.migrateUsersWithoutResumeId();
            return ResponseEntity.ok(BaseResponse.onSuccess("모든 사용자 resumeId 마이그레이션이 완료되었습니다."));
        } catch (Exception e) {
            log.error("전체 사용자 resumeId 마이그레이션 실패", e);
            return ResponseEntity.internalServerError()
                    .body(BaseResponse.onFailure("COMMON500", "마이그레이션 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 특정 사용자에게 기본 이력서 생성 및 resumeId 할당
     */
    @Operation(summary = "특정 사용자 resumeId 마이그레이션", 
               description = "지정된 사용자에게 기본 이력서를 생성하고 resumeId를 할당합니다.")
    @PostMapping("/user/{userId}")
    public ResponseEntity<BaseResponse<String>> createDefaultResumeForUser(@PathVariable Long userId) {
        try {
            resumeMigrationService.createDefaultResumeForUser(userId);
            return ResponseEntity.ok(BaseResponse.onSuccess("사용자 " + userId + "의 기본 이력서 생성이 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.onFailure("COMMON400", "잘못된 요청: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("사용자 {} resumeId 마이그레이션 실패", userId, e);
            return ResponseEntity.internalServerError()
                    .body(BaseResponse.onFailure("COMMON500", "마이그레이션 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
    }
}
