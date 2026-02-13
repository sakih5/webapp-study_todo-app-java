package com.example.todoapp.controller;

import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.todoapp.entity.User;
import com.example.todoapp.service.UserService;
import com.example.todoapp.entity.Category;
import com.example.todoapp.dto.CategoryRequest;
import com.example.todoapp.dto.CategoryResponse;
import com.example.todoapp.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final UserService userService;
    private final CategoryService categoryService;

    public CategoryController(UserService userService, CategoryService categoryService){
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @PostMapping
    public CategoryResponse create(@Valid @RequestBody CategoryRequest request) {
        // 認証済みユーザーを取得
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);

        // カテゴリ作成
        Category category = categoryService.create(user, request.getName(), request.getColor());
        return new CategoryResponse(category);
    }

    @GetMapping
    public List<CategoryResponse> list() {
        // 認証済みユーザーを取得
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);

        // カテゴリ作成
        List<CategoryResponse> responses = categoryService.findByUser(user).stream()
                .map(category -> new CategoryResponse(category))
                .toList();
        return responses;
    }

    @PatchMapping("/{id}")
    public CategoryResponse update(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request) {
        // 認証済みユーザーを取得
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);

        // カテゴリ更新
        Category category = categoryService.update(id, user, request.getName(), request.getColor());
        return new CategoryResponse(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build(); // ← 204
    }

}
