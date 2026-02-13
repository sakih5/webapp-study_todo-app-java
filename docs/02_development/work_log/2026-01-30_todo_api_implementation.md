# 作業ログ: 2026-01-30 (Todo CRUD API実装)

## 学習情報

- **日付**: 2026-01-30
- **学習フェーズ**: WBS 1.4.2〜1.4.5（Todo CRUD API）
- **ステータス**: 完了

---

## 実施内容

### 1. TodoService.java 作成

Todo機能のビジネスロジックを担当するServiceクラスを作成した。

**場所**: `src/main/java/com/example/todoapp/service/TodoService.java`

```java
@Service
public class TodoService {
    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;

    public TodoService(TodoRepository todoRepository, CategoryRepository categoryRepository) {
        this.todoRepository = todoRepository;
        this.categoryRepository = categoryRepository;
    }

    public Todo create(User user, TodoRequest request) {
        Todo todo = new Todo();
        todo.setId(UUID.randomUUID());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                .orElse(null);
            todo.setCategory(category);
        }

        todo.setUser(user);
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setDue(request.getDue());
        todo.setPriority(request.getPriority());
        todo.setIsCompleted(request.getIsCompleted());

        return todoRepository.save(todo);
    }

    public List<Todo> findByUser(User user) {
        return todoRepository.findByUser(user);
    }

    public Todo update(UUID id, User user, TodoRequest request) {
        Todo todo = todoRepository.findById(id).get();

        // 所有権チェック
        if (!todo.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("このTodoを更新する権限がありません");
        }

        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setDue(request.getDue());
        todo.setPriority(request.getPriority());
        todo.setIsCompleted(request.getIsCompleted());

        return todoRepository.save(todo);
    }

    public void delete(User user, UUID id) {
        Todo todo = todoRepository.findById(id).get();

        // 所有権チェック
        if (!todo.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("このTodoを削除する権限がありません");
        }

        // 論理削除
        LocalDateTime now = LocalDateTime.now();
        todo.setDeletedAt(now);
        todoRepository.save(todo);
    }
}
```

---

### 2. TodoController.java 作成

Todo APIのエンドポイントを定義するControllerクラスを作成した。

**場所**: `src/main/java/com/example/todoapp/controller/TodoController.java`

```java
@RestController
@RequestMapping("/api/todos")
public class TodoController {
    private final UserService userService;
    private final TodoService todoService;

    public TodoController(UserService userService, TodoService todoService){
        this.userService = userService;
        this.todoService = todoService;
    }

    @PostMapping
    public TodoResponse create(@Valid @RequestBody TodoRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);
        Todo todo = todoService.create(user, request);
        return new TodoResponse(todo);
    }

    @GetMapping
    public List<TodoResponse> list() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);
        List<TodoResponse> responses = todoService.findByUser(user).stream()
                .map(todo -> new TodoResponse(todo))
                .toList();
        return responses;
    }

    @PutMapping("/{id}")
    public TodoResponse update(@PathVariable UUID id, @Valid @RequestBody TodoRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);
        Todo todo = todoService.update(id, user, request);
        return new TodoResponse(todo);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);
        todoService.delete(user, id);
    }
}
```

---

### 3. curlによる動作確認

#### 3.1 ログイン（JWTトークン取得）

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "password123"}'
```

レスポンス:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNzY5NzQ4OTE5LCJleHAiOjE3Njk4MzUzMTl9.cN7pke6MHumDEoXE6PS44l5pS_AEgJncWmn_Cbx7zz8",
  "tokenType": "Bearer"
}
```

#### 3.2 Todo作成（POST /api/todos）

```bash
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNzY5NzQ4OTE5LCJleHAiOjE3Njk4MzUzMTl9.cN7pke6MHumDEoXE6PS44l5pS_AEgJncWmn_Cbx7zz8" \
  -d '{"title": "最初のTodo", "description": "テスト用", "priority": 2}'
```

レスポンス:
```json
{
  "id": "fc4e0a4a-7e55-4a6f-9554-a340e3ed8532",
  "categoryId": null,
  "title": "最初のTodo",
  "description": "テスト用",
  "due": null,
  "priority": 2,
  "isCompleted": null
}
```

#### 3.3 Todo一覧取得（GET /api/todos）

```bash
curl http://localhost:8080/api/todos \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNzY5NzQ4OTE5LCJleHAiOjE3Njk4MzUzMTl9.cN7pke6MHumDEoXE6PS44l5pS_AEgJncWmn_Cbx7zz8"
```

レスポンス:
```json
[
  {
    "id": "fc4e0a4a-7e55-4a6f-9554-a340e3ed8532",
    "categoryId": null,
    "title": "最初のTodo",
    "description": "テスト用",
    "due": null,
    "priority": 2,
    "isCompleted": null
  }
]
```

#### 3.4 Todo更新（PUT /api/todos/{id}）

```bash
curl -X PUT http://localhost:8080/api/todos/fc4e0a4a-7e55-4a6f-9554-a340e3ed8532 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNzY5NzQ4OTE5LCJleHAiOjE3Njk4MzUzMTl9.cN7pke6MHumDEoXE6PS44l5pS_AEgJncWmn_Cbx7zz8" \
  -d '{"title": "更新したTodo", "description": "更新テスト", "priority": 1, "isCompleted": true}'
```

レスポンス:
```json
{
  "id": "fc4e0a4a-7e55-4a6f-9554-a340e3ed8532",
  "categoryId": null,
  "title": "更新したTodo",
  "description": "更新テスト",
  "due": null,
  "priority": 1,
  "isCompleted": true
}
```

#### 3.5 Todo削除（DELETE /api/todos/{id}）

```bash
curl -X DELETE http://localhost:8080/api/todos/fc4e0a4a-7e55-4a6f-9554-a340e3ed8532 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNzY5NzQ4OTE5LCJleHAiOjE3Njk4MzUzMTl9.cN7pke6MHumDEoXE6PS44l5pS_AEgJncWmn_Cbx7zz8"
```

レスポンス: なし（204 No Content相当）

---

## 学んだこと

### 1. 所有権チェックの重要性

他のユーザーのTodoを更新・削除できないようにするため、所有権チェックが必要。

```java
if (!todo.getUser().getId().equals(user.getId())) {
    throw new RuntimeException("このTodoを更新する権限がありません");
}
```

- `todo.getUser()` でTodoの所有者を取得
- `user.getId()` と比較して一致しなければエラー
- UUIDの比較は `.equals()` を使う（`==` ではない）

### 2. コンストラクタ名はクラス名と一致させる

```java
// ❌ 間違い
public class TodoController {
    public CategoryController(...) { }  // クラス名と不一致
}

// ✅ 正しい
public class TodoController {
    public TodoController(...) { }  // クラス名と一致
}
```

### 3. DTOのフィールドにはgetterでアクセス

Lombokの `@Getter` を使っている場合でも、外部からはgetterメソッドでアクセスする。

```java
// ❌ 間違い（直接アクセス）
todo.setTitle(request.title);

// ✅ 正しい（getterを使う）
todo.setTitle(request.getTitle());
```

### 4. ServiceとControllerの引数を一致させる

ControllerでServiceを呼び出す際、引数の数と順序を一致させる必要がある。

```java
// Controller
todoService.update(id, user, request);

// Service（引数が一致している必要あり）
public Todo update(UUID id, User user, TodoRequest request) { ... }
```

### 5. curlコマンドのクォートの注意点

Authorizationヘッダーでは、`Bearer` とトークン全体を1つのダブルクォートで囲む。

```bash
# ❌ 間違い（Bearerの後でクォートが閉じている）
-H "Authorization: Bearer "eyJhbGci..."

# ✅ 正しい
-H "Authorization: Bearer eyJhbGci..."
```

---

## 修正したエラー

| 問題 | 原因 | 修正 |
|------|------|------|
| コンストラクタ名が間違い | CategoryControllerになっていた | TodoControllerに修正 |
| importの不足 | TodoService等のimportがなかった | 必要なimportを追加 |
| getterを使っていない | `request.title` と直接アクセス | `request.getTitle()` に修正 |
| 引数の不一致 | Serviceのupdateに `User user` がなかった | 引数を追加 |
| 変数の重複宣言 | `Todo todo` を同じメソッド内で2回宣言 | 重複を削除 |
| 所有権チェックなし | 他ユーザーのTodoを操作可能だった | チェックロジックを追加 |

---

## 完了したタスク

- [x] TodoService.java 作成
- [x] TodoController.java 作成
- [x] Todo作成API（POST /api/todos）
- [x] Todo一覧取得API（GET /api/todos）
- [x] Todo更新API（PUT /api/todos/{id}）
- [x] Todo削除API（DELETE /api/todos/{id}）
- [x] curlでの動作確認

## 次回のタスク

- [ ] 1.4.3 Todo検索API（POST /api/todos/search）- フィルタ・ソート対応
- [ ] 1.5.3 TodoとCategoryの関連付けテスト

---

## 備考

- 新規作成時の `isCompleted` がnullになっている → デフォルト値をfalseにする対応が必要
- 現在はPUTで更新しているが、API設計書ではPATCH → 後で対応検討

---

**作成日**: 2026-01-30
