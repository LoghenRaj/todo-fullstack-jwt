package com.loghen.todo.todo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByOwnerUsernameOrderByIdDesc(String ownerUsername);
    Optional<Todo> findByIdAndOwnerUsername(Long id, String ownerUsername);
}
