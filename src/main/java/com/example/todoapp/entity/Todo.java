package com.example.todoapp.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "todos")
public class Todo {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

}
