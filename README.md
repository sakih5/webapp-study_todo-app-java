# TodoApp

## プロジェクト概要

Todo管理（一覧閲覧・更新・削除）ができるWebアプリ

## 機能一覧

### ユーザーごとのTodo管理

- Todo一覧閲覧
- 新規Todo作成（タイトル、詳細、期限、優先度、カテゴリが登録可能）
- 既存Todoの内容更新・削除

### ユーザーごとのカテゴリー管理

- カテゴリー一覧閲覧
- 新規カテゴリー作成
- 既存カテゴリーの内容更新・削除

## 技術スタック

- フロントエンド: HTML / CSS / JavaScript
- バックエンド: Java 17, Spring Boot
- DB: PostgreSQL

## セットアップ手順

### 前提条件

- IDEはIntelliJを使用
- DBはDocker上に構築

### データベース起動方法

- `docker compose up -d`

### アプリ起動方法

- `export $(cat .env | xargs) && ./gradlew bootRun`

## 使い方

1. ユーザー登録
2. ログイン
3. カテゴリーを作成
4. Todoを作成・編集・完了・削除
