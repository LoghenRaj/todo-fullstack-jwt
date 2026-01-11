package com.loghen.todo.file;

import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.loghen.todo.file.FileDtos.*;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService storage;

    public FileController(FileStorageService storage) {
        this.storage = storage;
    }

    // Upload image
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> upload(Authentication auth, @RequestParam("file") MultipartFile file) {
        String username = auth.getName();
        UploadFile saved = storage.storeImage(username, file);

        String url = "/api/files/" + saved.getId();
        return ResponseEntity.ok(new UploadResponse(
                saved.getId(),
                saved.getOriginalFilename(),
                saved.getContentType(),
                saved.getSizeBytes(),
                url
        ));
    }

    // List my uploads
    @GetMapping
    public ResponseEntity<List<FileItem>> list(Authentication auth) {
        String username = auth.getName();
        List<FileItem> out = storage.list(username).stream()
                .map(f -> new FileItem(
                        f.getId(),
                        f.getOriginalFilename(),
                        f.getContentType(),
                        f.getSizeBytes(),
                        "/api/files/" + f.getId()
                ))
                .toList();
        return ResponseEntity.ok(out);
    }

    // Download/view a file I own
    @GetMapping("/{id}")
    public ResponseEntity<Resource> download(Authentication auth, @PathVariable Long id) {
        String username = auth.getName();
        UploadFile meta = storage.getMetaOrThrow(username, id);
        Resource resource = storage.loadAsResource(meta);

        // Show in browser if possible (inline), but also provide filename.
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(meta.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + safeHeaderFilename(meta.getOriginalFilename()) + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        String username = auth.getName();
        storage.delete(username, id);
        return ResponseEntity.noContent().build();
    }

    private String safeHeaderFilename(String name) {
        if (name == null) return "file";
        return name.replace("\r", "").replace("\n", "");
    }
}
