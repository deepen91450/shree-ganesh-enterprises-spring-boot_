package com.shreeganesh.enterprises.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class UploadStorageService {

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS =
            Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");

    private static final Set<String> ALLOWED_IMAGE_CONTENT_TYPES =
            Set.of("image/jpeg", "image/png", "image/gif", "image/webp");

    private final Path uploadRoot;

    public UploadStorageService(
            @Value("${app.upload-dir:F:/enterprises/uploads}") String uploadDir
    ) {
        this.uploadRoot = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();
    }

    public String storeImage(MultipartFile file, String subDirectory) throws IOException {

        if (file == null || file.isEmpty()) {
            return null;
        }

        String extension = getAllowedExtension(file);

        String cleanSubDirectory = cleanSubDirectory(subDirectory);

        String fileName = UUID.randomUUID() + extension;

        Path targetDirectory = uploadRoot
                .resolve(cleanSubDirectory)
                .normalize();

        if (!targetDirectory.startsWith(uploadRoot)) {
            throw new IllegalArgumentException("Invalid upload directory");
        }

        Files.createDirectories(targetDirectory);

        Path targetFile = targetDirectory
                .resolve(fileName)
                .normalize();

        if (!targetFile.startsWith(targetDirectory)) {
            throw new IllegalArgumentException("Invalid upload filename");
        }

        Files.copy(
                file.getInputStream(),
                targetFile,
                StandardCopyOption.REPLACE_EXISTING
        );

        return cleanSubDirectory.isBlank()
                ? "/uploads/" + fileName
                : "/uploads/" + cleanSubDirectory + "/" + fileName;
    }

    public void deletePublicFile(String publicPath) throws IOException {

        if (publicPath == null || !publicPath.startsWith("/uploads/")) {
            return;
        }

        String relativePath =
                publicPath.substring("/uploads/".length());

        Path targetFile = uploadRoot
                .resolve(relativePath)
                .normalize();

        if (targetFile.startsWith(uploadRoot)) {
            Files.deleteIfExists(targetFile);
        }
    }

    private String getAllowedExtension(MultipartFile file) {

        String contentType = file.getContentType();

        if (contentType == null
                || !ALLOWED_IMAGE_CONTENT_TYPES.contains(
                contentType.toLowerCase(Locale.ROOT))) {

            throw new IllegalArgumentException(
                    "Only JPG, PNG, GIF, and WebP images are allowed"
            );
        }

        String originalName = file.getOriginalFilename();

        if (originalName == null) {
            throw new IllegalArgumentException(
                    "Uploaded file must have a filename"
            );
        }

        String fileName = originalName.replace('\\', '/');

        fileName = fileName.substring(
                fileName.lastIndexOf('/') + 1
        );

        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex < 0) {
            throw new IllegalArgumentException(
                    "Uploaded image must have a file extension"
            );
        }

        String extension = fileName
                .substring(dotIndex)
                .toLowerCase(Locale.ROOT);

        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {

            throw new IllegalArgumentException(
                    "Only JPG, PNG, GIF, and WebP images are allowed"
            );
        }

        return extension;
    }

    private String cleanSubDirectory(String subDirectory) {

        if (subDirectory == null || subDirectory.isBlank()) {
            return "";
        }

        return subDirectory.replace('\\', '/')
                .replaceAll("^/+", "")
                .replaceAll("/+$", "");
    }
}