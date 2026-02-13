# JPAエンティティの書き方 - アノテーションと制約の設定方法

## 本記事を作成した背景

TodoアプリでUser、Category、Todoエンティティを作成する中で、JPAエンティティの書き方を体系的に整理しました。

## 本記事で取り組んだこと

- JPAエンティティの基本構造を理解する
- 各種制約（Null、Unique、外部キーなど）の設定方法を把握する
- ゲッター・セッターの役割を理解する
- Lombokを使った記述の簡略化を学ぶ

---

## 1. エンティティの基本構造

### 最小構成

```java
@Entity
@Table(name = "DBでのテーブル名")
public class Javaでのエンティティ名 {

    @Id
    @Column(name = "DBでのカラム名")
    private データ型 Javaでのフィールド名;
}
```

### 実際の例（Userエンティティ）

```java
package com.example.todoapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
}
```

### 各アノテーションの意味

| アノテーション | 意味 |
|--------------|------|
| `@Entity` | このクラスがJPAエンティティであることを示す |
| `@Table(name = "...")` | 対応するDBテーブル名を指定 |
| `@Id` | 主キーであることを示す |
| `@GeneratedValue` | 主キーの自動生成方法を指定 |
| `@Column` | カラムの詳細設定（名前、制約など） |

---

## 2. 制約の設定方法

### NOT NULL制約

カラムにNULLを許可しない場合:

```java
@Column(name = "email", nullable = false)
private String email;
```

### UNIQUE制約（単一カラム）

カラムの値を一意にする場合:

```java
@Column(name = "email", nullable = false, unique = true)
private String email;
```

### 複合ユニーク制約

複数カラムの組み合わせで一意にする場合（例: 同じユーザーが同名のカテゴリを作れない）:

```java
@Entity
@Table(name = "categories",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"}))
public class Category {
    // ...
}
```

### 外部キー制約

他のテーブルへの参照を設定する場合:

```java
// 多対1のリレーション（CategoryはUserに属する）
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;
```

| アノテーション | 意味 |
|--------------|------|
| `@ManyToOne` | 多対1のリレーション |
| `@JoinColumn` | 外部キーカラムの設定 |
| `fetch = FetchType.LAZY` | 遅延読み込み（アクセス時に取得） |

### 文字数制限

カラムの最大文字数を設定する場合:

```java
@Column(name = "name", nullable = false, length = 100)
private String name;
```

### デフォルト値設定

Javaのフィールド初期化で設定:

```java
@Column(name = "priority")
private Integer priority = 3;  // デフォルト値: 3

@Column(name = "is_completed")
private Boolean isCompleted = false;  // デフォルト値: false
```

**注意**: これはJavaレベルのデフォルト値です。DBレベルのデフォルト値を設定したい場合は`columnDefinition`を使います:

```java
@Column(name = "priority", columnDefinition = "integer default 3")
private Integer priority;
```

---

## 3. ゲッターとセッターの必要性

### なぜ必要なのか

JPAエンティティのフィールドは`private`で宣言します。外部からフィールドにアクセスするには、ゲッター（取得）とセッター（設定）が必要です。

```java
public class User {
    private Long id;
    private String email;

    // ゲッター: フィールドの値を取得
    public Long getId() {
        return id;
    }

    // セッター: フィールドに値を設定
    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
```

### 使用例

```java
User user = new User();
user.setEmail("test@example.com");  // セッターで値を設定
String email = user.getEmail();      // ゲッターで値を取得
```

### 問題点

フィールドが増えると、ゲッター・セッターの記述量が膨大になります。4つのフィールドがあれば8つのメソッドが必要です。

---

## 4. Lombokを使った簡略化

### Lombokとは

Lombokは、ゲッター・セッターなどのボイラープレートコード（定型コード）を自動生成してくれるライブラリです。

### 導入方法

`build.gradle.kts`に以下を追加:

```kotlin
dependencies {
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}
```

### 使い方

クラスに`@Data`アノテーションを付けるだけ:

```java
import lombok.Data;

@Data
@Entity
@Table(name = "categories")
public class Category {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "color", nullable = false, length = 20)
    private String color;
}
```

### @Dataが自動生成するもの

| 生成されるもの | 説明 |
|--------------|------|
| ゲッター | 全フィールドの`getXxx()`メソッド |
| セッター | 全フィールドの`setXxx()`メソッド |
| `toString()` | オブジェクトの文字列表現 |
| `equals()` | オブジェクトの等価性比較 |
| `hashCode()` | ハッシュコード生成 |

### 比較: Lombokあり vs なし

**Lombokなし（User.java: 55行）**:

```java
@Entity
@Table(name = "users")
public class User {
    private Long id;
    private String email;
    private String passwordHash;
    private LocalDateTime deletedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
```

**Lombokあり（Category.java: 33行）**:

```java
@Data
@Entity
@Table(name = "categories")
public class Category {
    private UUID id;
    private User user;
    private String name;
    private String color;
    private LocalDateTime deletedAt;
}
```

---

## 学び・次に活かせる知見

- エンティティは`@Entity`と`@Table`で定義し、`@Column`で各フィールドの制約を設定する
- 複合ユニーク制約は`@Table`の`uniqueConstraints`で設定する
- 外部キーは`@ManyToOne` + `@JoinColumn`で設定する
- Lombokの`@Data`を使うと、ゲッター・セッターを手動で書く必要がなくなる

---

## 参考文献

1. [Jakarta Persistence (JPA) Specification](https://jakarta.ee/specifications/persistence/)
2. [Lombok - @Data](https://projectlombok.org/features/Data)

---

## 更新履歴

- 2026-01-27: 初版作成
