package com.example.todoapp.dto;

import java.time.LocalDateTime;
import com.example.todoapp.entity.User;

public class UserResponse {
    private Long id;
    private String email;

    // Userエンティティから変換するコンストラクタ
    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
    }

    // getter/setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
