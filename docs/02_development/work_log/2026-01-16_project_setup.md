# 作業ログ: 2026-01-16 (プロジェクトセットアップ)

## 学習情報

- **日付**: 2026-01-16
- **学習フェーズ**: WBS 1.2 環境構築・Spring Boot学習
- **学習時間**: 約40分
- **ステータス**: プロジェクト作成完了、IntelliJでのロード中

---

## 完了したタスク

### 1. Spring Initializer でプロジェクト生成

**実施内容:**

- <https://start.spring.io/> にアクセス
- プロジェクト設定を選択
- ZIPファイルをダウンロード

**選択した設定:**

| 項目 | 選択内容 |
|------|---------|
| Project | Gradle - Kotlin |
| Language | Java |
| Spring Boot | 3.2.x |
| Group | com.example |
| Artifact | todoapp |
| Package name | com.example.todoapp |
| Packaging | Jar |
| Java | 17 |

**Dependencies（依存関係）:**

- Spring Web
- Spring Data JPA
- PostgreSQL Driver
- Spring Security
- Lombok
- Validation

---

## 生成されたプロジェクト構造

### ファイル・ディレクトリ一覧

```txt
todoapp/
│  .gitattributes
│  .gitignore
│  build.gradle.kts         ← ビルド設定ファイル（最重要）
│  gradlew                  ← Gradle Wrapper（Linux/Mac）
│  gradlew.bat              ← Gradle Wrapper（Windows）
│  HELP.md                  ← 参考リンク集
│  settings.gradle.kts      ← プロジェクト基本設定
│
├─gradle
│  └─wrapper
│          gradle-wrapper.jar
│          gradle-wrapper.properties
│
└─src
    ├─main
    │  ├─java
    │  │  └─com
    │  │      └─example
    │  │          └─todoapp
    │  │                  TodoappApplication.java  ← メインクラス
    │  │
    │  └─resources
    │      │  application.properties  ← 設定ファイル
    │      │
    │      ├─static           ← 静的ファイル（CSS, JS）
    │      └─templates        ← HTMLテンプレート
    └─test
        └─java
            └─com
                └─example
                    └─todoapp
                            TodoappApplicationTests.java
```

---

## ファイル・ディレクトリの役割

### ルートディレクトリのファイル

#### build.gradle.kts ⭐ 最重要

**役割**: ビルド設定ファイル（Gradleの設定）

**内容:**

- 使用するSpring Bootのバージョン
- プロジェクトの依存関係（Spring Web, JPA, PostgreSQLなど）
- Javaのバージョン
- ビルド方法

**類似**: package.json（Node.js）、pom.xml（Maven）

#### settings.gradle.kts

**役割**: プロジェクトの基本設定

**内容:**

- プロジェクト名（rootProject.name = "todoapp"）
- マルチモジュールプロジェクトの場合の設定

#### gradlew / gradlew.bat

**役割**: Gradle Wrapper（グラドル・ラッパー）

- `gradlew`: Linux/Mac用のシェルスクリプト
- `gradlew.bat`: Windows用のバッチファイル

**目的**: Gradleをインストールしていなくても、このスクリプトで実行できる

**使い方**:

- `./gradlew build`（Mac/Linux）
- `gradlew.bat build`（Windows）

#### .gitignore

**役割**: Gitで追跡しないファイルを指定

**内容:**

- `build/`（ビルド成果物）
- `.gradle/`（Gradleのキャッシュ）
- `*.class`（コンパイル済みファイル）

---

### gradle/wrapper/

**役割**: Gradle Wrapperの本体

- `gradle-wrapper.jar`: Wrapperの実行ファイル
- `gradle-wrapper.properties`: Gradleのバージョン指定

**なぜ必要？**
チーム全員が同じバージョンのGradleを使えるようにするため

---

### src/main/ - アプリケーション本体

#### src/main/java/

**役割**: Javaのソースコードを配置する場所

**現在のファイル:**

```
com/example/todoapp/TodoappApplication.java
└─ Spring Bootのメインクラス（アプリケーションの起動点）
```

**TodoappApplication.java の役割:**

```java
@SpringBootApplication
public class TodoappApplication {
    public static void main(String[] args) {
        SpringApplication.run(TodoappApplication.class, args);
    }
}
```

これが**アプリケーションの起動点**。

#### src/main/resources/

**役割**: リソースファイルを配置（設定ファイル、HTMLなど）

- `application.properties`: アプリケーションの設定
  - データベース接続情報
  - ポート番号
  - など

- `static/`: 静的ファイル（CSS, JS, 画像）

- `templates/`: HTMLテンプレート（Thymeleafなど）
  - ※今回はREST APIなので使わない予定

---

### src/test/ - テストコード

#### src/test/java/

**役割**: テストコードを配置

**現在のファイル:**

```
TodoappApplicationTests.java
└─ アプリケーションが起動するかのテスト
```

---

## プロジェクト構造の意味

### Javaの標準的なプロジェクト構造

```
src/
├─ main/          ← 本番用のコード
│  ├─ java/       ← Javaソースコード
│  └─ resources/  ← 設定ファイル
└─ test/          ← テストコード
   ├─ java/       ← テスト用Javaコード
   └─ resources/  ← テスト用設定
```

**なぜこの構造？**

- Maven/Gradleの標準規約
- ツールが自動で認識できる
- チーム開発で混乱しない

---

## 今後作成するファイル・ディレクトリ

### Phase 1で作成予定

```
src/main/java/com/example/todoapp/
├─ TodoappApplication.java  ← 既にある（起動点）
├─ entity/                  ← これから作る
│  ├─ User.java
│  ├─ Category.java
│  └─ Todo.java
├─ repository/              ← これから作る
│  ├─ UserRepository.java
│  ├─ CategoryRepository.java
│  └─ TodoRepository.java
├─ service/                 ← これから作る
│  ├─ AuthService.java
│  ├─ TodoService.java
│  └─ CategoryService.java
├─ controller/              ← これから作る
│  ├─ AuthController.java
│  ├─ TodoController.java
│  └─ CategoryController.java
├─ dto/                     ← これから作る
│  ├─ request/
│  │  ├─ SignupRequest.java
│  │  ├─ LoginRequest.java
│  │  └─ ...
│  └─ response/
│     ├─ UserResponse.java
│     ├─ TodoResponse.java
│     └─ ...
├─ config/                  ← これから作る
│  └─ SecurityConfig.java
└─ exception/               ← これから作る
   ├─ GlobalExceptionHandler.java
   └─ ...
```

これらを段階的に作っていく。

---

## IntelliJ IDEA でのプロジェクトオープン

### 実施内容

1. **IntelliJ IDEAを起動**

2. **プロジェクトを開く**
   - 「Open」をクリック
   - 解凍したフォルダ（`todoapp`）を選択
   - `build.gradle.kts` があるフォルダを選択

3. **Gradleプロジェクトのロードダイアログが表示**

   ```
   Gradle'TodoApp'ビルドスクリプトが見つかりました
   [ロード] [スキップ]
   ```

4. **「ロード」を選択**
   - build.gradle.kts を解析
   - 依存関係をダウンロード
   - プロジェクトとして認識

---

## Gradleプロジェクトのロードで起こること

### 1. build.gradle.kts の解析

- どんなライブラリが必要か読み取る

### 2. 依存関係のダウンロード

以下のライブラリが自動でダウンロードされる:

- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL Driver
- Spring Security
- Lombok
- Validation
- その他の推移的依存関係

### 3. プロジェクトとして認識

- コード補完が効くようになる
- Javaクラスが正しく認識される
- ビルドや実行ができるようになる

### 4. 初回は時間がかかる

- 依存関係のダウンロード: **数分〜10分程度**
- インターネット速度による
- キャッシュされるので、2回目以降は速い

---

## 次のステップ（ロード完了後）

### 確認すること

1. **エラーや警告が出ていないか**
   - 下部の「Build」タブを確認

2. **プロジェクト構造が表示されているか**
   - 左側のツリーに `src/main/java` などが見えるか

3. **外部ライブラリが読み込まれているか**
   - プロジェクトツリーの「External Libraries」を確認

### 実施予定

1. **build.gradle.kts の内容確認**
   - 依存関係が正しく含まれているか

2. **TodoappApplication.java の内容確認**
   - メインクラスの中身

3. **application.properties の設定**
   - PostgreSQL接続情報の追加

4. **PostgreSQL のインストールと設定**
   - データベース作成
   - 接続テスト

5. **Hello World API の作成**
   - 動作確認用の簡単なエンドポイント

---

## よくあるエラーと対処法

### エラー1: JDKが見つからない

**エラーメッセージ:**

```
"Project JDK is not defined"
```

**対処法:**

1. File → Project Structure
2. Project → SDK で Java 17 を選択
3. Apply → OK

### エラー2: ネットワークエラー

**エラーメッセージ:**

```
"Could not download dependencies"
```

**対処法:**

- インターネット接続を確認
- しばらく待ってリトライ

### エラー3: Gradleデーモンの起動失敗

**対処法:**

- IntelliJを再起動
- Gradle キャッシュをクリア（File → Invalidate Caches...）

---

## 学んだ重要な概念

### 1. Gradle Wrapper の役割

**目的:**

- プロジェクトごとに特定のGradleバージョンを使える
- Gradleをインストールしていなくても実行できる
- チーム全員が同じバージョンを使える

**仕組み:**

- `gradlew` スクリプトが指定されたGradleを自動ダウンロード
- `gradle-wrapper.properties` にバージョン情報

### 2. 標準的なプロジェクト構造

**Maven/Gradleの規約:**

```
src/main/java     ← 本番コード
src/main/resources ← 設定ファイル
src/test/java     ← テストコード
```

**メリット:**

- ツールが自動認識
- チーム開発で混乱しない
- ドキュメントが参考にしやすい

### 3. 依存関係の自動解決

**推移的依存関係:**

- Spring Webを追加すると、自動で以下も取得される:
  - spring-webmvc
  - tomcat-embed-core
  - jackson-databind
  - など

**メリット:**

- 全ての依存を手動で書く必要がない
- バージョンの整合性が保証される

---

## まとめ

**達成したこと:**

- ✅ Spring Initializrでプロジェクト生成
- ✅ ZIPファイルのダウンロード
- ✅ プロジェクト構造の理解
- ✅ IntelliJ IDEAでプロジェクトを開く
- ✅ Gradleプロジェクトのロード開始

**学んだこと:**

- Spring Initializrの使い方
- プロジェクト構造の意味
- Gradle Wrapperの役割
- 依存関係の自動解決

**現在の状態:**

- Gradleの依存関係をダウンロード中
- 完了を待っている

**次のフェーズ:**

- ロード完了後、build.gradle.kts と TodoappApplication.java を確認
- PostgreSQL のインストールと設定
- データベース接続の確認
- Hello World API の作成

---

**作成日**: 2026-01-16
**WBS進捗**: 1.2 環境構築・Spring Boot学習 - プロジェクト作成完了、ロード中
