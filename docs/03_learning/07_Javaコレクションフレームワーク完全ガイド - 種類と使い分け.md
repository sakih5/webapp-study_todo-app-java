# Javaコレクションフレームワーク完全ガイド - 種類と使い分け

## 本記事を作成した背景

これまでの記事で `List`、`Set`、`Map` について個別に学習してきました。

しかし、実際の開発では「このデータをどのコレクションで管理すべきか？」という判断が必要になります。

- Todoリストは `List`？ それとも `Map`？
- タグは `List`？ それとも `Set`？
- キューは何を使う？

**Javaのコレクションフレームワーク全体を理解し、適切な選択ができるようになる**ことが本記事の目的です。

## 本記事で取り組んだこと

- Javaコレクションフレームワークの全体像を把握
- 主要なコレクション（List, Set, Map, Queue, Deque）の特徴を整理
- それぞれの実装クラスの違いを理解
- 用途に応じた使い分けの判断基準を習得
- 実例に基づいた選択フローを作成

## 手順

### 前提

- **環境**: Java 17以降
- **前提知識**: Javaの基本文法、クラス、インターフェースの概念を理解している
- **前提記事**:
  - [05_JavaのListとArrayListとArray](./05_JavaのListとArrayListとArray%20-%20コレクション設計の基本.md)
  - [06_JavaのSetとMap](./06_JavaのSetとMap%20-%20用途に応じたコレクションの選択.md)

### 1. コレクションフレームワーク全体像

#### 🎯 目的

Javaが提供するコレクションの種類と階層構造を理解する。

#### 🛠️ 階層構造

```plaintext
データを複数扱う方法
│
├─ 配列（Array）
│   └─ String[] names = new String[10];
│
└─ コレクションフレームワーク
    │
    ├─ Collection<E> インターフェース
    │   │
    │   ├─ List<E> インターフェース（順序付き、重複OK）
    │   │   ├─ ArrayList<E> ← 最もよく使う
    │   │   ├─ LinkedList<E>
    │   │   └─ Vector<E> ← 古い、使わない
    │   │
    │   ├─ Set<E> インターフェース（順序なし、重複NG）
    │   │   ├─ HashSet<E> ← 最もよく使う
    │   │   ├─ LinkedHashSet<E>
    │   │   └─ TreeSet<E>
    │   │
    │   └─ Queue<E> インターフェース（FIFO: 先入れ先出し）
    │       ├─ LinkedList<E> ← ListとQueueの両方を実装
    │       ├─ PriorityQueue<E>
    │       └─ Deque<E> インターフェース（両端キュー）
    │           ├─ ArrayDeque<E>
    │           └─ LinkedList<E>
    │
    └─ Map<K, V> インターフェース（キー→値、Collectionは継承しない）
        ├─ HashMap<K, V> ← 最もよく使う
        ├─ LinkedHashMap<K, V>
        ├─ TreeMap<K, V>
        └─ Hashtable<K, V> ← 古い、使わない
```

#### 💡 理解ポイント - インターフェースと実装クラス

**インターフェース（何ができるか）**：

- `List` - 順序付きで管理、重複OK
- `Set` - 重複を許さない
- `Map` - キーと値のペア
- `Queue` - キュー（待ち行列）
- `Deque` - 両端キュー

**実装クラス（どう実装するか）**：

- `ArrayList` - 配列ベース
- `LinkedList` - 連結リストベース
- `HashSet` - ハッシュテーブルベース
- `HashMap` - ハッシュテーブルベース

#### 📝 補足 - 使わない古いクラス

以下は古い実装なので、新規コードでは使わない：

- `Vector` → `ArrayList` を使う
- `Hashtable` → `HashMap` を使う
- `Stack` → `Deque` を使う

### 2. 主要コレクションの特徴と使い分け

#### 🎯 目的

各コレクションの特性を理解し、適切な選択ができるようになる。

#### 🛠️ List - 順序付きリスト

##### 特徴

```java
List<String> list = new ArrayList<>();
```

| 項目 | 内容 |
| --- | --- |
| **順序** | あり（追加順） |
| **重複** | OK |
| **null** | OK |
| **識別** | index（0, 1, 2...） |
| **主な操作** | `add()`, `get(i)`, `remove(i)` |

##### 使うべき場合

- 順番が重要
- 重複を許す
- index でアクセスしたい

##### 実例

```java
// Todo一覧（順番が重要）
List<Todo> todos = new ArrayList<>();
todos.add(new Todo("買い物"));
todos.add(new Todo("掃除"));
todos.add(new Todo("買い物"));  // 重複OK

// 表示は追加順
for (Todo todo : todos) {
    System.out.println(todo);
}
```

##### 実装クラスの選択

```java
// ほとんどの場合：ArrayList
List<String> list = new ArrayList<>();

// 先頭への追加/削除が多い：LinkedList（稀）
List<String> list = new LinkedList<>();
```

#### 🛠️ Set - 重複なし集合

##### 特徴

```java
Set<String> set = new HashSet<>();
```

| 項目 | 内容 |
| --- | --- |
| **順序** | 基本なし（実装次第） |
| **重複** | NG（自動で除外） |
| **null** | 1つだけOK |
| **識別** | 値そのもの |
| **主な操作** | `add()`, `contains()`, `remove()` |

##### 使うべき場合

- 重複を許さない
- 存在チェックが多い
- 順序は不要

##### 実例

```java
// タグ（重複不可）
Set<String> tags = new HashSet<>();
tags.add("重要");
tags.add("緊急");
tags.add("重要");  // 無視される

System.out.println(tags.size());  // 2

// 高速な存在チェック
if (tags.contains("重要")) {
    // ...
}
```

##### 実装クラスの選択

```java
// 順序不要、最速：HashSet
Set<String> set = new HashSet<>();

// 挿入順を保持：LinkedHashSet
Set<String> set = new LinkedHashSet<>();

// 自動ソート：TreeSet
Set<String> set = new TreeSet<>();
```

#### 🛠️ Map - キー→値の辞書

##### 特徴

```java
Map<String, Todo> map = new HashMap<>();
```

| 項目 | 内容 |
| --- | --- |
| **順序** | 基本なし（実装次第） |
| **重複** | キーは重複NG、値はOK |
| **null** | キー1つ、値は複数OK（実装次第） |
| **識別** | キー |
| **主な操作** | `put(k, v)`, `get(k)`, `remove(k)` |

##### 使うべき場合

- ID で検索したい
- キーと値のペア
- 高速な検索が必要

##### 実例

```java
// ID → Todo のマッピング
Map<String, Todo> todoMap = new HashMap<>();
todoMap.put("id-1", new Todo("買い物"));
todoMap.put("id-2", new Todo("掃除"));

// 超高速検索（O(1)）
Todo todo = todoMap.get("id-1");
```

##### 実装クラスの選択

```java
// 順序不要、最速：HashMap
Map<String, Todo> map = new HashMap<>();

// 挿入順を保持：LinkedHashMap
Map<String, Todo> map = new LinkedHashMap<>();

// キーの自動ソート：TreeMap
Map<String, Todo> map = new TreeMap<>();
```

#### 🛠️ Queue - キュー（待ち行列）

##### 特徴

```java
Queue<String> queue = new LinkedList<>();
```

| 項目 | 内容 |
| --- | --- |
| **順序** | あり（FIFO: 先入れ先出し） |
| **重複** | OK |
| **null** | 実装次第 |
| **識別** | 順番 |
| **主な操作** | `add()`, `poll()`, `peek()` |

##### 使うべき場合

- FIFO（先に入れたものを先に取り出す）
- 処理待ちのタスク
- メッセージキュー

##### 実例

```java
// タスクキュー
Queue<String> taskQueue = new LinkedList<>();
taskQueue.add("タスク1");
taskQueue.add("タスク2");
taskQueue.add("タスク3");

// 先に追加したものから取り出す
String task = taskQueue.poll();  // "タスク1"
```

##### 実装クラスの選択

```java
// 一般的なキュー：LinkedList
Queue<String> queue = new LinkedList<>();

// 優先度付きキュー：PriorityQueue
Queue<String> queue = new PriorityQueue<>();
```

#### 🛠️ Deque - 両端キュー

##### 特徴

```java
Deque<String> deque = new ArrayDeque<>();
```

| 項目 | 内容 |
| --- | --- |
| **順序** | あり |
| **重複** | OK |
| **null** | NG（ArrayDeque） |
| **識別** | 位置 |
| **主な操作** | `addFirst()`, `addLast()`, `pollFirst()`, `pollLast()` |

##### 使うべき場合

- 両端から追加・削除したい
- スタック（LIFO）としても使える
- Queueの上位互換

##### 実例

```java
// スタックとして使う（Stack クラスより推奨）
Deque<String> stack = new ArrayDeque<>();
stack.push("A");  // 先頭に追加
stack.push("B");
stack.push("C");

stack.pop();  // "C" （後入れ先出し）

// 両端キューとして使う
Deque<String> deque = new ArrayDeque<>();
deque.addFirst("先頭");
deque.addLast("末尾");
```

##### 実装クラスの選択

```java
// 配列ベース、最速：ArrayDeque（推奨）
Deque<String> deque = new ArrayDeque<>();

// 連結リストベース：LinkedList
Deque<String> deque = new LinkedList<>();
```

#### 💡 理解ポイント - 一覧比較表

| コレクション | 順序 | 重複 | null | 主な用途 | 推奨実装 |
| --- | --- | --- | --- | --- | --- |
| **List** | ✅ | ✅ | ✅ | 順序付きリスト | ArrayList |
| **Set** | ❌ | ❌ | △ | 重複なし集合 | HashSet |
| **Map** | ❌ | key:❌<br>value:✅ | △ | キー→値 | HashMap |
| **Queue** | ✅ | ✅ | △ | FIFO待ち行列 | LinkedList |
| **Deque** | ✅ | ✅ | ❌ | 両端キュー | ArrayDeque |

#### 📝 補足 - LinkedListは複数のインターフェースを実装

```java
public class LinkedList<E>
    implements List<E>, Queue<E>, Deque<E> {
    // ...
}

// つまり、LinkedListは以下すべてとして使える
List<String> list = new LinkedList<>();
Queue<String> queue = new LinkedList<>();
Deque<String> deque = new LinkedList<>();
```

### 3. 実装クラスの詳細比較

#### 🎯 目的

同じインターフェースを実装するクラス間の違いを理解する。

#### 🛠️ List 実装クラスの比較

| 操作 | ArrayList | LinkedList | 推奨度 |
| --- | --- | --- | --- |
| **末尾への追加** | ⚡ O(1) | ⚡ O(1) | どちらでも |
| **先頭への追加** | 🐢 O(n) | ⚡ O(1) | LinkedList |
| **index指定取得** | ⚡ O(1) | 🐢 O(n) | ArrayList |
| **途中への挿入** | 🐢 O(n) | 🐢 O(n) | どちらも遅い |
| **途中の削除** | 🐢 O(n) | ⚡ O(1)※ | LinkedList※ |
| **メモリ効率** | ✅ 良い | ❌ 悪い | ArrayList |
| **実務での推奨** | ⭐⭐⭐⭐⭐ | ⭐ | **ArrayList** |

※ Iteratorで削除する場合

```java
// 99%のケース：ArrayList
List<String> list = new ArrayList<>();

// 先頭への追加/削除が非常に多い場合のみ：LinkedList
List<String> list = new LinkedList<>();
```

#### 🛠️ Set 実装クラスの比較

| 特徴 | HashSet | LinkedHashSet | TreeSet |
| --- | --- | --- | --- |
| **順序** | なし | 挿入順 | ソート順 |
| **速度** | ⚡ 最速 | 🏃 速い | 🐢 やや遅い |
| **null** | ✅ 1つだけOK | ✅ 1つだけOK | ❌ NG |
| **用途** | 一般的 | 順序保持 | ソート必要 |
| **実務での推奨** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐ |

```java
// 順序不要：HashSet（最速）
Set<String> set = new HashSet<>();
// 出力順序：不定

// 挿入順を保持：LinkedHashSet
Set<String> set = new LinkedHashSet<>();
set.add("C");
set.add("A");
set.add("B");
// 出力順序：C, A, B

// 自動ソート：TreeSet
Set<String> set = new TreeSet<>();
set.add("C");
set.add("A");
set.add("B");
// 出力順序：A, B, C
```

#### 🛠️ Map 実装クラスの比較

| 特徴 | HashMap | LinkedHashMap | TreeMap |
| --- | --- | --- | --- |
| **順序** | なし | 挿入順 | キーのソート順 |
| **速度** | ⚡ 最速 | 🏃 速い | 🐢 やや遅い |
| **null** | key:1つ<br>value:複数 | key:1つ<br>value:複数 | ❌ NG |
| **用途** | 一般的 | 順序保持 | ソート必要 |
| **実務での推奨** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐ |

```java
// 順序不要：HashMap（最速）
Map<String, Integer> map = new HashMap<>();

// 挿入順を保持：LinkedHashMap（LRUキャッシュなど）
Map<String, Integer> map = new LinkedHashMap<>();

// キーの自動ソート：TreeMap
Map<String, Integer> map = new TreeMap<>();
```

#### 💡 理解ポイント - 実装クラスの選択基準

```plaintext
順序が必要ない → Hash系（最速）
挿入順を保持したい → LinkedHash系
自動ソートしたい → Tree系
```

#### 📝 補足 - TreeSetとTreeMapの注意点

```java
// TreeSetはComparableが必要
Set<Todo> set = new TreeSet<>();  // ❌ Todoがソート不可ならエラー

// 独自クラスを使う場合はComparableを実装
public class Todo implements Comparable<Todo> {
    @Override
    public int compareTo(Todo other) {
        return this.title.compareTo(other.title);
    }
}

// またはComparatorを渡す
Set<Todo> set = new TreeSet<>((a, b) -> a.getTitle().compareTo(b.getTitle()));
```

### 4. 使い分けの判断フロー

#### 🎯 目的

実際のデータに対して、どのコレクションを選ぶべきかを判断できるようになる。

#### 🛠️ 判断フローチャート

```plaintext
データを複数管理したい
│
├─ キーと値のペア？
│   YES → Map
│   │     ├─ 順序不要 → HashMap
│   │     ├─ 挿入順保持 → LinkedHashMap
│   │     └─ キーソート → TreeMap
│   │
│   NO ↓
│
├─ 重複を許さない？
│   YES → Set
│   │     ├─ 順序不要 → HashSet
│   │     ├─ 挿入順保持 → LinkedHashSet
│   │     └─ 自動ソート → TreeSet
│   │
│   NO ↓
│
├─ 両端から操作？
│   YES → Deque
│   │     └─ ArrayDeque（推奨）
│   │
│   NO ↓
│
├─ FIFO（先入れ先出し）？
│   YES → Queue
│   │     ├─ 通常 → LinkedList
│   │     └─ 優先度付き → PriorityQueue
│   │
│   NO ↓
│
└─ 順序付きリスト
    └─ List
          ├─ 一般的 → ArrayList（99%）
          └─ 先頭操作多 → LinkedList（1%）
```

#### 💡 理解ポイント - 実例で判断

##### 例1：Todoリスト

```plaintext
Q: Todoを管理したい

判断プロセス：
- キーと値？ → NO（Todoオブジェクトだけ）
- 重複を許さない？ → NO（同じタイトルOK）
- 両端から操作？ → NO
- FIFO？ → NO
- 順序付き → YES（追加順に表示したい）

答え：List → ArrayList
```

```java
List<Todo> todos = new ArrayList<>();
```

##### 例2：タグ

```plaintext
Q: Todoのタグを管理したい

判断プロセス：
- キーと値？ → NO
- 重複を許さない？ → YES（同じタグは1回だけ）
- 順序不要

答え：Set → HashSet
```

```java
Set<String> tags = new HashSet<>();
```

##### 例3：ID検索

```plaintext
Q: TodoをIDで高速検索したい

判断プロセス：
- キーと値？ → YES（ID → Todo）
- 順序不要

答え：Map → HashMap
```

```java
Map<String, Todo> todoMap = new HashMap<>();
```

##### 例4：タスクキュー

```plaintext
Q: 処理待ちタスクを順番に処理したい

判断プロセス：
- FIFO（先入れ先出し）？ → YES

答え：Queue → LinkedList
```

```java
Queue<Task> taskQueue = new LinkedList<>();
```

##### 例5：操作履歴（Undo/Redo）

```plaintext
Q: 操作履歴を管理して、Undo/Redoしたい

判断プロセス：
- 両端から操作？ → YES（末尾に追加、末尾から削除）
- スタック的な動き → Deque

答え：Deque → ArrayDeque
```

```java
Deque<Action> undoStack = new ArrayDeque<>();
Deque<Action> redoStack = new ArrayDeque<>();
```

#### 📝 補足 - 複数のコレクションを併用

実務では、複数のコレクションを併用することもよくあります。

```java
public class TodoManager {
    private List<Todo> todos;              // 表示順用
    private Map<String, Todo> todoMap;     // ID検索用
    private Set<String> allTags;           // 全タグ（重複なし）

    public TodoManager() {
        this.todos = new ArrayList<>();
        this.todoMap = new HashMap<>();
        this.allTags = new HashSet<>();
    }

    public void addTodo(Todo todo) {
        todos.add(todo);                   // Listに追加
        todoMap.put(todo.getId(), todo);   // Mapにも追加
        allTags.addAll(todo.getTags());    // タグをSetに追加
    }

    public List<Todo> listTodos() {
        return todos;  // 順序保持
    }

    public Todo findById(String id) {
        return todoMap.get(id);  // 高速検索
    }

    public Set<String> getAllTags() {
        return allTags;  // 重複なし
    }
}
```

### 5. よくある質問と落とし穴

#### 🎯 目的

コレクション選択時のよくある疑問や注意点を理解する。

#### Q1. ListかSetか迷ったら？

**A. 重複を許すかどうかで判断**

```java
// 重複OK → List
List<String> history = new ArrayList<>();
history.add("ページA");
history.add("ページB");
history.add("ページA");  // 同じページに2回訪問 → OK

// 重複NG → Set
Set<String> visitedPages = new HashSet<>();
visitedPages.add("ページA");
visitedPages.add("ページB");
visitedPages.add("ページA");  // 無視される
```

#### Q2. ListとMapの併用は無駄？

**A. 用途が違うので無駄ではない**

- List：順序保持、全件表示
- Map：高速検索

```java
// データ量が多い場合は併用が効果的
private List<Todo> todos;      // 表示順
private Map<String, Todo> map;  // ID検索

// トレードオフ：
// - メモリ増（同じTodoを2箇所で参照）
// - 更新の手間（両方更新必要）
// + 検索速度大幅向上（O(n) → O(1)）
```

#### Q3. HashSetとTreeSetの使い分けは？

**A. ソートが必要かどうか**

```java
// ソート不要、最速 → HashSet
Set<String> tags = new HashSet<>();

// 常にソートされた状態 → TreeSet
Set<String> tags = new TreeSet<>();
for (String tag : tags) {
    System.out.println(tag);  // アルファベット順
}
```

#### Q4. nullを入れられる？

**A. 実装クラスによる**

| コレクション | null対応 |
| --- | --- |
| ArrayList | ✅ OK |
| LinkedList | ✅ OK |
| HashSet | ✅ 1つだけOK |
| TreeSet | ❌ NG（NullPointerException） |
| HashMap | ✅ key:1つ、value:複数OK |
| TreeMap | ❌ NG（NullPointerException） |
| ArrayDeque | ❌ NG（NullPointerException） |

```java
// ArrayList
List<String> list = new ArrayList<>();
list.add(null);  // OK

// TreeSet
Set<String> set = new TreeSet<>();
set.add(null);  // ❌ NullPointerException
```

#### Q5. スレッドセーフなコレクションは？

**A. 基本的にスレッドセーフではない**

マルチスレッドで使う場合：

```java
// 方法1：同期化ラッパー
List<String> list = Collections.synchronizedList(new ArrayList<>());

// 方法2：並行処理用コレクション（推奨）
List<String> list = new CopyOnWriteArrayList<>();
Map<String, String> map = new ConcurrentHashMap<>();
```

#### 💡 理解ポイント - よくある落とし穴

##### 落とし穴1：ループ中の削除

```java
// ❌ NG：ConcurrentModificationException
List<String> list = new ArrayList<>();
list.add("A");
list.add("B");
list.add("C");

for (String item : list) {
    if (item.equals("B")) {
        list.remove(item);  // ❌ エラー
    }
}

// ✅ OK：Iteratorを使う
Iterator<String> iterator = list.iterator();
while (iterator.hasNext()) {
    String item = iterator.next();
    if (item.equals("B")) {
        iterator.remove();  // ✅ OK
    }
}

// ✅ OK：removeIfを使う（Java 8以降）
list.removeIf(item -> item.equals("B"));
```

##### 落とし穴2：HashSetの順序に依存

```java
// ❌ NG：HashSetの順序は不定
Set<String> set = new HashSet<>();
set.add("A");
set.add("B");
set.add("C");
// 出力順序は保証されない

// ✅ OK：順序が必要ならLinkedHashSet
Set<String> set = new LinkedHashSet<>();
```

##### 落とし穴3：Mapのキーに可変オブジェクト

```java
// ❌ NG：キーが変更されると検索できなくなる
class MutableKey {
    private String value;
    public void setValue(String value) { this.value = value; }
    // equals/hashCodeはvalueに依存
}

Map<MutableKey, String> map = new HashMap<>();
MutableKey key = new MutableKey("A");
map.put(key, "値");

key.setValue("B");  // キーを変更
String value = map.get(key);  // ❌ 見つからない！

// ✅ OK：キーは不変オブジェクトを使う
Map<String, String> map = new HashMap<>();
```

#### 📝 補足 - パフォーマンス測定してから最適化

```java
// 小規模（〜1000件）ならListで十分
List<Todo> todos = new ArrayList<>();
for (Todo todo : todos) {
    if (todo.getId().equals(targetId)) {
        // 見つかった
    }
}

// 大規模（10000件以上）ならMapを検討
Map<String, Todo> todoMap = new HashMap<>();
Todo todo = todoMap.get(targetId);  // O(1)

// でも最初から最適化しすぎない
// 「測定してから最適化」が鉄則
```

## 学び・次に活かせる知見

- **コレクションフレームワークの全体像** - List/Set/Map/Queue/Dequeの特性と使い分けを理解
- **判断フローの習得** - データの特性から適切なコレクションを選択できる
- **実装クラスの選択** - Hash系/LinkedHash系/Tree系の違いと使い分け
- **よくある落とし穴** - ループ中の削除、順序への依存、可変キーなど
- **複数コレクションの併用** - 用途に応じて複数のコレクションを組み合わせる
- **過度な最適化を避ける** - 小規模なうちはシンプルに、測定してから最適化

**実務での鉄則**：

1. **迷ったらArrayList** - 99%のケースで正解
2. **重複NGならHashSet** - 一意性が必要な場合
3. **ID検索ならHashMap** - キーによる高速検索
4. **順序が必要なら注意** - Hash系は順序なし、LinkedHash系は挿入順、Tree系はソート順
5. **測定してから最適化** - 早すぎる最適化は悪

**次のステップ**：

- ジェネリクス（`<T>`）の深掘り
- Stream APIでのコレクション操作
- `equals()` と `hashCode()` の実装（HashSet/HashMapの仕組み）
- `Comparable` と `Comparator`（ソートの仕組み）
- 並行処理用コレクション（ConcurrentHashMapなど）

## 参考文献

1. [Java公式ドキュメント - Collections Framework](https://docs.oracle.com/javase/8/docs/technotes/guides/collections/overview.html)
2. [Effective Java 第3版](https://www.amazon.co.jp/dp/4621303252) - Chapter 9: General Programming
3. [Java Collections Framework Overview](https://docs.oracle.com/javase/tutorial/collections/index.html)
4. [Which Java Collection should I use?](https://www.baeldung.com/java-choose-list-set-queue-map)

## 更新履歴

- 2026-01-01：初版公開
