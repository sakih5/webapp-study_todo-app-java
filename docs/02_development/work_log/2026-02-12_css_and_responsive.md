# 作業ログ: 2026-02-12 (CSS作成・レスポンシブ対応)

## 学習情報

- **日付**: 2026-02-12
- **学習フェーズ**: WBS 1.6 フロントエンド（CSS作成 + 1.6.7 レスポンシブ対応）完了
- **ステータス**: 完了

---

## 実施内容

### 1. app.css の作成

**場所**: `src/main/resources/static/css/app.css`
**適用画面**: index.html（Todo一覧）、category.html（カテゴリー管理）

**スタイル構成**:
- ページ全体: フォント指定、max-width: 800px、中央寄せ
- テーブル: 幅100%、border-collapse、ヘッダー背景色（#3e677a）、白文字
- フォーム: ラベルを太字・ブロック表示、input/selectに枠線・角丸・padding
- ボタン: 保存ボタン（紺 #0b406b）、編集ボタン（グレー #c5c5c5）、削除ボタン（赤 #f44336）
- フォームとテーブルの間に余白（margin-bottom）

### 2. login.css の作成

**場所**: `src/main/resources/static/css/login.css`
**適用画面**: login.html（ログイン）、signup.html（ユーザー登録）

**スタイル構成**:
- ページ全体: max-width: 400px（ログインフォーム用にコンパクト）
- フォーム: app.cssと共通のinput/selectスタイル
- リンク: 青色（#2196F3）、hover時に下線表示
- エラーメッセージ: 赤文字・薄い赤背景・赤枠線

### 3. レスポンシブ対応（WBS 1.6.7）

**app.css のメディアクエリ** (`@media (max-width: 768px)`):
- テーブルのフォントサイズを14pxに縮小
- `.table-wrapper` に `overflow-x: auto` で横スクロール対応
- ボタンを `width: 100%` でタップしやすく

**login.css のメディアクエリ** (`@media (max-width: 768px)`):
- bodyのpaddingを16pxに拡大（画面端との余裕）
- ボタンを `width: 100%` でタップしやすく

**HTML修正**:
- index.html、category.html のテーブルを `<div class="table-wrapper">` で囲む

### 4. 編集ボタンのスタイル追加

- app.js で `editBtn.classList.add('edit-btn')` を追加
- category.js でも同様に追加
- app.css で `.edit-btn` のスタイルを定義（グレー背景）

---

## 学んだこと

### 1. CSSの基本

- セレクタの種類: タグ（`body`）、クラス（`.error`）、ID（`#todo-form`）、子孫（`table th`）
- `margin` は外側の余白、`padding` は内側の余白
- `box-sizing: border-box` でwidth + paddingがはみ出ない
- `border-collapse: collapse` でテーブルのセル境界線を1本にまとめる

### 2. 擬似クラス

- `a:hover` はマウスを乗せたときだけ適用されるスタイル
- `:hover` 以外にも `:focus`、`:active` などがある

### 3. メディアクエリ

- `@media (max-width: 768px) { ... }` で画面幅768px以下のときだけスタイルを適用
- 768pxはスマホ・小さいタブレットの一般的なブレークポイント
- Chrome DevToolsのデバイスモード（Ctrl + Shift + M）で確認できる

### 4. レスポンシブ対応のポイント

- テーブルの横スクロール: 親要素に `overflow-x: auto` を設定
- スマホではボタンを `width: 100%` にするとタップしやすい
- `<meta name="viewport" content="width=device-width,initial-scale=1">` がHTMLに必要（既に設定済みだった）

### 5. JavaScriptからCSSクラスを追加する方法

- `element.classList.add('クラス名')` でHTMLの要素にクラスを追加
- CSSでそのクラスのスタイルを定義すれば適用される

---

## 完了したタスク

### CSS作成（完了）
- [x] app.css 作成（Todo一覧・カテゴリー管理画面用）
- [x] login.css 作成（ログイン・ユーザー登録画面用）
- [x] ラベル・フォーム・テーブル・ボタンのスタイル設定
- [x] 編集ボタン・削除ボタンのスタイル設定
- [x] エラーメッセージのスタイル設定

### WBS 1.6.7 レスポンシブ対応（完了）
- [x] メディアクエリ設定（スマホ: 〜768px）
- [x] テーブルの横スクロール対応（table-wrapper + overflow-x: auto）
- [x] ボタンのサイズ調整（width: 100%、padding拡大）
- [x] スマホ表示での余白調整
- [x] Chrome DevToolsのデバイスモードで動作確認

---

## 現在のファイル構成

```
src/main/resources/static/
├── index.html          # Todo一覧画面（完成）
├── login.html          # ログイン画面（完成）
├── signup.html         # ユーザー登録画面（完成）
├── category.html       # カテゴリー管理画面（完成）
├── css/
│   ├── app.css         # Todo・カテゴリー画面用CSS（完成）★New
│   └── login.css       # ログイン・登録画面用CSS（完成）★New
└── js/
    ├── api.js          # 共通fetch関数（完成）
    ├── app.js          # Todo一覧のJS（完成）
    ├── login.js        # ログインのJS（完成）
    ├── signup.js       # 登録のJS（完成）
    └── category.js     # カテゴリー管理のJS（完成）
```

---

## 次回のタスク

WBS 1.6（フロントエンド）が全て完了。次のフェーズへ：
- 1.7 テスト・デバッグ
- 1.8 ドキュメント

---

**作成日**: 2026-02-12
