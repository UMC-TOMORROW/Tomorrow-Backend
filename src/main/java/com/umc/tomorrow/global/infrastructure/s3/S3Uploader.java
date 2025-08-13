/**
 * S3 Image uploader
 * 작성자: 이승주
 * 작성일: 2025-07-28
 */
package com.umc.tomorrow.global.infrastructure.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.umc.tomorrow.global.common.exception.RestApiException;
import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
import java.io.IOException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class S3Uploader {

    private AmazonS3 amazonS3;
    private final String bucketName;

    public S3Uploader(AmazonS3 amazonS3,
                      @Value("${cloud.aws.s3.bucket}") String bucketName) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
    }

    public String upload(MultipartFile file, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(bucketName, fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RestApiException(GlobalErrorStatus._S3_UPLOAD_ERROR);
        }

        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    public void delete(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);
        // S3 delete는 대상이 없어도 성공 처리됨 (idempotent)
        amazonS3.deleteObject(bucketName, key);
    }

    private String extractKeyFromUrl(String fileUrl) {
        String bucketUrl = amazonS3.getUrl(bucketName, "").toString(); // ex: https://bucket.s3.amazonaws.com/
        return fileUrl.replace(bucketUrl, "");
    }

}
