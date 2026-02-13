package com.example.todoapp.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.todoapp.entity.Category;
import com.example.todoapp.entity.User;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByUser(User user);
    List<Category> findByUserAndDeletedAtIsNull(User user);
    boolean existsByUserAndName(User user, String name);
    boolean existsByUserAndNameAndDeletedAtIsNull(User user, String name);

    // JpaRepositoryにはfindByIdメソッドが最初から用意されているため不要
    // List<Category> findById(UUID id);
    boolean existsById(UUID id);
}
