/**
 * 파일 업로드 관련 컨트롤러
 * 작성자: 이승주
 * 작성일: 2025-07-28
 */
package com.umc.tomorrow.domain.s3.controller;

import com.umc.tomorrow.domain.s3.s3.S3Uploader;
import com.umc.tomorrow.global.common.base.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping("/upload")
    public ResponseEntity<BaseResponse<String>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("dir") String dirName
            ) {
        String imageUrl = s3Uploader.upload(file, dirName);
        return ResponseEntity.ok(BaseResponse.onSuccess(imageUrl));
    }

    /**
     * 파일 삭제
     * @param fileUrl 삭제할 파일의 S3 전체 URL
     * @return 삭제한 파일의 URL
     */
    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse<String>> delete(@RequestParam("fileUrl") String fileUrl) {
        s3Uploader.delete(fileUrl);
        return ResponseEntity.ok(BaseResponse.onSuccess(fileUrl));
    }
}