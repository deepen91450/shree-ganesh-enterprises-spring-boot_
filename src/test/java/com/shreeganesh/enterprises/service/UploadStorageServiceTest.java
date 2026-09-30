package com.shreeganesh.enterprises.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UploadStorageServiceTest {

    @TempDir
    Path uploadRoot;

    @Test
    void storesImagesWithGeneratedSafeNames() throws Exception {
        UploadStorageService storage = new UploadStorageService(uploadRoot);
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "../unsafe.png",
                "image/png",
                "image-content".getBytes()
        );

        String publicPath = storage.storeImage(file, "products");

        assertThat(publicPath).startsWith("/uploads/products/");
        assertThat(publicPath).endsWith(".png");
        assertThat(Files.list(uploadRoot.resolve("products")).count()).isEqualTo(1);
    }

    @Test
    void rejectsNonImages() {
        UploadStorageService storage = new UploadStorageService(uploadRoot);
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "payload.html",
                "text/html",
                "<script>alert(1)</script>".getBytes()
        );

        assertThatThrownBy(() -> storage.storeImage(file, "products"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only JPG, PNG, GIF, and WebP images are allowed");
    }
}
