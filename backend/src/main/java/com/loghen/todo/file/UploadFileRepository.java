package com.loghen.todo.file;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

    List<UploadFile> findByOwnerUsernameOrderByIdDesc(String ownerUsername);

    Optional<UploadFile> findByIdAndOwnerUsername(Long id, String ownerUsername);

    boolean existsByStoredFilename(String storedFilename);
}
