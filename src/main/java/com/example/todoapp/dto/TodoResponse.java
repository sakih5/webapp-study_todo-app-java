package com.example.todoapp.dto;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import com.example.todoapp.entity.Todo;

@Getter
@Setter
public class TodoResponse {
    private UUID id;
    private UUID categoryId;
    private String title;
    private String description;
    private LocalDate due;
    private Integer priority;
    private Boolean isCompleted;
    private CategoryResponse category;

    // TodoエンティティからTodoResponseに変換するコンストラクタ
    public TodoResponse(Todo todo) {
        this.id = todo.getId();
        this.categoryId = todo.getCategory() != null ? todo.getCategory().getId() : null;
        this.title = todo.getTitle();
        this.description = todo.getDescription();
        this.due = todo.getDue();
        this.priority = todo.getPriority();
        this.isCompleted = todo.getIsCompleted();
        if (todo.getCategory() != null) {
            this.category = new CategoryResponse(todo.getCategory());
        } else {
            this.category = null;
        }
    }
}
