# JavaのListとArrayListとArray - コレクション設計の基本

## 本記事を作成した背景

TodoManagerクラスを実装している際、以下のコードに疑問を持ちました。

```java
private List<Todo> todos;           // フィールドはList型

public TodoManager() {
    this.todos = new ArrayList<>(); // 実体はArrayList
}
```

**なぜフィールドは `List` なのに、実体は `ArrayList` で初期化するのか？**

この疑問を深掘りすることで、Javaにおける「インターフェースと実装の分離」という設計思想、そしてコレクションフレームワークの理解につながると考えました。

## 本記事で取り組んだこと

- `List`（インターフェース）と `ArrayList`（実装クラス）の継承関係と使い分けの理由を理解
- `Array`（配列）、`List`（インターフェース）、`ArrayList`（クラス）の3つの違いを整理
- それぞれの具体的な使い方（要素の追加・削除・更新・ループ処理）を習得
- 「外には抽象、中では具体」というJava設計の基本原則を理解

## 手順

### 前提

- **環境**: Java 17以降
- **前提知識**: Javaの基本文法（クラス、メソッド、変数）を理解している
- **対象コード**: TodoManagerクラス（Todoアプリケーションのデータ管理クラス）

### 1. ArrayListとListの継承関係を理解する

#### 🎯 目的

**ArrayList は List インターフェースを実装（implements）したクラスである**ことを理解し、なぜこの設計が重要なのかを学ぶ。

#### 🛠️ 継承関係の詳細

```java
// Listはインターフェース（interface）
public interface List<E> extends Collection<E> {
    boolean add(E e);
    E get(int index);
    int size();
    // ... その他のメソッド定義
}

// ArrayListはListを実装（implements）したクラス
public class ArrayList<E> implements List<E> {
    // 実際の実装がここに書かれている
}
```

**重要ポイント**：

- `List` = **インターフェース**（できることの約束だけ）
- `ArrayList` = **実装クラス**（具体的な動作を実装）
- `ArrayList` は `List` の約束を守って作られている

#### 💡 理解ポイント - なぜ分離するのか？

```java
// ❌ 実装クラスで宣言（変更に弱い）
private ArrayList<Todo> todos = new ArrayList<>();

// ✅ インターフェースで宣言（変更に強い）
private List<Todo> todos = new ArrayList<>();
```

もし将来、データ量が増えて途中削除が多くなった場合：

```java
// 実装を変更するだけで対応可能
this.todos = new LinkedList<>();  // LinkedListもListを実装している
```

`List` 型で宣言していれば、他のコード（`add()`, `get()`, `size()` など）は一切変更不要です。

**これが「実装に依存しない設計」の威力です。**

#### 継承関係の図解

```plaintext
           Collection<E> (インターフェース)
                 |
              List<E> (インターフェース)
                 |
        +--------+--------+
        |                 |
   ArrayList<E>      LinkedList<E>
   (実装クラス)      (実装クラス)
```

#### 📝 補足 - 実務での推奨パターン

```java
// フィールド・引数・戻り値：インターフェース
private List<Todo> todos;
public List<Todo> getTodos() { ... }
public void addAll(List<Todo> newTodos) { ... }

// new する場所：実装クラス
this.todos = new ArrayList<>();
```

### 2. Array / List / ArrayList の違いを整理する

#### 🎯 目的

Javaで「複数のデータを扱う」際の3つの選択肢を正しく理解する。

#### 🛠️ 詳細比較

| 項目 | Array（配列） | List（インターフェース） | ArrayList（クラス） |
| --- | --- | --- | --- |
| **種類** | 言語組み込み機能 | interface | class |
| **サイズ変更** | ❌ 固定 | 実装次第 | ✅ 自動で伸びる |
| **newできる** | ✅ | ❌ | ✅ |
| **追加/削除** | ❌ | 定義のみ | ✅ |
| **要素アクセス** | `arr[0]` | `list.get(0)` | `list.get(0)` |
| **主な用途** | 固定サイズデータ | 設計（型として使う） | 実装 |

#### Array（配列）の特徴

```java
Todo[] todos = new Todo[3];  // サイズ固定
todos[0] = new Todo("買い物");
todos[1] = new Todo("掃除");
// todos[3] = ...  ❌ エラー！
```

- メリット：高速、シンプル、メモリ効率良い
- デメリット：サイズ変更不可、実務では扱いづらい

#### List（インターフェース）の特徴

```java
List<Todo> todos;            // これは「型」
todos = new ArrayList<>();   // 実体が必要
```

- `new List<>()` はできない（インターフェースなので）
- 「順序付きコレクションとしてできること」を定義
- 実装を隠せる

#### ArrayList（クラス）の特徴

```java
List<Todo> todos = new ArrayList<>();
todos.add(new Todo("買い物"));  // 自動で伸びる
todos.add(new Todo("掃除"));
```

- 内部で配列を使用
- サイズ自動調整
- 実務で最もよく使う

#### 💡 理解ポイント - たとえ話で理解する

- **Array**: 📦 仕切り付きの固定弁当箱（マスの数は最初から決まっている）
- **List**: 📜 「並べて管理できる」というルールブック
- **ArrayList**: 🧺 伸び縮みするカゴ（中身が増えたら勝手に大きくなる）

#### なぜJavaはListとArrayListを分けているのか？

```java
// 将来、実装を変更したくなったら
this.todos = new LinkedList<>();  // これだけでOK
```

使っている側（`add()`, `get()` など）は一切変更不要。
これがインターフェース設計の強みです。

#### 📝 補足 - JSONライブラリでも同じ思想

Gson（JSONライブラリ）の読み込み部分でも同じ思想：

```java
Type listType = new TypeToken<List<Todo>>(){}.getType();
todos = gson.fromJson(reader, listType);
```

- 「Todoのリスト」であることが重要
- 中身がArrayListかLinkedListかは本質ではない
- Gsonは内部的にArrayListを生成するが、`List` として受け取る

### 3. Array / List / ArrayList の具体的な使い方

#### 🎯 目的

それぞれの要素の追加・削除・更新・ループ処理の方法を習得する。

#### 🛠️ Array（配列）の使い方

##### 要素の追加・更新・削除

```java
// 作成（サイズ固定）
Todo[] todos = new Todo[3];

// 要素の設定（更新）
todos[0] = new Todo("買い物");
todos[1] = new Todo("掃除");
todos[2] = new Todo("洗濯");

// 要素の更新
todos[0] = new Todo("買い物（完了）");

// 要素の削除 → できない！
// 代わりに null を入れる
todos[1] = null;

// サイズの確認
int length = todos.length;  // 3（メソッドではなくフィールド）
```

##### ループ処理

```java
// 1. 通常のforループ
for (int i = 0; i < todos.length; i++) {
    System.out.println(todos[i]);
}

// 2. 拡張forループ（for-each）
for (Todo todo : todos) {
    System.out.println(todo);
}
```

#### 🛠️ List（インターフェース）の使い方

Listはインターフェースなので直接インスタンス化できません。
ArrayListやLinkedListなどの実装クラスを使います。

```java
// ❌ これはできない
List<Todo> todos = new List<>();

// ✅ 実装クラスを使う
List<Todo> todos = new ArrayList<>();
```

#### 🛠️ ArrayList（実装クラス）の使い方

##### 要素の追加・更新・削除

```java
// 作成（初期サイズは自動）
List<Todo> todos = new ArrayList<>();

// 要素の追加（末尾に追加）
todos.add(new Todo("買い物"));
todos.add(new Todo("掃除"));
todos.add(new Todo("洗濯"));

// 要素の追加（特定の位置に挿入）
todos.add(1, new Todo("料理"));  // index 1 に挿入

// 要素の取得
Todo first = todos.get(0);  // index 0 の要素

// 要素の更新
todos.set(0, new Todo("買い物（完了）"));  // index 0 を更新

// 要素の削除（indexで）
todos.remove(1);  // index 1 を削除

// 要素の削除（オブジェクトで）
Todo target = todos.get(0);
todos.remove(target);  // オブジェクトを削除

// サイズの確認
int size = todos.size();  // メソッド

// 空かどうかの確認
boolean isEmpty = todos.isEmpty();

// 全要素を削除
todos.clear();
```

##### ループ処理

```java
List<Todo> todos = new ArrayList<>();
todos.add(new Todo("買い物"));
todos.add(new Todo("掃除"));
todos.add(new Todo("洗濯"));

// 1. 通常のforループ（indexが必要な場合）
for (int i = 0; i < todos.size(); i++) {
    System.out.println(i + ": " + todos.get(i));
}

// 2. 拡張forループ（for-each）- 最もよく使う
for (Todo todo : todos) {
    System.out.println(todo);
}

// 3. forEachメソッド（Java 8以降）
todos.forEach(todo -> System.out.println(todo));

// 4. forEachメソッド（メソッド参照）
todos.forEach(System.out::println);

// 5. Iteratorを使う（昔ながらの方法）
Iterator<Todo> iterator = todos.iterator();
while (iterator.hasNext()) {
    Todo todo = iterator.next();
    System.out.println(todo);
}
```

#### 💡 理解ポイント - どのループを使うべきか？

| 用途 | 推奨ループ | 理由 |
| --- | --- | --- |
| 単純に全要素を処理 | 拡張for | シンプルで読みやすい |
| indexが必要 | 通常のfor | index番号を使える |
| ループ中に削除 | Iterator | 安全に削除できる |
| 関数型プログラミング | forEach | ラムダ式やメソッド参照が使える |

**実際のTodoManagerでの例**：

```java
// listTodos() メソッド
public void listTodos() {
    // 方法1：通常のfor（index表示が必要）
    for (int i = 0; i < todos.size(); i++) {
        System.out.println((i + 1) + ". " + todos.get(i));
    }

    // 方法2：拡張for（シンプル）
    for (Todo todo : todos) {
        System.out.println(todo);
    }
}

// completeTodo() メソッド
public void completeTodo(String id) {
    for (Todo todo : todos) {
        if (todo.getId().equals(id)) {
            todo.markAsCompleted();
            saveToFile();
            return;
        }
    }
    System.out.println("指定されたIDのタスクが見つかりません: " + id);
}
```

#### 📝 補足 - ArrayとArrayListのメソッド名の違い

| 操作 | Array | ArrayList |
| --- | --- | --- |
| サイズ取得 | `todos.length` | `todos.size()` |
| 要素アクセス | `todos[0]` | `todos.get(0)` |
| 要素設定 | `todos[0] = x` | `todos.set(0, x)` |
| 要素追加 | ❌ できない | `todos.add(x)` |
| 要素削除 | ❌ できない | `todos.remove(0)` |

### 4. 実践：TodoManagerの設計を見直す

#### 🎯 目的

学んだ知識を実際のコードに適用し、適切なコレクション選択を判断する。

#### 🛠️ 現在の設計分析

```java
private List<Todo> todos;  // これは正しい？
```

**使い方を確認**：

- Todoを追加する（`add`）
- 一覧表示する（順番に見る）
- 全件ループする
- ID検索する（`completeTodo`）

**判断**：

- 一覧表示・順番保持 → `List` が適切 ✅
- 実装は `ArrayList` で十分（追加・取得が多い）

#### 💡 理解ポイント - ArrayListとLinkedListの使い分け

| 操作 | ArrayList | LinkedList |
| --- | --- | --- |
| 末尾への追加 | ⚡ 速い | ⚡ 速い |
| 先頭への追加 | 🐢 遅い | ⚡ 速い |
| 途中への挿入 | 🐢 遅い | 🐢 遅い |
| index指定で取得 | ⚡ 速い | 🐢 遅い |
| 途中の削除 | 🐢 遅い | ⚡ 速い |

**TodoManagerの場合**：

- 末尾への追加が多い（`addTodo`）
- index指定の取得が多い（`listTodos`）
- 途中の削除は少ない

👉 **ArrayList が最適**

#### 📝 補足 - 過度な最適化は避ける

- 小規模アプリでは `ArrayList` で十分
- 実際に遅くなってから最適化
- 「測定してから最適化」が鉄則

**現時点では `List<Todo> todos = new ArrayList<>()` が正解**

## 学び・次に活かせる知見

- **ArrayListはListインターフェースを実装したクラス** - 継承関係を理解することで、なぜ「外には抽象、中では具体」なのかが腑に落ちる
- **「外には抽象、中では具体」** - フィールド/引数/戻り値は `List`、実体は `ArrayList` という設計パターンは、Javaの基本中の基本
- **インターフェースで宣言することの威力** - 将来の実装変更に強く、テスト・拡張にも有利
- **配列とリストの使い分け** - 固定サイズなら配列、可変サイズならリスト
- **ループ処理の選択** - 用途に応じて適切なループを選ぶ（拡張for、通常のfor、forEach）
- **ArrayList vs LinkedList** - 操作の特性を理解し、用途に応じて選択する

## 参考文献

1. [Java公式ドキュメント - Collections Framework](https://docs.oracle.com/javase/8/docs/technotes/guides/collections/overview.html)
2. [Effective Java 第3版](https://www.amazon.co.jp/dp/4621303252) - Item 64: Refer to objects by their interfaces
3. [ArrayList vs LinkedList in Java](https://www.baeldung.com/java-arraylist-linkedlist)
4. [Java List Interface](https://docs.oracle.com/javase/8/docs/api/java/util/List.html)

## 更新履歴

- 2026-01-01：初版公開
