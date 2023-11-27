package com.example.demo.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

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
        String contentType = request.getContentType();
        log.debug("contentType: {}", contentType);
        Long contentLengthLong = request.getContentLengthLong();
        log.debug("contentLengthLong: {}", contentLengthLong);

        final ServletInputStream servletInputStream = request.getInputStream();
        log.debug("inputStream: {}", servletInputStream.toString());

        final String path =
                "local-video/"
                        + UUID.randomUUID()
                        + "."
                        + contentType.substring(contentType.indexOf("/") + 1);
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
    }

}
