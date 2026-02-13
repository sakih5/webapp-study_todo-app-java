package com.example.todoapp.service;

import java.util.Optional;

import com.example.todoapp.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.todoapp.entity.User;
import com.example.todoapp.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // コンストラクタインジェクション
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ユーザー登録
    @Transactional
    public User signup(String email, String rawPassword) {
        // 1) メール重複チェック
        if (userRepository.existsByEmail(email)) {
            // プロジェクト方針に合わせて独自例外にしてOK（例: BusinessExceptionなど）
            throw new IllegalArgumentException("Email already exists.");
        }

        // 2) パスワードをハッシュ化
        String hashedPassword = passwordEncoder.encode(rawPassword);

        // 3) User を作って保存
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(hashedPassword);

        // 4) 保存して返却
        return userRepository.save(user);
    }

    // ログイン処理
    public String login(String email, String password) {
        // 1. メールアドレスでユーザーを検索
        Optional<User> userOptional = userRepository.findByEmail(email);

        // 2. ユーザーが見つからなければ除外
        User user = userOptional.orElseThrow(() ->
                new RuntimeException("ユーザーが見つかりません")
        );

        // 3. パスワード照合（一致しなければ例外）
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("パスワードが違います");
        }

        // 4. 認証成功 → JWTを生成して返す
        return jwtUtil.generateToken(email);
    }

    // メールアドレスでユーザーを検索
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
    }
}
