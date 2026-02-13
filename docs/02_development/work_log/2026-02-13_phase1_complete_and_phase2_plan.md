# 作業ログ: 2026-02-13 (Phase 1 完了 & Phase 2 計画)

## 学習情報

- **日付**: 2026-02-13
- **学習フェーズ**: WBS 1.7〜1.8 完了、Phase 2 計画策定
- **ステータス**: Phase 1 完了、Phase 2 開始準備中

---

## 実施内容

### 1. バグ修正（2/12の残り全件完了）

| # | 問題 | ステータス | 修正内容 |
|---|------|-----------|----------|
| 2 | 削除したカテゴリがTodo一覧に表示される | **修正済み** | TodoRepositoryに`findByUserAndCategory`メソッドを追加。CategoryService.deleteで該当Todoのcategoryをnullにしてから`saveAll()`で一括保存 |
| 3 | index.htmlにカテゴリー管理画面へのリンクが無い | **修正済み** | index.htmlに`<a href="category.html">カテゴリー管理画面へ</a>`を追加 |
| 4 | index.htmlにログアウトボタンが無い | **修正済み** | index.htmlに`<button id="logout" type="button">Logout</button>`を追加。app.jsにクリックイベントで`localStorage.removeItem("accessToken")`→ログイン画面へリダイレクト |
| 5 | カテゴリー管理画面で色登録時にカラーパレットが見えない | **修正済み** | category.htmlの色inputを`type="color"`に変更。app.cssに`input[type="color"]`のスタイル追加（width/height/padding調整） |

### 2. リグレッションテスト実施（合格）

全シナリオ通しで実行し、問題なし:
- シナリオA: 基本操作（登録→ログイン→カテゴリー作成→Todo CRUD→ログアウト）
- シナリオB: 異常系（空タイトル、間違ったパスワード）

### 3. README.md 作成（WBS 1.8 完了）

プロジェクトルートにREADME.mdを作成:
- プロジェクト概要、機能一覧、技術スタック、セットアップ手順、使い方

### 4. Phase 1 完了

WBS 1.0〜1.8 の全タスクが完了。

### 5. Phase 2 計画策定

Phase 1の品質向上を優先する方針に決定（新機能追加より先）。

---

## Phase 2 計画: Phase 1 品質向上

### 2.1 エラーハンドリングの改善（バックエンド）

| # | タスク | 内容 |
|---|--------|------|
| 1 | グローバル例外ハンドラー作成 | `@RestControllerAdvice`で例外処理を一元化 |
| 2 | ユーザー登録APIのエラーハンドリング | 重複メール→409、バリデーションエラー→400 |
| 3 | ログインAPIのエラーハンドリング | 認証失敗→401 |
| 4 | JWT認証フィルターのエラーハンドリング | トークン無効・期限切れ→適切なエラーレスポンス |

### 2.2 Todo APIの改善（バックエンド）

| # | タスク | 内容 |
|---|--------|------|
| 5 | PATCH用DTO作成 | 部分更新に対応する専用DTO |
| 6 | category_idのnull許可 | カテゴリー解除対応 |

### 2.3 フィルタ・ソートUI（フロントエンド）

| # | タスク | 内容 |
|---|--------|------|
| 7 | フィルタUI作成 | 完了/未完了、カテゴリー、優先度で絞り込み |
| 8 | ソートUI作成 | 期限日、作成日で並び替え |

**進める順番**: 2.1 → 2.2 → 2.3

---

## 次回の再開ポイント

**タスク 2.1-1「グローバル例外ハンドラーの作成」に着手する。**

指示内容（以下の順で進める）:

### ステップ1: カスタム例外クラスの作成

`src/main/java/com/example/todoapp/exception/` ディレクトリを作成し、以下の2クラスを作る:

1. **`BadRequestException.java`**
   - `RuntimeException`を継承
   - コンストラクタでString（エラーメッセージ）を受け取り、`super(message)`で親に渡す

2. **`UnauthorizedException.java`**
   - 同じ構造

### ステップ2: グローバル例外ハンドラーの作成

`src/main/java/com/example/todoapp/exception/GlobalExceptionHandler.java` を作成:

- クラスに `@RestControllerAdvice` を付ける
- `BadRequestException` をキャッチするメソッド:
  - `@ExceptionHandler(BadRequestException.class)` を付ける
  - `@ResponseStatus(HttpStatus.BAD_REQUEST)` を付ける（400を返す）
  - 戻り値は `Map<String, String>` で、キー `"error"` にエラーメッセージを入れて返す
- `UnauthorizedException` 用も同様（ステータスは `HttpStatus.UNAUTHORIZED`、401）

### ステップ3（ステップ1・2完了後）

既存コードの `throw new RuntimeException(...)` を新しい例外クラスに置き換えていく。

---

## 学んだこと

### 1. buttonのtype属性

| type | 意味 | 使う場面 |
|------|------|----------|
| `submit` | フォームを送信する | `<form>`の中の送信ボタン |
| `button` | 何もしない（JSで処理を書く） | ログアウト、編集、削除など |

### 2. addEventListener のイベント名

- `submit` → フォーム用
- `click` → ボタン用

### 3. input[type="color"] のCSS

- 通常のinputスタイル（width: 100%, padding: 8px）がそのまま適用されるとカラーピッカーのプレビューが潰れる
- `input[type="color"]` セレクタで個別にwidth/height/paddingを指定する必要がある

### 4. リグレッションテスト

- バグ修正後に「他の機能が壊れていないか」を確認するテスト
- 回帰テストとも呼ばれる

---

**作成日**: 2026-02-13
