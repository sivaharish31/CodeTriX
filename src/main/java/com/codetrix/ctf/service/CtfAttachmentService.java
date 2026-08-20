package com.codetrix.ctf.service;

import com.codetrix.ctf.exception.CtfException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class CtfAttachmentService {

    private final Path uploadDir;

    private static final Set<String> BLOCKED_EXTENSIONS = Set.of(
        ".exe", ".bat", ".cmd", ".com", ".msi", ".dll", ".scr", ".ps1",
        ".sh", ".bash", ".zsh", ".csh", ".ksh",
        ".jar", ".class", ".war", ".ear",
        ".php", ".phar", ".asp", ".aspx", ".jsp", ".jspx",
        ".py", ".pyc", ".pyo", ".pyw",
        ".rb", ".pl", ".cgi",
        ".vbs", ".vbe", ".wsf", ".wsh",
        ".hta", ".htaccess"
    );

    private static final Set<String> BLOCKED_CONTENT_TYPES = Set.of(
        "application/x-msdownload",
        "application/x-msdos-program",
        "application/x-executable",
        "application/x-sh",
        "application/x-csh",
        "application/x-httpd-php"
    );

    public CtfAttachmentService(@Value("${ctf.upload.dir:uploads/ctf}") String uploadPath) {
        this.uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create CTF upload directory", e);
        }
    }

    public AttachmentInfo saveAttachment(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "attachment";
        }

        validateFile(originalFilename, file.getContentType());

        String extension = getExtension(originalFilename);
        String storedFilename = UUID.randomUUID() + extension;

        try {
            Path targetPath = uploadDir.resolve(storedFilename).normalize();

            if (!targetPath.startsWith(uploadDir)) {
                throw CtfException.attachmentUploadFailed("Invalid file path");
            }

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Saved CTF attachment: {} as {}", originalFilename, storedFilename);

            return new AttachmentInfo(
                originalFilename,
                targetPath.toString(),
                file.getContentType()
            );
        } catch (IOException e) {
            log.error("Failed to save attachment: {}", e.getMessage());
            throw CtfException.attachmentUploadFailed(e.getMessage());
        }
    }

    public Resource loadAttachment(String storedPath) {
        try {
            Path filePath = Paths.get(storedPath).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw CtfException.attachmentNotFound(null);
            }
        } catch (MalformedURLException e) {
            throw CtfException.attachmentNotFound(null);
        }
    }

    public void deleteAttachment(String storedPath) {
        if (storedPath == null || storedPath.isEmpty()) {
            return;
        }

        try {
            Path filePath = Paths.get(storedPath).normalize();
            Files.deleteIfExists(filePath);
            log.info("Deleted CTF attachment: {}", storedPath);
        } catch (IOException e) {
            log.warn("Failed to delete attachment: {}", e.getMessage());
        }
    }

    private void validateFile(String filename, String contentType) {
        String extension = getExtension(filename).toLowerCase();

        if (BLOCKED_EXTENSIONS.contains(extension)) {
            throw CtfException.invalidAttachmentType(extension);
        }

        if (contentType != null && BLOCKED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw CtfException.invalidAttachmentType(contentType);
        }
    }

    private String getExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return filename.substring(lastDot);
    }

    public record AttachmentInfo(String originalFilename, String storedPath, String contentType) {}
}
