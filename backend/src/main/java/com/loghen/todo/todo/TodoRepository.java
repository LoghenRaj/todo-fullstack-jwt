package com.loghen.todo.todo;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    // List with sorting
    List<Todo> findByOwnerUsername(String ownerUsername, Sort sort);

    // Filter + sorting
    List<Todo> findByOwnerUsernameAndCompleted(String ownerUsername, boolean completed, Sort sort);

    // Search + sorting
    List<Todo> findByOwnerUsernameAndTitleContainingIgnoreCase(String ownerUsername, String q, Sort sort);

    // Search + filter + sorting
    List<Todo> findByOwnerUsernameAndCompletedAndTitleContainingIgnoreCase(
            String ownerUsername,
            boolean completed,
            String q,
            Sort sort
    );

    // Ownership checks
    Optional<Todo> findByIdAndOwnerUsername(Long id, String ownerUsername);
}
