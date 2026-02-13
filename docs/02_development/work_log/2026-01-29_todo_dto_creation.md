# 作業ログ: 2026-01-29 (TodoRequest・TodoResponse DTO作成)

## 学習情報

- **日付**: 2026-01-29
- **学習フェーズ**: WBS 1.4.2（Todo作成API - DTO作成）
- **ステータス**: 完了

---

## 実施内容

### 1. TodoRequest DTO作成

Todoの作成・更新リクエストを受け取るDTOを作成した。

```java
@Getter
@Setter
public class TodoRequest {
    @NotBlank
    @Size(max = 200)
    private String title;

    @Size(max = 2000)
    private String description;

    private LocalDate due;
    private Integer priority;
    private UUID categoryId;
    private Boolean isCompleted;
}
```

---

### 2. TodoResponse DTO作成

Todoのレスポンス用DTOを作成した。

```java
@Getter
@Setter
public class TodoResponse {
    private UUID id;
    private UUID categoryId;
    private String title;
    private String description;
    private LocalDate due;
    private Integer priority;
    private Boolean isCompleted;

    public TodoResponse(Todo todo) {
        this.id = todo.getId();
        this.categoryId = todo.getCategory() != null ? todo.getCategory().getId() : null;
        this.title = todo.getTitle();
        this.description = todo.getDescription();
        this.due = todo.getDue();
        this.priority = todo.getPriority();
        this.isCompleted = todo.getIsCompleted();
    }
}
```

---

### 3. WBS修正（API設計書との整合性）

API設計書を確認し、WBSを修正した。

**変更点:**

| 項目 | 変更前 | 変更後 |
|------|--------|--------|
| 1.4.3 | Todo一覧取得API (GET) | Todo検索API (POST /api/todos/search) |
| 1.4.4 | PUT /api/todos/{id} | PATCH /api/todos/{id} |
| 1.4.6 | Todo完了切り替えAPI | **削除**（PATCHで対応） |
| 1.4.7 | フィルタリング機能 | **1.4.3に統合** |

**理由:**
- API設計書ではPUTは使用しない方針（部分更新のみ）
- 完了切り替えは専用APIではなく、PATCH /api/todos/{id} で `is_completed` を更新する設計

---

## 学んだこと

### 1. @Sizeアノテーションの使い方

文字列の長さを制限するバリデーションアノテーション。

```java
import jakarta.validation.constraints.Size;

@Size(max = 200)              // 最大200文字
@Size(min = 1, max = 100)     // 1〜100文字
@Size(max = 200, message = "200文字以内で入力してください")  // カスタムメッセージ
```

- `@NotBlank` と `@Size` は併用できる
- `@Size` だけだと空文字やnullは許可される

---

### 2. DTOに含めるフィールドの判断基準

**原則**: リクエストDTOには「ユーザーが決める情報」だけを含める

| フィールド | 含める？ | 理由 |
|-----------|:--------:|------|
| title, description, due, priority | ✅ | ユーザーが入力する |
| categoryId | ✅ | ユーザーが選択する |
| isCompleted | ✅ | ユーザーが更新時に変更する |
| id | ❌ | サーバーが自動生成 |
| user | ❌ | JWTから自動的に特定できる |
| deletedAt | ❌ | サーバーが削除時に自動設定 |

**セキュリティ上の理由:**
- userIdをクライアントから送らせると、他人のIDを送って「なりすまし」が可能になる
- 認証トークンから取得することで、必ず本人のデータになる

---

### 3. DTOとEntityの違い（categoryId vs category）

| | TodoRequest (DTO) | Todo (Entity) |
|---|---|---|
| カテゴリ | `UUID categoryId` | `Category category` |
| 役割 | クライアントからIDを受け取る | DBとオブジェクトをマッピング |

**変換の流れ:**
```
クライアント: categoryId: "abc-123"
    ↓
サーバー: categoryIdでCategoryを検索
    ↓
サーバー: todo.setCategory(category)
    ↓
DB: category_idカラムに保存
```

---

### 4. レスポンスDTOでのnull安全な書き方

Categoryがnullの場合を考慮した三項演算子：

```java
// ❌ NullPointerExceptionの危険
this.categoryId = todo.getCategory().getId();

// ✅ null安全
this.categoryId = todo.getCategory() != null ? todo.getCategory().getId() : null;
```

---

### 5. レスポンスDTOにエンティティを含めない理由

```java
// ❌ 悪い例（エンティティを直接返す）
private User user;
private Category category;

// ✅ 良い例（必要な情報だけ返す）
private UUID categoryId;
```

**理由:**
- 不要な情報（passwordHash等）が漏れる可能性
- 循環参照でJSON変換エラーになる可能性
- API設計書に従う（userは含めない、categoryはIDだけ）

---

## 修正したエラー

### 1. importの不足・不要

| ファイル | 問題 | 修正 |
|---------|------|------|
| TodoRequest.java | `LocalDate`のimportなし | `import java.time.LocalDate;` 追加 |
| TodoRequest.java | `import Todo`が不要 | 削除 |
| TodoResponse.java | `User`, `Category`のimportが不要 | 削除 |

### 2. タイポ

| ファイル | 問題 | 修正 |
|---------|------|------|
| TodoResponse.java | `this.categoryId = = ...`（=が2つ） | `this.categoryId = ...` |

---

## 現在のDTO構成

```
src/main/java/com/example/todoapp/dto/
├── SignupRequest.java
├── UserResponse.java
├── LoginRequest.java
├── LoginResponse.java
├── CategoryRequest.java
├── CategoryResponse.java
├── TodoRequest.java      ← 新規作成
└── TodoResponse.java     ← 新規作成
```

---

## 完了したタスク

- [x] TodoRequest DTO作成
- [x] TodoResponse DTO作成
- [x] WBSの修正（API設計書との整合性）

## 次回のタスク

- [ ] TodoService作成
- [ ] TodoController作成
- [ ] Todo作成APIのcurlテスト

---

**作成日**: 2026-01-29
