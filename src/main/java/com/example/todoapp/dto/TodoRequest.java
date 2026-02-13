package com.example.todoapp.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoRequest {
    @NotBlank
    @Size(max = 200)
    private String title;

    @Size(max = 2000)
    private String description;

    private LocalDate due;
    private Integer priority;
    private UUID categoryId;
    private Boolean isCompleted;
}
