package com.example.todoapp.service;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Sort;

import com.example.todoapp.entity.User;
import com.example.todoapp.entity.Category;
import com.example.todoapp.entity.Todo;
import com.example.todoapp.dto.TodoRequest;
import com.example.todoapp.repository.TodoRepository;
import com.example.todoapp.repository.CategoryRepository;
import com.example.todoapp.specification.TodoSpecification;
import com.example.todoapp.dto.TodoSearchRequest;

@Service
public class TodoService {
    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;

    // コンストラクタインジェクション
    public TodoService(TodoRepository todoRepository, CategoryRepository categoryRepository) {
        this.todoRepository = todoRepository;
        this.categoryRepository = categoryRepository;
    }

    public Todo create(User user, TodoRequest request) {
        // Todoオブジェクト作成
        Todo todo = new Todo();
        todo.setId(UUID.randomUUID());

        todo.setUser(user);
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                .orElse(null);
            todo.setCategory(category);
        }

        todo.setDue(request.getDue());
        todo.setPriority(request.getPriority());

        // null でない場合のみ上書きする
        if (request.getIsCompleted() != null) {
            todo.setIsCompleted(request.getIsCompleted());
        }
        // null の場合は何もしない → エンティティで定義したデフォルト値（false）が維持される

        // 保存して返却
        return todoRepository.save(todo);
    }

    public List<Todo> findByUser(User user) {
        return todoRepository.findByUser(user);
    }

    public Optional<Todo> findById(UUID id) {
        return todoRepository.findById(id);
    }

    public Todo update(UUID id, User user, TodoRequest request) {
        Todo todo = todoRepository.findById(id).get();

        // 所有権チェック（deleteと同様）
        if (!todo.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("このTodoを更新する権限がありません");
        }

        // 更新
        todo.setTitle(request.getTitle());

        if (request.getDescription() != null) {
            todo.setDescription(request.getDescription());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                .orElse(null);
            todo.setCategory(category);
        }

        if (request.getDue() != null) {
            todo.setDue(request.getDue());
        }

        if (request.getPriority() != null) {
            todo.setPriority(request.getPriority());
        }

        if (request.getIsCompleted() != null) {
            todo.setIsCompleted(request.getIsCompleted());
        }

        // 保存して返却
        return todoRepository.save(todo);
    }

    public void delete(User user, UUID id) {

        Todo todo = todoRepository.findById(id).get();

        // Todoの所有者と一致するか確認
        if (!todo.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("このTodoを削除する権限がありません");
        }

        // 論理削除
        LocalDateTime now = LocalDateTime.now();
        todo.setDeletedAt(now);

        // 保存
        todoRepository.save(todo);
    }

    // Todo検索
    public List<Todo> search(User user, TodoSearchRequest request) {
        // 1. 必須条件: 自分のTodo & 削除されていない
        Specification<Todo> spec = Specification
            .where(TodoSpecification.belongsToUser(user))
            .and(TodoSpecification.notDeleted());

        // 2. オプション条件を追加
        spec = spec.and(TodoSpecification.hasStatus(request.getStatus()));
        spec = spec.and(TodoSpecification.hasCategory(request.getCategoryId()));
        spec = spec.and(TodoSpecification.hasPriorityIn(request.getPriority()));

        // 3. ソート条件を作成
        Sort sort = createSort(request.getSort(), request.getOrder());

        // 4. 検索実行
        return todoRepository.findAll(spec, sort);
    }

    // ソート条件を作成するヘルパーメソッド
    private Sort createSort(String sortField, String order) {
        // sortField が null の場合は "id" をデフォルトにする
        if (sortField == null) {
            sortField = "id";
        }

        // order が "asc" の場合は昇順、それ以外は降順
        if ("asc".equals(order)) {
            return Sort.by(Sort.Direction.ASC, sortField);
        } else {
            return Sort.by(Sort.Direction.DESC, sortField);
        }
    }
}
