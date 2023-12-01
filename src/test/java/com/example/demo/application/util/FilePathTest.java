package com.example.demo.application.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FilePathTest {

    @Test
    void test_generate() {
        String contentType = "video/mp4";
        String path = FilePath.generate(contentType);

        // assertThat(path).isEqualTo("local-video/1701397184059-b00011deebfe428ab00c66a4a040e6ac.mp4");
        assertThat(path)
                .startsWith("local-video/")
                .endsWith(".mp4")
                .hasSize(62);
    }

}
