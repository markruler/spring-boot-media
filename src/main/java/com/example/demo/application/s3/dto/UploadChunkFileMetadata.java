package com.example.demo.application.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UploadChunkFileMetadata {

    /**
     * 업로드 파일의 Etag
     */
    private String eTag;

    /**
     * 업로드 파일의 순서
     */
    private Integer index;

}
