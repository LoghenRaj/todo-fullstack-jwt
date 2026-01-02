package com.loghen.todo.todo;

import com.loghen.todo.todo.dto.TodoDtos;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.loghen.todo.todo.dto.TodoDtos.*;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<TodoResponse>> list(Authentication auth) {
        String username = auth.getName();
        List<TodoResponse> out = todoService.list(username).stream()
                .map(t -> new TodoResponse(t.getId(), t.getTitle(), t.isCompleted()))
                .toList();
        return ResponseEntity.ok(out);
    }

    @PostMapping
    public ResponseEntity<TodoResponse> create(Authentication auth, @RequestBody CreateTodoRequest req) {
        String username = auth.getName();
        Todo created = todoService.create(username, req.getTitle());
        return ResponseEntity.ok(new TodoResponse(created.getId(), created.getTitle(), created.isCompleted()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> update(
            Authentication auth,
            @PathVariable Long id,
            @RequestBody UpdateTodoRequest req
    ) {
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
