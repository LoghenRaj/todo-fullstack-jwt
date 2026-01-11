package com.loghen.todo.file;

public class FileDtos {

    public record UploadResponse(
            Long id,
            String originalFilename,
            String contentType,
            long sizeBytes,
            String url
    ) {}

    public record FileItem(
            Long id,
            String originalFilename,
            String contentType,
            long sizeBytes,
            String url
    ) {}
}
