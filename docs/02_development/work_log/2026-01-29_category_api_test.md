# 作業ログ: 2026-01-29 (Category API動作確認)

## 学習情報

- **日付**: 2026-01-29
- **学習フェーズ**: WBS 1.5.2（Category CRUD API - curlテスト）
- **ステータス**: 完了

---

## 実施内容

### 1. Category APIの動作確認

curlコマンドを使って、Category CRUD APIの全機能をテストした。

**テスト手順**:
1. ログインしてJWTトークンを取得
2. 各APIエンドポイントをテスト

---

### 2. テスト結果

| HTTP | エンドポイント | 結果 | 備考 |
|------|---------------|------|------|
| POST | `/api/categories` | ✅ 成功 | カテゴリ「仕事」を作成 |
| GET | `/api/categories` | ✅ 成功 | 作成したカテゴリが一覧に表示 |
| PUT | `/api/categories/{id}` | ✅ 成功 | 名前と色を更新 |
| DELETE | `/api/categories/{id}` | ✅ 成功 | カテゴリを削除 |

---

### 3. 実行したコマンド

**ログイン**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "password123"}'
```

レスポンス例:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer"
}
```

**カテゴリ作成**:
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{"name": "仕事", "color": "#FF0000"}'
```

レスポンス例:
```json
{
  "id": "0b96da02-9f57-4267-912c-c6a33e493c27",
  "name": "仕事",
  "color": "#FF0000"
}
```

**カテゴリ一覧取得**:
```bash
curl http://localhost:8080/api/categories \
  -H "Authorization: Bearer {token}"
```

レスポンス例:
```json
[
  {
    "id": "0b96da02-9f57-4267-912c-c6a33e493c27",
    "name": "仕事",
    "color": "#FF0000"
  }
]
```

**カテゴリ更新**:
```bash
curl -X PUT http://localhost:8080/api/categories/{id} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{"name": "仕事（更新）", "color": "#00FF00"}'
```

**カテゴリ削除**:
```bash
curl -X DELETE http://localhost:8080/api/categories/{id} \
  -H "Authorization: Bearer {token}"
```

---

## 確認できたこと

### 1. 認証が正しく機能している

- JWTトークンなしでAPIを呼ぶと401エラー
- 有効なトークンを付けるとAPIが正常に動作

### 2. ユーザーごとのデータ分離

- 認証済みユーザーのカテゴリのみが作成・取得される
- 他のユーザーのカテゴリは見えない

### 3. CRUD操作が一通り動作

- Create: 新規カテゴリ作成 ✅
- Read: カテゴリ一覧取得 ✅
- Update: カテゴリ更新 ✅
- Delete: カテゴリ削除 ✅

---

## 完了したWBSタスク

- [x] 1.5.2 curlでCategory APIの動作確認テスト

## 次回のタスク

- [ ] 1.4.2 Todo作成API
- [ ] 1.4.3 Todo一覧取得API
- [ ] 1.4.4 Todo更新API
- [ ] 1.4.5 Todo削除API
- [ ] 1.4.6 Todo完了切り替えAPI
- [ ] 1.5.3 TodoとCategoryの関連付け

---

**作成日**: 2026-01-29
