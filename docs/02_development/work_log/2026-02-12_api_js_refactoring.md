# 作業ログ: 2026-02-12 (API連携の共通化・api.js作成)

## 学習情報

- **日付**: 2026-02-12
- **学習フェーズ**: WBS 1.6.6（API連携・fetch処理の共通化）完了
- **ステータス**: 完了

---

## 実施内容

### 1. api.js の作成（WBS 1.6.6）

**場所**: `src/main/resources/static/js/api.js`

**作成した関数**:

1. `getAuthToken()` - localStorageからJWTトークンを取得。トークンがなければlogin.htmlへリダイレクト
2. `fetchWithAuth(token, url, method, body)` - 認証ヘッダー付きの共通fetch関数
   - tokenの有無でAuthorizationヘッダーの付与を切り替え
   - GETリクエストにはbodyを付けない
   - 401レスポンスで自動的にlogin.htmlへリダイレクト
   - 204 No Contentの場合はresponse.json()を呼ばない
   - 正常時はresponse.json()の結果を返す

**作成中に修正した問題**:

1. **tokenの有無の条件分岐が逆**
   - `!token`（tokenがない場合）にAuthorizationをセットしていた
   - 解決: 条件を修正、tokenがない場合はAuthorizationヘッダー自体を付けない設計に

2. **headersをlet/constなしで宣言（グローバル変数化）**
   - 解決: if文の前で `let headers;` と宣言

3. **JavaのメソッドをJavaScriptで使用**
   - `httpMethod.equals("GET")` → Javaの文字列比較メソッド
   - 解決: `method === "GET"` に修正（変数名も統一）

4. **fetchの戻り値をreturnしていない**
   - 呼び出し元で.then()が使えなかった
   - 解決: `return fetch(url, options)` に修正

5. **.then()に関数ではなくif文を直接渡していた**
   - 解決: アロー関数 `response => { ... }` の形に修正

6. **.then()チェーンで値が消える問題**
   - 最初の.then()でreturnしないと、次の.then()にundefinedが渡る
   - 解決: 2つの.then()を1つにまとめ、response.json()をreturnする形に

7. **GETとそれ以外で.then()の重複コード**
   - 解決: fetchに渡すoptionsを先に変数に組み立て、fetchと.then()は1回だけ書く構成に（DRY原則）

### 2. app.js の書き換え

**場所**: `src/main/resources/static/js/app.js`

**変更内容**:
- 冒頭の認証チェック＆トークン取得を `getAuthToken()` に置き換え
- 5箇所のfetch呼び出しを `fetchWithAuth` に置き換え
- Todo作成・編集のfetch処理の重複を解消（url/methodだけ分岐し、fetchは1回に）

**修正した問題**:

1. **カテゴリー取得で response.json() を二重に呼んでいた**
   - fetchWithAuth内で既にjson()済みなのに、.then()で再度json()していた
   - 解決: 余分な.then()を削除

2. **DELETE時に「削除に失敗しました」と表示される**
   - 原因: サーバーが200 OK（bodyなし）を返し、response.json()でパースエラー
   - 解決: TodoControllerのDELETEを`ResponseEntity.noContent().build()`（204）に変更、api.jsで204時はjson()を呼ばない処理を追加

3. **編集時にカテゴリーがフォームに自動入力されない**
   - 原因: `todo.category?.name`をセットしていたが、selectのoptionのvalueはid
   - 解決: `todo.category?.id` に変更

### 3. category.js の書き換え

**場所**: `src/main/resources/static/js/category.js`

**変更内容**:
- `getAuthToken()` と `fetchWithAuth` を使う形に書き換え

**修正した問題**:

1. **`.then(window.location.reload())`で即実行されていた**
   - .then()には関数を渡す必要がある
   - 解決: `.then(() => window.location.reload())`

### 4. TodoServiceの更新処理修正

**場所**: `src/main/java/com/example/todoapp/service/TodoService.java`

- updateメソッドにcategoryIdからCategoryを取得・設定する処理を追加
- createメソッドにはあったがupdateメソッドに欠けていた

### 5. Categoryユニーク制約の修正

**問題**: 論理削除済みカテゴリーと同じ名前のカテゴリーが作成できない

- 原因: `@UniqueConstraint(columnNames = {"user_id", "name"})` がDB上で物理的に存在し、論理削除済みレコードも含めてユニーク制約が適用されていた
- 解決:
  1. Category.javaから `@UniqueConstraint` を削除
  2. CategoryServiceで `deleted_at IS NULL` のレコードのみ重複チェックするロジックを追加
  3. DBの既存制約を手動で削除（`ALTER TABLE categories DROP CONSTRAINT ...`）
- 学び: `ddl-auto=update` はカラムや制約の**追加**はするが、**削除**はしない。エンティティから制約を消しても、DBには残り続ける

---

## 学んだこと

### 1. Promiseとreturn

- `fetch()` はPromiseを返す
- 関数内で `return fetch(...)` しないと、呼び出し元で `.then()` が使えない
- `.then()` の中で `return` した値が、次の `.then()` の引数になる
- 関数レベルの `return`（呼び出し元にPromiseを返す）と `.then()` 内の `return`（次の.then()に値を渡す）は役割が違う

### 2. アロー関数

```javascript
// 通常の関数
function(response) { return response.json(); }

// アロー関数（同じ意味）
response => response.json()              // 1行の場合（returnが自動）
response => { return response.json(); }  // 複数行の場合（return必要）
() => window.location.reload()           // 引数なしの場合
```

- `.then()` には関数を渡す必要がある
- `.then(someFunction())` → 即実行される（NG）
- `.then(() => someFunction())` → Promiseが解決されてから実行される（OK）

### 3. DRY原則の適用パターン

- 条件分岐の中で違う部分だけを変数に組み立てる
- 共通部分は分岐の外に1回だけ書く
- api.jsのoptions組み立て、app.jsのurl/method分岐で同じパターンを適用

### 4. fetchWithAuth導入後の注意点

- fetchWithAuthが返すのはJSONパース済みのデータ（responseオブジェクトではない）
- 呼び出し元で `response.ok` や `response.json()` は使えない
- DELETEなどbodyなしのレスポンスは `response.json()` でエラーになる → ステータスコードで分岐が必要

### 5. ddl-autoの制限

- `update` モードではテーブル・カラム・制約の追加のみ行われる
- エンティティから制約を削除してもDB側には反映されない
- DB側の制約削除は手動で `ALTER TABLE ... DROP CONSTRAINT ...` を実行する必要がある

---

## 完了したタスク

### WBS 1.6.6 API連携（fetch処理）の共通化（完了）
- [x] api.js 作成（共通fetch関数）
- [x] 認証ヘッダー自動付与（Authorization: Bearer {token}）
- [x] エラーハンドリング（401 Unauthorized → ログイン画面へ）
- [x] 204 No Contentへの対応
- [x] app.js で api.js を使用するよう修正
- [x] category.js で api.js を使用するよう修正
- [x] 動作確認（Todo CRUD、カテゴリー CRUD）

### バグ修正
- [x] TodoService.updateでカテゴリーが反映されない問題を修正
- [x] TodoControllerのDELETEレスポンスを204に変更
- [x] Categoryのユニーク制約を論理削除対応に修正

---

## 現在のファイル構成

```
src/main/resources/static/
├── index.html          # Todo一覧画面（完成）
├── login.html          # ログイン画面（完成）
├── signup.html         # ユーザー登録画面（完成）
├── category.html       # カテゴリー管理画面（完成）
├── css/
│   └── app.css         # 未作成
└── js/
    ├── api.js          # 共通fetch関数（完成）★New
    ├── app.js          # Todo一覧のJS（api.js使用に修正済み）
    ├── login.js        # ログインのJS
    ├── signup.js       # 登録のJS
    └── category.js     # カテゴリー管理のJS（api.js使用に修正済み）
```

---

## 次回のタスク

残りのフロントエンドタスク：
- 1.6.7 レスポンシブ対応
- CSS作成（各画面のデザイン）

---

**作成日**: 2026-02-12
