# 作業ログ: 2026-01-13 (API設計編)

## 学習情報

- **日付**: 2026-01-13
- **学習フェーズ**: WBS 1.1 設計フェーズ - API設計
- **学習時間**: 約2時間
- **ステータス**: API設計（api_design.md）完成

---

## 完了したタスク

### 作成したドキュメント

- **ファイル名**: `docs/01_specs/api/api_design.md`
- **目的**: RESTful APIの詳細設計

---

## API設計の概要

### API設計の基本原則

**RESTful設計:**
- リソース指向のURL設計
- HTTPメソッドの適切な使い分け
- ステートレスな通信

**HTTPメソッド:**
- GET: データ取得
- POST: データ作成
- PATCH: データ更新（一部）
- DELETE: データ削除

※ PUTは使用しない（意図しない上書きを防ぐため）

**ステータスコード:**
- 200 OK: 取得・更新成功
- 201 Created: 作成成功
- 204 No Content: 削除成功
- 400 Bad Request: バリデーションエラー
- 401 Unauthorized: 認証されていない
- 403 Forbidden: 権限がない
- 404 Not Found: リソースが存在しない
- 409 Conflict: 一意制約違反
- 500 Internal Server Error: サーバーエラー

---

## 実装するAPI一覧

### 1. 認証API（4エンドポイント）

| メソッド | エンドポイント | 説明 |
|---------|---------------|------|
| POST | /api/auth/register | ユーザー登録 |
| POST | /api/auth/login | ログイン |
| POST | /api/auth/logout | ログアウト |
| GET | /api/auth/me | 現在のユーザー情報取得 |

### 2. Todo API（4エンドポイント）

| メソッド | エンドポイント | 説明 |
|---------|---------------|------|
| POST | /api/todos/search | Todo一覧取得（検索） |
| POST | /api/todos | Todo作成 |
| PATCH | /api/todos/{id} | Todo更新 |
| DELETE | /api/todos/{id} | Todo削除 |

### 3. Category API（4エンドポイント）

| メソッド | エンドポイント | 説明 |
|---------|---------------|------|
| GET | /api/categories | カテゴリー一覧取得 |
| POST | /api/categories | カテゴリー作成 |
| PATCH | /api/categories/{id} | カテゴリー更新 |
| DELETE | /api/categories/{id} | カテゴリー削除 |

**合計**: 12エンドポイント（Phase 1）

---

## API設計の重要な決定事項

### 1. PUTを使わない理由

**PUTの特徴:**
- リソース全体の置き換え
- 未指定のフィールドは削除またはデフォルト値に

**問題点:**
- 意図しない上書きが発生しやすい
- クライアントが全フィールドを送る必要がある

**採用した方式: PATCH（部分更新）**
- 指定したフィールドのみ更新
- 未指定フィールドは変更されない
- より安全で柔軟

---

### 2. GETでリクエストボディを使わない

**設計方針:**
- **GET**: クエリパラメータを使用（軽微な絞り込み）
- **POST /search**: リクエストボディを使用（複雑な検索条件）

**理由:**
- GETでリクエストボディを使うのは仕様的にグレーゾーン
- プロキシやキャッシュで問題が起きる可能性
- 複雑な検索条件は`POST /search`エンドポイントを用意

**例: Todo検索**
```
POST /api/todos/search
{
  "status": "completed",
  "category_id": "xxx",
  "priority": [3,4,5],
  "sort": "due",
  "order": "asc"
}
```

---

### 3. user_idをレスポンスに含めない

**設計方針:**
- user_idはJWTトークンから取得
- APIのリクエスト・レスポンスには含めない

**理由:**
- セキュリティ向上（他のユーザーのIDを知られない）
- APIがシンプルになる
- クライアント側で管理する必要がない

**例外: GET /api/auth/me**
- ユーザー情報取得APIなので、idを返すのは自然

---

### 4. エラーコードの体系化

**エラーコードの命名規則:**

**認証エラー: AUTH_xxx**
- AUTH_MISSING_TOKEN: トークン未指定
- AUTH_INVALID_TOKEN: トークン不正
- AUTH_EXPIRED_TOKEN: トークン期限切れ
- AUTH_INVALID_CREDENTIALS: ログイン失敗

**バリデーションエラー: VALIDATION_xxx**
- VALIDATION_REQUIRED_FIELD: 必須フィールド欠落
- VALIDATION_INVALID_FORMAT: 形式不正

**リソースエラー: RESOURCE_xxx**
- RESOURCE_NOT_FOUND: リソースが存在しない
- RESOURCE_ALREADY_EXISTS: リソースが既に存在
- RESOURCE_FORBIDDEN: アクセス権限なし

**メリット:**
- 機械的な判定が可能
- エラーハンドリングが容易
- 一貫性がある

---

## 共通エラーレスポンス仕様

### エラーレスポンスフォーマット

すべてのエラーは以下の形式で返却：

```json
{
  "code": "ERROR_CODE",
  "message": "Human readable message",
  "details": {},
  "fieldErrors": {}
}
```

**フィールドの説明:**
- `code`: エラーコード（機械判定用）
- `message`: 人間が読めるエラーメッセージ
- `details`: 追加情報（オプション）
- `fieldErrors`: フィールドごとのエラー（バリデーションエラー時）

---

## 主要APIの設計詳細

### POST /api/auth/register（ユーザー登録）

**リクエスト:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**バリデーション:**
- email: 必須、有効なメールアドレス形式、大文字小文字を区別
- password: 必須、8文字以上、英数字を含む

**レスポンス（201 Created）:**
```json
{
  "user": {
    "email": "user@example.com"
  },
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9....",
  "token_type": "Bearer",
  "expires_in": 3600
}
```

**設計のポイント:**
- 登録後すぐログインできるよう、JWTを返す
- `access_token`はJWT本体（Authorizationヘッダーにそのまま入れられる）
- `expires_in`は秒単位（3600秒 = 1時間）

---

### POST /api/todos（Todo作成）

**リクエスト:**
```json
{
  "title": "買い物に行く",
  "description": "木綿豆腐と豆板醤を買う",
  "due": "2025-12-31",
  "priority": 3,
  "category_id": "750e8400-e29b-41d4-a716-446655440000"
}
```

**バリデーション:**
- title: 必須、1〜100文字、前後空白はトリム
- description: 任意、文字制限なし、前後空白はトリム
- due: 任意、YYYY-MM-DD形式
- priority: 必須、1〜5の整数
- category_id: 任意、UUID形式、存在するカテゴリーIDであること

**レスポンス（201 Created）:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "category_id": "750e8400-e29b-41d4-a716-446655440000",
  "title": "買い物に行く",
  "description": "木綿豆腐と豆板醤を買う",
  "due": "2025-12-31",
  "priority": 3,
  "is_completed": false
}
```

---

### PATCH /api/todos/{id}（Todo更新）

**リクエスト:**
```json
{
  "due": "2025-12-31",
  "priority": 4,
  "category_id": null,
  "is_completed": true
}
```

**PATCHの更新ルール:**
- 更新対象は、リクエストボディに含まれるフィールドのみ
- 少なくとも1項目以上を含むこと
- null 指定は原則不可
- 例外: category_idは null 指定を許可（カテゴリ未設定に戻す）

**設計のポイント:**
- 部分更新なので、必要なフィールドだけ送る
- 未指定のフィールドは変更されない
- 柔軟で安全な更新が可能

---

### POST /api/todos/search（Todo検索）

**リクエスト:**
```json
{
  "status": "completed",
  "category_id": "850e8400-e29b-41d4-a716-446655470000",
  "priority": [3,4,5],
  "sort": "due",
  "order": "asc"
}
```

**パラメータ:**
- status: completed / incomplete / all（デフォルト: all）
- category_id: カテゴリーでフィルタ（省略可）
- priority: 優先度の配列でフィルタ（省略可）
- sort: todo_id / title / due / priority（デフォルト: id）
- order: asc / desc（デフォルト: desc）

**レスポンス（200 OK）:**
```json
{
  "todos": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "category_id": "750e8400-e29b-41d4-a716-446655440000",
      "title": "買い物に行く",
      "description": "木綿豆腐と豆板醤を買う",
      "due": "2025-12-31",
      "priority": 3,
      "is_completed": false,
      "category": {
        "id": "750e8400-e29b-41d4-a716-446655440000",
        "name": "家事",
        "color": "#9c7449"
      }
    }
  ]
}
```

**設計のポイント:**
- Todoと関連するCategoryの情報も含める（JOINして取得）
- 配列を直接返さず`{ "todos": [...] }`でラップ（拡張性）

---

## 学んだ重要な概念

### 1. RESTful API設計の原則

**リソース指向:**
- URLはリソースを表す（動詞ではなく名詞）
- 例: `/api/todos/create` ではなく `POST /api/todos`

**HTTPメソッドで操作を表現:**
- GET: 取得
- POST: 作成
- PATCH/PUT: 更新
- DELETE: 削除

**ステートレス:**
- サーバーはクライアントの状態を保持しない
- 各リクエストは独立している
- 認証情報はJWTで毎回送る

---

### 2. バリデーションの設計

**2段階のバリデーション:**
1. **クライアントサイド**: UX向上（即座にエラー表示）
2. **サーバーサイド**: セキュリティ（必須）

**サーバーサイドバリデーション:**
- 型チェック（String, Integer, UUID）
- 必須チェック（null不可）
- 長さチェック（1〜100文字）
- 形式チェック（メールアドレス、日付、HEX色コード）
- 存在チェック（外部キー参照）
- 一意性チェック（ユニーク制約）

---

### 3. エラーハンドリングの設計

**エラーの分類:**
1. **バリデーションエラー（400）**: ユーザーの入力ミス
2. **認証エラー（401）**: ログインしていない、トークン不正
3. **認可エラー（403）**: 権限がない
4. **リソースエラー（404）**: データが存在しない
5. **競合エラー（409）**: 一意制約違反
6. **サーバーエラー（500）**: プログラムのバグ

**統一されたエラーレスポンス:**
- すべてのエラーで同じフォーマット
- 機械判定可能な`code`フィールド
- 人間が読める`message`フィールド
- フィールドごとのエラー情報`fieldErrors`

---

### 4. 認証と認可の違い

**認証（Authentication, 401 Unauthorized）:**
- 「あなたは誰ですか？」
- JWTトークンが有効か確認
- ログインしているか確認

**認可（Authorization, 403 Forbidden）:**
- 「あなたはこの操作をしていいですか？」
- 他のユーザーのデータにアクセスしていないか確認
- 権限があるか確認

---

## API設計で工夫したポイント

### 1. 冪等性の考慮

**冪等性とは:**
- 同じ操作を複数回実行しても、結果が変わらない性質

**DELETE APIの冪等性:**
- すでに削除済みのリソースを削除しても 204 を返す
- エラーにしない（ネットワークエラーでリトライしやすい）

### 2. 拡張性を考慮したレスポンス

**配列をラップする:**
```json
❌ 悪い例:
[
  { "id": "xxx", ... }
]

✅ 良い例:
{
  "todos": [
    { "id": "xxx", ... }
  ]
}
```

**理由:**
- 後でページネーション情報を追加できる
- メタデータ（合計件数など）を追加できる

### 3. null許可フィールドの明確化

**PATCHでのnull扱い:**
- 原則: null不可（意図しない削除を防ぐ）
- 例外: category_id は null 許可（カテゴリ未設定に戻せる）

**設計意図を明記:**
- APIドキュメントにnull許可フィールドを明記
- 実装時の判断基準になる

---

## 発生した課題と解決

### 課題1: Todo一覧取得をGETにするかPOSTにするか

**検討内容:**
- GET: RESTful的には正しい
- POST: 複雑な検索条件を送りやすい

**解決:**
- POST /api/todos/search を採用
- 理由: 検索条件が多い（status, category_id, priority配列, sort, order）

### 課題2: PUTとPATCHのどちらを使うか

**検討内容:**
- PUT: リソース全体の更新
- PATCH: 一部のフィールドの更新

**解決:**
- PATCHを採用、PUTは使わない
- 理由: 部分更新の方が安全で柔軟

### 課題3: エラーコードをどう体系化するか

**検討内容:**
- シンプルなコード（VALIDATION_ERROR）
- 詳細なコード（VALIDATION_EMAIL_INVALID）

**解決:**
- カテゴリ別に体系化（AUTH_xxx, VALIDATION_xxx, RESOURCE_xxx）
- 理由: 一貫性と管理しやすさのバランス

---

## 次のステップ

API設計が完了したので、次は以下を実施：

1. **ワイヤーフレーム作成**
   - 各画面のレイアウト設計
   - 画面遷移図

2. **設計レビュー**
   - すべての設計ドキュメントの整合性確認
   - 不整合の修正

---

## まとめ

API設計を通じて、フロントエンドとバックエンドの通信方法が明確になりました。

**達成したこと:**
- ✅ 12エンドポイントの詳細設計
- ✅ RESTful設計の原則に従った設計
- ✅ リクエスト・レスポンススキーマの定義
- ✅ エラーレスポンスの統一
- ✅ バリデーションルールの明確化
- ✅ エラーコードの体系化

**学んだこと:**
- RESTful API設計の原則
- HTTPメソッドとステータスコードの使い分け
- バリデーションの設計
- エラーハンドリングの設計
- 認証と認可の違い
- 冪等性の考慮
- 拡張性を考慮した設計

**設計の原則:**
- リソース指向のURL設計
- ステートレスな通信
- 統一されたエラーレスポンス
- 拡張性を考慮したレスポンス構造

**次のフェーズ:**
ワイヤーフレーム作成と設計レビューを行い、実装フェーズに進みます。

---

**作成日**: 2026-01-13
**WBS進捗**: 1.1 設計フェーズ - API設計 完了
