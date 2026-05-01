package com.delivery.util;

import com.delivery.exception.DeliveryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class FileStorageUtil {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/png", "image/webp", "image/jpg",
            "image/*", "application/octet-stream"
    );

    public String saveFile(MultipartFile file, String subfolder) {
        if (file == null || file.isEmpty()) {
            throw DeliveryException.badRequest("Fichier vide");
        }

        String contentType = file.getContentType();
        if (!ALLOWED_TYPES.contains(contentType)) {
            throw DeliveryException.badRequest("Type de fichier non autorisé. Formats acceptés: JPG, PNG, WEBP");
        }

        try {
            Path uploadPath = Paths.get(uploadDir, subfolder);
            Files.createDirectories(uploadPath);

            String extension = getExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + "." + extension;
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String relativePath = "/" + uploadDir + subfolder + "/" + fileName;
            log.info("File saved: {}", relativePath);
            return relativePath;

        } catch (IOException e) {
            log.error("Error saving file: {}", e.getMessage());
            throw DeliveryException.badRequest("Erreur lors de la sauvegarde du fichier");
        }
    }

    public void deleteFile(String filePath) {
        if (filePath == null) return;
        try {
            Path path = Paths.get(filePath.replaceFirst("^/", ""));
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("Could not delete file: {}", filePath);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
