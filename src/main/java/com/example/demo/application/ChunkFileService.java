package com.example.demo.application;

import com.example.demo.application.util.FilePath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ChunkFileService {

    // 파일 업로드 위치
    @Value("${video.upload.path}")
    private String uploadPath;

    public List<String> listFiles() {
        File dir = new File(uploadPath);

        // 파일 목록
        String[] files = dir.list();
        if (files != null) {
            return List.of(files);
        }

        return Collections.emptyList();
    }

    /**
     * 파일 동기 업로드
     *
     * @param file
     * @param chunkNumber
     * @param totalChunks
     * @return
     * @throws IOException
     */
    public boolean uploadChunk(
            final MultipartFile file,
            final int chunkNumber,
            final int totalChunks
    ) throws IOException {

        long start = System.currentTimeMillis();
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 임시 저장 파일 이름
        final String originalFilename = file.getOriginalFilename();
        String filename = originalFilename + ".part" + chunkNumber;
        Path filePath = Paths.get(uploadPath, filename);

        // Chunk file(Partial Content) 임시 저장
        Files.write(filePath, file.getBytes());

        // 첫 번째 조각이 전송 됐을 경우
        if (chunkNumber == 0) {
            log.info("File upload started: {}", LocalDateTime.now());
        }

        // 마지막 조각이 전송 됐을 경우
        if (chunkNumber == totalChunks - 1) {
            log.debug("File <{}> upload finished", originalFilename);
            String[] split = originalFilename.split("\\.");
            final String outputFilename =
                    FilePath.epochTime()
                            + "-"
                            + UUID.randomUUID().toString().replace("-", "").toLowerCase()
                            + "."
                            + split[split.length - 1];
            Path outputFile = Paths.get(uploadPath, outputFilename);
            Files.createFile(outputFile);

            // 임시 파일들을 하나로 합침
            for (int index = 0; index < totalChunks; index++) {
                Path chunkFile = Paths.get(uploadPath, originalFilename + ".part" + index);
                Files.write(outputFile, Files.readAllBytes(chunkFile), StandardOpenOption.APPEND);
                // 합친 후 삭제
                Files.delete(chunkFile);
            }
            log.info("File uploaded successfully: {}", LocalDateTime.now());
            log.debug("File stream upload took {} s", elapsedTime(start));
            return true;
        }
        log.debug("File stream upload took {} s", elapsedTime(start));
        return false;
    }

    private double elapsedTime(long start) {
        long finish = System.currentTimeMillis();
        return (finish - start) / 1000d;
    }

    public Resource getFilePath(String filename) {
        return new FileSystemResource(uploadPath + "/" + filename);
    }

}
