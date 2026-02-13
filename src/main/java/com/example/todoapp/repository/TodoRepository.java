package com.example.todoapp.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Sort;

import com.example.todoapp.entity.Todo;
import com.example.todoapp.entity.User;
import com.example.todoapp.entity.Category;

public interface TodoRepository
    extends JpaRepository<Todo, UUID>,
            JpaSpecificationExecutor<Todo> {
    List<Todo> findByUser(User user);
    List<Todo> findByUserAndCategory(User user, Category category);

    // Todo取得時にCategoryも一緒に取得してねの意 ← N+1問題対策
    @EntityGraph(attributePaths = {"category"})
    List<Todo> findAll(Specification<Todo> spec, Sort sort);
}
