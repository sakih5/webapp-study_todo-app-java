# 作業ログ: 2026-02-03 (ログイン画面完成・ユーザー登録画面作成)

## 学習情報

- **日付**: 2026-02-03
- **学習フェーズ**: WBS 1.6.1（ログイン画面）完了、WBS 1.6.2（ユーザー登録画面）作成中
- **ステータス**: 作業中

---

## 実施内容

### 1. login.js 完成

前回途中だったlogin.jsを完成させた。

**場所**: `src/main/resources/static/js/login.js`

**追加した処理**:
- localStorageへのトークン保存
- Todo一覧画面（index.html）へのリダイレクト
- エラーメッセージの表示

```javascript
.then(data => {
    // トークンをlocalStorageに保存
    localStorage.setItem('accessToken', data.accessToken);
    // 別ページへ移動
    window.location.href = '/index.html';
})
.catch(error => {
    const errorDiv = document.getElementById('error-message');
    errorDiv.textContent = 'ログインに失敗しました。メールアドレスまたはパスワードを確認してください。';
    errorDiv.hidden = false;
});
```

**動作確認結果**:
- 正しいメール・パスワード → index.htmlへ遷移（成功）
- 間違ったパスワード → エラーメッセージ表示（成功）
- localStorageにaccessTokenが保存されていることを確認（成功）

### 2. signup.html 作成

**場所**: `src/main/resources/static/signup.html`

**構成**:
- メールアドレス入力欄
- パスワード入力欄
- パスワード確認入力欄
- 登録ボタン
- エラーメッセージ表示エリア
- ログインページへのリンク

### 3. signup.js 作成

**場所**: `src/main/resources/static/js/signup.js`

**処理の流れ**:
1. フォームのsubmitイベントをキャッチ
2. パスワードとパスワード確認の一致チェック
3. 一致しない場合はエラー表示して `return` で終了
4. fetchで `POST /api/auth/register` にリクエスト
5. 成功したら `/login.html` へリダイレクト
6. 失敗したらエラーメッセージを表示

**レビューで修正した点**:
- パスワード不一致時に `return;` を追加（そのままfetchが実行されるバグを修正）
- リンク先を `/login` → `/login.html` に修正

---

## 学んだこと

### 1. `<button type="submit">` の type 属性

| type | 動作 |
|------|------|
| `submit` | フォームを送信する（デフォルト） |
| `button` | 何もしない（JSで自分で処理を書く用） |
| `reset` | フォームの入力をリセットする |

### 2. `<form>` タグの役割

- ユーザーの入力をまとめてサーバーに送信するための「入れ物」
- 昔ながらの方法: `action` と `method` 属性でページ遷移
- 今回の方法: `event.preventDefault()` で通常送信をキャンセルし、fetchでAPIを呼ぶ

**`<form>` を使うメリット**:
- Enterキーで送信できる
- `required` 属性で空欄チェック（ブラウザが自動）
- `type="email"` でメール形式チェック（ブラウザが自動）

### 3. 条件分岐後の `return`

条件に合致したときに処理を終了したい場合は `return;` を書く必要がある。
書かないと、後続の処理がそのまま実行されてしまう。

```javascript
if (password !== password_confirm) {
    // エラー表示
    return;  // ← これがないと下のfetchが実行される
}
fetch(...);
```

### 4. localStorageの確認方法

- 開発者ツール > Application > Local Storage
- または、Consoleで `localStorage.getItem('accessToken')` を実行

---

## トラブルシューティング

| 問題 | 原因 | 解決方法 |
|------|------|----------|
| HTTP Status 400 Bad Request | ブラウザのCookieが溜まっている | 開発者ツール > Application > Cookies > Clear |
| 「ローカルストレージは検出されませんでした」 | 404ページで確認しようとした | login.htmlに戻ってから確認 |

---

## 完了したタスク

### WBS 1.6.1 ログイン画面（完了）
- [x] login.html 作成
- [x] login.js 作成（トークン保存、リダイレクト、エラー表示）
- [x] 動作確認（成功・失敗両方）
- [x] localStorageにトークンが保存されることを確認

### WBS 1.6.2 ユーザー登録画面（完了）
- [x] signup.html 作成
- [x] signup.js 作成
- [x] パスワード一致チェック実装
- [x] 動作確認（3つのテストケースすべて成功）

---

## トラブルシューティング（追記）

### ユーザー登録APIが403を返す問題

**症状**: `/api/auth/register` にPOSTすると403 Forbiddenが返る

**調査過程**:
1. SecurityConfigの設定は正しい（`/api/auth/**` は `permitAll()`）
2. JwtAuthenticationFilterも問題なし
3. curlで `/api/auth/login` は200、`/api/auth/register` は403
4. **Spring Bootのコンソールログで真の原因を発見**

**原因**:
```
ERROR: null value in column "created_at" of relation "users" violates not-null constraint
```
DBのusersテーブルに `created_at` カラム（NOT NULL）があるが、Userエンティティにフィールドがなかった。
INSERT文に `created_at` が含まれず、NOT NULL制約違反でエラー → 403として返された。

**解決方法**:
DBから `created_at` と `updated_at` カラムを削除した。

```sql
ALTER TABLE users DROP COLUMN created_at;
ALTER TABLE users DROP COLUMN updated_at;
```

**学び**:
- ブラウザのエラーだけでなく、**Spring Bootのコンソールログ**を確認することで真の原因が分かる
- 403はSpring Securityのエラーだけでなく、サーバー内部エラーの場合もある

---

## 次回のタスク

WBS 1.6.3 Todo一覧画面の作成に進む

- index.html の作成
- app.js の作成（Todo一覧取得・表示）
- 認証チェック（JWTがなければログイン画面へリダイレクト）

---

## ファイル構成

```
src/main/resources/static/
├── login.html          # 完成
├── signup.html         # 完成（動作確認待ち）
├── css/
│   └── login.css       # 未作成
└── js/
    ├── login.js        # 完成
    └── signup.js       # 完成（動作確認待ち）
```

---

**作成日**: 2026-02-03
