/**
 * 파일 업로드 관련 컨트롤러
 * 작성자: 이승주
 * 작성일: 2025-07-28
 */
package com.umc.tomorrow.global.infrastructure.controller;

import com.umc.tomorrow.global.infrastructure.s3.S3Uploader;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "S3", description = "S3파일 관련 API")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Uploader s3Uploader;

    /**
     * 파일 업로드 (이미지, PDF 등 모두 가능)
     * @param file 업로드할 파일
     * @param dirName S3 디렉토리명
     * @return 업로드된 파일의 S3 URL
     */
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(summary = "파일 업로드", description = "파일과 디렉토리명을 받아 S3에 업로드합니다.")
    @ApiResponse(responseCode = "201", description = "파일 업로드 성공")
    public ResponseEntity<BaseResponse<String>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("dir") String dirName
    ) {
        String imageUrl = s3Uploader.upload(file, dirName);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.onSuccessCreate(imageUrl));
    }

    /**
     * 파일 삭제
     * @param fileUrl 삭제할 파일의 S3 전체 URL
     * @return 삭제한 파일의 URL
     */
    @DeleteMapping("/delete")
    @Operation(summary = "파일 삭제", description = "파일 url을 받아 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "파일 삭제 성공")
    public ResponseEntity<BaseResponse<String>> delete(@RequestParam("fileUrl") String fileUrl) {
        s3Uploader.delete(fileUrl);
        return ResponseEntity.ok(BaseResponse.onSuccess(fileUrl));
    }
}