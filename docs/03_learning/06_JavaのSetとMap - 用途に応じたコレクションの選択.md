# JavaのSetとMap - 用途に応じたコレクションの選択

## 本記事を作成した背景

前回の記事で `List` と `ArrayList` の使い分けを学びました。
しかし、Javaのコレクションフレームワークには他にも重要な型があります：`Set` と `Map` です。

TodoManagerクラスで以下のような処理を考えた際、疑問が生まれました：

```java
// 現在のコード（List使用）
public void completeTodo(String id) {
    for (Todo todo : todos) {           // 全件ループ
        if (todo.getId().equals(id)) {
            todo.markAsCompleted();
            return;
        }
    }
}
```

**IDで検索するのに毎回全件ループするのは効率的だろうか？**

この疑問から、用途に応じた適切なコレクションの選択が重要だと学びました。

## 本記事で取り組んだこと

- `List` / `Set` / `Map` の違いと特性を理解
- 「何をキーにしてデータを扱うか」という設計の視点を習得
- それぞれの具体的な使い方（要素の追加・削除・更新・ループ処理）を学習
- データ構造がパフォーマンスに与える影響を理解（O(n) vs O(1)）

## 手順

### 前提

- **環境**: Java 17以降
- **前提知識**: Listの基本的な使い方を理解している
- **前提記事**: [05_JavaのListとArrayListとArray](./05_JavaのListとArrayListとArray%20-%20コレクション設計の基本.md) を読了

### 1. List / Set / Map の違いを整理する

#### 🎯 目的

Javaのコレクションフレームワークの全体像を理解し、適切な使い分けができるようになる。

#### 🛠️ 詳細比較 - 「何をキーにしてデータを扱うか」が違う

| 種類 | データ識別 | 順序 | 重複 | 取得方法 | 主な実装 |
| --- | --- | --- | --- | --- | --- |
| **List** | index | あり | OK | `get(i)` | ArrayList |
| **Set** | 値そのもの | 基本なし | NG | `contains(value)` | HashSet |
| **Map** | キー | 実装次第 | keyはNG | `get(key)` | HashMap |

#### List - 順番がすべて

```java
List<String> list = new ArrayList<>();
list.add("A");
list.add("A");  // 重複OK

list.get(0);  // "A"
list.get(1);  // "A"
```

向いている用途：

- 並びが重要
- 履歴・ログ
- Todo一覧、画面表示順

#### Set - 重複しない集合

```java
Set<String> set = new HashSet<>();
set.add("A");
set.add("A");  // 2回目は無視される

System.out.println(set.size());  // 1
```

向いている用途：

- 一意性を保証したい
- タグ管理
- 「含まれているか？」の高速判定（`contains()` が超高速）

#### Map - キー→値

```java
Map<String, Todo> map = new HashMap<>();
map.put("id-1", new Todo("買い物"));
map.put("id-2", new Todo("掃除"));

Todo todo = map.get("id-1");  // 超高速
```

向いている用途：

- ID検索
- 辞書
- 設定情報
- DBの主キー的な使い方

#### 💡 理解ポイント - たとえ話で理解する

- **List**: 📚 図書館の本棚（1番、2番、3番...と順番で管理）
- **Set**: 🎫 スタンプカード（同じスタンプは1回だけ）
- **Map**: 📖 辞書（単語→意味、IDで即座に引ける）

#### 📝 補足 - 継承関係

```plaintext
Collection<E> (インターフェース)
    |
    +--- List<E> (インターフェース)
    |      |
    |      +--- ArrayList<E>
    |      +--- LinkedList<E>
    |
    +--- Set<E> (インターフェース)
           |
           +--- HashSet<E>
           +--- LinkedHashSet<E>
           +--- TreeSet<E>

Map<K, V> (インターフェース)
    |
    +--- HashMap<K, V>
    +--- LinkedHashMap<K, V>
    +--- TreeMap<K, V>
```

※ MapはCollectionを継承していない（別系統）

### 2. Set の具体的な使い方

#### 🎯 目的

Setの特性（一意性・高速な存在チェック）を活かした使い方を習得する。

#### 🛠️ Setの基本操作

##### 要素の追加・削除・確認

```java
// 作成
Set<String> tags = new HashSet<>();

// 要素の追加
tags.add("重要");
tags.add("緊急");
tags.add("重要");  // 2回目は無視される（戻り値: false）

// サイズの確認
System.out.println(tags.size());  // 2

// 要素が含まれているか確認（超高速）
boolean hasTag = tags.contains("重要");  // true

// 要素の削除
tags.remove("緊急");

// 全要素を削除
tags.clear();

// 空かどうかの確認
boolean isEmpty = tags.isEmpty();
```

##### ループ処理

```java
Set<String> tags = new HashSet<>();
tags.add("重要");
tags.add("緊急");
tags.add("バグ");

// 1. 拡張forループ（順序は保証されない）
for (String tag : tags) {
    System.out.println(tag);
}

// 2. forEachメソッド
tags.forEach(tag -> System.out.println(tag));

// 3. forEachメソッド（メソッド参照）
tags.forEach(System.out::println);

// 4. Iteratorを使う
Iterator<String> iterator = tags.iterator();
while (iterator.hasNext()) {
    String tag = iterator.next();
    System.out.println(tag);
}
```

#### 💡 理解ポイント - Setの実装クラスの違い

| 実装クラス | 順序 | 速度 | 用途 |
| --- | --- | --- | --- |
| **HashSet** | なし | 最速 | 一般的な用途 |
| **LinkedHashSet** | 挿入順 | 速い | 順序も保持したい |
| **TreeSet** | ソート順 | やや遅い | 自動でソートしたい |

```java
// HashSet - 順序なし
Set<String> hashSet = new HashSet<>();
hashSet.add("C");
hashSet.add("A");
hashSet.add("B");
// 出力順序は不定：A, C, B など

// LinkedHashSet - 挿入順を保持
Set<String> linkedHashSet = new LinkedHashSet<>();
linkedHashSet.add("C");
linkedHashSet.add("A");
linkedHashSet.add("B");
// 出力順序：C, A, B（挿入順）

// TreeSet - ソート順
Set<String> treeSet = new TreeSet<>();
treeSet.add("C");
treeSet.add("A");
treeSet.add("B");
// 出力順序：A, B, C（ソート順）
```

#### 📝 補足 - Todoアプリでの実例

```java
// Todoにタグ機能を追加する場合
public class Todo {
    private String id;
    private String title;
    private Set<String> tags;  // タグは重複不可

    public Todo(String title) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.tags = new HashSet<>();  // Setで初期化
    }

    public void addTag(String tag) {
        tags.add(tag);  // 重複は自動で除外される
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);  // 超高速
    }
}
```

### 3. Map の具体的な使い方

#### 🎯 目的

Mapの特性（キーによる高速検索）を活かした使い方を習得する。

#### 🛠️ Mapの基本操作

##### 要素の追加・削除・確認

```java
// 作成
Map<String, Todo> todoMap = new HashMap<>();

// 要素の追加（キーと値のペア）
todoMap.put("id-1", new Todo("買い物"));
todoMap.put("id-2", new Todo("掃除"));
todoMap.put("id-1", new Todo("買い物（更新）"));  // 同じキーなら上書き

// 要素の取得
Todo todo = todoMap.get("id-1");

// キーが存在するか確認
boolean exists = todoMap.containsKey("id-1");

// 値が存在するか確認（遅い）
boolean hasTodo = todoMap.containsValue(todo);

// 要素の削除
todoMap.remove("id-1");

// サイズの確認
int size = todoMap.size();

// 空かどうかの確認
boolean isEmpty = todoMap.isEmpty();

// 全要素を削除
todoMap.clear();
```

##### ループ処理

```java
Map<String, Todo> todoMap = new HashMap<>();
todoMap.put("id-1", new Todo("買い物"));
todoMap.put("id-2", new Todo("掃除"));

// 1. キーのセットを取得してループ
for (String id : todoMap.keySet()) {
    Todo todo = todoMap.get(id);
    System.out.println(id + ": " + todo);
}

// 2. 値のコレクションを取得してループ
for (Todo todo : todoMap.values()) {
    System.out.println(todo);
}

// 3. エントリ（キーと値のペア）でループ（最も効率的）
for (Map.Entry<String, Todo> entry : todoMap.entrySet()) {
    String id = entry.getKey();
    Todo todo = entry.getValue();
    System.out.println(id + ": " + todo);
}

// 4. forEachメソッド（Java 8以降）
todoMap.forEach((id, todo) -> {
    System.out.println(id + ": " + todo);
});
```

#### 💡 理解ポイント - Mapの実装クラスの違い

| 実装クラス | 順序 | 速度 | 用途 |
| --- | --- | --- | --- |
| **HashMap** | なし | 最速 | 一般的な用途 |
| **LinkedHashMap** | 挿入順 | 速い | 順序も保持したい |
| **TreeMap** | キーのソート順 | やや遅い | 自動でソートしたい |

```java
// HashMap - 順序なし
Map<String, String> hashMap = new HashMap<>();
hashMap.put("3", "C");
hashMap.put("1", "A");
hashMap.put("2", "B");
// 出力順序は不定

// LinkedHashMap - 挿入順を保持
Map<String, String> linkedHashMap = new LinkedHashMap<>();
linkedHashMap.put("3", "C");
linkedHashMap.put("1", "A");
linkedHashMap.put("2", "B");
// 出力順序：3=C, 1=A, 2=B（挿入順）

// TreeMap - キーのソート順
Map<String, String> treeMap = new TreeMap<>();
treeMap.put("3", "C");
treeMap.put("1", "A");
treeMap.put("2", "B");
// 出力順序：1=A, 2=B, 3=C（キーのソート順）
```

#### 📝 補足 - MapとListの併用パターン

Mapだけでは順序が保証されないため、ListとMapを併用することが多い：

```java
private List<Todo> todos;              // 表示順用
private Map<String, Todo> todoMap;     // ID検索用

public TodoManager() {
    this.todos = new ArrayList<>();
    this.todoMap = new HashMap<>();
}

public void addTodo(String title) {
    Todo todo = new Todo(title);
    todos.add(todo);                   // Listに追加
    todoMap.put(todo.getId(), todo);   // Mapにも追加
}

public void listTodos() {
    // 順番が重要な場合はList
    for (Todo todo : todos) {
        System.out.println(todo);
    }
}

public void completeTodo(String id) {
    // ID検索はMap（超高速）
    Todo todo = todoMap.get(id);
    if (todo != null) {
        todo.markAsCompleted();
    }
}
```

### 4. データ構造がパフォーマンスに与える影響

#### 🎯 目的

データ構造の選択がパフォーマンスにどれだけ影響するかを理解する。

#### 🛠️ 実例比較 - Listでの全件検索 vs Mapでの直接取得

##### パターン1：List使用（現在のコード）

```java
public void completeTodo(String id) {
    for (Todo todo : todos) {           // 全件ループ
        if (todo.getId().equals(id)) {
            todo.markAsCompleted();
            return;
        }
    }
}
```

**計算量**: O(n) - 件数が増えると遅くなる

- 1件なら1回チェック
- 100件なら平均50回チェック
- 10,000件なら平均5,000回チェック

##### パターン2：Map使用

```java
Map<String, Todo> todoMap = new HashMap<>();

public void completeTodo(String id) {
    Todo todo = todoMap.get(id);       // 直接取得
    if (todo != null) {
        todo.markAsCompleted();
    }
}
```

**計算量**: O(1) - ほぼ一定時間で取得可能

- 1件でも10,000件でも同じ速度

#### 💡 理解ポイント - いつMapを使うべきか？

**データ構造を変えるだけで性能が激変します。**

| 状況 | 推奨データ構造 | 理由 |
| --- | --- | --- |
| データ量が少ない（〜100件） | List | シンプルで十分 |
| IDでの検索が多い | Map | 高速検索 |
| 順番が重要 + ID検索も多い | List + Map併用 | 両方のメリット |
| 重複を許さない | Set | 一意性保証 |

#### 📝 補足 - 実務での判断基準

```plaintext
一覧・順番・画面表示     → List
一意性・存在チェック     → Set
ID検索・辞書・高速アクセス → Map
```

**現時点のTodoManagerでは**：

- データ量が少ない（数十件程度）
- ID検索はそれほど頻繁ではない
- 順番が重要

👉 **List のみで十分**

データ量が増えた際の改善案：

```java
private List<Todo> todos;              // 表示順用
private Map<String, Todo> todoMap;     // ID検索用

// トレードオフ：
// - メモリ使用量増加
// - 更新時の手間（両方更新必要）
// - 検索パフォーマンス大幅向上
```

**過度な最適化は避ける - 「測定してから最適化」が鉄則**

### 5. よくある混乱ポイント

#### 🎯 目的

SetとMapに関するよくある疑問を解消する。

#### Q1. Listで全部やればよくない？

**A.** 小規模ならOK、データが増えた瞬間に破綻する

- 100件程度まではListで問題なし
- 1,000件を超えるとMapの高速性が重要に
- ID検索が頻繁なら最初からMap

#### Q2. Setに順番はないの？

**A.** 実装クラスによる

- `HashSet`: 順序なし（最速）
- `LinkedHashSet`: 挿入順を保持
- `TreeSet`: ソート順

「順番が目的」なら素直に `List` を使う

#### Q3. MapってDB？

**A.** 発想は同じ、メモリ上のミニDB

- DBの主キー検索 = Mapのキー検索
- どちらも O(1) で高速
- Mapはメモリ上、DBはディスク上

## 学び・次に活かせる知見

- **コレクションの使い分け** - List/Set/Mapの特性を理解し、用途に応じて適切に選択することでパフォーマンスが激変する
- **データ構造とアルゴリズム** - O(n) vs O(1) の違いは、データ量が増えたときに顕著に現れる
- **Setの活用** - 重複を許さないデータ（タグ、カテゴリなど）にはSetが最適
- **Mapの威力** - ID検索が頻繁なら、Mapを使うことで劇的に高速化できる
- **ListとMapの併用パターン** - 順序保持とID検索の両方が必要なら併用も検討
- **過度な最適化を避ける** - 小規模なうちはシンプルに、問題が出てから最適化する

**次のステップ**：

- ジェネリクスの仕組み（`<T>` の意味）
- Stream APIを使ったコレクション操作
- `equals()` と `hashCode()` の関係（HashSet/HashMapの仕組み）
- `Comparable` と `Comparator`（ソートの仕組み）

## 参考文献

1. [Java公式ドキュメント - Collections Framework](https://docs.oracle.com/javase/8/docs/technotes/guides/collections/overview.html)
2. [Java HashMap Internals](https://www.baeldung.com/java-hashmap-advanced)
3. [Java HashSet vs TreeSet vs LinkedHashSet](https://www.baeldung.com/java-hashset-vs-treeset-vs-linkedhashset)
4. [When to use HashMap vs TreeMap vs LinkedHashMap](https://stackoverflow.com/questions/2889777/difference-between-hashmap-linkedhashmap-and-treemap)

## 更新履歴

- 2026-01-01：初版公開
