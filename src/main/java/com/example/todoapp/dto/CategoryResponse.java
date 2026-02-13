package com.example.todoapp.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import com.example.todoapp.entity.Category;

@Getter
@Setter
public class CategoryResponse {
    private UUID id;

    private String name;
    private String color;

    // Categoryエンティティから変換するコンストラクタ
    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.color = category.getColor();
    }
}
