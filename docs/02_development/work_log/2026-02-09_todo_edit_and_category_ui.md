# 作業ログ: 2026-02-09 (Todo編集機能完成・カテゴリー管理画面実装)

## 学習情報

- **日付**: 2026-02-09
- **学習フェーズ**: WBS 1.6.4（Todo作成・編集フォーム）完了 → WBS 1.6.5（カテゴリー管理画面）完了
- **ステータス**: 完了

---

## 実施内容

### 1. Todo編集機能の実装（WBS 1.6.4 完了）

**場所**: `src/main/resources/static/js/app.js`

**実装した機能**:
- 編集ボタンの追加（一覧テーブルの各行）
- 編集ボタンクリックでフォームにデータを読み込む
- `editingTodoId` 変数で作成モード（POST）と編集モード（PATCH）を切り替え
- タイトル必須バリデーション（HTMLの `required` 属性で対応）

**修正した問題**:

1. **編集ボタンが表示されない**
   - 原因: `tdEdit.appendChild(editBtn)` を書き忘れていた
   - 学び: 2026-02-05のチェックボックスと同じパターン。DOM要素は `appendChild` しないと表示されない

2. **編集後の保存ができない（editingTodoIdがundefined）**
   - 原因: `let editingTodoId = null;` を `forEach` のブロック内で宣言していた
   - フォームのsubmit処理（`forEach` の外）からは参照できなかった
   - 解決: ファイルのトップレベル（どのブロックにも属さない場所）に移動
   - 学び: `let` はブロックスコープ。共有する変数はトップレベルに宣言する

3. **PATCH送信時に `todo is not defined` エラー**
   - 原因: フォームsubmit処理内で `todo.id` を参照していたが、`todo` は `forEach` のブロック内の変数
   - 解決: `editingTodoId` を使うように修正（これがそのための変数）

4. **PATCH URLの `/` 抜け**
   - 原因: `'/api/todos' + editingTodoId` → `/api/todosfbc08d4f-...` になっていた
   - 解決: `'/api/todos/' + editingTodoId` に修正

### 2. カテゴリー管理画面の実装（WBS 1.6.5 完了）

**場所**: `src/main/resources/static/category.html`, `src/main/resources/static/js/category.js`

**実装した機能**:
- カテゴリー一覧表示（GET /api/categories）
- カテゴリー新規追加（POST /api/categories）
- カテゴリー編集（PUT /api/categories/{id}）
- カテゴリー削除（DELETE /api/categories/{id}）

**修正した問題**:

1. **カテゴリ取得で403エラー**
   - 最初はSecurityConfigの問題と思い `permitAll()` を追加したが、根本原因はJWTトークンの有効期限切れだった
   - 解決: 再ログインで新しいトークンを取得
   - 学び: 403 ≠ 必ずしもSecurityConfigの問題。トークン期限切れも403になる。`permitAll()` はセキュリティを無効化するだけで根本解決にならない

2. **GETリクエストにbodyを付けていた**
   - 原因: `app.js` からコピーした際に `body: JSON.stringify({})` が残っていた
   - 解決: `body` の行を削除
   - 学び: 2026-02-05と同じミス。GETにはbodyを付けられない

3. **`data.todos.forEach` で `Cannot read properties of undefined`**
   - 原因: カテゴリーAPIのレスポンスなのに `data.todos` にアクセスしていた（`app.js` からのコピペ残り）
   - 解決: レスポンス構造に合わせて修正

4. **カテゴリー更新で403エラー**
   - 原因: `editingCategoryId` のスコープ問題（app.jsと同じミス）を修正後、HTTPメソッドの不一致が判明
   - フロントが `PATCH` を送信、Controller側は `@PutMapping` だった
   - Spring Bootコンソール: `HttpRequestMethodNotSupportedException: Request method 'PATCH' is not supported`
   - 解決: Controller側を `@PatchMapping` に変更（または fetch側を `PUT` に変更）

5. **カテゴリー削除が実際には反映されない（リロードで復活）**
   - 原因: 論理削除（`deletedAt` に日時をセット）は成功していたが、一覧取得時に論理削除済みのデータも返していた
   - 解決: `CategoryRepository` に `findByUserAndDeletedAtIsNull(User user)` メソッドを追加、`CategoryService` で使用
   - 学び: Spring Data JPAのメソッド命名規則 - `IsNull` サフィックスで `WHERE ... IS NULL` を自動生成できる

### 3. SecurityConfigの理解

**学んだこと**:
- 静的ファイル（HTML/JS/CSS）を `permitAll()` にしても、APIが認証で保護されていればデータは漏れない
- HTMLファイル自体にはデータが含まれず、JavaScriptからAPIを呼ぶ時に認証ヘッダーが必要
- JS側の認証チェック（トークンがなければログイン画面へリダイレクト）も入っている

---

## 学んだこと

### 1. letのブロックスコープ（再確認）

```javascript
// NG: forEach内で宣言 → 外からは見えない
data.todos.forEach(todo => {
    let editingTodoId = null;  // このブロック内だけ有効
});
form.addEventListener('submit', function() {
    editingTodoId;  // ReferenceError!
});

// OK: トップレベルで宣言 → どこからでもアクセス可能
let editingTodoId = null;
data.todos.forEach(todo => { ... });
form.addEventListener('submit', function() {
    editingTodoId;  // OK
});
```

### 2. JWTトークンの有効期限切れ

- 403エラーの原因はSecurityConfigだけではない
- トークンの `exp` が切れていても403になる
- 調査手順: Networkタブ → Spring Bootコンソール → トークンの中身確認（jwt.io）

### 3. Spring Data JPAのメソッド命名規則

- `findByUserAndDeletedAtIsNull(User user)` → `WHERE user = ? AND deleted_at IS NULL`
- `IsNull` サフィックスで引数不要のNULLチェックができる

### 4. コピペ時の注意点

`app.js` → `category.js` のコピーで以下のミスが繰り返し発生した：
- `data.todos` → レスポンス構造に合わせて修正が必要
- `body: JSON.stringify({})` → GETリクエストではbody不要
- スコープの問題 → 変数宣言の位置を確認する
- HTTPメソッドの不一致 → Controller側の定義を確認する

**教訓**: コピペ後は、元のコードとの違いを意識して全体を見直す

---

## 完了したタスク

### WBS 1.6.4 Todo作成・編集フォーム（完了）
- [x] 編集ボタン追加
- [x] フォームへのデータ読み込み
- [x] 作成モード・編集モードの切り替え（editingTodoId）
- [x] 編集時: PATCH /api/todos/{id}
- [x] バリデーション（HTMLのrequired属性）
- [x] 動作確認

### WBS 1.6.5 カテゴリー管理画面（完了）
- [x] HTML作成（category.html）
- [x] JavaScript実装（category.js）
- [x] カテゴリー一覧表示（GET /api/categories）
- [x] カテゴリー追加（POST /api/categories）
- [x] カテゴリー編集（PUT → PATCH /api/categories/{id}）
- [x] カテゴリー削除（DELETE /api/categories/{id}）
- [x] 論理削除済みカテゴリーの非表示対応
- [x] 動作確認

---

## 現在のファイル構成

```
src/main/resources/static/
├── index.html          # Todo一覧画面（完成）
├── login.html          # ログイン画面（完成）
├── signup.html         # ユーザー登録画面（完成）
├── category.html       # カテゴリー管理画面（完成）★New
├── css/
│   └── app.css         # 未作成
└── js/
    ├── app.js          # Todo一覧のJS（完成）
    ├── login.js        # ログインのJS（完成）
    ├── signup.js       # 登録のJS（完成）
    └── category.js     # カテゴリー管理のJS（完成）★New
```

---

## 次回のタスク

残りのフロントエンドタスク：
- 1.6.6 API連携の共通化（api.js作成）
- 1.6.7 レスポンシブ対応
- CSS作成（各画面のデザイン）

---

**作成日**: 2026-02-09
