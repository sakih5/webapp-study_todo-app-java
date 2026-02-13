package com.example.todoapp.entity;

import java.util.UUID;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "color", nullable = false, length = 20)
    private String color;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
