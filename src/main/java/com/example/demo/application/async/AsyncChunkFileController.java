package com.example.demo.application.async;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RequestMapping("/async")
@RestController
@RequiredArgsConstructor
public class AsyncChunkFileController {

    private final AsyncChunkFileService service;

    @GetMapping("/init")
    public String init() {
        return service.issueAsyncUploadId();
    }

    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @PostMapping("/upload")
    public String upload(
            @RequestParam String uploadId,
            @RequestParam String index,
            @RequestPart("chunk") MultipartFile file
    ) throws IOException, NoSuchAlgorithmException, ExecutionException, InterruptedException, TimeoutException {
        return service.uploadChunkAsync(
                uploadId,
                index,
                file
        ).get(10, TimeUnit.SECONDS);
    }

    @PostMapping("/complete")
    public void complete(
            @RequestBody CompleteAsyncChunkUpload request
    ) throws IOException {
        final boolean result =
                service.completeChunkAsync(
                        request.getUploadId(),
                        request.getEtags(),
                        request.getFileExtension()
                );
        if (!result) {
            throw new IllegalArgumentException("파일 업로드에 실패했습니다.");
        }
    }

}
