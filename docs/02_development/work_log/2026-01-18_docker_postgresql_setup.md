# 作業ログ: 2026-01-18 (Docker PostgreSQLセットアップ)

## 学習情報

- **日付**: 2026-01-18
- **学習フェーズ**: WBS 1.2.2 Spring Boot + PostgreSQL環境構築
- **学習時間**: 約30分
- **ステータス**: 完了

---

## 実施内容

### 1. Docker Compose設定ファイルの作成

PostgreSQLをDockerで起動するための設定を作成。

**docker-compose.yml:**
```yaml
services:
  postgres:
    image: postgres:16
    container_name: todoapp-postgres
    environment:
      POSTGRES_DB: ${POSTGRES_DB}
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

### 2. 環境変数ファイルの作成

パスワードをdocker-compose.ymlにべた書きしないため、.envファイルを使用。

**.env:**
```
POSTGRES_DB=todoapp
POSTGRES_USER=todoapp
POSTGRES_PASSWORD=todoapp123
```

### 3. セキュリティ対策

- `.gitignore` に `.env` を追加
- `.env.example` を作成（サンプルとしてGitにコミット可能）

---

## 発生した問題と解決策

### 問題: Windows改行コード（CRLF）による認証エラー

**症状:**
```
FATAL: password authentication failed for user "todoapp"
```

**原因:**
Windowsで作成したファイルに含まれるCRLF改行コード（`\r\n`）がLinux環境で問題を起こした。

**確認方法:**
```bash
cat -A .env
# 問題がある場合: POSTGRES_DB=todoapp^M$
# 正常な場合: POSTGRES_DB=todoapp$
```

`^M` はキャリッジリターン（CR, `\r`）を示す。

**解決策:**

方法1: sedで修正
```bash
sed -i 's/\r$//' .env
sed -i 's/\r$//' docker-compose.yml
```

方法2: ファイルを作り直す（WSL2のターミナルで）
```bash
rm .env
cat > .env << 'EOF'
POSTGRES_DB=todoapp
POSTGRES_USER=todoapp
POSTGRES_PASSWORD=todoapp123
EOF
```

### 問題: シェル環境変数に古い値が残っていた

**症状:**
ファイルを修正しても `docker compose config` に `\r` が表示される。

**原因:**
`export $(cat .env | xargs)` で読み込んだ古い環境変数がシェルに残っていた。

**解決策:**
```bash
unset POSTGRES_DB POSTGRES_USER POSTGRES_PASSWORD
export $(cat .env | xargs)
```

### 問題: データベースが存在しない

**症状:**
```
FATAL: database "todoapp" does not exist
```

**原因:**
PostgreSQLのDockerイメージは、**初回起動時のみ**環境変数を使ってユーザーとDBを作成する。
問題のある.envで起動した後、.envを修正してもデータベースは再作成されない。

**解決策:**
ボリュームを削除してコンテナを再作成:
```bash
docker compose down -v  # -v でボリュームも削除
docker compose up -d
```

---

## 学んだこと

### 1. Docker Composeと環境変数

- `.env` ファイルは Docker Compose が自動で読み込む
- `${変数名}` 形式で参照可能
- シェルの環境変数も使用される（優先される）

### 2. PostgreSQL Dockerイメージの初期化

- `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` は**初回起動時のみ**適用される
- 設定を変更したい場合はボリュームを削除して再作成が必要

### 3. Windows/Linux間のファイル共有時の注意

- 改行コードの違い（CRLF vs LF）に注意
- WSL2で開発する場合は、ファイルもWSL2上で作成するのが安全
- `cat -A` コマンドで改行コードを可視化できる

---

## コマンドまとめ

```bash
# コンテナ起動
docker compose up -d

# コンテナ停止・削除（ボリューム込み）
docker compose down -v

# 設定確認
docker compose config

# PostgreSQL接続テスト
docker exec -it todoapp-postgres psql -U todoapp -d todoapp -c "SELECT 1"

# 環境変数の読み込み
export $(cat .env | xargs)

# 環境変数のクリア
unset POSTGRES_DB POSTGRES_USER POSTGRES_PASSWORD
```

---

**作成日**: 2026-01-18
