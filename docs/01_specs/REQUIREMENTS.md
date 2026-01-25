# Todoアプリ 要件定義書

## 1. プロジェクト概要

### 1.1 目的

時間管理と振り返りを重視したマルチユーザー対応のTodoアプリケーション。
日々のタスク管理だけでなく、時間の使い方を可視化し、振り返りを通じて生産性を向上させることを目指す。

### 1.2 対象ユーザー

- 日々のタスクを管理したい個人ユーザー
- 時間の使い方を分析・改善したいユーザー
- 複数のプロジェクトやカテゴリーでタスクを管理したいユーザー

## 2. 技術スタック

### 2.1 フロントエンド

- **言語**: HTML, JavaScript (Vanilla JS)
- **スタイリング**: CSS

### 2.2 バックエンド

- **フレームワーク**: Spring Boot (Java)
- **認証**: JWT (JSON Web Token) - Spring Security
- **バリデーション**: Bean Validation (Hibernate Validator)

### 2.3 データベース

- **RDBMS**: PostgreSQL
- **ORM**: JPA / Hibernate

### 2.4 その他

- **API仕様**: RESTful API
- **データ形式**: JSON

## 3. 機能要件

### Phase 1: MVP (Minimum Viable Product)

#### 3.1 ユーザー認証

- [ ] ユーザー登録（メールアドレス、パスワード）
- [ ] ログイン機能
- [ ] ログアウト機能
- [ ] パスワードのハッシュ化（bcrypt等）
- [ ] JWTトークンによる認証

#### 3.2 基本的なTodo管理

- [ ] Todo作成
  - タスク名（必須）
  - 説明（任意）
  - 期限日（任意）
  - 優先度（高/中/低）
  - カテゴリー/プロジェクト（任意）
- [ ] Todo一覧表示
  - 自分のTodoのみ表示
  - 作成日時の新しい順にデフォルト表示
- [ ] Todo詳細表示
- [ ] Todo編集
- [ ] Todo削除
- [ ] Todo完了/未完了の切り替え

#### 3.3 基本UI

- [ ] ログイン画面
- [ ] ユーザー登録画面
- [ ] Todoリスト画面
- [ ] Todo作成/編集フォーム
- [ ] レスポンシブデザイン（基本）

### Phase 2: 時間管理機能

#### 3.4 時間トラッキング

- [ ] 予定時間の設定
  - Todoに予定所要時間を設定可能
- [ ] 実績時間の手動入力
  - Todoに実際にかかった時間を記録
  - 複数の作業セッションを記録可能
- [ ] 予定時間と実績時間の比較表示

#### 3.5 フィルタリング・検索・ソート

- [ ] フィルタリング
  - 完了/未完了
  - カテゴリー別
  - 優先度別
  - 期限日（今日、今週、期限切れなど）
- [ ] ソート機能
  - 期限日順
  - 優先度順
  - 作成日順
  - 更新日順
- [ ] 検索機能
  - タスク名での検索
  - 説明文での検索

#### 3.6 基本的な可視化

- [ ] 日別の時間分布
  - 指定日にどのタスクに何時間使ったか
  - 円グラフまたは棒グラフで表示
- [ ] カテゴリー別集計
  - カテゴリーごとの時間配分を表示

### Phase 3: 高度な機能

#### 3.7 サブタスク

- [ ] Todoの中にサブタスクを作成
- [ ] サブタスクの完了/未完了管理
- [ ] サブタスクの進捗率表示

#### 3.8 タグ機能

- [ ] 複数タグの付与
- [ ] タグによるフィルタリング
- [ ] タグの作成・編集・削除

#### 3.9 繰り返しタスク

- [ ] 繰り返しパターンの設定（毎日、毎週、毎月など）
- [ ] 繰り返しタスクの自動生成
- [ ] 繰り返しタスクの編集・削除

#### 3.10 通知機能

- [ ] 期限が近づいたタスクの通知
- [ ] ブラウザ通知
- [ ] メール通知（オプション）

#### 3.11 高度な可視化・振り返り機能

- [ ] 24時間の積み上げ棒グラフ
  - 時系列順：1日の時間の流れを表示
  - カテゴリー別集計：同じカテゴリーをまとめて表示
  - 表示切り替え機能
- [ ] 週/月単位の統計
  - 週間・月間の時間使用状況
  - カテゴリー別の時間配分推移
- [ ] タスク完了率
  - 日別・週別・月別の完了率
  - 完了したタスク数の推移
- [ ] 振り返りダッシュボード
  - 期間を指定して統計を表示
  - 生産性の可視化

## 4. データモデル設計

### 4.1 User（ユーザー）

- id: UUID (Primary Key)
- email: String (Unique, Not Null)
- password_hash: String (Not Null)
- deleted_at: DateTime (Nullable)

### 4.2 Category（カテゴリー）

- id: UUID (Primary Key)
- user_id: UUID (Foreign Key -> User)
- name: String (Not Null)
- color: String (Optional, HEX色コード)
- deleted_at: DateTime (Nullable)

### 4.3 Todo（タスク）

- id: UUID (Primary Key)
- user_id: UUID (Foreign Key -> User, Not Null)
- category_id: UUID (Foreign Key -> Category, Nullable)
- title: String (Not Null)
- description: Text (Nullable)
- priority: INT (1-5)
- due_date: Date (Nullable)
- estimated_time: Integer (分単位, Nullable) ※ Phase2 で実装予定
- is_completed: Boolean (Default: False)
- deleted_at: DateTime (Nullable)
- completed_at: DateTime (Nullable) ※ Phase2 で実装予定
- parent_todo_id: UUID (Foreign Key -> Todo, Nullable) ※サブタスク用、Phase2で実装予定

### 4.4 TimeEntry（時間記録）※Phase 2

- id: UUID (Primary Key)
- todo_id: UUID (Foreign Key -> Todo)
- duration: Integer (分単位, Not Null)
- start_time: DateTime (記録した作業の開始時刻)
- end_time: DateTime (記録した作業の終了時刻)
- note: Text (Nullable)
- created_at: DateTime (記録を作成した日時)
- updated_at: DateTime

### 4.5 Tag（タグ）※Phase 3

- id: UUID (Primary Key)
- user_id: UUID (Foreign Key -> User)
- name: String (Not Null)
- created_at: DateTime

### 4.6 TodoTag（多対多の中間テーブル）※Phase 3

- todo_id: UUID (Foreign Key -> Todo)
- tag_id: UUID (Foreign Key -> Tag)
- Primary Key: (todo_id, tag_id)

## 5. API設計概要

### 5.1 認証API

- POST   /api/auth/register      # ユーザー登録
- POST   /api/auth/login         # ログイン
- POST   /api/auth/logout        # ログアウト
- GET    /api/auth/me            # 現在のユーザー情報取得

### 5.2 Todo API

- POST    /api/todos/search      # Todo一覧取得（クエリパラメータでフィルタ・ソート）
- POST   /api/todos              # Todo作成
- PATCH  /api/todos/{id}         # Todo更新
- DELETE /api/todos/{id}         # Todo削除

### 5.3 カテゴリーAPI

GET    /api/categories         # カテゴリー一覧取得
POST   /api/categories         # カテゴリー作成
PUT    /api/categories/{id}    # カテゴリー更新
DELETE /api/categories/{id}    # カテゴリー削除

### 5.4 時間記録API（Phase 2）

POST   /api/todos/{id}/time-entries        # 時間記録追加
GET    /api/todos/{id}/time-entries        # 特定Todoの時間記録一覧
PUT    /api/time-entries/{id}              # 時間記録更新
DELETE /api/time-entries/{id}              # 時間記録削除

### 5.5 統計・可視化API（Phase 2-3）

GET    /api/stats/daily        # 日別統計
GET    /api/stats/weekly       # 週別統計
GET    /api/stats/monthly      # 月別統計
GET    /api/stats/timeline     # 24時間タイムライン
GET    /api/stats/category     # カテゴリー別集計

### 5.6 タグAPI（Phase 3）

GET    /api/tags               # タグ一覧取得
POST   /api/tags               # タグ作成
DELETE /api/tags/{id}          # タグ削除

## 6. UI/UX要件

### 6.1 基本レイアウト

- ヘッダー（ロゴ、ユーザー情報、ログアウトボタン）
- サイドバーまたはナビゲーションメニュー
  - Todo一覧
  - カテゴリー管理
  - 統計・振り返り
- メインコンテンツエリア

### 6.2 Todo一覧画面

- Todoカードまたはリスト形式
- 各Todoに表示する情報
  - タスク名
  - 期限日
  - 優先度（色分け）
  - カテゴリー
  - 完了チェックボックス
- フィルター・ソート・検索バー
- 「新規Todo作成」ボタン

### 6.3 Todo作成/編集画面

- モーダルまたは専用ページ
- フォーム項目
  - タスク名（必須）
  - 説明
  - 期限日（日付ピッカー）
  - 優先度（ドロップダウンまたはボタン）
  - カテゴリー（ドロップダウン）
  - 予定時間（Phase 2）

### 6.4 可視化画面（Phase 2-3）

- グラフエリア
  - 日付選択機能
  - グラフタイプ切り替え
  - 24時間積み上げ棒グラフ
  - カテゴリー別円グラフ
- 統計サマリー
  - 合計時間
  - タスク完了数
  - カテゴリー別内訳

### 6.5 レスポンシブデザイン

- モバイル（スマートフォン）対応
- タブレット対応
- デスクトップ対応

## 7. 非機能要件

### 7.1 セキュリティ

- パスワードのハッシュ化（BCryptPasswordEncoder等）
- JWTトークンの安全な管理
- SQLインジェクション対策（ORMの適切な使用）
- XSS対策（入力のサニタイゼーション）
- CSRF対策
- HTTPS通信（本番環境）

### 7.2 パフォーマンス

- API レスポンス時間: 平均500ms以下
- ページロード時間: 3秒以内
- データベースクエリの最適化（インデックス設定）

### 7.3 可用性

- エラーハンドリング
  - 適切なエラーメッセージ表示
  - サーバーエラー時のフォールバック
- バリデーション
  - クライアントサイド・サーバーサイド両方で実施

### 7.4 保守性

- コードの可読性
- 適切なコメント
- READMEドキュメント
- API ドキュメント（Swagger / OpenAPI）

### 7.5 拡張性

- マイクロサービス化を見据えた設計
- 将来的な機能追加に対応できる柔軟なデータモデル

## 8. 開発方針

### 8.1 開発順序

1. **Phase 1 (MVP)**: 基本的なTodo管理とユーザー認証
2. **Phase 2**: 時間管理と基本的な可視化
3. **Phase 3**: 高度な機能（サブタスク、タグ、繰り返し、通知、高度な可視化）

### 8.2 開発プロセス

- MVP最優先で開発
- 各フェーズごとにテストを実施
- フェーズ完了後にユーザーフィードバックを反映

### 8.3 テスト方針

- バックエンド: JUnit / Mockitoによる単体テスト、統合テスト
- フロントエンド: 手動テスト（MVP）、自動テスト導入は将来的に検討
- APIテスト: Postman等を使用

## 9. 将来的な拡張案

- モバイルアプリ（React Native等）
- チーム機能（タスクの共有、割り当て）
- カレンダー連携
- 外部サービス連携（Google Calendar、Slack等）
- AI による時間予測機能
- ダークモード
- 多言語対応

---

**作成日**: 2025-12-14
**最終更新**: 2025-12-14
**バージョン**: 1.0
