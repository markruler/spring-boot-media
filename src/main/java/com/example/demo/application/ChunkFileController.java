package com.example.demo.application;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Controller
@RequiredArgsConstructor
public class ChunkFileController {

    private final ChunkFileService chunkFileService;

    @GetMapping("/")
    public String chunkUploadPage(Model model) {
        model.addAttribute("filelist", chunkFileService.listFiles());
        return "chunk";
    }

    /**
     * @see <a href="https://velog.io/@haerong22/%EC%98%81%EC%83%81-%EC%8A%A4%ED%8A%B8%EB%A6%AC%EB%B0%8D-2.-%ED%8C%8C%EC%9D%BC-%EB%B6%84%ED%95%A0-%EC%97%85%EB%A1%9C%EB%93%9C">파일 분할 업로드</a>
     * @see <a href="https://www.w3schools.com/html/html5_video.asp">Sample Video</a>
     */
    @ResponseBody
    @PostMapping("/chunk/upload")
    public ResponseEntity<String> chunkUpload(
            @RequestParam("chunk") MultipartFile file,
            @RequestParam("chunkNumber") int chunkNumber,
            @RequestParam("totalChunks") int totalChunks
    ) throws IOException {
        return chunkFileService.chunkUpload(file, chunkNumber, totalChunks) ?
                // 200 OK
                ResponseEntity.ok("File uploaded successfully") :
                // 206 Partial Content
                ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).build();
    }

    /**
     * @see <a href="https://velog.io/@haerong22/%EC%98%81%EC%83%81-%EC%8A%A4%ED%8A%B8%EB%A6%AC%EB%B0%8D-6.-%EC%8A%A4%ED%8A%B8%EB%A6%AC%EB%B0%8D">영상 스트리밍(video tag)</a>
     */
    @ResponseBody
    @GetMapping("/vod/{filename}")
    public ResponseEntity<Resource> vod(
            @PathVariable String filename
    ) throws IOException {
        final Resource resource = chunkFileService.getFilePath(filename);

        return ResponseEntity.ok()
                .contentType(
                        MediaTypeFactory
                                .getMediaType(resource)
                                .orElse(MediaType.APPLICATION_OCTET_STREAM))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    /**
     * @see <a href="https://velog.io/@haerong22/%EC%98%81%EC%83%81-%EC%8A%A4%ED%8A%B8%EB%A6%AC%EB%B0%8D-6.-%EC%8A%A4%ED%8A%B8%EB%A6%AC%EB%B0%8D">영상 스트리밍(video tag)</a>
     */
    @ResponseBody
    @GetMapping("/vod/chunk/{filename}")
    public ResponseEntity<ResourceRegion> vodChunk(
            @RequestHeader HttpHeaders headers,
            @PathVariable String filename
    ) throws IOException {
        final Resource resource = chunkFileService.getFilePath(filename);

        final long ONE_MB = 1024 * 1024L;
        long chunkSize = 5 * ONE_MB;
        long contentLength = resource.contentLength();

        HttpRange httpRange =
                headers
                        .getRange()
                        .stream()
                        .findFirst()
                        .orElse(
                                HttpRange
                                        .createByteRange(0, contentLength - 1));

        long rangeLength =
                calculateRangeLength(
                        httpRange,
                        contentLength,
                        chunkSize);
        ResourceRegion region =
                new ResourceRegion(
                        resource,
                        httpRange.getRangeStart(contentLength),
                        rangeLength);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .cacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES))
                .contentType(
                        MediaTypeFactory
                                .getMediaType(resource)
                                .orElse(MediaType.APPLICATION_OCTET_STREAM))
                .header("Accept-Ranges", "bytes")
                // .eTag(path)
                .body(region);
    }

    private long calculateRangeLength(
            final HttpRange httpRange,
            final long contentLength,
            final long chunkSize
    ) {
        long start = httpRange.getRangeStart(contentLength);
        long end = httpRange.getRangeEnd(contentLength);
        return Long.min(chunkSize, end - start + 1);
    }

}
