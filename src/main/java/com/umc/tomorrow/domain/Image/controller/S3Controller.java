package com.umc.tomorrow.domain.Image.controller;

import com.umc.tomorrow.domain.Image.s3.S3Uploader;
import com.umc.tomorrow.global.common.base.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Uploader s3Uploader;

    @PostMapping("/upload")
    public ResponseEntity<BaseResponse<String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = s3Uploader.upload(file, "tomorrow");
        return ResponseEntity.ok(BaseResponse.onSuccess(imageUrl));
    }
}