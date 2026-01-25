## 目的

フロントエンドとバックエンドがどのようにデータをやり取りするかを定義する。

## API概要一覧

1. 認証API
    - ユーザー登録
    - ログイン
    - ログアウト
    - 現在のユーザー情報取得
2. Todo API
    - Todo一覧取得（検索）
    - Todo作成
    - Todo更新
    - Todo削除
3. Category API
    - カテゴリー一覧取得
    - カテゴリー作成
    - カテゴリー更新
    - カテゴリー削除

## API設計の基本原則

RESTful設計

| HTTPメソッド | 用途               | 例                                             |
|--------------|--------------------|------------------------------------------------|
| GET          | データ取得         | GET /api/todos （一覧取得）                    |
| POST         | データ作成         | POST /api/todos （新規作成）                   |
| PUT          | データ更新（全体） | PUT /api/todos/123 （更新）                    |
| PATCH        | データ更新（一部） | PATCH /api/todos/123/complete （完了切り替え） |
| DELETE       | データ削除         | DELETE /api/todos/123 （削除）                 |

※ 本APIでは PUT は使用しない。
PUT はリソース全体の置き換えを前提とするが、本システムでは更新対象を明示的に指定する部分更新（PATCH）のみを採用し、未指定フィールドの意図しない上書きを防ぐことを目的とする。

ステータスコード

| コード | 意味                  | 使う場面                                 |
|--------|-----------------------|------------------------------------------|
| 200    | OK                    | 取得・更新成功                           |
| 201    | Created               | 作成成功                                 |
| 204    | No Content            | 削除成功（レスポンスボディなし）         |
| 400    | Bad Request           | バリデーションエラー                     |
| 401    | Unauthorized          | 認証されていない                         |
| 403    | Forbidden             | 権限がない                               |
| 404    | Not Found             | リソースが存在しない                     |
| 409    | Conflict              | 一意制約違反（重複データが存在）          |
| 500    | Internal Server Error | サーバーエラー                           |

- POST / PATCH のレスポンスでは、更新後のリソース状態を返却する。
- 論理削除情報（deleted_at / is_deleted）はクライアントには返さない。

## APIの設計方針

- user_id は JWT トークンから取得するため、APIのリクエスト／レスポンスには含めない。
- HTTPメソッドの使い分け方針は以下とする。

  - GET
    - 単純なリソース取得・軽微な絞り込み
    - クエリパラメータを使用 ※ **GETでリクエストボディを使用する設計は採用しない**
  - POST
    - 検索条件が多い、または構造化された検索を行う場合
    - リクエストボディ（JSON）を使用（ヘッダーに`Content-Type: application/json`記載）
    - 検索用途の場合は `/search` エンドポイントを切り出す

## 共通エラーレスポンス仕様

すべてのエラーは以下の形式で返却する。

```json
{
  "code": "ERROR_CODE",
  "message": "Human readable message",
  "details": {},
  "fieldErrors": {}
}
```

- **「認証(401)」と「認可(403)」を分けて書く**（特に見出しで）
- 404の文言は **存在しない** だけに限定（permissionを混ぜない）
- 403の対象を **明確に例示**（「他ユーザーのカテゴリ」など）

### エラーコード体系

エラーコードは以下の命名規則で体系化する。

- 認証エラー: `AUTH_xxx`
  - `AUTH_INVALID_TOKEN`
  - `AUTH_EXPIRED_TOKEN`
  - `AUTH_MISSING_TOKEN`
  - `AUTH_INVALID_CREDENTIALS`（ログイン失敗など、資格情報が不正）
- バリデーションエラー: `VALIDATION_xxx`
  - `VALIDATION_REQUIRED_FIELD`
  - `VALIDATION_INVALID_FORMAT`
- リソースエラー: `RESOURCE_xxx`
  - `RESOURCE_NOT_FOUND`
  - `RESOURCE_ALREADY_EXISTS`
  - `RESOURCE_FORBIDDEN`

> 補足: HTTPステータスは従来どおり 400/401/403/404/409 を使用し、`code` で機械判定可能な詳細理由を表現する。

### 共通エラー（多くの認証必須エンドポイントで共通）

#### 401 Unauthorized（認証）

JWTに関する認証エラーは、状況に応じて以下のいずれかのコードを返す。

- `AUTH_MISSING_TOKEN`: Authorizationヘッダーが未指定、またはBearerトークンが空
- `AUTH_INVALID_TOKEN`: 署名不正、改ざん、形式不正などによりトークンが検証できない
- `AUTH_EXPIRED_TOKEN`: 有効期限切れ

```json
// AUTH_MISSING_TOKEN（例）
{
  "code": "AUTH_MISSING_TOKEN",
  "message": "Authorization token is missing",
  "details": {},
  "fieldErrors": {}
}
```

```json
// AUTH_INVALID_TOKEN（例）
{
  "code": "AUTH_INVALID_TOKEN",
  "message": "Authorization token is invalid",
  "details": {},
  "fieldErrors": {}
}
```

```json
// AUTH_EXPIRED_TOKEN（例）
{
  "code": "AUTH_EXPIRED_TOKEN",
  "message": "Authorization token has expired",
  "details": {},
  "fieldErrors": {}
}
```

#### 403 Forbidden（認可）

- 自分が所有しないリソース（他ユーザーのTodo/カテゴリ等）へアクセスした場合

```json
{
  "code": "RESOURCE_FORBIDDEN",
  "message": "You don't have permission to access this resource",
  "details": {},
  "fieldErrors": {}
}
```

#### 500 Internal Server Error

- 想定外のサーバーエラー

```json
{
  "code": "INTERNAL_ERROR",
  "message": "Unexpected server error",
  "details": {},
  "fieldErrors": {}
}
```

※ 以降、各エンドポイントでは「固有エラー（追加で起こり得るもの）」のみを記載する。
（401/403/500 が発生し得る場合は上記の共通定義に従う）

## 各API詳細

### 1. 認証API

#### POST /api/auth/register

**説明**: 新規ユーザーを登録し、JWTを発行する

**認証**: 不要

※registerにJWTはまだ存在しないため。

**ヘッダー**:

- Content-Type: application/json

**リクエスト**:

```json
{
"email": "user@example.com",
"password": "password123"
}
```

**バリデーション**:

- email:
  - 必須
  - 有効なメールアドレス形式
  - 大文字小文字を区別する
- password:
  - 必須
  - 8文字以上
  - 英数字を含む

**レスポンス**:

- 登録後すぐログインできるようにしたい → レスポンスにJWTを含める
- 登録成功時、access_token を返却する（Bearer JWT）
  - `access_token` は **JWT本体**（Authorizationにそのまま入れられる）
  - `expires_in` は秒（例：3600）で統一
  - `token_type` は固定で `"Bearer"`（将来の拡張用）

```json
// 201 Created
{
  "user": {
    "email": "user@example.com"
  },
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9....",
  "token_type": "Bearer",
  "expires_in": 3600
}
```

**エラーレスポンス**:

```json
// 400 Bad Request
{
  "code": "VALIDATION_INVALID_FORMAT",
  "message": "Invalid email or password",
  "details": {},
  "fieldErrors": {
    "email": "Invalid email format"
  }
}

// 409 Conflict
{
  "code": "RESOURCE_ALREADY_EXISTS",
  "message": "A user with this email already exists",
  "details": {},
  "fieldErrors": {
    "email": "Already in use"
  }
}
```

#### POST /api/auth/login

**説明**: メールアドレスとパスワードでログインし、JWTを発行する

**認証**: 不要

**ヘッダー**:

- Content-Type: application/json

**リクエスト**:

```json
{
"email": "user@example.com",
"password": "password123"
}
```

**バリデーション**:

- email:
  - 必須
  - 有効なメールアドレス形式
  - 大文字小文字を区別する
- password:
  - 必須
  - 8文字以上
  - 英数字を含む

**レスポンス**:

- 登録成功時、access_token を返却する（Bearer JWT）
  - `access_token` は **JWT本体**（Authorizationにそのまま入れられる）
  - `expires_in` は秒（例：3600）で統一
  - `token_type` は固定で `"Bearer"`（将来の拡張用）

```json
// 200 OK
{
  "user": {
    "email": "user@example.com"
  },
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9....",
  "token_type": "Bearer",
  "expires_in": 3600
}
```

**エラーレスポンス**:

```json
// 401 Unauthorized（メールアドレスが登録されていない）
{
  "code": "AUTH_INVALID_CREDENTIALS",
  "message": "This email address is not registered",
  "details": {},
  "fieldErrors": {}
}

// 401 Unauthorized（パスワードが不正）
{
  "code": "AUTH_INVALID_CREDENTIALS",
  "message": "The password you entered is incorrect",
  "details": {},
  "fieldErrors": {}
}
```

#### POST /api/auth/logout

**説明**: ログアウトする（クライアント側でJWTを破棄する）

**認証**: 必須（Bearer JWT）

**ヘッダー**:

- Authorization: Bearer {JWT_TOKEN}

**リクエスト**: ボディなし

**レスポンス**:

- 204 No Content（レスポンスボディなし）

**補足**:

- 本APIは「ログアウトの操作点」を提供する目的（将来の監査ログ・トークン失効方式への拡張）で用意する
- トークンの実体破棄はクライアント側で行う

**エラーレスポンス**:

- なし（401/500 は共通定義に従う）
- もし将来的に refresh_token を導入するなら、logout は「refresh_tokenの無効化」として意味を持ちます

#### GET /api/auth/me

**説明**: 現在のユーザー情報（ログイン中ユーザー）を取得する

**認証**: 必須（Bearer JWT）

**ヘッダー**:

- Authorization: Bearer {JWT_TOKEN}

**リクエスト**: クエリ・ボディなし

**レスポンス**:

```json
// 200 OK
{
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com"
  }
}
```

**補足**:

- 以下理由を考慮して、**idをクライアントに返す**
  - 返すメリット: フロント側で「自分のユーザーID」をキーにキャッシュできる、将来のプロフィール拡張に自然
  - 返さないメリット: 露出最小化（ただし実益は薄い）
- 既に「user_idはJWTから取得し、各APIのレスポンスに含めない」方針だが、/me は「ユーザー情報取得API」なので例外として id を含めても矛盾しない（むしろ自然）

**エラーレスポンス**:

- なし（401/500 は共通定義に従う）
- 403/404 は原則不要（「自分自身」を返すだけなので認可/存在判定に揺れが出にくい）

### 2. Todo API

#### POST /api/todos/search （Todo一覧取得）

**説明**: 条件を指定してTodo一覧を取得する

**認証**: 必須

**ヘッダー**:

- Authorization: Bearer {JWT_TOKEN}
- Content-Type: application/json

**リクエスト**:

```json
{
  "status": "completed",
  "category_id": "850e8400-e29b-41d4-a716-446655470000",
  "priority": [3,4,5],
  "sort": "todo_id",
  "order": "desc"
}
```

- status: completed / incomplete / all（デフォルト: all）
- category_id: カテゴリーでフィルタ（省略可）
- priority: 優先度でフィルタ（省略可）
- sort: todo_id / title / due / priority（デフォルト: id）
- order: asc / desc（デフォルト: desc）

**レスポンス**:

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
      "is_deleted": false,
      "category": {
        "id": "750e8400-e29b-41d4-a716-446655440000",
        "name": "家事",
        "color": "#9c7449"
      }
    }
  ]
}
```

- Todoを返す時、関連するCategoryの情報も含める
- 配列を直接返すのではなく、`{ "todos": [...] }`のようにラップする（拡張性が高い）

**エラーレスポンス**:

```json
// 400 Bad Request（検索条件のバリデーション）
{
  "code": "VALIDATION_INVALID_FORMAT",
  "message": "Invalid search parameters",
  "details": {},
  "fieldErrors": {
    "status": "Allowed values are completed, incomplete, all"
  }
}
```

#### POST /api/todos

**説明**: Todoを新規作成する

**認証**: 必須

**ヘッダー**:

- Authorization: Bearer {JWT_TOKEN}
- Content-Type: application/json

**リクエスト**:

```json
{
"title": "買い物に行く",
"description": "木綿豆腐と豆板醤を買う",
"due": "2025-12-31",
"priority": 3,
"category_id": "750e8400-e29b-41d4-a716-446655440000"  // 省略可
}
```

※ category_id は省略可（未指定の場合はカテゴリ未設定として扱う）

**バリデーション**:

- title: 必須 / 1〜100文字 / 前後空白はトリム
- description: 任意 / 文字制限なし / 前後空白はトリム
- due: 任意 / YYYY-MM-DD
- priority: 必須 / 1〜5の整数
- category_id: 任意 / UUID形式
  - 指定された場合、存在するカテゴリIDであること
  - 指定されたカテゴリが削除済みの場合は 400

**レスポンス**:

```json
// 201 Created
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

※ category_id 未指定の場合は null を返す

**エラーレスポンス**:

- 400 Bad Request（バリデーション／削除済みカテゴリ指定）
- 404 Not Found（category_id が存在しない）

```json
// 400 Bad Request
{
  "code": "VALIDATION_REQUIRED_FIELD",
  "message": "Required field is missing",
  "details": {},
  "fieldErrors": {
    "title": "Title is required"
  }
}
```

```json
// 400 Bad Request（削除済みカテゴリ指定）
{
  "code": "VALIDATION_INVALID_FORMAT",
  "message": "The specified category is deleted",
  "details": {},
  "fieldErrors": {
    "category_id": "Deleted category cannot be assigned"
  }
}
```

```json
// 404 Not Found（category_id が存在しない）
{
  "code": "RESOURCE_NOT_FOUND",
  "message": "Category with the specified id does not exist",
  "details": {},
  "fieldErrors": {
    "category_id": "Not found"
  }
}
```

#### PATCH /api/todos/{id}

**説明**: 既存Todoの一部項目を更新する（部分更新）

**認証**: 必須

**ヘッダー**:

- Authorization: Bearer {JWT_TOKEN}
- Content-Type: application/json

**リクエスト**:

- 更新対象はリクエストボディに含まれるフィールドのみ
- 少なくとも1項目以上を含むこと
- null 指定は原則不可
- 例外: category_id は null 指定を許可し、カテゴリ未設定に戻す

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "due": "2025-12-31",
  "priority": 4,
  "category_id": null,
  "is_completed": true
}
```

**バリデーション**:

- title（指定された場合）: null不可 / 1〜100文字 / 前後空白はトリム
- description（指定された場合）: null不可 / 文字制限なし / 前後空白はトリム
- due（指定された場合）: null不可 / YYYY-MM-DD
- priority（指定された場合）: null不可 / 1〜5の整数
- category_id（指定された場合）:
  - UUID形式、または null（カテゴリ未設定に戻す場合のみ許可）
  - UUID指定時は存在するカテゴリIDであること
  - UUID指定時に削除済みカテゴリなら 400
- is_completed（指定された場合）: null不可 / boolean

**レスポンス**:

```json
// 200 OK
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "category_id": null,
  "title": "買い物に行く",
  "description": "木綿豆腐と豆板醤を買う",
  "due": "2025-12-31",
  "priority": 3,
  "is_completed": false,
  "deleted_at": null
}
```

**エラーレスポンス**:

- 400 Bad Request（空ボディ／バリデーション／不正なnull指定）
- 404 Not Found（Todoが存在しない）
- 404 Not Found（category_id が存在しない ※ UUID指定時）

```json
// 400 Bad Request（空ボディ）
{
  "code": "VALIDATION_REQUIRED_FIELD",
  "message": "Request body must include at least one updatable field",
  "details": {},
  "fieldErrors": {}
}
```

```json
// 400 Bad Request（null指定：category_id 以外）
{
  "code": "VALIDATION_INVALID_FORMAT",
  "message": "Null is not allowed for this field",
  "details": {},
  "fieldErrors": {
    "priority": "Null is not allowed"
  }
}
```

```json
// 404 Not Found（Todoが存在しない）
{
  "code": "RESOURCE_NOT_FOUND",
  "message": "Todo with the specified id does not exist",
  "details": {},
  "fieldErrors": {}
}
```

```json
// 404 Not Found（category_id が存在しない）
{
  "code": "RESOURCE_NOT_FOUND",
  "message": "Category with the specified id does not exist",
  "details": {},
  "fieldErrors": {
    "category_id": "Not found"
  }
}
```

#### DELETE /api/todos/{id}

**説明**: Todoを論理削除する（`deleted_at` に削除日時を設定）

**認証**: 必須

**ヘッダー**:

- Authorization: Bearer {JWT_TOKEN}

**リクエスト**: ボディなし

**レスポンス**:

- 204 No Content（レスポンスボディなし）
- すでに削除済みの場合も 204 を返す（冪等）

**認可（Authorization）ルール**:

- 対象カテゴリが **自分の所有リソース**である場合のみ削除可能
- 他ユーザーのカテゴリIDを指定した場合は 403 Forbidden を返す

**エラーレスポンス**:

- （原則）削除は冪等のため、存在しない場合も 204 を返す

### 3. Category API

#### GET /api/categories

**説明**: カテゴリ一覧を取得する

**認証**: 必須

**ヘッダー**:

- Authorization: Bearer {JWT_TOKEN}

**リクエスト**: リクエストパラメータは無し

**取得ルール**:

- 削除済みカテゴリ（`deleted_at` が設定されているもの）は返さない
- カテゴリが1件も存在しない場合は空配列を返す
- 並び順は作成日時の昇順

**レスポンス**:

```json
// 200 OK
{
  "categories": [
    {
      "id": "750e8400-e29b-41d4-a716-446655440000",
      "name": "仕事",
      "color": "#49839c"
    }
  ]
}
```

**エラーレスポンス**:

- 本APIは、認証が成功していればエラーを返さず、常に 200 OK を返す
- カテゴリが存在しない場合も空配列を返す

#### POST /api/categories

**説明**: 新規カテゴリ作成する

**認証**: 必須

**ヘッダー**:

- Authorization: Bearer {JWT_TOKEN}
- Content-Type: application/json

**リクエスト**:

```json
{
  "name": "リフレッシュ",
  "color": "#499c5c"
}
```

**バリデーション**:

- `name`: 必須、1〜50文字、前後空白はトリム
- `name`: 大文字小文字を区別せず一意（case-insensitive）※
- `color`: 必須、形式は`^#[0-9a-fA-F]{6}$`（#RRGGBB）

※ 補足: `Refresh` と `refresh` は同一カテゴリとして扱う。既に `refresh` が存在する場合、`Refresh` の作成は 409 Conflict

**レスポンス**:

```json
// 201 Created
{
  "id": "850e8400-e29b-41d4-a716-446655440000",
  "name": "リフレッシュ",
  "color": "#499c5c"
}
```

**エラーレスポンス**:

- 400 Bad Request（バリデーション）
- 409 Conflict（カテゴリ名重複）

```json
// 400 Bad Request
{
  "code": "VALIDATION_REQUIRED_FIELD",
  "message": "Required field is missing",
  "details": {},
  "fieldErrors": {
    "name": "Category name is required"
  }
}
```

```json
// 409 Conflict
{
  "code": "RESOURCE_ALREADY_EXISTS",
  "message": "A category with this name already exists",
  "details": {},
  "fieldErrors": {
    "name": "Already in use"
  }
}
```

#### PATCH /api/categories/{id}

**説明**: 既存カテゴリの一部項目を更新する

**認証**: 必須

**ヘッダー**:

- Authorization: Bearer {JWT_TOKEN}
- Content-Type: application/json

**リクエスト**:

```json
{
  "name": "リフレッシュ",
  "color": "#499c5c"
}
```

**バリデーション**:

- リクエストボディは少なくとも1項目以上を含むこと
- 各フィールドの値は `null` を許可しない
- `name`（指定された場合）:
  - null 不可
  - 1〜50文字、前後空白はトリム
- `color`（指定された場合）:
  - null 不可
  - 形式は `^#[0-9a-fA-F]{6}$`

※ 補足:

- Refresh と refresh は同一カテゴリとして扱う
- 自分自身のカテゴリ名との一致は許可することで、`color`のみの変更ができるようになる
- 他のカテゴリで同名（case-insensitive）が存在する場合は`409 Conflict`

**PATCH の更新ルール**:

- 更新対象は、リクエストボディに **含まれているフィールドのみ**
- 値が `null` のフィールドは **無効**
- `null` が指定された場合は 400 Bad Request を返す
- フィールドを削除・初期化する目的で `null` は使用しない

**レスポンス**:

```json
// 200 OK
{
  "id": "850e8400-e29b-41d4-a716-446655440000",
  "name": "リフレッシュ",
  "color": "#499c5c",
  "deleted_at": null
}
```

**エラーレスポンス**:

- 400 Bad Request（空ボディ／バリデーション）
- 404 Not Found（カテゴリが存在しない）
- 409 Conflict（カテゴリ名重複）

```json
// 400 Bad Request（空ボディ）
{
  "code": "VALIDATION_REQUIRED_FIELD",
  "message": "Request body must include at least one updatable field",
  "details": {},
  "fieldErrors": {}
}
```

```json
// 404 Not Found
{
  "code": "RESOURCE_NOT_FOUND",
  "message": "Category with the specified id does not exist",
  "details": {},
  "fieldErrors": {}
}
```

```json
// 409 Conflict
{
  "code": "RESOURCE_ALREADY_EXISTS",
  "message": "A category with this name already exists",
  "details": {},
  "fieldErrors": {
    "name": "Already in use"
  }
}
```

#### DELETE /api/categories/{id}

**説明**: カテゴリを論理削除する

**認証**: 必須

**ヘッダー**:

- Authorization: Bearer {JWT_TOKEN}

**リクエスト**: ボディなし

**レスポンス**:

- 204 No Content（レスポンスボディなし）
- すでに削除済みの場合も 204 を返す（冪等）

**認可（Authorization）ルール**:

- 対象カテゴリが **自分の所有リソース**である場合のみ削除可能
- 他ユーザーのカテゴリIDを指定した場合は 403 Forbidden を返す

**エラーレスポンス**:

- （原則）削除は冪等のため、存在しない場合も 204 を返す
