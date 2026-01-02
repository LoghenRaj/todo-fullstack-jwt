package com.loghen.todo.todo;

import com.loghen.todo.todo.dto.TodoDtos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> list(String username) {
        return todoRepository.findByOwnerUsernameOrderByIdDesc(username);
    }

    public Todo create(String username, String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new RuntimeException("Title is required");
        }
        Todo todo = new Todo(title.trim(), username);
        return todoRepository.save(todo);
    }

    @Transactional
    public Todo update(String username, Long id, TodoDtos.UpdateTodoRequest req) {
        Todo todo = todoRepository.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (req.getTitle() != null) {
            String t = req.getTitle().trim();
            if (t.isEmpty()) throw new RuntimeException("Title cannot be empty");
            todo.setTitle(t);
        }
        if (req.getCompleted() != null) {
            todo.setCompleted(req.getCompleted());
        }
        return todo;
    }

    public void delete(String username, Long id) {
        Todo todo = todoRepository.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        todoRepository.delete(todo);
    }
}
