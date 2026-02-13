package com.example.todoapp.entity;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "todos")
public class Todo {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "due")
    private LocalDate due;

    @Column(name = "priority")
    private Integer priority = 3;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
