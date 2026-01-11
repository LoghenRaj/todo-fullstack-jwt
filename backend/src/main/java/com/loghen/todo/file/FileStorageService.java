package com.loghen.todo.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final UploadFileRepository repo;
    private final Path uploadDir;

    public FileStorageService(
            UploadFileRepository repo,
            @Value("${app.upload.dir:uploads}") String uploadDir
    ) {
        this.repo = repo;
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create upload directory");
        }
    }

    public UploadFile storeImage(String username, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only image uploads are allowed");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "upload" : file.getOriginalFilename());

        // Prevent weird paths like ../../
        if (originalName.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid filename");
        }

        String ext = "";
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0 && dot < originalName.length() - 1) {
            ext = originalName.substring(dot).toLowerCase();
            // optional basic extension allowlist (keep simple)
            if (!(ext.equals(".png") || ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".gif") || ext.equals(".webp"))) {
                // still allow if content-type is image/*, but you can tighten here if you want
            }
        }

        String storedName = UUID.randomUUID() + ext;
        Path target = uploadDir.resolve(storedName).normalize();

        // Safety check: ensure resolved path is inside uploadDir
        if (!target.startsWith(uploadDir)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid filename");
        }

        try {
            // Copy file to disk
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Failed to store file for user={}", username, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store file");
        }

        UploadFile saved = repo.save(new UploadFile(
                username,
                originalName,
                storedName,
                contentType,
                file.getSize()
        ));

        log.info("File uploaded: user={}, id={}, name={}, size={}", username, saved.getId(), saved.getOriginalFilename(), saved.getSizeBytes());
        return saved;
    }

    public List<UploadFile> list(String username) {
        return repo.findByOwnerUsernameOrderByIdDesc(username);
    }

    public UploadFile getMetaOrThrow(String username, Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file id");
        }
        return repo.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
    }

    public Resource loadAsResource(UploadFile meta) {
        try {
            Path file = uploadDir.resolve(meta.getStoredFilename()).normalize();
            if (!file.startsWith(uploadDir)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file path");
            }

            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found on disk");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to read file");
        }
    }

    public void delete(String username, Long id) {
        UploadFile meta = getMetaOrThrow(username, id);

        Path file = uploadDir.resolve(meta.getStoredFilename()).normalize();
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.warn("Failed deleting file on disk: id={}, stored={}", id, meta.getStoredFilename(), e);
            // still delete DB row to avoid zombie records? usually yes:
        }

        repo.delete(meta);
        log.info("File deleted: user={}, id={}", username, id);
    }
}
