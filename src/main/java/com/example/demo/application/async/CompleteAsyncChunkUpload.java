package com.example.demo.application.async;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * 비동기 청크 파일 업로드 완료
 */
@Getter
@ToString
public class CompleteAsyncChunkUpload {

    /**
     * 업로드 식별자
     */
    private String uploadId;

    /**
     * 업로드 한 Etag (Entity Tag) 목록
     */
    private List<String> etags;

    /**
     * 파일 확장자
     */
    private String fileExtension;

}
