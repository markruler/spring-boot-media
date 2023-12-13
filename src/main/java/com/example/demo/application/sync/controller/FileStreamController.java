package com.example.demo.application.sync.controller;

import com.example.demo.application.util.FilePath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileStreamController {

    /**
     * Stream 업로드
     */
    @PostMapping("/stream/upload")
    public void uploadStream(
            final HttpServletRequest request
    ) throws IOException {
        log.debug("File stream upload started: {}", LocalDateTime.now());
        long start = System.currentTimeMillis();

        final String contentType = request.getContentType();
        log.debug("contentType: {}", contentType);
        final Long contentLengthLong = request.getContentLengthLong();
        log.debug("contentLengthLong: {}", contentLengthLong);

        final var servletInputStream = request.getInputStream();
        log.debug("inputStream: {}", servletInputStream.toString());

        final String path = FilePath.generate(contentType);
        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            // 데이터 읽기 및 쓰기
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = servletInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }
        log.debug("File stream upload completed: {}", LocalDateTime.now());
        log.debug("File stream upload took {} s", elapsedTime(start));
    }

    private double elapsedTime(long start) {
        long finish = System.currentTimeMillis();
        return (finish - start) / 1000d;
    }

}
