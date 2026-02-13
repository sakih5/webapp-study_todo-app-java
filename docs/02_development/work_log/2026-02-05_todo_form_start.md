# 作業ログ: 2026-02-05 (チェックボックス完成・Todo作成フォーム実装)

## 学習情報

- **日付**: 2026-02-05
- **学習フェーズ**: WBS 1.6.3（Todo一覧画面）完了 → WBS 1.6.4（Todo作成・編集フォーム）作業中
- **ステータス**: 作業中

---

## 実施内容

### 1. 完了チェックボックスの実装・修正（午前）

**場所**: `src/main/resources/static/js/app.js`

**実装した機能**:
- チェックボックスの作成（`input type="checkbox"`）
- `todo.isCompleted` で初期状態を反映
- changeイベントで `PATCH /api/todos/{id}` を呼び出し
- エラー時にチェック状態を巻き戻す処理

**修正した問題**:

1. **チェックボックスが表示されない**
   - 原因: `tdCompleted` を作成したが、`tr.appendChild(tdCompleted)` を書き忘れていた
   - 学び: DOM要素は作っただけでは表示されない。`appendChild` で親要素に追加する必要がある

2. **チェックボックス操作で403エラー**
   - 原因: app.jsでは `PATCH` メソッドを使用、Controller側は `@PutMapping` だった
   - Spring Bootのコンソールログで確認: `HttpRequestMethodNotSupportedException: Request method 'PATCH' is not supported`
   - 解決: Controller側を `@PatchMapping` に変更

3. **PATCHリクエストでバリデーションエラー**
   - 原因: `TodoRequest` の `title` に `@NotBlank` があるが、リクエストボディに `{isCompleted: true}` しか送っていなかった
   - 解決（暫定）: リクエストボディに `title: todo.title` も含めるように修正
   - 将来対応: PATCH用DTOを別途作成する（TODO_backlog.md #1に記録済み）

### 2. TODO バックログ作成（午前）

**場所**: `docs/02_development/TODO_backlog.md`

WBSに散在していた「後で対応」の課題を1つのファイルに集約した。

### 3. Todo作成フォームのバグ修正と完成（午後）

**場所**: `src/main/resources/static/js/app.js`, `src/main/resources/static/index.html`

**修正した問題**:

1. **UUIDデシリアライズエラー（Todo保存失敗）**
   - エラー: `HttpMessageNotReadableException: Cannot deserialize value of type java.util.UUID from Object value (token JsonToken.START_OBJECT)`
   - 原因: categoryIdにUUID文字列ではなくオブジェクトが送られていた
   - 解決: categoryIdの値を正しくセレクトボックスの `.value`（UUID文字列）で取得するように修正
   - 調査方法: ブラウザのNetworkタブでリクエストボディを確認

2. **カテゴリーセレクトボックスが選択できない（「読み込み中」のまま）**
   - 原因: `<select>` に `disabled` 属性が付いたままだった
   - 解決: カテゴリー取得完了後に `select.disabled = false` を追加
   - 調査方法: `console.log` でDOM要素を出力して `disabled` 属性を発見

3. **「読み込み中...」のオプションが残る**
   - 解決: `<option>` に `id="loading-option"` を付けて、`loadingOption.remove()` で削除
   - 追加: 「選択してください」プレースホルダーを動的に追加（`selected`, `disabled`, `value=""` の組み合わせ）

4. **カテゴリー名の表示がおかしい（一覧テーブル）**
   - 原因: `todo.category` はオブジェクトなのに、そのまま `textContent` に設定していた（`[object Object]` と表示）
   - 解決: `todo.category?.name ?? ''` に修正（オプショナルチェーニング使用）
   - 注意: `todo.category` が `null` の場合、`todo.category.name` だと `TypeError` でforEach全体が止まる

---

## 学んだこと

### 1. DOM要素の表示には appendChild が必要

```javascript
// 要素を作っただけでは表示されない
const td = document.createElement('td');

// 親要素に追加して初めて表示される
tr.appendChild(td);
```

### 2. HTTPメソッドの一致

- フロントエンド（fetch）とバックエンド（Controller）で同じHTTPメソッドを使う必要がある
- 不一致の場合、Spring Bootコンソールに `HttpRequestMethodNotSupportedException` が出る

### 3. GETリクエストにはbodyを付けられない

- GETメソッドはリクエストボディを持てない
- パラメータを送りたい場合はクエリパラメータ（`?key=value`）を使う

### 4. 固定値 vs 動的取得の判断基準

- ユーザーが追加・変更するデータ（カテゴリーなど）→ APIから動的取得
- アプリ固有の固定値（優先度1〜5など）→ HTMLにベタ打ち

### 5. 変数名の衝突に注意（シャドウイング）

- forEachのループ変数名と、外側のDOM要素の変数名がかぶるとバグの原因になる
- 内側の変数が優先され、外側の変数にアクセスできなくなる

### 6. エラーメッセージの読み方: 型の不一致

- `Cannot deserialize value of type java.util.UUID from Object value` → UUID文字列が来るべき場所にオブジェクトが来ている
- Networkタブでリクエストボディを確認するのが有効

### 7. console.logデバッグ

- エラーが出ないのに動かない場合は、`console.log` で処理の通過確認が有効
- DOM要素を `console.log` すると、属性（disabled等）も確認できる

### 8. オプショナルチェーニング `?.`

```javascript
// todo.category が null の場合、エラーにならず undefined を返す
todo.category?.name ?? ''
```

- `null` の可能性があるオブジェクトのプロパティに安全にアクセスする方法
- `??`（Null合体演算子）と組み合わせてデフォルト値を設定できる

### 9. selectのプレースホルダー

- `<option selected disabled value="">` の3属性の組み合わせで「初期表示だけど選び直せない」プレースホルダーを作れる

---

## 完了したタスク

### WBS 1.6.3 Todo一覧画面（完了）
- [x] 認証チェック
- [x] Todo一覧取得・表示
- [x] 削除ボタン
- [x] 完了チェックボックス

### WBS 1.6.4 Todo作成・編集フォーム（作業中）
- [x] HTMLフォーム作成
- [x] 優先度セレクトボックス（ベタ打ち）
- [x] フォーム送信処理（app.js）
- [x] カテゴリー動的取得の修正（変数名衝突、appendChild対象、GETのbody削除）
- [x] カテゴリーセレクトボックスのdisabled解除・プレースホルダー設定
- [x] リクエストボディのキー名修正（categoryId）
- [x] カテゴリー表示のnull対策（オプショナルチェーニング）
- [x] 動作確認（新規作成）★完了
- [ ] 編集機能（編集ボタン追加、フォームへの読み込み、PATCH送信）
- [ ] バリデーション（タイトル必須チェック）

---

## 次回のタスク（再開時の作業）

### Todo編集機能の実装

1. **編集ボタンの追加**
   - 一覧テーブルの各行に「編集」ボタンを追加（削除ボタンの隣）

2. **フォームへのデータ読み込み**
   - 編集ボタンクリックで、そのTodoの情報をフォームの各入力欄にセットする
   - `document.getElementById('title').value = todo.title` など

3. **作成モード・編集モードの切り替え**
   - 編集中のTodoのIDをどこかに保持する（変数 or data-*属性）
   - submit時にIDの有無で POST（作成）と PATCH（更新）を切り替える

---

**作成日**: 2026-02-05
