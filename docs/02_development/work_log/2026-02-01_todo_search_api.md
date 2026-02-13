# 作業ログ: 2026-02-01 (Todo検索API・N+1問題対策)

## 学習情報

- **日付**: 2026-02-01
- **学習フェーズ**: WBS 1.4.3（Todo検索API）、1.5.3（TodoとCategoryの関連付け）
- **ステータス**: 完了

---

## 実施内容

### 1. Spring Data JPA Specificationの学習

動的なクエリを構築するためのSpecificationパターンを学習した。

**学習ドキュメント**: `docs/03_learning/18_Spring Data JPA Specificationとは.md`

**主なポイント**:
- Specificationは「WHERE句の組み立て部品」
- JPQLと違い、条件を動的に組み合わせられる
- JpaSpecificationExecutorをRepositoryに追加して使用

### 2. TodoSearchRequest.java 作成

検索条件を受け取るDTOを作成した。

**場所**: `src/main/java/com/example/todoapp/dto/TodoSearchRequest.java`

```java
@Getter
@Setter
public class TodoSearchRequest {
    private String status;

    @JsonProperty("category_id")
    private UUID categoryId;

    private List<Integer> priority;
    private String sort;
    private String order;
}
```

### 3. TodoSpecification.java 作成

検索条件をSpecificationとして定義した。

**場所**: `src/main/java/com/example/todoapp/specification/TodoSpecification.java`

**実装したメソッド**:
- `belongsToUser(User user)` - 自分のTodoのみ
- `notDeleted()` - 削除されていないもののみ
- `hasStatus(String status)` - completed/incomplete/allフィルタ
- `hasCategory(UUID categoryId)` - カテゴリフィルタ
- `hasPriorityIn(List<Integer> priorities)` - 優先度フィルタ

### 4. TodoService.searchメソッド 追加

Specificationを組み合わせて検索するメソッドを実装した。

```java
public List<Todo> search(User user, TodoSearchRequest request) {
    Specification<Todo> spec = Specification
        .where(TodoSpecification.belongsToUser(user))
        .and(TodoSpecification.notDeleted());

    spec = spec.and(TodoSpecification.hasStatus(request.getStatus()));
    spec = spec.and(TodoSpecification.hasCategory(request.getCategoryId()));
    spec = spec.and(TodoSpecification.hasPriorityIn(request.getPriority()));

    Sort sort = createSort(request.getSort(), request.getOrder());

    return todoRepository.findAll(spec, sort);
}
```

### 5. TodoController.searchエンドポイント 追加

**エンドポイント**: `POST /api/todos/search`

```java
@PostMapping("/search")
public Map<String, List<TodoResponse>> search(@RequestBody TodoSearchRequest request) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userService.findByEmail(email);

    List<TodoResponse> todos = todoService.search(user, request).stream()
        .map(todo -> new TodoResponse(todo))
        .toList();

    return Map.of("todos", todos);
}
```

### 6. N+1問題対策（@EntityGraph）

TodoとCategoryを1回のSQLで取得するように修正した。

**場所**: `src/main/java/com/example/todoapp/repository/TodoRepository.java`

```java
@EntityGraph(attributePaths = {"category"})
List<Todo> findAll(Specification<Todo> spec, Sort sort);
```

**結果**: LEFT JOINを含む1回のSQLでTodoとCategoryを同時取得

### 7. TodoResponseにCategory情報追加

**場所**: `src/main/java/com/example/todoapp/dto/TodoResponse.java`

```java
private CategoryResponse category;

// コンストラクタ内
if (todo.getCategory() != null) {
    this.category = new CategoryResponse(todo.getCategory());
} else {
    this.category = null;
}
```

---

## 学んだこと

### 1. Specificationパターン

- 検索条件を「部品」として分離できる
- nullを返すと条件が無視される（動的クエリに便利）
- `Specification.where().and().and()` で条件を組み合わせ

### 2. ラムダ式 `(root, query, cb) ->`

```java
return (root, query, cb) -> cb.equal(root.get("user"), user);
```

- `root`: 検索対象のエンティティ（テーブル）
- `query`: クエリ全体の設定
- `cb`: CriteriaBuilder（条件を組み立てる道具）

### 3. Javaの文字列比較

```java
// ❌ 間違い（参照の比較）
if (order == "asc")

// ✅ 正しい（値の比較）
if ("asc".equals(order))
```

### 4. デフォルト値の上書き問題

エンティティでデフォルト値を設定しても、setterでnullを渡すと上書きされる。

```java
// エンティティ
private Boolean isCompleted = false;

// サービス（問題のあるコード）
todo.setIsCompleted(request.getIsCompleted()); // nullが渡されると上書き

// 修正後
if (request.getIsCompleted() != null) {
    todo.setIsCompleted(request.getIsCompleted());
}
```

### 5. N+1問題と@EntityGraph

**N+1問題**: 親データN件に対して、子データ取得がN回発生する問題

**対策**: `@EntityGraph(attributePaths = {"category"})` でJOIN FETCH

**確認方法**: `spring.jpa.show-sql=true` でSQLログを確認し、`left join`があればOK

---

## 修正したエラー

| 問題 | 原因 | 修正 |
|------|------|------|
| importの不足 | JpaSpecificationExecutor等のimportがなかった | 必要なimportを追加 |
| List が raw type | `List` のまま使用 | `List<Integer>` に修正 |
| createSortにreturnがない | 値を返していなかった | return文を追加 |
| 文字列比較に == を使用 | `order == "asc"` と記述 | `"asc".equals(order)` に修正 |
| isCompletedがnullになる | setterでnullを設定していた | nullチェックを追加 |

---

## 完了したタスク

- [x] Spring Data JPA Specificationの学習
- [x] TodoSearchRequest.java 作成
- [x] TodoSpecification.java 作成
- [x] TodoRepository に JpaSpecificationExecutor 追加
- [x] TodoService.search メソッド追加
- [x] TodoController に POST /api/todos/search 追加
- [x] フィルタ機能（status, priority, category_id）
- [x] ソート機能（sort, order）
- [x] N+1問題対策（@EntityGraph）
- [x] TodoResponseにCategory情報追加
- [x] curlでの動作確認

## 次回のタスク

- [ ] 1.5.3 Categoryが削除されたときのTodoの扱い
- [ ] 1.6 フロントエンド実装開始

---

## 動作確認

### 1. ログイン（JWTトークン取得）

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "password123"}'
```

**レスポンス:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNzY5OTMyNzMzLCJleHAiOjE3NzAwMTkxMzN9.OaSFXCqSjsUR-Q2tk_8TrCn0ekLzTZtUEUUh4w-MO-8",
  "tokenType": "Bearer"
}
```

### 2. カテゴリ作成

```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {トークン}" \
  -d '{"name": "仕事", "color": "#49839c"}'
```

### 3. カテゴリ付きTodo作成

```bash
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {トークン}" \
  -d '{"title": "会議の準備", "priority": 2, "category_id": "{カテゴリID}"}'
```

### 4. 全件検索（POST /api/todos/search）

```bash
curl -X POST http://localhost:8080/api/todos/search \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {トークン}" \
  -d '{}'
```

**レスポンス:**
```json
{
  "todos": [
    {
      "id": "d3877413-0b68-4ecf-9dfd-ef2f3f9090f9",
      "categoryId": null,
      "title": "完了したTodo",
      "description": null,
      "due": null,
      "priority": 3,
      "isCompleted": true,
      "category": null
    },
    {
      "id": "3eca6cd6-f720-4865-b3b5-536ae3a93f68",
      "categoryId": null,
      "title": "優先度高いTodo",
      "description": null,
      "due": null,
      "priority": 1,
      "isCompleted": false,
      "category": null
    }
  ]
}
```

### 5. 未完了フィルタ

```bash
curl -X POST http://localhost:8080/api/todos/search \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {トークン}" \
  -d '{"status": "incomplete"}'
```

**レスポンス:**
```json
{
  "todos": [
    {
      "id": "c3133426-8318-4c04-9318-94772e4d8b44",
      "categoryId": null,
      "title": "優先度高いTodo",
      "description": null,
      "due": null,
      "priority": 1,
      "isCompleted": false,
      "category": null
    }
  ]
}
```

### 6. 優先度フィルタ

```bash
curl -X POST http://localhost:8080/api/todos/search \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {トークン}" \
  -d '{"priority": [1, 2]}'
```

**レスポンス:**
```json
{
  "todos": [
    {
      "id": "3eca6cd6-f720-4865-b3b5-536ae3a93f68",
      "categoryId": null,
      "title": "優先度高いTodo",
      "description": null,
      "due": null,
      "priority": 1,
      "isCompleted": false,
      "category": null
    }
  ]
}
```

### 7. N+1問題対策の確認（SQLログ）

`spring.jpa.show-sql=true` でSQLログを確認。LEFT JOINを含む1回のSQLで取得できていることを確認した。

```sql
select
    t1_0.id,
    c1_0.id,
    c1_0.color,
    c1_0.name,
    ...
from
    todos t1_0
left join
    categories c1_0
        on c1_0.id=t1_0.category_id
where
    t1_0.user_id=?
    and t1_0.deleted_at is null
order by
    t1_0.id desc
```

---

## 備考

- Specificationパターンは検索画面・管理画面で非常に有用
- @EntityGraphはN+1問題の簡単な対策方法として覚えておく
- SQLログで実際に発行されるクエリを確認する習慣をつける

---

**作成日**: 2026-02-01
