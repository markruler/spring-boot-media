package com.example.demo.application.s3.controller;

import com.example.demo.application.s3.dto.CompleteS3MultipartUpload;
import com.example.demo.application.s3.dto.UploadChunkFileMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/s3")
@RestController
@RequiredArgsConstructor
public class S3UploadController {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final String S3_DEMO_KEY = "videos/demo/example.mp4";

    private final S3Client s3Client;

    @GetMapping("/init")
    public String init() {
        final CreateMultipartUploadResponse createMultipartUpload =
                s3Client.createMultipartUpload(
                        builder ->
                                builder
                                        .bucket(bucket)
                                        .key(S3_DEMO_KEY)
                                        .build()
                );
        return createMultipartUpload.uploadId();
    }

    @PostMapping("/upload")
    public UploadChunkFileMetadata upload(
            @RequestParam String uploadId,
            @RequestParam Integer index,
            @RequestPart("chunk") MultipartFile file
    ) throws IOException {
        UploadPartResponse uploadPartResponse = s3Client.uploadPart(
                builder ->
                        builder
                                .bucket(bucket)
                                .key(S3_DEMO_KEY)
                                .uploadId(uploadId)
                                .partNumber(index)
                                .build(),
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes())
        );
        return new UploadChunkFileMetadata(
                uploadPartResponse.eTag().replace("\"", ""),
                index
        );
    }

    @PostMapping("/complete")
    public String complete(
            @RequestBody CompleteS3MultipartUpload request
    ) {
        List<CompletedPart> parts = new ArrayList<>();
        for (UploadChunkFileMetadata metadata : request.getETags()) {
            parts.add(
                    CompletedPart.builder()
                            .eTag(metadata.getETag())
                            .partNumber(metadata.getIndex())
                            .build()
            );
        }

        CompleteMultipartUploadResponse completeMultipartUploadResponse =
                s3Client.completeMultipartUpload(
                completeMultipartUploadRequest ->
                        completeMultipartUploadRequest
                                .bucket(bucket)
                                .key(S3_DEMO_KEY)
                                .uploadId(request.getUploadId())
                                .multipartUpload(
                                        completedMultipartUpload ->
                                                completedMultipartUpload
                                                        .parts(parts)
                                                        .build()
                                )
                                .build()
        );
        return completeMultipartUploadResponse.location();
    }

}
