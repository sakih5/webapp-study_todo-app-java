package com.example.todoapp.controller;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.todoapp.entity.User;
import com.example.todoapp.service.UserService;
import com.example.todoapp.entity.Todo;
import com.example.todoapp.dto.TodoRequest;
import com.example.todoapp.dto.TodoResponse;
import com.example.todoapp.service.TodoService;
import com.example.todoapp.dto.TodoSearchRequest;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    private final UserService userService;
    private final TodoService todoService;

    public TodoController(UserService userService, TodoService todoService){
        this.userService = userService;
        this.todoService = todoService;
    }

    @PostMapping
    public TodoResponse create(@Valid @RequestBody TodoRequest request) {
        // 認証済みユーザーを取得
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);

        // 作成
        Todo todo = todoService.create(user, request);
        return new TodoResponse(todo);
    }

    @GetMapping
    public List<TodoResponse> list() {
        // 認証済みユーザーを取得
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);

        // Todo一覧取得
        List<TodoResponse> responses = todoService.findByUser(user).stream()
                .map(todo -> new TodoResponse(todo))
                .toList();
        return responses;
    }

    @PatchMapping("/{id}")
    public TodoResponse update(@PathVariable UUID id, @Valid @RequestBody TodoRequest request) {
        // 認証済みユーザーを取得
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);

        // 更新
        Todo todo = todoService.update(id, user, request);
        return new TodoResponse(todo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);

        todoService.delete(user, id);

        return ResponseEntity.noContent().build(); // ← 204
    }

    @PostMapping("/search")
    public Map<String, List<TodoResponse>> search(@RequestBody TodoSearchRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);

        List<TodoResponse> todos = todoService.search(user, request).stream()
            .map(todo -> new TodoResponse(todo))
            .toList();

        return Map.of("todos", todos);
    }

}
