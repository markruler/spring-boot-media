package com.example.demo.application.async;

import com.example.demo.application.util.FilePath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

@Slf4j
@Service
public class AsyncChunkFileService {

    // 파일 업로드 위치
    @Value("${video.upload.path}")
    private String uploadPath;

    /**
     * 파일 비동기 업로드 식별자 발급.
     * 식별자는 DB에 저장한다고 가정.
     *
     * @return 업로드 식별자
     */
    public String issueAsyncUploadId() {
        final String uploadId = UUID.randomUUID().toString();
        log.info("uploadChunkAsync uploadId: {}", uploadId);
        // repository.save(uploadChunkFile);
        return uploadId;
    }

    /**
     * 파일 비동기 업로드
     *
     * @param file     파일
     * @param uploadId 업로드 식별자
     * @return Etag (Entity Tag)
     */
    @Async
    public Future<String> uploadChunkAsync(
            final String uploadId,
            final String etag,
            final MultipartFile file
    ) throws IOException {
        log.info("uploadChunkAsync uploadId: {}", uploadId);

        long start = System.currentTimeMillis();
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 임시 저장 파일 이름
        final String originalFilename = file.getOriginalFilename();
        log.debug("Original file name: {}", originalFilename);

        // DB에 Etag와 index를 함께 저장한다면 Etag를 반환 받지만,
        // 간단한 데모를 위해 Etag를 반환 받지 않고 index만 반환 받는다.
        // var uploadChunkFile = repository.findById(uploadId);
        // uploadChunkFile.setMetadata(index, etag);
        // repository.save(uploadChunkFile);
        final Path filePath = Paths.get(uploadPath, chunkFilename(uploadId, etag));

        // Chunk file(Partial Content) 임시 저장
        Files.write(filePath, file.getBytes());

        long end = System.currentTimeMillis();
        log.info("File upload completed: {}, elapsed time: {}ms", LocalDateTime.now(), end - start);
        return new AsyncResult<>(etag);
    }

    @Deprecated
    private String generateEtag(MultipartFile file)
            throws NoSuchAlgorithmException, IOException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(file.getBytes()); // 리소스를 식별할 수 있는 digest 생성
        return DatatypeConverter.printHexBinary(digest).toUpperCase();
    }

    /**
     * 파일 업로드 완료
     *
     * @param uploadId 업로드 식별자
     * @param etags    파일 업로드 완료 후 반환된 ETag 목록
     * @return 성공 여부
     */
    public boolean completeChunkAsync(
            final String uploadId,
            final List<String> etags,
            final String fileExtension
    ) throws IOException {
        log.debug("complete uploadId: {}", uploadId);
        log.debug("Etags: {}", etags);
        // etags.sort(String::compareTo); // "1", "10", "2" 순으로 정렬됨.
        etags.sort((o1, o2) -> {
            int i1 = Integer.parseInt(o1);
            int i2 = Integer.parseInt(o2);
            return Integer.compare(i1, i2);
        });

        final String outputFilename = getOutputFilename(fileExtension);
        Path outputFile = Paths.get(uploadPath, outputFilename);
        Files.createFile(outputFile);

        // 임시 파일들을 하나로 합침
        for (String etag : etags) {
            Path chunkFile = Paths.get(uploadPath, chunkFilename(uploadId, etag));
            Files.write(outputFile, Files.readAllBytes(chunkFile), StandardOpenOption.APPEND);
            // 합친 후 삭제
            Files.delete(chunkFile);
        }

        return true;
    }

    private static String chunkFilename(String uploadId, String etag) {
        return uploadId + ".part." + etag;
    }

    private String getOutputFilename(String fileExtension) {
        return FilePath.epochTime()
                + "-"
                + uuid()
                + "."
                + fileExtension;
    }

    private String uuid() {
        return UUID
                .randomUUID()
                .toString()
                .replace("-", "")
                .toLowerCase();
    }

}
