# Spring Boot環境構築 - Java・IDEインストールからHello World APIまで

## 本記事を作成した背景

JavaでWebアプリケーションを開発するために、Spring Bootの開発環境をゼロから構築しました。JDKとIDEのインストールから始めて、Spring Initializrでのプロジェクト作成、DockerでのPostgreSQL起動、実際にHello World APIが動作するまでの一連の手順を記録します。

## 本記事で取り組んだこと

- JDK（Java Development Kit）のインストール
- IntelliJ IDEA Community Editionのインストールと初期設定
- Spring Initializrでプロジェクトの雛形を生成
- IntelliJ IDEAでプロジェクトを開いてGradleの依存関係を解決
- Docker ComposeでPostgreSQLを起動
- Spring BootからPostgreSQLへ接続
- Hello World APIを作成して動作確認

## 手順

### 前提

- **環境**: Windows 11 + WSL2 (Ubuntu 24.04)
- **前提知識**: Linuxの基本コマンドがわかる
- **前提状態**: 以下がインストール済み
  - WSL2 (Ubuntu 24.04)
  - Docker（WSL2上にDocker Engine）
  - Git

※ DockerはWSL2上にDocker Engineのみインストール済（Windows PCにDocker Desktopはインストールしていない）

---

### 1. JDK（Java Development Kit）のインストール

#### 🎯 目的

Javaプログラムを開発・実行するために必要なJDKをインストールする。

#### 🛠️ 手順詳細

**WSL2側にJDKをインストール**

WSL2のターミナルで以下を実行:

```bash
# パッケージリストを更新
sudo apt update

# OpenJDK 17をインストール
sudo apt install openjdk-17-jdk -y

# インストール確認
java -version
```

成功すると以下のように表示される:

```bash
openjdk version "17.0.x" 2024-xx-xx
OpenJDK Runtime Environment (build 17.0.x+x-Ubuntu-xubuntu1)
OpenJDK 64-Bit Server VM (build 17.0.x+x-Ubuntu-xubuntu1, mixed mode, sharing)
```

**JAVA_HOME環境変数を設定**

```bash
# JDKのインストールパスを確認
sudo update-alternatives --config java
```

表示されたパスを元に、`~/.bashrc` に以下を追加:

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$PATH:$JAVA_HOME/bin
```

設定を反映:

```bash
source ~/.bashrc

# 確認
echo $JAVA_HOME
```

#### 💡 理解ポイント

**JDKとは？**

- **JDK (Java Development Kit)**: Javaプログラムを開発するためのツールキット
- **含まれるもの**: コンパイラ（javac）、JRE（実行環境）、開発ツール
- **バージョン17を選ぶ理由**: Spring Boot 3.x系はJava 17以上が必須

**OpenJDKとは？**

- Javaのオープンソース実装
- Oracle JDKと機能的にはほぼ同等
- 無料で商用利用可能

#### 📝 補足

**Windows側でもJDKが必要な理由**

WSL2上でSpring Bootを実行する場合でも、
**Windows上で動作するIntelliJ IDEA自体はJDKを必要とする**ため、Windows側にもJDKが必要になる。

---

### 2. IntelliJ IDEA Community Editionのインストール

#### 🎯 目的

Javaの統合開発環境（IDE）をインストールし、効率的に開発できる環境を整える。

#### 🛠️ 手順詳細

**1. IntelliJ IDEAをダウンロード**

- 公式サイト <https://www.jetbrains.com/idea/download/> にアクセス
- **Community Edition**（無料版）をダウンロード
- Windows用のインストーラー（.exe）を実行

**2. インストール**

- インストールウィザードに従って進める
- オプション選択:
  - 「64-bit launcher」にチェック
  - 「Add "Open Folder as Project"」にチェック（推奨）
  - 「.java」の関連付けにチェック（任意）

**3. 初回起動時の設定**

- **テーマ選択**: Dark（Darcula）またはLight
- **プラグイン**: デフォルトのまま進める（後から追加可能）

**4. JDKの設定（Windows側）**

IntelliJ IDEAはWindows側にもJDKが必要。初回プロジェクト作成時にダウンロードできる:

1. 「New Project」をクリック
2. 「JDK」のドロップダウンから「Download JDK...」を選択
3. 以下を選択:
   - Version: 17
   - Vendor: Microsoft（または任意）
4. 「Download」をクリック

#### 💡 理解ポイント

**Community Edition vs Ultimate Edition**

| 項目 | Community Edition | Ultimate Edition |
|------|-------------------|------------------|
| 価格 | 無料 | 有料（年額） |
| Java開発 | ○ | ○ |
| Spring Boot | △（基本機能のみ） | ○（専用サポート） |
| Web開発 | △ | ○ |
| 学習用途 | 十分 | オーバースペック |

学習目的であればCommunity Editionで十分。

**なぜWindows側にもJDKが必要か？**

- IntelliJ IDEAはWindows上で動作する
- IDEがコード補完や構文チェックをするためにJDKが必要
- WSL2上のJDKはWindows上のIntelliJ IDEAから直接は使用できない

**なぜIntelliJはWindowsに入れるのか？**

開発をWSL上で行うのであれば、エディタもWSL上に入れた方が上記のようなややこしいことをしなくて済むのではないかと思ったのだが、

どうも**開発はWSL上で行い、エディタ（IDE）はWindows上に入れる**というのが通常のようである。

これはなぜかというと、

IntelliJはLinuxよりもWindowsの方が

- セットアップが簡単
- UIが安定している

からである。

#### 📝 補足

**トラブルシューティング: Runウィンドウが表示されない**

初回実行時にRunウィンドウが表示されない場合:

1. File → Invalidate Caches...
2. 「Invalidate and Restart」をクリック
3. IntelliJ IDEAが再起動される

この操作でIDEのキャッシュがクリアされ、多くの問題が解決する。

---

### 3. Spring Initializrでプロジェクト生成

#### 🎯 目的

Spring Bootプロジェクトの雛形を自動生成する。手動で設定ファイルやディレクトリ構造を作成するのは大変なので、公式ツールを使って効率化する。

#### 🛠️ 手順詳細

1. **Spring Initializrにアクセス**
   - ブラウザで <https://start.spring.io/> を開く

2. **プロジェクト設定を選択**

   | 項目 | 選択内容 |
   |------|---------|
   | Project | Gradle - Kotlin |
   | Language | Java |
   | Spring Boot | 3.2.x（最新の安定版） |
   | Group | com.example |
   | Artifact | todoapp |
   | Package name | com.example.todoapp |
   | Packaging | Jar |
   | Java | 17 |

3. **依存関係（Dependencies）を追加**
   - Spring Web
   - Spring Data JPA
   - PostgreSQL Driver
   - Spring Security
   - Lombok
   - Validation

4. **「Generate」ボタンをクリック**
   - ZIPファイルがダウンロードされる

5. **ZIPを解凍**
   - WSL2のホームディレクトリに配置することを推奨

   ```bash
   # 例: ~/projects/todoapp に配置
   mkdir -p ~/projects
   cd ~/projects
   unzip todoapp.zip
   ```

#### 💡 理解ポイント

**Spring Initializrとは？**

- Spring Bootプロジェクトの雛形を自動生成するWebサービス
- Spring公式が無料で提供
- 面倒な初期設定（ディレクトリ構造、build.gradle、設定ファイルなど）を自動化

**Gradle - Kotlin を選んだ理由**

- モダンなビルドツールを学べる
- Kotlin DSLは型安全でIDEの補完が効く
- ビルド速度が速い（増分ビルド、ビルドキャッシュ）

※ アプリケーション本体はJavaで書く。Kotlinで書くのはビルドスクリプト（build.gradle.kts）だけ。

#### 📝 補足

**MavenとGradleの違いについて**

- どちらも推移的依存関係を自動解決する（「Mavenは再現性が悪い」というのは誤解）
- Gradleはより柔軟でビルドが速い
- Mavenはシンプルで学習コストが低い
- どちらを選んでもチーム開発に適している

---

### 4. IntelliJ IDEAでプロジェクトを開く

#### 🎯 目的

IDEでプロジェクトを認識させ、コード補完やビルド機能を使えるようにする。

#### 🛠️ 手順詳細

1. **IntelliJ IDEAを起動**

2. **プロジェクトを開く**
   - 「Open」をクリック
   - 解凍したフォルダ（`todoapp`）を選択
   - `build.gradle.kts` があるフォルダを選択

3. **Gradleプロジェクトをロード**
   - 「Gradle'TodoApp'ビルドスクリプトが見つかりました」ダイアログが表示される
   - 「ロード」を選択

4. **依存関係のダウンロードを待つ**
   - 初回は数分〜10分程度かかる
   - 下部の「Build」タブで進捗を確認

#### 💡 理解ポイント

**Gradleプロジェクトのロードで起こること**

1. `build.gradle.kts` を解析し、必要なライブラリを特定
2. Maven Central等のリポジトリから依存関係をダウンロード
3. プロジェクト構造をIDEが認識
4. コード補完が効くようになる

**Gradle Wrapperの役割**

- `gradlew`（Linux/Mac）と`gradlew.bat`（Windows）
- Gradleをインストールしていなくても実行できる
- チーム全員が同じバージョンのGradleを使える

#### 📝 補足

**よくあるエラーと対処法**

JDKが見つからない場合:

```
"Project JDK is not defined"
```

→ File → Project Structure → Project → SDK で Java 17 を選択

ネットワークエラーの場合:

```
"Could not download dependencies"
```

→ インターネット接続を確認して再試行

---

### 5. WSL2への移行（推奨）

#### 🎯 目的

開発環境を統一し、パフォーマンスを向上させる。Dockerがインストールされているのと同じ環境（WSL2）でJavaアプリケーションを動かす。

#### 🛠️ 手順詳細

1. **プロジェクトをWSL2に移動**

   ```bash
   # WSL2のホームディレクトリに移動
   mkdir -p ~/projects
   cp -r /mnt/c/Users/YOUR_USERNAME/Downloads/todoapp ~/projects/
   cd ~/projects/todoapp
   ```

2. **gradlewに実行権限を付与**

   ```bash
   chmod +x gradlew
   ```

   - Windowsからコピーしたファイルは実行権限が失われている

3. **IntelliJ IDEAでWSL2上のプロジェクトを開き直す**
   - `\\wsl$\Ubuntu\home\YOUR_USERNAME\projects\todoapp` のようなパスで開く

#### 💡 理解ポイント

**なぜWSL2にプロジェクトを置くべきか？**

- Windows↔WSL2間のファイルアクセスは遅い
- Docker、Java、Gradleがすべて同じLinux環境で動く
- 本番環境（通常Linux）に近い環境で開発できる

**WSL2からWindowsファイルへのアクセス**

- `/mnt/c/Users/...` のパスでアクセス可能
- ただしパフォーマンスの観点から、開発には非推奨

#### 📝 補足

Windowsで作成したファイルには改行コード（CRLF）の問題が発生することがある。WSL2上でファイルを作成するのが安全。

---

### 6. Docker ComposeでPostgreSQLを起動

#### 🎯 目的

Spring BootアプリケーションのデータベースとしてPostgreSQLを用意する。Dockerを使うことで、ローカルにPostgreSQLをインストールせずに環境を構築できる。

#### 🛠️ 手順詳細

1. **docker-compose.ymlを作成**

   プロジェクトルートに `docker-compose.yml` を作成:

   ```yaml
   services:
     postgres:
       image: postgres:16
       container_name: todoapp-postgres
       environment:
         POSTGRES_DB: ${POSTGRES_DB}
         POSTGRES_USER: ${POSTGRES_USER}
         POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
       ports:
         - "5432:5432"
       volumes:
         - postgres_data:/var/lib/postgresql/data

   volumes:
     postgres_data:
   ```

2. **環境変数ファイル（.env）を作成**

   プロジェクトルートに `.env` を作成:

   ```plaintext
   POSTGRES_DB=todoapp
   POSTGRES_USER=todoapp
   POSTGRES_PASSWORD=todoapp123
   ```

3. **.gitignoreに.envを追加**

   ```bash
   echo ".env" >> .gitignore
   ```

4. **.env.exampleを作成（サンプル用）**

   ```plaintext
   POSTGRES_DB=todoapp
   POSTGRES_USER=todoapp
   POSTGRES_PASSWORD=your_password_here
   ```

5. **PostgreSQLを起動**

   ```bash
   docker compose up -d
   ```

6. **接続を確認**

   ```bash
   docker exec -it todoapp-postgres psql -U todoapp -d todoapp -c "SELECT 1"
   ```

   成功すると以下のように表示される:

   ```
    ?column?
   ----------
          1
   (1 row)
   ```

#### 💡 理解ポイント

**Docker Composeと環境変数**

- `.env` ファイルはDocker Composeが自動で読み込む
- `${変数名}` 形式でdocker-compose.yml内から参照可能
- パスワードをymlファイルにべた書きしないのがベストプラクティス

**PostgreSQL Dockerイメージの初期化**

- `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` は**初回起動時のみ**適用される
- 設定を変更したい場合はボリュームを削除して再作成が必要

#### 📝 補足

**Windows改行コード（CRLF）によるエラー**

WSL2で以下のエラーが出た場合:

```
FATAL: password authentication failed for user "todoapp"
```

改行コードを確認:

```bash
cat -A .env
# ^M が表示されたらCRLFが混入している
```

修正方法:

```bash
sed -i 's/\r$//' .env
sed -i 's/\r$//' docker-compose.yml
```

**ボリュームを削除して再作成**

```bash
docker compose down -v  # -v でボリュームも削除
docker compose up -d
```

---

### 7. application.propertiesの設定

#### 🎯 目的

Spring BootアプリケーションからPostgreSQLに接続するための設定を行う。

#### 🛠️ 手順詳細

`src/main/resources/application.properties` を編集:

```properties
spring.application.name=todoapp

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/todoapp
spring.datasource.username=${POSTGRES_USER:todoapp}
spring.datasource.password=${POSTGRES_PASSWORD}

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

#### 💡 理解ポイント

**各設定の意味**

| 設定項目 | 説明 |
|---------|------|
| `spring.datasource.url` | JDBC接続URL |
| `${POSTGRES_USER:todoapp}` | 環境変数から取得、なければデフォルト値 |
| `${POSTGRES_PASSWORD}` | 環境変数から取得（デフォルト値なし） |
| `ddl-auto=update` | エンティティに合わせてテーブルを自動作成/更新 |
| `show-sql=true` | 実行されるSQLをログに出力（学習に便利） |

**環境変数の記法**

- `${変数名}` - 環境変数を参照
- `${変数名:デフォルト値}` - 環境変数がなければデフォルト値を使用

#### 📝 補足

`ddl-auto` の各オプション:

- `none`: 何もしない
- `validate`: エンティティとテーブルの整合性を検証
- `update`: 差分があればテーブルを更新（開発向け）
- `create`: 毎回テーブルを作り直す
- `create-drop`: 起動時に作成、終了時に削除

本番環境では `none` または `validate` を使用する。

---

### 8. Gradleの環境変数設定

#### 🎯 目的

Gradleで起動するSpring Bootアプリケーションに環境変数を渡す。

Gradle経由でSpring Bootを起動する場合、
**IDEやGradleの実行コンテキストによっては環境変数が渡らないことがある**。

そのため、`bootRun` タスクで明示的に `System.getenv()` を渡している。

#### 🛠️ 手順詳細

`build.gradle.kts` に以下を追加:

```kotlin
tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    environment(System.getenv())
}
```

#### 💡 理解ポイント

この設定により、`./gradlew bootRun` 実行時にシェルの環境変数がすべてJavaプロセスに渡される。これで `${POSTGRES_PASSWORD}` などがSpring Bootから参照できるようになる。

---

### 9. Spring Boot起動確認

#### 🎯 目的

PostgreSQLに正常に接続できることを確認する。

#### 🛠️ 手順詳細

1. **環境変数を読み込み**

   ```bash
   export $(cat .env | xargs)
   ```

2. **Spring Bootを起動**

   ```bash
   ./gradlew bootRun
   ```

3. **起動ログを確認**

   成功すると以下のようなログが表示される:

   ```
     .   ____          _            __ _ _
    /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
   ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
    \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
     '  |____| .__|_| |_|_| |_\__, | / / / /
    =========|_|==============|___/=/_/_/_/

    :: Spring Boot ::                (v3.2.x)

   ... HikariPool-1 - Start completed.
   ... Started TodoappApplication in 2.553 seconds
   ```

#### 💡 理解ポイント

**確認ポイント**

- `HikariPool-1 - Start completed.` → データベース接続プール起動成功
- `Started TodoappApplication` → アプリケーション起動完了

**HikariCPとは**

- Spring Boot標準のコネクションプール
- データベース接続を効率的に管理
- 接続の作成・破棄のオーバーヘッドを削減

#### 📝 補足

起動時に以下の警告が出ることがある:

```
spring.jpa.open-in-view is enabled by default.
```

これは後で明示的に設定すれば消える。初期段階では無視して問題ない。

---

### 10. Hello World APIの作成

#### 🎯 目的

REST APIエンドポイントを作成し、Spring Bootが正常に動作することを確認する。

#### 🛠️ 手順詳細

1. **コントローラーを作成**

   `src/main/java/com/example/todoapp/controller/HelloController.java`:

   ```java
   package com.example.todoapp.controller;

   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RestController;

   @RestController
   public class HelloController {

       @GetMapping("/api/hello")
       public String hello() {
           return "Hello, World!";
       }
   }
   ```

2. **Spring Security設定を作成**

   `src/main/java/com/example/todoapp/config/SecurityConfig.java`:

   ```java
   package com.example.todoapp.config;

   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.security.config.annotation.web.builders.HttpSecurity;
   import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
   import org.springframework.security.web.SecurityFilterChain;

   @Configuration
   @EnableWebSecurity
   public class SecurityConfig {

       @Bean
       public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
           http
               .csrf(csrf -> csrf.disable()) // ※ REST APIのみを提供する場合、CSRFは通常無効化
               .authorizeHttpRequests(auth -> auth
                   .requestMatchers("/api/hello").permitAll()
                   .anyRequest().authenticated()
               );
           return http.build();
       }
   }
   ```

3. **アプリケーションを再起動**

   ```bash
   ./gradlew bootRun
   ```

4. **APIを呼び出して確認**

   ```bash
   curl http://localhost:8080/api/hello
   ```

   成功すると以下が返る:

   ```
   Hello, World!
   ```

#### 💡 理解ポイント

**アノテーションの意味**

| アノテーション | 説明 |
|--------------|------|
| `@RestController` | REST APIのコントローラーであることを示す |
| `@GetMapping("/path")` | GETリクエストを受け付けるエンドポイントを定義 |
| `@Configuration` | 設定クラスであることを示す |
| `@EnableWebSecurity` | Spring Securityを有効化 |
| `@Bean` | メソッドの戻り値をSpringが管理するBeanとして登録 |

**Spring Securityの設定**

- `.requestMatchers("/api/hello").permitAll()` - 特定のパスを認証なしで許可
- `.anyRequest().authenticated()` - それ以外は認証が必要

**パッケージ構造**

パッケージ名とフォルダ構造は一致している必要がある:

| パッケージ名 | フォルダ |
|------------|--------|
| `com.example.todoapp` | `src/main/java/com/example/todoapp/` |
| `com.example.todoapp.controller` | `src/main/java/com/example/todoapp/controller/` |
| `com.example.todoapp.config` | `src/main/java/com/example/todoapp/config/` |

#### 📝 補足

**401 Unauthorizedエラーが出る場合**

Spring Securityがデフォルトで全APIを保護している。`SecurityConfig.java` で許可設定を追加する。

**curlでのデバッグ**

```bash
curl -v http://localhost:8080/api/hello  # 詳細表示
```

- `200 OK`: 成功
- `401 Unauthorized`: 認証が必要（Security設定を確認）
- `404 Not Found`: パスが間違っている

---

## 学び・次に活かせる知見

- WSL2とWindowsの両方にJDKが必要な理由を理解した（IDEはWindows、実行環境はWSL2）
- IntelliJ IDEAのキャッシュ問題は「Invalidate Caches」で解決できる
- Spring Initializrを使えば、煩雑な初期設定をスキップしてすぐに開発を始められる
- WSL2でDocker/Java/Gradleを統一すると、環境の整合性とパフォーマンスが向上する
- Gradleで`./gradlew bootRun`を使う場合、環境変数を明示的に渡す設定が必要
- Spring Securityはデフォルトで全エンドポイントを保護するので、開発初期は許可設定を追加する

## 参考文献

1. Spring Initializr - <https://start.spring.io/>
2. Spring Boot公式ドキュメント - <https://docs.spring.io/spring-boot/>
3. PostgreSQL Docker Hub - <https://hub.docker.com/_/postgres>
4. IntelliJ IDEA公式 - <https://www.jetbrains.com/idea/>

---

## 更新履歴

- 2026-01-19：初版公開
