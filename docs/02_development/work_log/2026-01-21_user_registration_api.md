# 作業ログ: 2026-01-21 (ユーザー登録API完成)

## 学習情報

- **日付**: 2026-01-21
- **学習フェーズ**: WBS 1.3.1〜1.3.5
- **学習時間**: 約2時間
- **ステータス**: 完了

---

## 実施内容

### 1. 前回からの続き（ユーザー登録API実装）

前回（2026-01-20）に作成途中だった以下のファイルを完成させた。

#### UserRepository.java の更新

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

**学んだこと:**
- `Optional<User>` を使うには `import java.util.Optional;` が必要
- `existsByEmail` はSpring Data JPAが自動で `SELECT count(*) > 0` クエリを生成する
- メソッド名の命名規則に従えば、実装なしでクエリが自動生成される

#### UserService.java の作成

```java
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // コンストラクタインジェクション
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User signup(String email, String rawPassword) {
        // 1) メール重複チェック
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists.");
        }

        // 2) パスワードをハッシュ化
        String hashedPassword = passwordEncoder.encode(rawPassword);

        // 3) createdAt / updatedAt を現在時刻に設定
        LocalDateTime now = LocalDateTime.now();

        // 4) User を作って保存
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(hashedPassword);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // 5) 保存して返却
        return userRepository.save(user);
    }
}
```

**学んだこと:**
- `@Service` アノテーションでサービス層のBeanとして登録
- コンストラクタインジェクションがベストプラクティス（`@Autowired` より推奨）
- `@Transactional` でトランザクション管理
- `PasswordEncoder.encode()` でパスワードをハッシュ化

#### AuthController.java の作成

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody SignupRequest request) {
        User user = userService.signup(request.getEmail(), request.getPassword());
        return new UserResponse(user);
    }
}
```

**学んだこと:**
- `@RestController` = `@Controller` + `@ResponseBody`
- `@RequestMapping("/api/auth")` でベースパスを設定
- `@Valid @RequestBody` はパラメータの前に書く
- メソッドの構文: `public 戻り値型 メソッド名(パラメータ) { ... }`
- ローカル変数には `private` を使わない（クラスフィールドにのみ使用）

### 2. SecurityConfig.java の更新

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/hello").permitAll()
                    .requestMatchers("/api/auth/**").permitAll()
                    .anyRequest().authenticated()
            );
    return http.build();
}
```

**学んだこと:**
- REST APIではCSRFを無効化するのが一般的（`.csrf(csrf -> csrf.disable())`）
- `/api/auth/**` でワイルドカード指定（register, login両方に対応）
- `permitAll()` で認証不要に設定

### 3. 環境変数の問題と解決

**問題:**
```
FATAL: password authentication failed for user "todoapp"
```

**原因:**
`source .env` はシェル変数として読み込むだけで、子プロセス（Gradle/Java）に渡されない。

**解決策:**
```bash
export $(cat .env | xargs) && ./gradlew bootRun
```

**違い:**
- `source .env` → シェル変数（子プロセスに渡されない）
- `export $(cat .env | xargs)` → 環境変数としてエクスポート（子プロセスに渡される）

### 4. 動作確認

#### ユーザー登録成功
```bash
$ curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "password123"}'

{"createdAt":"2026-01-21T13:53:55.724699736","email":"test@example.com","id":1}
```

#### パスワードのハッシュ化確認
```sql
todoapp=# SELECT id, email, password_hash FROM users;
 id |      email       |                        password_hash
----+------------------+--------------------------------------------------------------
  1 | test@example.com | $2a$10$L2qny7TibrTGP52OThwUkuUDx21S0RVu6Dgy5z/tzz0QigeYG2fFC
```

BCryptでハッシュ化されていることを確認。

---

## セキュリティ学習: ソルトとレインボーテーブル

### レインボーテーブル攻撃とは

攻撃者が事前に作成した「よくあるパスワード → ハッシュ値」の対応表。
DBからハッシュ値が漏洩した場合、表を引くだけで元のパスワードが分かってしまう。

### ソルトとは

パスワードに追加するランダムな文字列。

```
ソルトなし: password123 → abc123...
ソルトあり: password123 + "X7kp9Q" → xyz789...（毎回異なる）
```

同じパスワードでもソルトが違えばハッシュ値が変わるため、レインボーテーブルが無効化される。

### BCryptのハッシュ値の構造

```
$2a$10$L2qny7TibrTGP52OThwUkuUDx21S0RVu6Dgy5z/tzz0QigeYG2fFC
```

| 部分 | 意味 |
|------|------|
| `$2a$` | BCryptアルゴリズム |
| `10$` | コストファクター（計算の複雑さ） |
| `L2qny7TibrTGP52OThwUku` | ソルト（22文字） |
| `UDx21S0RVu6Dgy5z/tzz0QigeYG2fFC` | ハッシュ値 |

**BCryptPasswordEncoderはソルトを自動生成・保存してくれるため、開発者が意識する必要がない。**

---

## 現在のプロジェクト構造

```
src/main/java/com/example/todoapp/
├── TodoappApplication.java
├── config/
│   └── SecurityConfig.java     ← 更新（CSRF無効化、/api/auth/**許可）
├── controller/
│   ├── HelloController.java
│   └── AuthController.java     ← 今回作成
├── dto/
│   ├── SignupRequest.java
│   └── UserResponse.java
├── entity/
│   └── User.java
├── repository/
│   └── UserRepository.java     ← 更新（existsByEmail追加）
└── service/
    └── UserService.java        ← 今回作成
```

---

## JWT基礎学習（WBS 1.3.6）

### JWTとは

ログイン後に「この人は認証済み」という情報を安全にやり取りするためのトークン。

**従来のセッション方式との違い:**
- セッション方式: サーバーがセッション情報を保持（ステートフル）
- JWT方式: サーバーは状態を持たない（ステートレス）→ スケールしやすい

### JWTの構造

```
ヘッダー.ペイロード.署名
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIn0.XXXXX
```

| 部分 | 内容 |
|------|------|
| Header | アルゴリズム情報 `{"alg": "HS256"}` |
| Payload | ユーザー情報など `{"sub": "email", "iat": 発行日時}` |
| Signature | 秘密鍵で作成した署名（改ざん防止） |

**注意**: PayloadはBase64エンコードされているだけで暗号化されていない。パスワードなどの機密情報は入れない。

### jjwtライブラリの追加

`build.gradle.kts` に追加:
```kotlin
// JWT
implementation("io.jsonwebtoken:jjwt-api:0.12.6")
runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
```

学習ドキュメント: `docs/03_learning/14_JWTとは何か.md`

---

## 残課題

- [ ] エラーハンドリング（重複メール時に403ではなく適切なエラーレスポンスを返す）
  - 現状: `IllegalArgumentException` がスローされると403が返る
  - 対策: `@ExceptionHandler` または `@ControllerAdvice` でグローバルエラーハンドリングを実装

---

## 次回のタスク

1. WBS 1.3.6 JWT基礎学習（続き）- JwtUtilクラスの作成
2. WBS 1.3.7 ログインAPI（JWT発行）

---

**作成日**: 2026-01-21
**最終更新**: 2026-01-21
