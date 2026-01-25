## 1. ORM（Object-Relational Mapping）とは何か

SQLをベタ打ちせずに、RDB側で行ってほしい処理をJavaやPythonといったプログラミング言語で記載すること。

**なぜ必要なのか**:
プログラミング言語で記載することによって、

- 生SQLを書かなくて済む（MySQL → PostgreSQL への移行対応がラク）
- 返ってきたデータの変換処理が不要になる（ORMにて既に定義済みの型に自動変換されるため）
- 型安全性（コンパイル時にエラーを検出できる）

といったメリットがある。

**Javaオブジェクトとデータベーステーブルの対応関係**:

| Java   | RDB      |
| ------ | -------- |
| クラス    | テーブル     |
| インスタンス | レコード（1行） |
| フィールド  | カラム      |
| フィールド値 | カラム値     |
| 参照（関連） | 外部キー     |

## 2. JPAとHibernateの関係

**JPAは「仕様」、Hibernateは「実装」**:

- JPA（Java Persistence API）
  - Java標準の ORM仕様（インターフェース・ルール）
  - 「どう振る舞うべきか」を定義している
  - 実装は持たない
- Hibernate
  - JPA仕様を満たした 具体的なORM実装
  - 実際にSQLを発行し、DBと通信する
  - JPA以外の独自拡張も多数持つ

**Spring Data JPAがどう関わるか**:
Spring Data JPAは、JPAをさらに使いやすくするためのSpringの抽象レイヤー。

Spring Data JPAのおかげで、

- EntityManager を直接触らない
- CRUDメソッドを自作しない
- SQLを書かずにRepository操作が可能

という状態になる。

**Spring Data JPAの威力**:
JpaRepositoryを継承するだけでsave(), findAll(), findById(),delete()などが自動で使える。

**JPA → Hibernate → JDBCの階層構造**:
JDBCはJava標準の低レベルDB接続APIであり、「直接SQLを投げて、結果を受け取る」だけ。ORMの概念は無い。

Hibernateは最終的に必ずJDBCを使う。ログ設定でSQLを確認可能。

| レイヤー            | 役割                       |
| --------------- | ------------------------ |
| Spring Data JPA | Repositoryを自動生成、CRUDの簡略化 |
| JPA             | ORMの標準仕様                 |
| Hibernate       | ORMの実装（SQL発行・マッピング）      |
| JDBC            | DBとの低レベル通信               |

## 3. 主要なアノテーション

**@Entity - クラスをエンティティとして定義**:

- このクラスが DBと永続化される対象であることを示す
- JPA管理下のオブジェクトになる

```java
@Entity
public class Todo {
}
```

これが無いクラスは、ORMの対象にならない。

**@Table - テーブル名を指定**:

- 対応するテーブル名を明示的に指定
- 省略時はクラス名を元に自動決定される

```java
@Entity
@Table(name = "todos")
public class Todo {
}
```

命名規則（複数形・スネークケースなど）をDB側に合わせるためによく使う。

**@Id - 主キーを指定**:

- エンティティの 一意識別子
- 必須（JPAでは必ず主キーが必要）

```java
@Id
private Long id;
```

これが無いとエンティティとして成立しません。

**@GeneratedValue - 主キーの生成戦略**:

- 主キーを**誰が・どう生成するか**を指定
- PostgreSQLでの主キー戦略: PostgreSQLでは**IDENTITY**が一般的です（SERIAL型に対応）

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

代表的な戦略:

| 戦略       | 説明                |
| -------- | ----------------- |
| IDENTITY | DBのAUTO_INCREMENT |
| SEQUENCE | DBのシーケンス          |
| AUTO     | 実装依存で自動選択         |

**@Column - カラムの設定**:

- カラム名や制約を指定
- 省略可能だが、制約を明示したい場合に使用

```java
@Column(name = "title", nullable = false, length = 255)
private String title;
```

指定できる代表例:

- `name`: カラム名
- `nullable`: NULL可否
- `length`: VARCHAR長
- `unique`: 一意制約

### まとめ

```java
@Entity
@Table(name = "todos")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
}
```

この1クラスで、

- Javaオブジェクト
- DBテーブル
- 主キー生成
- カラム制約

までを宣言的に定義できるのが、JPA + ORMです。
