package com.loghen.todo.todo;

import com.loghen.todo.todo.dto.TodoDtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoService.class);

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    // list supports q/completed/sort/dir
    public List<Todo> list(String username, String q, Boolean completed, String sortBy, String dir) {
        Sort sort = buildSort(sortBy, dir);

        boolean hasQ = q != null && !q.trim().isBlank();
        String query = hasQ ? q.trim() : null;

        // Light debug logging (won't spam unless your log level includes DEBUG)
        log.debug("List todos: user={}, q={}, completed={}, sortBy={}, dir={}", username, query, completed, sortBy, dir);

        if (hasQ && completed != null) {
            return todoRepository.findByOwnerUsernameAndCompletedAndTitleContainingIgnoreCase(username, completed, query, sort);
        }
        if (hasQ) {
            return todoRepository.findByOwnerUsernameAndTitleContainingIgnoreCase(username, query, sort);
        }
        if (completed != null) {
            return todoRepository.findByOwnerUsernameAndCompleted(username, completed, sort);
        }
        return todoRepository.findByOwnerUsername(username, sort);
    }

    public Todo create(String username, String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }

        Todo todo = new Todo(title.trim(), username);
        Todo saved = todoRepository.save(todo);

        log.info("Todo created: user={}, id={}, title={}", username, saved.getId(), saved.getTitle());
        return saved;
    }

    @Transactional
    public Todo update(String username, Long id, TodoDtos.UpdateTodoRequest req) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid todo id");
        }
        if (req == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }

        Todo todo = todoRepository.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));

        boolean changed = false;

        if (req.getTitle() != null) {
            String t = req.getTitle().trim();
            if (t.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title cannot be empty");
            }
            todo.setTitle(t);
            changed = true;
        }

        if (req.getCompleted() != null) {
            todo.setCompleted(req.getCompleted());
            changed = true;
        }

        if (changed) {
            log.info("Todo updated: user={}, id={}, title={}, completed={}", username, todo.getId(), todo.getTitle(), todo.isCompleted());
        }

        return todo; // entity is managed; transaction will flush changes
    }

    public void delete(String username, Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid todo id");
        }

        Todo todo = todoRepository.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));

        todoRepository.delete(todo);
        log.info("Todo deleted: user={}, id={}", username, id);
    }

    private Sort buildSort(String sortBy, String dir) {
        // defaults: id desc (same behavior as old orderByIdDesc)
        String field = (sortBy == null || sortBy.isBlank()) ? "id" : sortBy.trim();
        String direction = (dir == null || dir.isBlank()) ? "desc" : dir.trim();

        // allowlist prevents invalid sort fields causing errors
        field = switch (field) {
            case "id", "title", "completed" -> field;
            default -> "id";
        };

        Sort.Direction d = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(d, field);
    }
}
