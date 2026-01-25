# 作業ログ: 2026-01-22 (JwtUtilクラス作成)

## 学習情報

- **日付**: 2026-01-22
- **学習フェーズ**: WBS 1.3.6（JWT基礎学習 - JwtUtilクラス作成）
- **ステータス**: 完了

---

## 実施内容

### 1. JwtUtilクラスの作成

`src/main/java/com/example/todoapp/util/JwtUtil.java` を作成。

```java
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    // 署名用キー取得（共通処理をメソッド化）
    public SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // トークン生成
    public String generateToken(String email) {
        SecretKey key = getSigningKey();
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    // メールアドレス取得
    public String getEmailFromToken(String token) {
        SecretKey key = getSigningKey();
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    // トークン検証
    public boolean validateToken(String token) {
        try {
            SecretKey key = getSigningKey();
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

### 2. application.propertiesの更新

JWT関連の設定を追加:

```properties
# JWT
jwt.secret=hda9ghergyuhfiwakjnkcmkjiorjioajieoahgreiughaihru
jwt.expiration-ms=86400000
```

---

## 学んだこと

### jjwtライブラリの使い方

| メソッド | 用途 |
|---------|------|
| `Jwts.builder()` | JWTトークンを生成するビルダー |
| `.subject(email)` | ペイロードにsubjectを設定 |
| `.issuedAt(new Date())` | 発行日時を設定 |
| `.expiration(...)` | 有効期限を設定 |
| `.signWith(key)` | 秘密鍵で署名 |
| `.compact()` | 文字列に変換 |

| メソッド | 用途 |
|---------|------|
| `Jwts.parser()` | JWTトークンを解析するビルダー |
| `.verifyWith(key)` | この鍵で署名を検証 |
| `.build()` | 設定を固定化 |
| `.parseSignedClaims(token)` | トークンを解析・検証 |
| `.getPayload()` | ペイロード（Claims）を取得 |

### Spring Bootの設定値読み込み

```java
@Value("${jwt.secret}")
private String secretKey;
```

- `@Value` アノテーションで `application.properties` の値を読み込める
- `${プロパティ名}` の形式で指定
- importは `org.springframework.beans.factory.annotation.Value`

### DRY原則（Don't Repeat Yourself）

同じ処理（`Keys.hmacShaKeyFor(secretKey.getBytes())`）が複数箇所にあったため、`getSigningKey()` メソッドに切り出した。

### 例外処理

- jjwtはトークンが無効な場合 `JwtException` をスローする
- `try-catch` で例外をキャッチし、成功/失敗を `boolean` で返す

---

## 現在のプロジェクト構造

```
src/main/java/com/example/todoapp/
├── TodoappApplication.java
├── config/
│   └── SecurityConfig.java
├── controller/
│   ├── HelloController.java
│   └── AuthController.java
├── dto/
│   ├── SignupRequest.java
│   └── UserResponse.java
├── entity/
│   └── User.java
├── repository/
│   └── UserRepository.java
├── service/
│   └── UserService.java
└── util/
    └── JwtUtil.java        ← 今回作成
```

---

## 次回のタスク

WBS 1.3.7 ログインAPI（JWT発行）の実装:

1. LoginRequest.java (DTO) 作成
2. LoginResponse.java (DTO) 作成
3. AuthController.java にログインエンドポイント追加
4. UserService.java にログイン処理追加

---

**作成日**: 2026-01-22
**最終更新**: 2026-01-22
