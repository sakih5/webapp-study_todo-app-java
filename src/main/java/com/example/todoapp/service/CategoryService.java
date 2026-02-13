package com.example.todoapp.service;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.todoapp.entity.Category;
import com.example.todoapp.entity.User;
import com.example.todoapp.entity.Todo;
import com.example.todoapp.repository.TodoRepository;
import com.example.todoapp.repository.CategoryRepository;

@Service
public class CategoryService {
    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;

    // コンストラクタインジェクション
    public CategoryService(TodoRepository todoRepository, CategoryRepository categoryRepository) {
        this.todoRepository = todoRepository;
        this.categoryRepository = categoryRepository;
    }

    public Category create(User user, String name, String color) {
        // カテゴリ重複チェック
        if (categoryRepository.existsByUserAndNameAndDeletedAtIsNull(user,name)) {
            throw new RuntimeException("Category already exists.");
        }

        // 2) Categoryオブジェクト作成
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setUser(user);
        category.setName(name);
        category.setColor(color);

        // 4) 保存して返却
        return categoryRepository.save(category);
    }

    public List<Category> findByUser(User user) {
        return categoryRepository.findByUserAndDeletedAtIsNull(user);
    }

    public Optional<Category> findById(UUID id) {
        return categoryRepository.findById(id);
    }

    public Category update(UUID id, User user, String name, String color) {
        // 1) カテゴリ存在チェック
        // if (categoryRepository.existsById(id)) {
        //     throw new BusinessException("Category does not exist.");
        // }

        // 2) Categoryオブジェクト更新
        Category category = findById(id).get();
        category.setUser(user);
        category.setName(name);
        category.setColor(color);

        // 4) 保存して返却
        return categoryRepository.save(category);
    }

    public void delete(UUID id) {
        // Categoryオブジェクト作成
        Category category = categoryRepository.findById(id).get();
        LocalDateTime now = LocalDateTime.now();
        category.setDeletedAt(now);

        // 保存
        categoryRepository.save(category);

        // このカテゴリーを使っているTodoの category を null にする
        User user = category.getUser();
        List<Todo> todoList = todoRepository.findByUserAndCategory(user, category);
        todoList.forEach(todo -> {
            todo.setCategory(null);
        });
        todoRepository.saveAll(todoList);
    }
}
