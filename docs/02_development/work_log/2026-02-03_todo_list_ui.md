# 作業ログ: 2026-02-03 (Todo一覧画面の実装)

## 学習情報

- **日付**: 2026-02-03
- **学習フェーズ**: WBS 1.6.3（Todo一覧画面）
- **ステータス**: 作業中

---

## 実施内容

### 1. app.js のレビューと修正

**場所**: `src/main/resources/static/js/app.js`

**修正した問題**:

1. **括弧の対応エラー**
   - forEachの閉じ括弧とfetchチェーンの.catch()の位置がずれていた
   - 余分な`})`があった

2. **APIレスポンス形式の対応**
   - レスポンスが `{ todos: [...] }` 形式だったが、`data`を直接配列として扱っていた
   - `data.todos` でアクセスするように修正

3. **タイポ修正**
   - `data.todo.forEach` → `data.todos.forEach`（sが抜けていた）

### 2. SecurityConfigの修正

**場所**: `src/main/java/com/example/todoapp/config/SecurityConfig.java`

**問題**: index.htmlにアクセスすると403エラー

**原因**: `index.html` が `permitAll()` に含まれていなかった

**解決**: `.requestMatchers("/index.html").permitAll()` を追加

### 3. 静的ファイルのキャッシュ問題

**問題**: app.jsを修正してもブラウザに反映されない

**原因**:
- ブラウザのキャッシュ
- Spring Bootの静的ファイルキャッシュ

**解決方法**:
1. ブラウザ: Disable cache + ハードリロード
2. Spring Boot: アプリケーションの再起動

**学び**: 静的ファイル変更後に反映されない場合は、両方のキャッシュを疑う

### 4. 削除ボタンのイベントリスナー実装

**実装内容**:
- 確認ダイアログ（`confirm()`）
- `DELETE /api/todos/{id}` の呼び出し
- 成功時に `tr.remove()` で行を削除
- エラーハンドリング（`.catch()`）

**学んだこと: 非同期処理の扱い**

```javascript
// 悪い例（fetchの完了を待たない）
fetch('/api/todos/' + todo.id, { method: 'DELETE' })
tr.remove()  // APIが失敗しても実行される

// 良い例（fetchの完了を待つ）
fetch('/api/todos/' + todo.id, { method: 'DELETE' })
.then(response => {
    if (response.ok) {
        tr.remove();
    }
})
```

---

## 学んだこと

### 1. JavaScriptの非同期処理

- `fetch()` は非同期。`.then()` で完了を待つ必要がある
- APIの成功/失敗に応じて処理を分岐する

### 2. イベントリスナーの追加

```javascript
element.addEventListener('click', function() {
    // クリック時の処理
});
```

### 3. DOMの操作

- `document.createElement('tag')` - 要素を作成
- `element.textContent = 'text'` - テキストを設定（XSS対策）
- `parent.appendChild(child)` - 子要素を追加
- `element.remove()` - 要素を削除

### 4. デバッグ方法

- `console.log()` で処理の通過を確認
- 開発者ツールのSourcesタブで実際に読み込まれているコードを確認
- Networkタブでリクエスト/レスポンスを確認

---

## 完了したタスク

### WBS 1.6.3 Todo一覧画面
- [x] 認証チェック（JWTがなければログイン画面へ）
- [x] Todo一覧取得（POST /api/todos/search）
- [x] Todo一覧表示（テーブル形式）
- [x] 削除ボタン（確認ダイアログ、API呼び出し、行削除）
- [ ] 完了チェックボックス ← 次回
- [ ] フィルタUI
- [ ] ソートUI

---

## 次回のタスク

### 完了チェックボックスの実装

実装場所: app.jsの `tdPriority` の後、`tdActions` の前

必要な処理:
1. checkboxを作成
2. `todo.isCompleted` の値でchecked状態を設定
3. changeイベントで `PUT /api/todos/{id}` を呼ぶ
4. bodyには `title`（必須）と `isCompleted` を含める
5. 失敗時はチェック状態を元に戻す

---

## 現在のファイル構成

```
src/main/resources/static/
├── index.html          # Todo一覧画面（完成）
├── login.html          # ログイン画面（完成）
├── signup.html         # ユーザー登録画面（完成）
├── css/
│   └── app.css         # 未作成
└── js/
    ├── app.js          # Todo一覧のJS（作業中）
    ├── login.js        # ログインのJS（完成）
    └── signup.js       # 登録のJS（完成）
```

---

**作成日**: 2026-02-03
