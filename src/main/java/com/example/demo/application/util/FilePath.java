package com.example.demo.application.util;

import lombok.experimental.UtilityClass;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 파일 경로 생성
 */
@UtilityClass
public class FilePath {

    public static String generate(String contentType) {
        return "local-video/"
                + epochTime()
                + "-"
                + UUID.randomUUID().toString().replace("-", "").toLowerCase()
                + "."
                + contentType.substring(contentType.indexOf("/") + 1);
    }

    public static long epochTime() {
        return Timestamp.valueOf(LocalDateTime.now()).getTime();
    }

}
