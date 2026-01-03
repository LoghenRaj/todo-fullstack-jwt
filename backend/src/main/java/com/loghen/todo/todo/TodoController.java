package com.loghen.todo.todo;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.List;

import static com.loghen.todo.todo.dto.TodoDtos.*;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // supports search/filter/sort/dir
    @GetMapping
    public ResponseEntity<List<TodoResponse>> list(
            Authentication auth,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false, defaultValue = "id") String sort,
            @RequestParam(required = false, defaultValue = "desc") String dir
    ) {
        String username = auth.getName();

        List<TodoResponse> out = todoService.list(username, q, completed, sort, dir).stream()
                .map(t -> new TodoResponse(t.getId(), t.getTitle(), t.isCompleted()))
                .toList();

        return ResponseEntity.ok(out);
    }

    @PostMapping
    public ResponseEntity<TodoResponse> create(Authentication auth, @RequestBody(required = false) CreateTodoRequest req) {
        if (req == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }

        String username = auth.getName();
        Todo created = todoService.create(username, req.getTitle());

        TodoResponse body = new TodoResponse(created.getId(), created.getTitle(), created.isCompleted());

        // 201 Created + Location header is nicer REST style
        return ResponseEntity
                .created(URI.create("/api/todos/" + created.getId()))
                .body(body);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> update(
            Authentication auth,
            @PathVariable Long id,
            @RequestBody(required = false) UpdateTodoRequest req
    ) {
        if (req == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }

        String username = auth.getName();
        Todo updated = todoService.update(username, id, req);

        return ResponseEntity.ok(new TodoResponse(updated.getId(), updated.getTitle(), updated.isCompleted()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        String username = auth.getName();
        todoService.delete(username, id);
        return ResponseEntity.noContent().build();
    }
}
