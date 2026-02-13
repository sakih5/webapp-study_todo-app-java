package com.example.todoapp.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import com.example.todoapp.entity.Category;

@Getter
@Setter
public class CategoryRequest {
    @NotBlank
    private String name;

    private String color;
}
