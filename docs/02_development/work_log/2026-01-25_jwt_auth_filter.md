# 作業ログ: 2026-01-25 (ログインAPI完成 + JWT認証フィルター)

## 学習情報

- **日付**: 2026-01-25
- **学習フェーズ**: WBS 1.3.7（ログインAPI - JWT発行）後半 + WBS 1.3.8（JWT認証フィルター）
- **ステータス**: 完了

---

## 実施内容

### 1. UserService.loginメソッドの完成

#### 設計の考え方

**目的**: メールアドレスとパスワードを受け取り、認証が成功したらJWTを返す

**処理フロー設計**:
```
入力: email, password
  ↓
1. メールアドレスでユーザーを検索
  ↓ 見つからない → 例外
2. パスワードを照合（BCryptで比較）
  ↓ 不一致 → 例外
3. JWTを生成して返す
  ↓
出力: JWT文字列
```

**設計判断**:
- Serviceはエンティティやプリミティブ型を扱い、DTOは扱わない（レイヤードアーキテクチャの原則）
- パスワード照合は`PasswordEncoder.matches()`を使用（BCryptのソルト処理を自動で行ってくれる）
- 認証失敗時は`RuntimeException`をスロー（後でカスタム例外に置き換え可能）

#### 実装コード

```java
public String login(String email, String password) {
    // 1. メールアドレスでユーザーを検索
    Optional<User> userOptional = userRepository.findByEmail(email);

    // 2. ユーザーが見つからなければ例外
    User user = userOptional.orElseThrow(() ->
            new RuntimeException("ユーザーが見つかりません")
    );

    // 3. パスワード照合（一致しなければ例外）
    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
        throw new RuntimeException("パスワードが違います");
    }

    // 4. 認証成功 → JWTを生成して返す
    return jwtUtil.generateToken(email);
}
```

---

### 2. AuthControllerにログインエンドポイント追加

#### 設計の考え方

**目的**: HTTPリクエストを受け取り、Serviceを呼び出し、HTTPレスポンスを返す

**責務の分離**:
```
Controller層（AuthController）
  - HTTPリクエスト/レスポンスの処理
  - DTOの受け取り・返却
  - バリデーション（@Valid）
  ↓ email, password を渡す（DTOを分解）
Service層（UserService）
  - ビジネスロジック（認証処理）
  - DTOを知らない
```

**レスポンス設計**:
- `accessToken`: 生成されたJWT文字列
- `tokenType`: "Bearer"（OAuth 2.0の標準、固定値）

#### 実装コード

```java
@PostMapping("/login")
public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    String accessToken = userService.login(request.getEmail(), request.getPassword());
    return new LoginResponse(accessToken, "Bearer");
}
```

---

### 3. ログインAPIのテスト（curl）

```bash
# 正常系（成功）
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "password123"}'
# → JWTトークンが返却された

# 異常系（パスワード間違い）→ 403
# 異常系（存在しないユーザー）→ 403
```

---

### 4. JwtAuthenticationFilter作成

#### 設計の考え方

**目的**: すべてのリクエストに対してJWTを検証し、認証情報をSpring Securityに渡す

**フィルターの位置づけ**:
```
HTTPリクエスト
    ↓
[JwtAuthenticationFilter] ← ここで認証チェック
    ↓
[他のSpring Securityフィルター]
    ↓
[Controller]
```

**処理フロー設計**:
```
1. Authorizationヘッダーを取得
   ↓ ない or "Bearer "で始まらない → スキップ（認証なしで次へ）
2. "Bearer "の後ろのトークン部分を抽出
   ↓
3. トークンを検証（JwtUtil.validateToken）
   ↓ 無効 → 認証情報を設定せずに次へ
4. 有効ならSecurityContextに認証情報を設定
   ↓
5. 次のフィルターへ（filterChain.doFilter）
```

**設計判断**:
- `OncePerRequestFilter`を継承（1リクエストにつき1回だけ実行を保証）
- トークンがない場合は認証なしで通過させる（公開エンドポイント用）
- 認証の強制はSecurityConfigの`authorizeHttpRequests`で制御

#### 実装コード

```java
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // ステップ1: Authorizationヘッダーを取得
        String authHeader = request.getHeader("Authorization");

        // ステップ2: ヘッダーの確認とトークン抽出
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // ステップ3: トークン検証と認証情報の設定
        if (jwtUtil.validateToken(token)) {
            String email = jwtUtil.getEmailFromToken(token);

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // ステップ4: 次のフィルターへ
        filterChain.doFilter(request, response);
    }
}
```

---

### 5. SecurityConfigにフィルター登録

#### 設計の考え方

**目的**: 作成したJwtAuthenticationFilterをSpring Securityのフィルターチェーンに組み込む

**セキュリティ設定の全体像**:
```
SecurityFilterChain
├── CSRF無効化（REST APIなので）
├── セッション管理: STATELESS（JWTはステートレス認証）
├── 認可ルール
│   ├── /api/hello → 認証不要
│   ├── /api/auth/** → 認証不要
│   └── その他 → 認証必要
└── JwtAuthenticationFilter（UsernamePasswordAuthenticationFilterの前に配置）
```

**設計判断**:
- `SessionCreationPolicy.STATELESS`: JWTはトークン自体に情報が含まれるため、サーバー側でセッションを保持しない
- `addFilterBefore`: 標準の認証フィルターより前にJWT認証を行う
- フィルターはBean登録して依存性注入を可能にする

#### 実装コード

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/hello").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

### 6. JWT認証フィルターの動作確認

```bash
# トークンなし → 403（アクセス拒否）
curl -i http://localhost:8080/api/protected

# トークンあり → 200（認証成功）
curl -i http://localhost:8080/api/protected \
  -H "Authorization: Bearer <JWT>"
```

---

## 学んだこと

### getter と setter の違い

| メソッド | 役割 | 例 |
|---------|------|-----|
| `getXxx()` | 値を取得する | `user.getPasswordHash()` |
| `setXxx(value)` | 値を設定する | `user.setPasswordHash(hash)` |

間違えやすいポイント:
```java
// 間違い（setterは値を返さない）
passwordEncoder.matches(password, user.setPasswordHash())

// 正しい
passwordEncoder.matches(password, user.getPasswordHash())
```

### 文字列リテラルの書き方

```java
// 間違い
String "Bearer"

// 正しい（Stringは不要）
"Bearer"
```

変数宣言時は`String`が必要、値を直接使う時は不要。

### Bearer トークンについて

- OAuth 2.0 / JWT認証の業界標準の固定値
- ハードコーディングで問題ない
- `Authorization: Bearer <token>` の形式で送信

### @Override アノテーション

- **メソッド**に付けるアノテーション
- **クラス**には付けない
- 親クラスのメソッドを上書きすることを明示

```java
// 間違い
@Override
public class MyClass extends ParentClass { }

// 正しい
public class MyClass extends ParentClass {
    @Override
    protected void someMethod() { }
}
```

### OncePerRequestFilter

- Spring Securityのフィルター基底クラス
- 1リクエストにつき1回だけ実行されることを保証
- `doFilterInternal`メソッドをオーバーライドして処理を実装

### デバッグの重要性

問題調査のためにログ出力を追加することで、処理の流れを追跡できる。

```java
System.out.println("=== JwtAuthenticationFilter ===");
System.out.println("Authorization header: " + authHeader);
System.out.println("Token valid: " + isValid);
```

---

## 現在のプロジェクト構造

```
src/main/java/com/example/todoapp/
├── TodoappApplication.java
├── config/
│   └── SecurityConfig.java      ← JWT認証フィルター登録
├── controller/
│   ├── HelloController.java     ← テスト用エンドポイント追加
│   └── AuthController.java      ← ログインエンドポイント追加
├── dto/
│   ├── SignupRequest.java
│   ├── UserResponse.java
│   ├── LoginRequest.java
│   └── LoginResponse.java
├── entity/
│   └── User.java
├── filter/
│   └── JwtAuthenticationFilter.java  ← 今回作成
├── repository/
│   └── UserRepository.java
├── service/
│   └── UserService.java         ← loginメソッド完成
└── util/
    └── JwtUtil.java
```

---

## 完了したWBSタスク

- [x] 1.3.7 ログインAPI（JWT発行）
- [x] 1.3.8 JWT認証フィルター

**WBS 1.3（バックエンド認証機能）完了！**

---

## 次回のタスク

WBS 1.4: バックエンド Todo機能

1. Todoエンティティ作成
2. Todo作成API
3. Todo一覧取得API
4. Todo更新API
5. Todo削除API
6. Todo完了切り替えAPI
7. フィルタリング機能

---

**作成日**: 2026-01-25
**最終更新**: 2026-01-25
