# Spring Bootとは何か？

## 1. Spring Bootの概要

### 1-1. Spring Bootとは何か？

Springを「すぐ動く・実務向け」にした、JavaのWebアプリケーションフレームワーク。

**Spring Frameworkとの違い**

Spring FrameworkはSpring Bootの基盤である。
Spring Frameworkに

- デフォルトのサーバ
- デフォルトの環境設定

をプラスしたものがSpring Bootである。
サーバ構築・設定負荷が下がっている。

**なぜSpring Bootが人気なのか？**

- 環境構築がラク
- API、DB、認証など必要な機能が揃っている
- Javaバックエンド開発のデファクトスタンダードとなっている

### 1-2. Spring Bootの特徴

初期構築が早く、API・DB・認証まで揃っている。

**自動設定（Auto-configuration）**

Webアプリを作りたい、DB接続したい…
→ ざっくりそうと宣言するだけで、細かな設定は勝手にしてくれる。

**組み込みサーバー（Tomcat）**

Spring Bootには組み込みWebサーバー（デフォルトはTomcat）が含まれている。
→ **外部サーバー構築の手間を省く**ことができ、マイクロサービス・コンテナ運用との相性が良い。

**スターター依存関係（Starter Dependencies）**

用途ごとに**依存関係をまとめたセット**が用意されている。

例：

- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-security

メリット：

- ライブラリ選定で迷わない
- バージョン衝突が起きにくい
- 設定と依存関係が一貫する

### 1-3. Spring Bootで何ができるか？

モダンなJavaバックエンド開発の全てができる。

**REST APIの作成**

- JSONベースのAPIを簡単に実装可能
- Controller + Annotation で完結

用途例:

- フロントエンド（React / Vue）用API
- モバイルアプリ向けAPI
- マイクロサービス間通信

**データベース接続**

対応範囲は非常に広い。

- RDB：PostgreSQL / MySQL / Oracle 等
- ORM：JPA / Hibernate
- マイグレーション：Flyway / Liquibase

特徴:

- 接続設定は application.yml だけ
- CRUDはRepositoryで自動生成
- トランザクション管理も自動

**認証・認可**

Spring Security と組み合わせることで、以下が可能。

- ログイン／ログアウト
- JWT認証
- ロール・権限制御
- OAuth2 / OpenID Connect

---

## 2. Bean（ビーン）を理解する

### 2-1. Beanとは何か？

**Bean = Springが管理するオブジェクト**

通常のJavaでは、オブジェクトは自分で `new` して作る：

```java
// 通常のJava
UserService userService = new UserService();
TodoService todoService = new TodoService();
```

Springでは、**Springが代わりにオブジェクトを作って管理してくれる**。この「Springが管理するオブジェクト」をBeanと呼ぶ。

### 2-2. なぜBeanを使うのか？

**1. 依存関係の自動注入**

```java
@RestController
public class TodoController {

    private final TodoService todoService;

    // Springが自動でTodoServiceのBeanを渡してくれる
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }
}
```

`new TodoService()` と書かなくても、Springが自動でBeanを渡してくれる。

**2. シングルトン管理**

デフォルトでは、Beanはアプリ全体で1つだけ作られる。何度呼び出しても同じインスタンスが使われるので、メモリ効率が良い。

**3. ライフサイクル管理**

Beanの生成・初期化・破棄をSpringが管理する。開発者はビジネスロジックに集中できる。

### 2-3. Beanの登録方法

**方法1: クラスにアノテーションを付ける（よく使う）**

```java
@Service          // これでBeanになる
public class TodoService { ... }

@Repository       // これでBeanになる
public class TodoRepository { ... }

@Controller       // これでBeanになる
public class TodoController { ... }

@Component        // 汎用的なBean（上記に当てはまらない場合）
public class MyComponent { ... }
```

**方法2: `@Bean`アノテーションを使う**

設定クラス内でメソッドに`@Bean`を付けると、そのメソッドの戻り値がBeanになる：

```java
@Configuration
public class SecurityConfig {

    @Bean  // このメソッドの戻り値がBeanとして登録される
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        // ...設定処理...
        return http.build();
    }
}
```

この方法は「自分で作ったクラスではないもの」や「複雑な初期化が必要なもの」をBean化するときに使う。

### 2-4. BeanとDIコンテナ

**DIコンテナ（= IoCコンテナ）**

Beanを作成・保管・管理する仕組みのこと。Spring Bootでは `ApplicationContext` がこの役割を担う。

```
┌─────────────────────────────────────────┐
│           DIコンテナ                     │
│  ┌───────────┐  ┌───────────┐          │
│  │TodoService│  │UserService│  ...     │
│  │  (Bean)   │  │  (Bean)   │          │
│  └───────────┘  └───────────┘          │
│                                         │
│  必要に応じてBeanを取り出して注入         │
└─────────────────────────────────────────┘
           ↓ 注入
┌─────────────────────────────────────────┐
│  TodoController                         │
│  - todoService: TodoService ← 自動注入  │
└─────────────────────────────────────────┘
```

### 2-5. Bean関連の用語まとめ

| 用語 | 意味 |
|-----|------|
| Bean | Springが管理するオブジェクト |
| DIコンテナ | Beanを作成・管理する仕組み |
| `@Component` | クラスをBeanとして登録（汎用） |
| `@Service` | サービス層のBeanとして登録 |
| `@Repository` | データアクセス層のBeanとして登録 |
| `@Controller` | コントローラー層のBeanとして登録 |
| `@Bean` | メソッドの戻り値をBeanとして登録 |
| `@Configuration` | Bean定義を含む設定クラスであることを示す |

---

## 3. 主要なアノテーションを理解する

| アノテーション | 役割 | 使う場所 |
|--------------|------|---------|
| `@SpringBootApplication` | Spring Bootアプリケーションのエントリーポイント | Mainクラス |
| `@RestController` | REST API化する | コントローラークラス |
| `@Service` | ビジネスロジック処理を担当（Beanとして登録） | サービスクラス |
| `@Repository` | DB接続を担当（Beanとして登録） | リポジトリクラス |
| `@Entity` | JavaクラスとDBテーブルの対応付けを行う | エンティティクラス |
| `@GetMapping` | HTTP GETリクエストを処理 | コントローラーメソッド |
| `@PostMapping` | HTTP POSTリクエストを処理 | コントローラーメソッド |
| `@RequestBody` | リクエストボディをJavaオブジェクトに変換 | メソッド引数 |
| `@PathVariable` | URLパスからパラメータを取得 | メソッド引数 |

### @SpringBootApplication

Springの初期化・設定読み込み・DI開始をまとめて行う。

中身は実は3つのアノテーションの集合：

- `@Configuration`（設定クラス）
- `@EnableAutoConfiguration`（自動設定）
- `@ComponentScan`（Bean探し）

```java
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

`@ComponentScan` により、このクラスと同じパッケージ以下にある `@Service`, `@Repository`, `@Controller` などが自動的にBeanとして登録される。

### @RestController

HTTPリクエストを受け取り、JSONを返す窓口（画面HTMLは返さない）。

- `@Controller` + `@ResponseBody` の省略形
- Web API専用（だから画面HTMLは無理でResponse BodyのJSONのみOK）
- ここでは**リクエスト受付やパラメータ変換、Service呼び出し**など、簡単な処理だけ行う
- **複雑な業務ロジックやDBの直接操作**等はServiceに切り出す

```java
@RestController
@RequestMapping("/api/todos")
public class TodoController {
    @GetMapping
    public List<Todo> list() {
        return todoService.findAll();
    }
}
```

### @Service

ControllerとRepositoryの間に立って、アプリの**ビジネスロジック**を担当。

※補足: なぜControllerを薄くする？

- ロジックとAPI処理に切り分けることで、それぞれのテストがしやすくなるため
- 1つのロジックを複数のAPIに再利用することができるため

```java
@Service
public class TodoService {
    public void completeTodo(UUID id) {
        // 業務ルール
        // 状態チェック
        // Repository呼び出し
    }
}
```

---

## 4. 依存性注入（DI）とIoCを理解する

### 4-1. 依存性注入（DI）とは？

**Dependency Injection（依存性注入）**

必要なオブジェクト（Bean）の生成や管理を、

- `new` を使って自分の関数の中で行うの**ではなく**
- **Springに任せる**ことで

疎結合にして**変更に強い設計とする**という設計の考え方のこと。

### 4-2. IoC（制御の反転）とは？

**Inversion of Control（制御の反転）**

- **プログラマーではなく、フレームワーク**がオブジェクト（Bean）のライフサイクル（生成や依存関係解決等）を管理すること

← IoCを実現するために、DIをしている。

### 4-3. @Autowiredアノテーション

Springに「この依存関係を自動で解決して注入してほしい」と伝える印。

※ コンストラクタが1つだけの場合（以下例参照）、`@Autowired` が自動的に適用されるため、**省略可能**。

### 4-4. コンストラクタインジェクション（推奨）

そのクラス（例: `TodoController`）が動作するために必須な依存関係（Bean）を、コンストラクタの引数（例: `TodoService`）として受け取る設計。

```java
@Service
public class TodoService {
    // ...
}

@RestController
public class TodoController {
    private final TodoService todoService;

    // コンストラクタインジェクション
    // SpringがTodoServiceのBeanを自動で渡してくれる
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }
}
```

ポイント:

- `TodoService` をコンストラクタ（`public TodoController`）の引数で受け取っている
- `new TodoService()` は書いていない（クラス内部で `new` してはダメ）
- なぜ `final` と付いているのか？ → 初期化時にSpringに依存関係の調整をさせた後、変更されないの意 = この依存関係は必須という設計意思の表明

### 4-5. フィールドインジェクション（非推奨）

クラス直下のフィールドに、コンストラクタを通さずにフレームワークが直接依存オブジェクト（Bean）を注入すること。

```java
@Service
public class TodoService {
    // ...
}

@RestController
public class TodoController {
    @Autowired
    private TodoService todoService;  // Springが直接注入
}
```

### 4-6. コンストラクタ vs フィールドインジェクション

| 観点 | フィールドインジェクション | コンストラクタインジェクション |
|-----|------------------------|--------------------------|
| 注入場所 | フィールド（メンバ変数） | コンストラクタ引数 |
| 注入タイミング | オブジェクト生成後 | オブジェクト生成時 |
| 依存関係の見え方 | コードから分かりにくい | 必須依存が明示される |
| 不変性（final） | 付与できない | 付与できる |
| 設計上の安全性 | 低い（後付け注入） | 高い（生成時に完全な状態） |
| テストのしやすさ | 低い | 高い |
| 推奨度（Spring） | 非推奨 | **推奨** |

---

## 5. プロジェクト構造を理解する

### 5-1. 典型的なプロジェクト構造

```
src/
  main/
    java/
      com/example/todo/
        TodoApplication.java         ← エントリーポイント
        controller/                  ← REST APIのエンドポイント
          TodoController.java
          CategoryController.java
        service/                     ← ビジネスロジック
          TodoService.java
          CategoryService.java
        repository/                  ← データアクセス
          TodoRepository.java
          CategoryRepository.java
        entity/                      ← データモデル
          Todo.java
          Category.java
          User.java
        dto/                         ← データ転送オブジェクト
          TodoRequest.java
          TodoResponse.java
        config/                      ← 設定クラス
          SecurityConfig.java
    resources/
      application.properties         ← 設定ファイル
```

- `com/example/todo/`: アプリケーション全体のルート。`TodoApplication.java` を起点に、この配下のクラスがSpringで自動検出される。
- 原則: すべての Controller / Service / Repository は `TodoApplication.java` より下のパッケージに置く。

### 5-2. 各層の役割

| 層 | 役割 | Beanアノテーション |
|----|------|------------------|
| Controller層 | API化（リクエスト受付） | `@RestController` |
| Service層 | ビジネスロジック | `@Service` |
| Repository層 | DB接続 | `@Repository` |
| Entity層 | データモデル | `@Entity`（※Beanではない） |
| DTO層 | APIのリクエスト・レスポンス用データ | なし（※Beanではない） |
| Config層 | 設定・Bean定義 | `@Configuration` |

### 5-3. この構造をとる理由

変更に耐える設計であるから（変更理由ごとに変更箇所が限定される）。

| 変更内容 | 影響範囲 |
|---------|---------|
| API仕様変更 | Controller / DTO |
| 業務ルール変更 | Service |
| DB変更 | Entity / Repository |
| フロント追加 | DTOのみ |

---

## まとめ

Spring Bootを理解するための重要な概念：

1. **Bean** - Springが管理するオブジェクト
2. **DIコンテナ** - Beanを作成・管理する仕組み
3. **DI（依存性注入）** - Beanを自動で渡してもらう仕組み
4. **IoC（制御の反転）** - フレームワークがオブジェクトを管理する考え方
5. **アノテーション** - `@Service`, `@Repository` などでBeanを登録し、層を明示する

これらの仕組みにより、開発者は `new` でオブジェクトを管理する必要がなく、ビジネスロジックに集中できる。
