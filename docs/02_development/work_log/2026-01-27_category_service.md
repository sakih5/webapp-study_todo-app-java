# 作業ログ: 2026-01-27 (CategoryService・CategoryController完成)

## 学習情報

- **日付**: 2026-01-27
- **学習フェーズ**: WBS 1.5.2（Category CRUD API）
- **ステータス**: 完了

---

## 実施内容

### 1. CategoryServiceの修正完了

前回のセッションで残っていたバグを修正した。

**修正箇所**:

| 行 | 問題 | 修正 |
|----|------|------|
| 6行目 | `java.util.LocalDateTime` | `java.time.LocalDateTime` |
| 46行目 | `findByUser(id)` | `categoryRepository.findById(id)` |
| 72行目 | 変数宣言・引数・Optional | `Category category = categoryRepository.findById(id).get();` |
| 73行目 | `LocalDateTime()` | `LocalDateTime.now()` |
| 74行目 | `setAtDeleted` | `setDeletedAt` |

---

### 2. CategoryRequest DTO作成

クライアントからのリクエストを受け取るDTOを作成した。

```java
@Getter
@Setter
public class CategoryRequest {
    @NotBlank
    private String name;
    private String color;
}
```

**学び**: Lombokの使い方
- `@Getter` / `@Setter` でgetter/setterを自動生成
- スペルは `lombok`（小文字で始まる）

---

### 3. CategoryResponse DTO作成

クライアントに返すレスポンス用DTOを作成した。

```java
@Getter
@Setter
public class CategoryResponse {
    private UUID id;
    private String name;
    private String color;

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.color = category.getColor();
    }
}
```

**学び**: DTOの役割
- エンティティを直接返さない（不要なフィールドを隠す）
- コンストラクタでエンティティ→DTO変換

---

### 4. UserServiceにfindByEmailメソッド追加

CategoryControllerで認証済みユーザーを取得するために追加した。

```java
public User findByEmail(String email) {
    return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
}
```

**学び**: レイヤードアーキテクチャ
- ControllerからRepositoryを直接使わず、Serviceを経由する
- 理由: 再利用性、ビジネスロジックの集約、テスト容易性

---

### 5. CategoryController作成

CRUD操作のAPIエンドポイントを作成した。

| HTTP | パス | メソッド | 説明 |
|------|------|---------|------|
| POST | `/api/categories` | `create` | カテゴリ作成 |
| GET | `/api/categories` | `list` | 一覧取得 |
| PUT | `/api/categories/{id}` | `update` | 更新 |
| DELETE | `/api/categories/{id}` | `delete` | 削除 |

**認証済みユーザーの取得パターン**:
```java
String email = SecurityContextHolder.getContext().getAuthentication().getName();
User user = userService.findByEmail(email);
```

---

### 6. Stream APIの学習

`List<Category>` を `List<CategoryResponse>` に変換するためにStream APIを学習した。

```java
List<CategoryResponse> responses = categoryService.findByUser(user).stream()
        .map(category -> new CategoryResponse(category))
        .toList();
```

**学習記事作成**: `docs/03_learning/17_Stream APIとは何か - リスト変換の基本.md`

---

## 学んだこと

### 1. Lombokアノテーション

| アノテーション | 機能 |
|---------------|------|
| `@Getter` | getterを自動生成 |
| `@Setter` | setterを自動生成 |
| `@NoArgsConstructor` | 引数なしコンストラクタ |
| `@AllArgsConstructor` | 全引数コンストラクタ |

### 2. Stream APIの基本パターン

```java
// List<A> → List<B> 変換
listA.stream()
     .map(a -> new B(a))
     .toList();
```

### 3. 認証済みユーザーの取得

Spring Securityの `SecurityContextHolder` から認証情報を取得:
```java
String email = SecurityContextHolder.getContext().getAuthentication().getName();
```

### 4. パスパラメータの受け取り

`@PathVariable` でURLのパラメータを受け取る:
```java
@PutMapping("/{id}")
public CategoryResponse update(@PathVariable UUID id, ...) { ... }
```

### 5. エンティティからDTOへの変換（重要）

Serviceが返すエンティティ（`Category`）と、Controllerが返すDTO（`CategoryResponse`）は**別の型**。
変換するには `new` でDTOを生成する必要がある。

```java
// ❌ 間違い（型が一致しない）
public CategoryResponse update(...) {
    return categoryService.update(...);  // Categoryを返している
}

// ✅ 正しい（DTOに変換）
public CategoryResponse update(...) {
    Category category = categoryService.update(...);  // Categoryを受け取る
    return new CategoryResponse(category);            // DTOに変換して返す
}
```

**ポイント**:
- `categoryService.update()` は `Category` 型を返す
- メソッドの戻り値は `CategoryResponse` 型
- `new CategoryResponse(category)` で変換が必要

---

## 現在のプロジェクト構造

```
src/main/java/com/example/todoapp/
├── TodoappApplication.java
├── config/
│   └── SecurityConfig.java
├── controller/
│   ├── HelloController.java
│   ├── AuthController.java
│   └── CategoryController.java  ← 新規作成
├── dto/
│   ├── SignupRequest.java
│   ├── UserResponse.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── CategoryRequest.java     ← 新規作成
│   └── CategoryResponse.java    ← 新規作成
├── entity/
│   ├── User.java
│   ├── Category.java
│   └── Todo.java
├── filter/
│   └── JwtAuthenticationFilter.java
├── repository/
│   ├── UserRepository.java
│   ├── CategoryRepository.java
│   └── TodoRepository.java
├── service/
│   ├── UserService.java         ← findByEmail追加
│   └── CategoryService.java     ← 修正完了
└── util/
    └── JwtUtil.java
```

---

## 完了したWBSタスク

- [x] CategoryService完成（findById, update, deleteメソッド修正）
- [x] CategoryRequest DTO作成
- [x] CategoryResponse DTO作成
- [x] UserServiceにfindByEmailメソッド追加
- [x] CategoryController作成（create, list, update, delete）

## 次回のタスク

- [ ] curlでCategory APIの動作確認テスト
- [ ] 1.5.3 TodoとCategoryの関連付け

---

**作成日**: 2026-01-27
**最終更新**: 2026-01-27
