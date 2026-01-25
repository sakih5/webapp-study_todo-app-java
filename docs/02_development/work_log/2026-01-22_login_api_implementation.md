# 作業ログ: 2026-01-22 (ログインAPI実装 - 前半)

## 学習情報

- **日付**: 2026-01-22
- **学習フェーズ**: WBS 1.3.7（ログインAPI - JWT発行）
- **ステータス**: 進行中

---

## 実施内容

### 1. LoginRequest DTO作成

`src/main/java/com/example/todoapp/dto/LoginRequest.java` を作成。

```java
@Data
public class LoginRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
```

### 2. LoginResponse DTO作成

`src/main/java/com/example/todoapp/dto/LoginResponse.java` を作成。

```java
@Data
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String tokenType;
}
```

### 3. UserServiceにJwtUtilをインジェクション

```java
private final JwtUtil jwtUtil;

public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
}
```

### 4. loginメソッドの実装（途中）

骨格まで作成。パスワード照合とJWT生成部分は次回実装予定。

---

## 学んだこと

### @AllArgsConstructorとデフォルト値

- `@AllArgsConstructor` は**全フィールド**を引数に取るコンストラクタを生成する
- フィールドにデフォルト値を設定しても、コンストラクタ経由で上書きされる
- デフォルト値を使いたい場合は、手動でコンストラクタを書くか、使用時に値を渡す

### レイヤードアーキテクチャとDTOの扱い

```
Controller層: HTTPリクエスト/レスポンスの処理（DTOを扱う）
     ↓ DTOを分解して個別の値を渡す
Service層: ビジネスロジック（DTOを知らない）
     ↓
Repository層: データベース操作
```

- Service層がDTOに依存しない設計を選択
- ControllerでDTOを分解してServiceに渡す

### Optionalの使い方

| メソッド | 説明 |
|---------|------|
| `isPresent()` | 値があれば `true` |
| `isEmpty()` | 値がなければ `true` |
| `get()` | 値を取得（空だと例外発生） |
| `orElse(デフォルト値)` | 値があれば返す、なければデフォルト値 |
| `orElseThrow(() -> 例外)` | 値があれば返す、なければ例外をスロー |

```java
User user = userOptional.orElseThrow(() ->
    new RuntimeException("ユーザーが見つかりません")
);
```

### メソッド呼び出しの構文

```java
// 間違い（メソッド宣言の構文）
Optional<User> findByEmail(String email);

// 正しい（メソッド呼び出しの構文）
Optional<User> userOptional = userRepository.findByEmail(email);
```

---

## 現在のプロジェクト構造

```
src/main/java/com/example/todoapp/
├── TodoappApplication.java
├── config/
│   └── SecurityConfig.java
├── controller/
│   ├── HelloController.java
│   └── AuthController.java
├── dto/
│   ├── SignupRequest.java
│   ├── UserResponse.java
│   ├── LoginRequest.java      ← 今回作成
│   └── LoginResponse.java     ← 今回作成
├── entity/
│   └── User.java
├── repository/
│   └── UserRepository.java
├── service/
│   └── UserService.java       ← JwtUtilインジェクション追加
└── util/
    └── JwtUtil.java
```

---

## 次回のタスク

UserService.loginメソッドの完成:

1. パスワード照合処理を実装
   - `passwordEncoder.matches(平文, ハッシュ値)`
   - `user.getPasswordHash()` でハッシュ値を取得
2. JWT生成して返す
   - `jwtUtil.generateToken(email)`
3. AuthControllerにログインエンドポイント追加
4. curlでテスト

---

**作成日**: 2026-01-22
**最終更新**: 2026-01-22
