package com.example.demo.application.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 비동기 청크 파일 업로드 완료
 *
 * @see com.example.demo.application.async.dto.CompleteAsyncChunkUpload
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
// com.fasterxml.jackson.databind.exc.InvalidDefinitionException:
// Problem deserializing 'setterless' property 'etags': get method returned null
@AllArgsConstructor
public class CompleteS3MultipartUpload {

    /**
     * 업로드 식별자
     */
    private String uploadId;

    /**
     * 업로드 한 Etag (Entity Tag) 목록
     */
    private List<UploadChunkFileMetadata> eTags;

    /**
     * 파일 확장자
     */
    private String fileExtension;

}
