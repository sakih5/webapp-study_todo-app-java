package com.example.todoapp.specification;

import org.springframework.data.jpa.domain.Specification;
import com.example.todoapp.entity.Todo;
import com.example.todoapp.entity.User;
import java.util.List;
import java.util.UUID;

public class TodoSpecification {

    // 自分のTodoのみ
    public static Specification<Todo> belongsToUser(User user) {
        return (root, query, cb) ->
            cb.equal(root.get("user"), user);
    }

    // 削除されていないもののみ
    public static Specification<Todo> notDeleted() {
        return (root, query, cb) ->
            cb.isNull(root.get("deletedAt"));
    }

    // statusフィルタ（completed / incomplete / all）
    public static Specification<Todo> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.equals("all")) {
                return null;
            }
            if (status.equals("completed")) {
                return cb.equal(root.get("isCompleted"), true);
            }
            if (status.equals("incomplete")) {
                return cb.equal(root.get("isCompleted"), false);
            }
            return null;
        };
    }

    // categoryIdフィルタ
    public static Specification<Todo> hasCategory(UUID categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return null;
            }
            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }

    // priorityフィルタ（リストに含まれる優先度）
    public static Specification<Todo> hasPriorityIn(List<Integer> priorities) {
        return (root, query, cb) -> {
            if (priorities == null || priorities.isEmpty()) {
                return null;
            }
            return root.get("priority").in(priorities);
        };
    }
}
