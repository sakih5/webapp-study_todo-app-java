# 作業ログ: 2026-01-18 (Spring Boot起動確認)

## 学習情報

- **日付**: 2026-01-18
- **学習フェーズ**: WBS 1.2.2 Spring Boot + PostgreSQL環境構築
- **学習時間**: 約20分
- **ステータス**: 完了

---

## 実施内容

### 1. application.propertiesの設定

`src/main/resources/application.properties` にデータベース接続設定を追加。

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

# Temporarily disable Spring Security (for initial testing)
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
```

**各設定の意味:**
- `spring.datasource.*` - データベース接続情報
- `${POSTGRES_PASSWORD}` - 環境変数から読み込む
- `${POSTGRES_USER:todoapp}` - 環境変数がなければデフォルト値を使用
- `ddl-auto=update` - エンティティに合わせてテーブルを自動作成/更新
- `show-sql=true` - 実行されるSQLをログに出力（学習に便利）

### 2. build.gradle.ktsに環境変数設定を追加

Gradleは環境変数を自動でJavaプロセスに渡さないため、明示的に設定が必要。

**build.gradle.ktsに追加:**
```kotlin
tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    environment(System.getenv())
}
```

### 3. Spring Boot起動

```bash
# 環境変数を読み込み
export $(cat .env | xargs)

# アプリケーション起動
./gradlew bootRun
```

---

## 起動成功時のログ

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v4.0.1)

... HikariPool-1 - Start completed.
... Database info:
        Database JDBC URL [jdbc:postgresql://localhost:5432/todoapp]
        Database driver: PostgreSQL JDBC Driver
        Database version: 16.10
... Started TodoappApplication in 2.553 seconds
```

**確認ポイント:**
- `HikariPool-1 - Start completed.` → データベース接続プール起動成功
- `Database version: 16.10` → PostgreSQL 16に接続成功
- `Started TodoappApplication` → アプリケーション起動完了

---

## 発生した問題と解決策

### 問題: 環境変数がJavaプロセスに渡されない

**症状:**
```
FATAL: password authentication failed for user "todoapp"
```

**原因:**
Gradleは子プロセスとしてJavaアプリケーションを起動するが、シェルの環境変数を自動で渡さない。

**解決策:**
`build.gradle.kts` に以下を追加:
```kotlin
tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    environment(System.getenv())
}
```

---

## 現在の警告（今後対応予定）

### 1. spring.jpa.open-in-view

```
spring.jpa.open-in-view is enabled by default.
```

- デフォルトで有効になっているOpen Session in View パターン
- 後で明示的に設定する予定

### 2. Spring Security生成パスワード

```
Using generated security password: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

- Spring Securityが有効なため、自動生成されたパスワード
- `spring.autoconfigure.exclude` で無効化する設定が効いていない（後で確認）

---

## 学んだこと

### 1. Spring Bootの環境変数

- `${変数名}` 形式で環境変数を参照可能
- `${変数名:デフォルト値}` 形式でデフォルト値を指定可能
- Gradle経由で起動する場合は、環境変数を明示的に渡す設定が必要

### 2. HikariCP

- Spring Boot標準のコネクションプール
- データベース接続を効率的に管理
- `HikariPool-1 - Start completed.` が表示されれば接続成功

### 3. Hibernate/JPA設定

- `ddl-auto=update` でエンティティからテーブルを自動作成
- `show-sql=true` でSQLをログ出力（開発時に便利）

---

## 次回のタスク

1. Hello World APIを作成して動作確認
2. 警告への対応（`spring.jpa.open-in-view` の設定）
3. Spring Securityの一時無効化を確認

---

**作成日**: 2026-01-18
