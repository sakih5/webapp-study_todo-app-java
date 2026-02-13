## 1. Specification とは何か

**Specification**とは、

> **JPA Criteria API を使って「検索条件そのもの」をオブジェクトとして表現する仕組み**

です。

Spring Data JPA では、Specification は次のように定義されています。

```java
@FunctionalInterface
public interface Specification<T> {
    Predicate toPredicate(
        Root<T> root,
        CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder
    );
}
```

ポイントは以下です。

- SQL / JPQL を文字列で書かない
- 条件を**Javaコードとして**安全に構築できる
- 条件そのものを**部品（オブジェクト）として**扱える

つまり Specification は
「**WHERE 句を組み立てるためのオブジェクト**」
と捉えると理解しやすいです。

## 2. なぜ必要なのか（複数条件を動的に組み合わせるため）

Specification が必要になる典型的な理由は次です。

❌ JPQL / Repositoryメソッドの限界

条件が増えると、以下のような地獄になります。

```java
findByStatusAndCategoryAndCreatedAtAfter(...)
findByStatusAndCategory(...)
findByStatusAndCreatedAtAfter(...)
findByCategory(...)
```

- 条件の組み合わせ爆発
- null 条件対応がつらい
- Repository が肥大化

✅ Specification を使うと

- 条件を 必要なものだけ組み立てる
- AND / OR を 動的に結合
- Repository のメソッド数を増やさない

```java
Specification<User> spec = Specification
    .where(hasStatus(status))
    .and(hasEmail(email))
    .and(createdAfter(date));
```

→ **検索条件を「組み立てる」発想に切り替えられる**のが最大の価値です。

## 3. 基本的な使い方

### Repository に追加するもの

Specificationを使うには、Repositoryにこれを追加します。

```java
public interface UserRepository
    extends JpaRepository<User, Long>,
            JpaSpecificationExecutor<User> {
}
```

ここが最重要ポイントです。

- JpaRepository：CRUD
- JpaSpecificationExecutor：Specification 実行用

### Specification の定義例

```java
public class UserSpecifications {

    public static Specification<User> hasEmail(String email) {
        return (root, query, cb) ->
            email == null ? null : cb.equal(root.get("email"), email);
    }

    public static Specification<User> hasStatus(UserStatus status) {
        return (root, query, cb) ->
            status == null ? null : cb.equal(root.get("status"), status);
    }
}
```

- 条件不要なら null を返す → Spring 側が無視する
- 1条件 = 1メソッド、という分割が定石

### 実行側（Service）

```java
Specification<User> spec = Specification
    .where(UserSpecifications.hasEmail(email))
    .and(UserSpecifications.hasStatus(status));

List<User> users = userRepository.findAll(spec);
```

- AND / OR を自由に組み替え可能
- 条件追加・削除が容易

## 4. キーワード

### Spring Data JPA Specification

- Spring Data JPA が提供する 動的クエリ構築機構
- 内部的には JPA Criteria API を使用
- 型安全・部品化・再利用が可能

### JpaSpecificationExecutor

- Specification を実行するための Repository拡張インターフェース
- 主なメソッド：

```java
List<T> findAll(Specification<T> spec);
Page<T> findAll(Specification<T> spec, Pageable pageable);
long count(Specification<T> spec);
```

## 5. Q&A

### Q1. Specification と JPQL の違いは何か？

| 観点   | Specification | JPQL |
| ---- | ------------- | ---- |
| 記述方法 | Javaコード       | 文字列  |
| 動的条件 | 得意            | 苦手   |
| 型安全  | あり            | なし   |
| 再利用性 | 高い            | 低い   |
| 可読性  | 慣れが必要         | 直感的  |

→ まとめると…

- 固定クエリ → JPQL
- 検索条件が動的・複雑 → Specification

### Q2. Specification を使うために Repository に何を追加する必要があるか？

`JpaSpecificationExecutor<T>`を`extends`に追加する必要がある。

```java
public interface UserRepository
    extends JpaRepository<User, Long>,
            JpaSpecificationExecutor<User> {
}
```

これがないと`findAll(Specification)`は使えない。

### 6. まとめ

- Specification = WHERE句の組み立て部品
- 複雑な検索条件が出たら導入価値が高い
- Repository は薄く、条件ロジックは Specification に集約
- 検索画面・管理画面・絞り込み検索と相性が良い
