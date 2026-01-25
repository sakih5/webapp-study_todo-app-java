# 作業ログ: 2026-01-19 (Hello World API作成)

## 学習情報

- **日付**: 2026-01-19
- **学習フェーズ**: WBS 1.2.2 Spring Boot + PostgreSQL環境構築
- **学習時間**: 約30分
- **ステータス**: 完了

---

## 実施内容

### 1. HelloController の作成

REST APIのエンドポイントを作成した。

**ファイル**: `src/main/java/com/example/todoapp/controller/HelloController.java`

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

**学んだこと:**
- `@RestController`: REST APIのコントローラーであることを示すアノテーション
- `@GetMapping("/api/hello")`: GETリクエストを受け付けるエンドポイントを定義
- 戻り値の文字列がそのままHTTPレスポンスボディになる

### 2. SecurityConfig の作成

Spring Securityの設定を行い、`/api/hello` へのアクセスを許可した。

**ファイル**: `src/main/java/com/example/todoapp/config/SecurityConfig.java`

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
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/hello").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

**学んだこと:**
- `@Configuration`: 設定クラスであることを示すアノテーション
- `@EnableWebSecurity`: Spring Securityを有効化
- `@Bean`: メソッドの戻り値をSpringが管理するBeanとして登録
- `.requestMatchers("/api/hello").permitAll()`: 特定のパスを認証なしで許可
- `.anyRequest().authenticated()`: それ以外のリクエストは認証が必要

---

## 発生した問題と解決策

### 問題1: PostgreSQLに接続できない

**症状:**
```
Connection to localhost:5432 refused.
```

**原因:**
PostgreSQLのDockerコンテナが起動していなかった。

**解決策:**
```bash
docker compose up -d
```

### 問題2: 401 Unauthorized エラー

**症状:**
```bash
curl http://localhost:8080/api/hello
# 何も返ってこない

curl -v http://localhost:8080/api/hello
# HTTP/1.1 401
```

**原因:**
Spring Securityがデフォルトで有効になっており、すべてのAPIが保護されていた。

**解決策:**
`SecurityConfig.java` を作成し、`/api/hello` へのアクセスを許可する設定を追加した。

---

## 学んだ重要な概念

### 1. Javaのパッケージとフォルダ構造

パッケージ名とフォルダ構造は一致している必要がある。

| パッケージ名 | フォルダ |
|------------|--------|
| `com.example.todoapp` | `src/main/java/com/example/todoapp/` |
| `com.example.todoapp.controller` | `src/main/java/com/example/todoapp/controller/` |
| `com.example.todoapp.config` | `src/main/java/com/example/todoapp/config/` |

ファイルの1行目には `package パッケージ名;` を書く必要がある。

### 2. Spring Boot REST APIの基本構造

```
@RestController        ← このクラスはREST APIのコントローラー
public class XxxController {

    @GetMapping("/path")   ← GET /path にマッピング
    public String method() {
        return "response";  ← レスポンスボディ
    }
}
```

### 3. curlコマンドでのデバッグ

- `curl URL`: 基本的なリクエスト
- `curl -v URL`: 詳細表示（ステータスコード、ヘッダーなど）

### 4. HTTPステータスコード

- `200 OK`: 成功
- `401 Unauthorized`: 認証が必要
- `404 Not Found`: リソースが見つからない
- `500 Internal Server Error`: サーバーエラー

---

## 現在のプロジェクト構造

```
src/main/java/com/example/todoapp/
├── TodoappApplication.java      ← メインクラス（既存）
├── controller/                  ← 今回作成
│   └── HelloController.java
└── config/                      ← 今回作成
    └── SecurityConfig.java
```

---

## 次回のタスク

1. JPA/Hibernate基礎学習
2. 簡単なEntityとRepositoryを作成して接続テスト
3. データベースへのデータ保存・取得を実践

---

**作成日**: 2026-01-19
