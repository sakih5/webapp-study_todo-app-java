# 作業ログ: 2025-12-25 (SimpleTodo完成編)

## 学習情報

- **日付**: 2025-12-25
- **学習フェーズ**: WBS 1.0.4 簡単なJavaプログラム作成（完了）
- **学習時間**: 約2-3時間
- **ステータス**: SimpleTodoアプリケーション完成、WBS 1.0.4 完了

---

## 完了したタスク

### プロジェクト情報

- **プロジェクト名**: SimpleTodo
- **プロジェクトパス**: /home/sakih/projects/webapp_study_todo-app-java/SimpleTodo
- **開発環境**: Windows (IntelliJ IDEA)
- **ビルドシステム**: Maven
- **JDK**: 17
- **依存関係**: Gson 2.10.1

---

## SimpleTodoアプリケーションの概要

### アプリケーションの機能

コマンドライン版ToDoアプリケーションを作成しました。

**実装した機能**:
1. タスクの追加（add）
2. タスクの一覧表示（list）
3. タスクの完了（complete）
4. ファイルへのデータ保存（JSON形式）

### アーキテクチャ

```
SimpleTodo/
  src/
    main/
      java/
        org/example/
          Todo.java          ← Todoデータクラス
          TodoManager.java   ← Todo管理クラス
          Main.java          ← メインクラス
  todos.json                 ← データ保存ファイル
```

**責務分離**:
- `Todo.java`: データモデル（1件のタスク）
- `TodoManager.java`: ビジネスロジック（CRUD操作、永続化）
- `Main.java`: ユーザーインターフェース（コマンドライン引数処理）

---

## 実装の詳細

### 1. Todo.java

**目的**: タスク1件分のデータを表現するクラス

**実装した要素**:

1. **フィールド**
   - `id` (String): UUID形式の一意なID
   - `title` (String): タスクのタイトル
   - `completed` (boolean): 完了状態
   - `createdAt` (long): 作成日時（Unixタイムスタンプ）

2. **コンストラクタ**
   ```java
   Todo(String title) {
       id = UUID.randomUUID().toString();
       this.title = title;
       completed = false;
       createdAt = System.currentTimeMillis();
   }
   ```

3. **getter メソッド**
   - `getId()`, `getTitle()`, `getCompleted()`, `getCreatedAt()`
   - すべてフィールドの値を返すだけ（変更しない）

4. **markAsCompleted() メソッド**
   - `completed`を`true`に変更

5. **toString() メソッド**
   - `@Override`アノテーションを使用
   - 完了状態に応じて表示を変更
   - `[完]` または `[未]` のマークを表示

**学んだポイント**:
- `this`キーワードの使い方（引数とフィールドの区別）
- getterは値を返すだけで変更しない
- `@Override`アノテーションの意味
- UUIDによる一意なID生成
- Unixタイムスタンプの使い方

---

### 2. TodoManager.java

**目的**: 複数のTodoを管理し、ファイル保存・読み込みを行うクラス

**実装した要素**:

1. **フィールド**
   ```java
   private List<Todo> todos;
   private static final String FILE_PATH = "todos.json";
   private Gson gson;
   ```

2. **コンストラクタ**
   - `ArrayList`で`todos`を初期化
   - `Gson`オブジェクトを初期化
   - `loadFromFile()`を呼び出してデータ読み込み

3. **addTodo(String title)**
   - 新しい`Todo`オブジェクトを作成
   - リストに追加
   - `saveToFile()`でファイルに保存

4. **listTodos()**
   - `for`ループまたは拡張for文で全タスクを表示
   - `todo.toString()`で整形済み文字列を出力

5. **completeTodo(String id)**
   - リストをループしてIDを検索
   - 一致するTodoを見つけたら`markAsCompleted()`を呼び出し
   - `saveToFile()`でファイルに保存
   - 見つからなければエラーメッセージ表示

6. **saveToFile()**
   - `gson.toJson(todos)`でJSON文字列に変換
   - `FileWriter`でファイルに書き込み
   - 例外処理（`try-catch`）

7. **loadFromFile()**
   - ファイルの存在チェック
   - `FileReader`でファイルから読み込み
   - `TypeToken<List<Todo>>`でジェネリクス型を指定
   - `gson.fromJson()`でJSON文字列をListに変換
   - 例外処理（`try-catch`）

**学んだポイント**:
- `List<Todo>`の使い方（`add()`, `get()`, `size()`）
- インスタンスメソッド vs staticメソッドの違い
- ファイルI/O（FileWriter, FileReader）
- 例外処理の実装
- Gsonによるシリアライゼーション
- TypeTokenの使い方（ジェネリクス型の指定）
- メソッドから`return`で早期終了

---

### 3. Main.java

**目的**: コマンドライン引数を処理してTodoManagerを操作するクラス

**実装した要素**:

1. **mainメソッド**
   - コマンドライン引数（`args`）を処理
   - `TodoManager`のインスタンスを作成

2. **引数チェック**
   ```java
   if (args.length == 0) {
       showUsage();
       return;
   }
   ```

3. **switch文によるコマンド分岐**
   - `case "add"`: タスク追加
   - `case "list"`: 一覧表示
   - `case "complete"`: タスク完了
   - `default`: 不明なコマンド

4. **各caseでの引数チェック**
   - `add`と`complete`は2つ以上の引数が必要
   - 不足している場合はエラーメッセージ表示

5. **showUsage()メソッド**
   - 使い方を表示
   - 読みやすい複数行のフォーマット

**学んだポイント**:
- コマンドライン引数の処理（`String[] args`）
- switch文の`break`の重要性（フォールスルー防止）
- 引数の数チェック（`args.length`）
- 配列のインデックスアクセス（`args[0]`, `args[1]`）
- ユーザーフレンドリーなエラーメッセージ

---

## 発生したエラーと解決方法

### エラー1: 間違ったimport文

**状況**:
```java
import static com.sun.tools.jdeprscan.DeprDB.loadFromFile;
```

**原因**: IntelliJ IDEAが間違った候補を自動提案

**解決**: この行を削除（`loadFromFile`は自分で実装するメソッド）

---

### エラー2: すべてのメソッドが`static`

**状況**:
```java
public static void addTodo() { ... }
```

**原因**: staticとインスタンスメソッドの違いを理解していなかった

**解決**:
- すべてのメソッドから`static`を削除
- TodoManagerは各オブジェクトが自分の`todos`リストを持つ必要がある

**学び**:
- staticメソッド: クラスに属する（オブジェクト不要）
- インスタンスメソッド: オブジェクトに属する（オブジェクトごとに異なるデータ）

---

### エラー3: addTodo()に引数がない

**状況**:
```java
public static void addTodo() {
    Todo todo = new Todo();  // コンストラクタにはtitleが必要
}
```

**解決**:
```java
public void addTodo(String title) {
    Todo todo = new Todo(title);
    todos.add(todo);
    saveToFile();
}
```

---

### エラー4: Listの要素に配列構文を使用

**状況**:
```java
System.out.println(todos[i]);  // Listには[]は使えない
```

**解決**:
```java
System.out.println(todos.get(i));  // get(i)を使う
```

**別の書き方**:
```java
for (Todo todo : todos) {  // 拡張for文
    System.out.println(todo);
}
```

---

### エラー5: completeTodo()でreturnがない

**状況**:
```java
for (Todo todo : todos) {
    if (todo.getId().equals(id)) {
        todo.markAsCompleted();
        saveToFile();
        // returnがないので、ループが続く
    }
}
System.out.println("見つかりません");  // 常に表示される
```

**解決**:
```java
if (todo.getId().equals(id)) {
    todo.markAsCompleted();
    saveToFile();
    return;  // ここで処理を終了
}
```

---

### エラー6: switch文にbreakがない

**状況**:
```java
switch (args[0]) {
    case "add":
        manager.addTodo(args[1]);
        // breakがない → 次のcaseも実行される（フォールスルー）
    case "list":
        manager.listTodos();
    case "complete":
        // ...
}
```

**解決**: 各caseの最後に`break;`を追加

**学び**: switch文では`break`がないと次のケースに進む（意図しない動作）

---

### エラー7: 引数の数チェックがない

**状況**:
```java
case "add":
    manager.addTodo(args[1]);  // args[1]が存在しない場合エラー
```

**解決**:
```java
case "add":
    if (args.length < 2) {
        System.out.println("エラー: タスク名を指定してください");
        showUsage();
        break;
    }
    manager.addTodo(args[1]);
```

---

### エラー8: package文がない

**状況**:
```
シンボルが見つけられません: TodoManager
```

**原因**: Todo.javaとTodoManager.javaに`package org.example;`がなかった

**解決**: 両ファイルの先頭に`package org.example;`を追加

**学び**:
- package文はファイルの一番最初に書く
- ファイルの場所とpackage名が一致する必要がある

---

### エラー9: mvnコマンドが使えない

**状況**:
```
mvn : 用語 'mvn' は...認識されません
```

**原因**: WindowsのコマンドラインにMavenがインストールされていない

**解決**: IntelliJ IDEAから実行
1. Main.javaを開く
2. mainメソッドの左の緑ボタン▶をクリック
3. Edit Configurations...でProgram argumentsを設定
4. 実行

---

## 実行テスト結果

### テストシナリオ

**1. タスクを追加**
```
Program arguments: add 買い物に行く
結果: タスクを追加しました ✅
```

**2. タスクを追加（2つ目、3つ目）**
```
Program arguments: add 本を読む
Program arguments: add 掃除をする
結果: 各タスクが追加された ✅
```

**3. 一覧表示**
```
Program arguments: list
結果:
[未]  買い物に行く(ID: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx)
[未]  本を読む(ID: yyyyyyyy-yyyy-yyyy-yyyy-yyyyyyyyyyyy)
[未]  掃除をする(ID: zzzzzzzz-zzzz-zzzz-zzzz-zzzzzzzzzzzz)
✅
```

**4. タスクを完了**
```
Program arguments: complete xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
結果: タスクを完了しました ✅
```

**5. 一覧表示（完了確認）**
```
Program arguments: list
結果:
[完]  買い物に行く(ID: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx)
[未]  本を読む(ID: yyyyyyyy-yyyy-yyyy-yyyy-yyyyyyyyyyyy)
[未]  掃除をする(ID: zzzzzzzz-zzzz-zzzz-zzzz-zzzzzzzzzzzz)
✅
```

**6. データの永続化確認**
```
todos.json ファイルが作成されている ✅
JSON形式でデータが保存されている ✅
```

### エラーハンドリングのテスト

**引数なし**
```
Program arguments: （空欄）
結果: 使い方が表示された ✅
```

**不明なコマンド**
```
Program arguments: delete
結果: 不明なコマンドです + 使い方表示 ✅
```

**引数が足りない**
```
Program arguments: add
結果: エラー: タスク名を指定してください + 使い方表示 ✅
```

---

## 学んだ重要な概念

### Java言語の要素

1. **thisキーワード**
   - フィールドと引数を区別する
   - `this.title = title;`

2. **getterの原則**
   - 値を返すだけで変更しない
   - 戻り値の型はフィールドと同じ

3. **@Overrideアノテーション**
   - 親クラスのメソッドをオーバーライドすることを明示
   - タイプミス防止

4. **switch文のbreak**
   - `break`がないと次のケースに進む（フォールスルー）
   - 意図しない動作の原因になる

5. **拡張for文**
   - `for (Todo todo : todos)`
   - インデックスを使わずに要素を直接取得

6. **List vs 配列**
   - 配列: `array[i]`
   - List: `list.get(i)`

7. **メソッドからの早期終了**
   - `return;` で処理を終了
   - 条件分岐をシンプルにできる

---

### オブジェクト指向設計

1. **責務分離（Single Responsibility Principle）**
   - Todo: データの保持
   - TodoManager: ビジネスロジック
   - Main: ユーザーインターフェース

2. **カプセル化**
   - フィールドは`private`
   - getterで読み取り専用アクセス
   - 状態変更は専用メソッド（`markAsCompleted()`）

3. **レイヤー化アーキテクチャ**
   ```
   Main（UIレイヤー）
     ↓
   TodoManager（ビジネスロジックレイヤー）
     ↓
   Todo（データモデルレイヤー）
     ↓
   todos.json（永続化レイヤー）
   ```

4. **疎結合**
   - MainはTodoの内部実装を知らない
   - TodoManagerは表示方法を知らない
   - 後でWebUIに変更してもTodoManagerは再利用可能

---

### Java開発の実践

1. **package文の役割**
   - クラスを整理する「フォルダのようなもの」
   - ファイルの場所とpackage名を一致させる
   - package文は必ずファイルの最初に書く

2. **import文の管理**
   - 使用するクラスをimport
   - 自動補完に頼りすぎず、正しいクラスをimport

3. **ファイルI/O**
   - FileWriter: ファイルへの書き込み
   - FileReader: ファイルからの読み込み
   - try-catch: 例外処理

4. **例外処理の実装**
   ```java
   try {
       // エラーが起きる可能性のあるコード
   } catch (IOException e) {
       // エラーが起きた時の処理
   }
   ```

5. **Gsonライブラリの使用**
   - `gson.toJson(object)`: オブジェクト → JSON文字列
   - `gson.fromJson(json, type)`: JSON文字列 → オブジェクト
   - TypeTokenでジェネリクス型を指定

---

### 設計の考え方

1. **データモデルファースト**
   - まずデータ構造を設計（Todoクラス）
   - その後、操作を設計（TodoManager）

2. **永続化の重要性**
   - アプリ終了後もデータを保持
   - JSON形式で保存（人間が読める）

3. **エラーハンドリング**
   - ユーザーの入力ミスを想定
   - わかりやすいエラーメッセージ
   - 使い方を表示

4. **シンプルさの重要性**
   - 過度な機能追加をしない
   - 必要最小限の実装

---

## 進捗状況

### WBS 1.0 Java基礎学習 - 完了

- ✅ **WBS 1.0.1**: Java基本文法学習（100%完了）
  - 変数、型、演算子
  - 制御構文（if, for, while）
  - 配列とArrayList
  - メソッドの定義と呼び出し
  - 例外処理

- ✅ **WBS 1.0.2**: オブジェクト指向プログラミング（100%完了）
  - クラスとオブジェクト
  - コンストラクタ
  - カプセル化
  - 継承
  - インターフェース
  - ポリモーフィズム

- ✅ **WBS 1.0.3**: Maven/Gradle基礎（100%完了）
  - Mavenの役割
  - pom.xmlの構造
  - 依存関係管理
  - Mavenコマンド（clean, compile, package）

- ✅ **WBS 1.0.4**: 簡単なJavaプログラム作成（100%完了）
  - Todo.javaクラスの実装
  - TodoManager.javaの実装
  - Main.javaの実装
  - アプリケーションのテスト

**Java基礎学習フェーズ完了！🎉**

---

## 次のステップ

### WBS 1.1 設計（2.7日）

本番のTodoアプリケーション開発に向けて、設計フェーズに入ります。

#### 1.1.1 画面設計（1.3日）

**作成するもの**:
- ログイン画面のワイヤーフレーム
- ユーザー登録画面のワイヤーフレーム
- Todo一覧画面のワイヤーフレーム
- Todo作成・編集フォームのワイヤーフレーム
- カテゴリー管理画面のワイヤーフレーム
- 画面遷移図

**作業内容**:
- 各画面に必要な要素を洗い出す
- ワイヤーフレームを作成（手書きまたはツール使用）
- 画面間の遷移を設計
- 各画面で表示するデータを明確にする

#### 1.1.2 データモデル設計（0.7日）

**作成するもの**:
- Userテーブル設計（id, email, password_hash, created_at, updated_at）
- Categoryテーブル設計（id, user_id, name, color）
- Todoテーブル設計（id, user_id, category_id, title, description, priority, due_date, is_completed, completed_at）
- ER図（テーブル間のリレーション）

**作業内容**:
- 画面設計から必要なデータを抽出
- テーブル定義
- リレーション定義（外部キー制約）
- インデックス設計
- 正規化の確認

#### 1.1.3 API設計（0.7日）

**作成するもの**:
- 認証API定義（POST /api/auth/register, POST /api/auth/login）
- Todo API定義（GET/POST /api/todos, GET/PUT/DELETE /api/todos/{id}, PATCH /api/todos/{id}/complete）
- Category API定義（GET/POST /api/categories, PUT/DELETE /api/categories/{id}）
- リクエスト・レスポンススキーマ定義
- エラーレスポンス定義

**作業内容**:
- RESTful設計の原則に従う
- リクエストとレスポンスのフォーマットを定義
- ステータスコードを定義（200, 201, 400, 401, 404, 500）
- クエリパラメータ設計（フィルタ、ソート用）

---

## 振り返り

### 良かった点

1. **段階的な実装**
   - Todo.java → TodoManager.java → Main.java の順で実装
   - 各クラスの責務が明確だった

2. **エラーからの学び**
   - 多くのエラーに遭遇したが、一つずつ解決
   - エラーの原因を理解することで知識が定着

3. **実践的な学習**
   - これまで学んだJava基礎、OOP、Mavenの知識を統合
   - 実際に動くアプリケーションを作成

4. **コードレビューの効果**
   - レビューを通じて改善点を発見
   - 正しい書き方を学べた

### 苦労した点

1. **staticとインスタンスメソッドの違い**
   - 最初はすべて`static`にしてしまった
   - オブジェクトごとにデータを持つ必要性を理解

2. **switch文のbreak**
   - `break`を忘れてフォールスルーが発生
   - デバッグで気づけた

3. **package文の理解**
   - 最初はpackage文の必要性がわからなかった
   - ファイルの場所とpackage名の関係を理解

4. **Gsonの使い方**
   - TypeTokenの構文が難しかった
   - ジェネリクス型の指定方法を学べた

### 次回への改善点

1. **事前の設計**
   - 今回は実装しながら考えた部分もあった
   - 次回は設計をしっかり行ってから実装

2. **テストケースの事前準備**
   - 実装後にテストを考えた
   - テスト駆動開発（TDD）も検討

3. **エラーハンドリングの充実**
   - より詳細なエラーメッセージ
   - ログ出力の追加

---

## まとめ

SimpleTodoアプリケーションの作成を通じて、Java基礎学習フェーズ（WBS 1.0）を完了しました。

**習得したスキル**:
- Java言語の基本構文
- オブジェクト指向プログラミング
- Mavenによるプロジェクト管理
- ファイルI/Oとデータ永続化
- エラーハンドリング
- コマンドライン引数の処理

**次のフェーズ**:
これらの知識を基に、本番のTodoアプリケーションの設計に入ります。Spring BootとPostgreSQLを使った本格的なWebアプリケーション開発に向けて、まずは設計をしっかり行います。

**学習の進捗**:
- Java基礎学習: 完了（WBS 1.0.1 〜 1.0.4）
- 次: 設計フェーズ（WBS 1.1.1 〜 1.1.3）
- その後: Spring Boot学習、環境構築へ

SimpleTodoで学んだ設計思想（責務分離、カプセル化、レイヤー化）は、Spring Bootでも同じように適用されます。この経験が次のフェーズに直接活きてきます。

---

**作成日**: 2025-12-25
**次回学習予定**: WBS 1.1.1 画面設計
**WBS進捗**: 1.0 Java基礎学習 100%完了 → 1.1 設計フェーズへ
