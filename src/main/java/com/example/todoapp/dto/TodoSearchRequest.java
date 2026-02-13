package com.example.todoapp.dto;

import java.util.UUID;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoSearchRequest {
    private String status;

    @JsonProperty("category_id")
    private UUID categoryId;

    private List<Integer> priority;
    private String sort;
    private String order;
}
