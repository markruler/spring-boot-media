package com.example.demo.application.sync.controller;

import com.example.demo.application.util.FilePath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {

    // 파일 업로드 위치
    @Value("${video.upload.path}")
    private String uploadPath;

    /**
     * Stream 업로드
     */
    @PostMapping("/upload")
    public void uploadFile(
            @RequestPart(name = "file") final MultipartFile file
    ) throws IOException {
        long start = System.currentTimeMillis();

        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 파일 이름
        String originalFilename = file.getOriginalFilename();
        String[] split = originalFilename.split("\\.");
        final String outputFilename =
                FilePath.epochTime()
                        + "-"
                        + UUID.randomUUID().toString().replace("-", "").toLowerCase()
                        + "."
                        + split[split.length - 1];
        Path filePath = Paths.get(uploadPath, outputFilename);

        Files.write(filePath, file.getBytes());

        log.debug("File stream upload took {} s", elapsedTime(start));
    }

    private double elapsedTime(long start) {
        long finish = System.currentTimeMillis();
        return (finish - start) / 1000d;
    }

}
