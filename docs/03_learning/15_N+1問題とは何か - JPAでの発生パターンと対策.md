# N+1問題とは何か - JPAでの発生パターンと対策

## 本記事を作成した背景

TodoアプリでCategoryエンティティを作成しているとき、CategoryにUserオブジェクトが含まれている構造を見て「これはN+1問題を起こさないのか？」という疑問が生まれました。この機会にN+1問題について整理しました。

## 本記事で取り組んだこと

- N+1問題とは何かを理解する
- JPAでN+1問題が発生するパターンを把握する
- 今回のTodoアプリの構造でN+1問題が起きにくい理由を理解する
- 将来N+1問題に直面したときの対策を知る

---

## N+1問題とは

### 概要

N+1問題とは、**1回のクエリで済むはずのデータ取得が、N+1回のクエリになってしまう**パフォーマンス問題です。

### 具体例

カテゴリ10件を取得して、それぞれの所有者（User）のメールアドレスを表示する場合を考えます。

**理想（1回のクエリ）**:
```sql
SELECT c.*, u.email
FROM categories c
JOIN users u ON c.user_id = u.id;
```

**N+1問題が発生した場合（11回のクエリ）**:
```sql
-- 1回目: カテゴリ一覧を取得
SELECT * FROM categories;

-- 2〜11回目: 各カテゴリのUserを個別に取得
SELECT * FROM users WHERE id = 1;
SELECT * FROM users WHERE id = 2;
SELECT * FROM users WHERE id = 3;
...（10回繰り返し）
```

10件なら11回で済みますが、1000件なら1001回のクエリが発行されます。これがパフォーマンス低下の原因になります。

---

## JPAでN+1問題が発生するパターン

### 発生しやすいコード例

```java
// 全カテゴリを取得
List<Category> allCategories = categoryRepository.findAll();

// 各カテゴリの所有者名を表示 ← ここでN+1発生！
for (Category cat : allCategories) {
    System.out.println(cat.getUser().getEmail());
}
```

このコードでは:
1. `findAll()`で全カテゴリを取得（1回のクエリ）
2. ループ内で`getUser()`を呼ぶたびにUserを取得（N回のクエリ）

### なぜこうなるのか？

JPAの`FetchType.LAZY`（遅延読み込み）が原因です。

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;
```

- `LAZY` = 関連エンティティは**アクセスするまで読み込まない**
- `category.getUser()`を呼んだ瞬間に、初めてUserを取得するクエリが走る
- ループ内で呼ぶと、毎回クエリが発行される

---

## 今回のTodoアプリでN+1問題が起きにくい理由

### 理由1: FetchType.LAZYを使っている

```java
// Category.java
@ManyToOne(fetch = FetchType.LAZY)
private User user;
```

`getUser()`を呼ばなければ、Userのクエリは発行されません。

### 理由2: ユースケースを考える

CategoryServiceのメソッド:
```java
public List<Category> findByUser(User user) {
    return categoryRepository.findByUser(user);
}
```

このメソッドを呼ぶとき:
1. すでに**ログイン中のUserオブジェクト**を持っている
2. そのUserのカテゴリ一覧を取得する
3. レスポンスで返すのはカテゴリ情報（id, name, color）だけ
4. **category.getUser()を呼ぶ必要がない**

→ Userへのアクセスがないので、N+1問題は発生しません。

### まとめ表

| 状況 | N+1問題 |
|------|---------|
| ログインユーザーの自分のカテゴリを取得 | 発生しない |
| 全カテゴリを取得して各Userにアクセス | 発生する可能性あり |

---

## N+1問題の対策（将来のために）

もし将来、管理画面などで「全ユーザーのデータを一覧表示」するような機能を作る場合の対策です。

### 対策1: JOIN FETCHを使う

```java
@Query("SELECT c FROM Category c JOIN FETCH c.user")
List<Category> findAllWithUser();
```

これにより、1回のクエリでCategoryとUserを同時に取得できます。

### 対策2: @EntityGraphを使う

```java
@EntityGraph(attributePaths = {"user"})
List<Category> findAll();
```

`@EntityGraph`を使うと、指定した関連エンティティを一緒に読み込みます。

### 対策3: DTOプロジェクションを使う

```java
@Query("SELECT new com.example.dto.CategoryWithUserDto(c.id, c.name, u.email) " +
       "FROM Category c JOIN c.user u")
List<CategoryWithUserDto> findAllWithUserEmail();
```

必要なフィールドだけを取得するDTOを使う方法です。

---

## 学び・次に活かせる知見

- N+1問題は「ループ内で関連エンティティにアクセスする」パターンで発生しやすい
- `FetchType.LAZY`は必要なときだけデータを取得するので効率的だが、使い方を誤るとN+1問題の原因になる
- 今回のTodoアプリのように「自分のデータだけ扱う」場合は、N+1問題を心配する必要はほぼない
- 将来、管理画面などで大量データを扱うときは`JOIN FETCH`や`@EntityGraph`を検討する

---

## 参考文献

1. [Spring Data JPA - Query Methods](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods)
2. [Hibernate - Fetching Strategies](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#fetching)

---

## 更新履歴

- 2026-01-27: 初版作成
