# 作業ログ: 2025-12-23 (SimpleTodoアプリ作成編)

## 学習情報

- **日付**: 2025-12-23
- **学習フェーズ**: WBS 1.0.4 簡単なJavaプログラム作成
- **学習時間**: 約30分
- **ステータス**: Todo.javaクラス完成、TodoManager.java設計まで

---

## 完了したタスク

### プロジェクト情報

- **プロジェクト名**: SimpleTodo
- **プロジェクトパス**: /home/sakih/projects/webapp_study_todo-app-java/SimpleTodo
- **ビルドシステム**: Maven
- **JDK**: 17
- **依存関係**: Gson 2.10.1

---

## プロジェクトの目的

これまで学んだJava基礎、OOP、Mavenの知識を統合して、**コマンドライン版ToDoアプリ**を作成する。

### アプリケーションの機能

1. タスクの追加
2. タスクの一覧表示
3. タスクの完了
4. ファイルへのデータ保存（JSON形式）

### アーキテクチャ

```
SimpleTodo/
  src/
    main/
      java/
        org/example/
          Todo.java          ← Todoデータクラス（完成）
          TodoManager.java   ← Todo管理クラス（設計済み、未実装）
          Main.java          ← メインクラス（未着手）
  todos.json                 ← データ保存ファイル
```

**役割分担:**
- `Todo.java`: タスク1件分のデータモデル
- `TodoManager.java`: ビジネスロジック（CRUD操作、永続化）
- `Main.java`: エントリーポイント（コマンドライン引数処理）

---

## 実装の詳細

### 1. Todo.javaクラスの作成

#### ファイルパス
`src/main/java/org/example/Todo.java`

#### 学んだ概念

**データクラス（POJOパターン）:**
- Plain Old Java Object
- フィールド + getter + ビジネスロジック最小限
- データの入れ物として機能

**UUIDの使用:**
- `UUID.randomUUID().toString()` で一意なIDを生成
- データベースの主キーとしても使われる

**Unixタイムスタンプ:**
- `System.currentTimeMillis()` で現在時刻をlong型で取得
- 1970年1月1日からのミリ秒数

#### 実装コード

```java
import java.util.UUID;

public class Todo {
    private String id;
    private String title;
    private boolean completed;
    private long createdAt;

    Todo(String title) {
        id = UUID.randomUUID().toString();
        this.title = title;
        completed = false;
        createdAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public boolean getCompleted() {
        return completed;
    }
    public long getCreatedAt() {
        return createdAt;
    }

    public void markAsCompleted(){
        completed = true;
    }

    @Override
    public String toString(){
        if (completed){
            return "[完]  " + this.title + "(ID: " + id + ")";
        } else {
            return "[未]  " + this.title + "(ID: " + id + ")";
        }
    }
}
```

#### レビューで学んだポイント

**1. `this`キーワードの重要性**

❌ 間違い:
```java
Todo(String title) {
    title = title;  // フィールドに代入されない！
}
```

✅ 正しい:
```java
Todo(String title) {
    this.title = title;  // this.titleはフィールドを指す
}
```

**理由:**
- 引数名とフィールド名が同じ場合、引数が優先される
- `this.` を付けることでフィールドを明示的に指定

**2. getterの戻り値の型**

❌ 間違い:
```java
public String getCompleted() {  // booleanフィールドなのにStringを返す
    return completed;
}
```

✅ 正しい:
```java
public boolean getCompleted() {  // フィールドと同じ型を返す
    return completed;
}
```

**getterの原則:**
- フィールドの値を**取得するだけ**
- 値を変更してはいけない
- 戻り値の型はフィールドと同じ

**3. `@Override`アノテーション**

```java
@Override
public String toString() {
    // ...
}
```

**意味:**
- 親クラス（Object）のメソッドをオーバーライドしていることを明示
- タイプミスがあればコンパイルエラーで気づける
- コードの意図が明確になる

**4. booleanの自然な書き方**

❌ 冗長:
```java
if (completed == true) {
    // ...
}
```

✅ 簡潔:
```java
if (completed) {
    // ...
}
```

**理由:** booleanはすでにtrue/falseなので、比較不要

---

### 2. TodoManager.javaクラスの設計

#### ファイルパス
`src/main/java/org/example/TodoManager.java`（次回実装）

#### 責務

1. **複数のTodoを管理** - List<Todo>で保持
2. **CRUD操作** - 追加、表示、完了
3. **永続化** - JSON形式でファイル保存・読み込み

#### 設計仕様

**フィールド:**
```java
private List<Todo> todos;           // Todoのリスト
private static final String FILE_PATH = "todos.json";  // 保存先
private Gson gson;                  // JSON変換用
```

**メソッド:**

| メソッド | 説明 | 引数 | 戻り値 |
|---------|------|------|--------|
| `TodoManager()` | コンストラクタ、ファイルから読み込み | なし | - |
| `addTodo(String title)` | 新しいタスクを追加 | タイトル | void |
| `listTodos()` | すべてのタスクを表示 | なし | void |
| `completeTodo(String id)` | タスクを完了にする | タスクID | void |
| `saveToFile()` | ファイルに保存 | なし | void |
| `loadFromFile()` | ファイルから読み込み | なし | void |

#### 使用する技術

**1. Listインターフェース**
```java
List<Todo> todos = new ArrayList<>();
```
- 可変長の配列のような機能
- `add()`, `get()`, `size()` などのメソッド

**2. Gsonによるシリアライゼーション**
```java
// オブジェクト → JSON文字列
String json = gson.toJson(todos);

// JSON文字列 → オブジェクト
List<Todo> todos = gson.fromJson(json, 型情報);
```

**3. ファイルI/O**
```java
// 書き込み
FileWriter writer = new FileWriter(FILE_PATH);
writer.write(json);
writer.close();

// 読み込み
FileReader reader = new FileReader(FILE_PATH);
// gson.fromJson()で変換
reader.close();
```

---

## 次回の作業

### 1. TodoManager.javaの実装

**実装順序:**
1. フィールドとコンストラクタ
2. `addTodo()` メソッド
3. `listTodos()` メソッド
4. `completeTodo()` メソッド
5. `saveToFile()` メソッド
6. `loadFromFile()` メソッド

**学習ポイント:**
- Listの使い方
- ファイルI/O
- 例外処理（try-catch）
- Gsonの使い方

### 2. Main.javaの実装

**コマンドライン引数の処理:**
```
java Main add "買い物に行く"       → タスク追加
java Main list                    → タスク一覧
java Main complete <ID>           → タスク完了
```

**学習ポイント:**
- `public static void main(String[] args)`
- コマンドライン引数の処理
- switch文やif-elseでのコマンド分岐

### 3. アプリケーションのテスト

**テストシナリオ:**
1. タスクを追加
2. 一覧表示で確認
3. タスクを完了
4. 再度一覧表示
5. アプリケーションを終了・再起動してデータが保存されているか確認

---

## 学んだ重要な概念

### OOPの実践

**クラスの責務分離:**
- `Todo`: データモデル
- `TodoManager`: ビジネスロジック
- `Main`: ユーザーインターフェース

**カプセル化:**
- フィールドはprivate
- getterで読み取り専用アクセス
- markAsCompleted()で状態変更を制御

### Java言語の要素

1. **thisキーワード** - フィールドと引数の区別
2. **getterの原則** - 取得のみ、変更しない
3. **@Override** - メソッドのオーバーライドを明示
4. **UUID** - 一意な識別子の生成
5. **toString()** - オブジェクトの文字列表現

### 設計の考え方

**レイヤー化:**
```
Main（UIレイヤー）
  ↓
TodoManager（ビジネスロジックレイヤー）
  ↓
Todo（データモデルレイヤー）
  ↓
todos.json（永続化レイヤー）
```

**疎結合:**
- Mainはビジネスロジックを知らない
- TodoManagerはUI（コマンドライン）を知らない
- 後でWebUIに変更しても、TodoManagerは再利用可能

---

## 発生したエラーと解決

### エラー1: コンストラクタでフィールドに値が設定されない

**状況:**
```java
Todo(String title) {
    title = title;  // 動かない
}
```

**原因:**
引数とフィールドの名前が同じ場合、引数が優先される。

**解決:**
```java
Todo(String title) {
    this.title = title;  // thisでフィールドを明示
}
```

### エラー2: getterの戻り値の型が間違っている

**状況:**
```java
public String getCompleted() {  // booleanフィールドなのに
    return completed;
}
```

**原因:**
フィールドの型とgetterの戻り値の型が一致していない。

**解決:**
```java
public boolean getCompleted() {  // フィールドと同じ型
    return completed;
}
```

### エラー3: getterで値を変更してしまう

**状況:**
```java
public boolean getCompleted() {
    completed = false;  // getterで値を変更してはいけない
    return completed;
}
```

**原因:**
getterは値を取得するだけで、変更してはいけない。

**解決:**
```java
public boolean getCompleted() {
    return completed;  // 取得のみ
}
```

---

## まとめ

### 達成したこと

✅ SimpleTodoプロジェクトの作成
✅ Gson依存関係の追加
✅ Todo.javaクラスの完成
✅ TodoManager.javaの設計完了

### 次のステップ

次回は以下を実装します:
1. TodoManager.javaの実装（ファイルI/O、Listの操作）
2. Main.javaの実装（コマンドライン引数処理）
3. アプリケーションの動作テスト

### 学習の進捗

**WBS進捗:**
- WBS 1.0.1: Java基礎（完了）
- WBS 1.0.2: OOP基礎（完了）
- WBS 1.0.3: Maven基礎（完了）
- **WBS 1.0.4: 簡単なJavaプログラム作成（50%完了）** ← 現在ここ

---

**作成日**: 2025-12-23
**最終更新**: 2025-12-23
