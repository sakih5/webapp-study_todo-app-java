# 作業ログ: 2026-02-02 (フロントエンド開発開始・ログイン画面)

## 学習情報

- **日付**: 2026-02-02
- **学習フェーズ**: WBS 1.6.1（ログイン画面）
- **ステータス**: 作業中

---

## 実施内容

### 1. login.html 作成・レビュー

ログイン画面のHTMLを作成した。

**場所**: `src/main/resources/static/login.html`

**構成**:
- メールアドレス入力欄（type="email"、required）
- パスワード入力欄（type="password"、required）
- ログインボタン
- エラーメッセージ表示エリア
- ユーザー登録リンク

**レビューで修正した点**:
- viewportメタタグの引用符がおかしかった（`initial-scale=1″` → `initial-scale=1"`）
- `form`タグから`action`と`method`属性を削除（fetchでJSONリクエストするため）
- `form`タグに`id="login-form"`を追加

### 2. fetchとは何か - 学習

JavaScriptからHTTPリクエストを送るための機能。

**ポイント**:
- 通常のフォーム送信：ページ全体が遷移
- fetch：ページはそのまま、サーバーとJSONでやり取り

**curlとの対応**:
```bash
# curl
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

```javascript
// fetch（同じことをJavaScriptで）
fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({email: 'test@example.com', password: 'password123'})
})
```

### 3. login.js 作成（途中）

**場所**: `src/main/resources/static/js/login.js`

**実装済み**:
- フォーム要素の取得（`document.getElementById`）
- submitイベントのリッスン（`addEventListener`）
- デフォルト送信のキャンセル（`event.preventDefault()`）
- 入力値の取得
- fetchでPOSTリクエスト送信
- レスポンスのokチェックとJSONパース
- `console.log(data)` でレスポンス確認

**未実装**:
- localStorageへのトークン保存
- Todo一覧画面へのリダイレクト
- エラーメッセージの表示

```javascript
// 現在の状態（29-35行目が未完成）
.then(data => {
    // JSONデータの処理
    console.log(data);
    // TODO: localStorage.setItem('accessToken', data.accessToken);
    // TODO: window.location.href = '/index.html';
})
.catch(error => {
    // エラー処理
    // TODO: エラーメッセージを表示
});
```

### 4. SecurityConfig 修正

静的ファイルへのアクセスを許可する設定を追加した。

**場所**: `src/main/java/com/example/todoapp/config/SecurityConfig.java`

**追加した設定**:
```java
.requestMatchers("/login.html").permitAll()
.requestMatchers("/signup.html").permitAll()
.requestMatchers("/css/**").permitAll()
.requestMatchers("/js/**").permitAll()
```

**理由**:
- `.anyRequest().authenticated()` により、認証していないユーザーは全てのリクエストが拒否されていた
- 静的ファイル（HTML、CSS、JS）は認証なしでアクセスできる必要がある

### 5. 「Request header is too large」エラー対応

**症状**: ブラウザで `http://localhost:8080/login.html` にアクセスすると400 Bad Requestになる

**エラーメッセージ**:
```
java.lang.IllegalArgumentException: Request header is too large
```

**原因**: ブラウザのCookieが大量に溜まっていた（localhost用）

**解決方法**:
1. 開発者ツール（F12）を開く
2. 「Application」タブ → 「Storage」→「Cookies」→ `http://localhost:8080`
3. 右クリックして「Clear」
4. ブラウザを完全に閉じて再度開く

---

## 学んだこと

### 1. fetchの基本構文

```javascript
fetch('URL', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({ キー: 値 })
})
.then(response => {
    if (!response.ok) {
        throw new Error('エラー');
    }
    return response.json();
})
.then(data => {
    // データを使った処理
})
.catch(error => {
    // エラー処理
});
```

### 2. event.preventDefault()

フォームのsubmitイベントで呼ぶと、通常のフォーム送信（ページ遷移）をキャンセルできる。

### 3. ブラウザの開発者ツール

- **Console**タブ: `console.log()` の出力を確認
- **Network**タブ: 実際のHTTPリクエスト/レスポンスを確認
- **Application**タブ: Cookie、localStorageの確認・削除

### 4. Spring Securityの静的ファイル許可

静的ファイルへのアクセスを許可するには、`requestMatchers()` でパスを指定して `permitAll()` する。

---

## 修正したエラー

| 問題 | 原因 | 修正 |
|------|------|------|
| viewportの引用符 | 全角引用符が混入 | 半角に修正 |
| フォーム送信でページ遷移 | action/method属性があった | 属性を削除、fetchを使用 |
| 400 Bad Request | Spring Securityで静的ファイルがブロック | requestMatchers追加 |
| Request header is too large | Cookieが大量に溜まっていた | ブラウザのCookieをクリア |

---

## 完了したタスク

- [x] login.html 作成
- [x] login.html レビュー・修正
- [x] fetchの基本を理解
- [x] login.js 基本構造作成
- [x] SecurityConfig 静的ファイル許可設定
- [x] ブラウザでlogin.htmlが表示されることを確認

## 次回のタスク（再開時の作業）

### login.js の完成

1. **localStorageへのトークン保存**
   ```javascript
   .then(data => {
       localStorage.setItem('accessToken', data.accessToken);
       // ...
   })
   ```

2. **Todo一覧画面へのリダイレクト**
   ```javascript
   window.location.href = '/index.html';
   ```

3. **エラーメッセージの表示**
   ```javascript
   .catch(error => {
       const errorDiv = document.getElementById('error-message');
       errorDiv.textContent = 'ログインに失敗しました';
       errorDiv.hidden = false;
   });
   ```

4. **動作確認**
   - 正しいメール・パスワードでログイン成功を確認
   - 間違ったパスワードでエラー表示を確認
   - ログイン後、localStorageにトークンが保存されていることを確認

### その後の作業（WBS 1.6.1の残り）

- [ ] css/login.css 作成（デザイン調整）
- [ ] エラーハンドリング強化（認証失敗時のメッセージ表示）

---

## ファイル構成

```
src/main/resources/static/
├── login.html          # ログイン画面（作成済み）
├── css/
│   └── login.css       # 未作成
└── js/
    └── login.js        # 作成中（基本構造のみ）
```

---

## 備考

- 開発時はブラウザの開発者ツールを活用する
- Cookieが溜まるとエラーになることがあるので、問題が起きたらCookieクリアを試す
- fetchの処理は非同期なので、`.then()` でチェーンして処理を繋げる

---

**作成日**: 2026-02-02
