# Stream APIとは何か - リスト変換の基本

## 本記事を作成した背景

CategoryControllerで `List<Category>` を `List<CategoryResponse>` に変換する必要がありました。Javaでリストの各要素を別の型に変換するには、**Stream API** を使うのが一般的です。この記事では、Stream APIの基本的な使い方を学びます。

## 本記事で取り組んだこと

- Stream APIの基本概念を理解する
- `stream()`, `map()`, `toList()` の使い方を学ぶ
- リストの型変換パターンを習得する

## 手順

### 前提

- **環境**: Windows 11 + WSL2 (Ubuntu 24.04)
- **前提知識**: Javaの基本文法（List、ラムダ式の基礎）
- **Java バージョン**: Java 17以上（`toList()` を使用するため）

### 1. Stream APIとは

#### 🎯 目的

Stream APIの概念を理解します。

#### 💡 理解ポイント

**Stream API** は、Java 8で導入されたコレクション操作のための機能です。

**従来の方法（forループ）**:
```java
List<Category> categories = categoryService.findByUser(user);
List<CategoryResponse> responses = new ArrayList<>();

for (Category category : categories) {
    CategoryResponse response = new CategoryResponse(category);
    responses.add(response);
}

return responses;
```

**Stream APIを使う方法**:
```java
List<Category> categories = categoryService.findByUser(user);

return categories.stream()
        .map(category -> new CategoryResponse(category))
        .toList();
```

Stream APIを使うと、**宣言的**にデータ変換を記述できます。

---

### 2. 基本的なメソッド

#### 🎯 目的

よく使うStream APIのメソッドを理解します。

#### 🛠️ 手順詳細

| メソッド | 説明 | 例 |
|---------|------|-----|
| `stream()` | リストをStreamに変換 | `list.stream()` |
| `map()` | 各要素を変換 | `.map(x -> x * 2)` |
| `filter()` | 条件に合う要素だけ抽出 | `.filter(x -> x > 10)` |
| `toList()` | Streamをリストに変換 | `.toList()` |
| `collect()` | 結果を集める（汎用） | `.collect(Collectors.toList())` |

#### 💡 理解ポイント

**Streamの処理の流れ**:

```
List<Category>
    ↓ stream()
Stream<Category>
    ↓ map(category -> new CategoryResponse(category))
Stream<CategoryResponse>
    ↓ toList()
List<CategoryResponse>
```

1. `stream()` でリストをStreamに変換
2. `map()` で各要素を変換（Category → CategoryResponse）
3. `toList()` でStreamをリストに戻す

---

### 3. ラムダ式の書き方

#### 🎯 目的

`map()` の中で使うラムダ式の書き方を理解します。

#### 🛠️ 手順詳細

**ラムダ式の基本形**:
```java
(引数) -> { 処理 }
```

**省略形**（処理が1行の場合）:
```java
(引数) -> 処理
```

**例**:
```java
// 基本形
.map(category -> {
    return new CategoryResponse(category);
})

// 省略形（同じ意味）
.map(category -> new CategoryResponse(category))
```

#### 💡 理解ポイント

- `category` は変数名（好きな名前でOK）
- `->` の右側が変換処理
- 処理が1行なら `{}` と `return` を省略できる

---

### 4. 実践例：CategoryControllerでの使用

#### 🎯 目的

実際のコードでStream APIを使います。

#### 🛠️ 手順詳細

```java
@GetMapping
public List<CategoryResponse> list() {
    // 認証済みユーザーを取得
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userService.findByEmail(email);

    // カテゴリ一覧取得
    List<Category> categories = categoryService.findByUser(user);

    // List<Category> → List<CategoryResponse> に変換
    return categories.stream()
            .map(category -> new CategoryResponse(category))
            .toList();
}
```

#### 💡 理解ポイント

この変換が必要な理由:
- `Category` はエンティティ（DBの構造を反映）
- `CategoryResponse` はDTO（クライアントに返す情報だけを含む）
- エンティティを直接返すと、不要な情報（deletedAt等）も返してしまう

---

### 5. その他のStream操作例

#### 🎯 目的

Stream APIの他の使い方を知っておきます。

#### 🛠️ 手順詳細

**フィルタリング（条件で絞り込み）**:
```java
// 削除されていないカテゴリだけ取得
List<Category> activeCategories = categories.stream()
        .filter(category -> category.getDeletedAt() == null)
        .toList();
```

**複数の操作を組み合わせる**:
```java
// 削除されていないカテゴリをDTOに変換
List<CategoryResponse> responses = categories.stream()
        .filter(category -> category.getDeletedAt() == null)
        .map(category -> new CategoryResponse(category))
        .toList();
```

**要素数を数える**:
```java
long count = categories.stream()
        .filter(category -> category.getDeletedAt() == null)
        .count();
```

#### 📝 補足

Streamは**チェーン**で繋げて書くのが一般的です。各操作が次の操作に渡されます。

---

## 学び・次に活かせる知見

- `List<A>` を `List<B>` に変換するときは `stream().map().toList()` パターンを使う
- ラムダ式 `x -> ...` は「各要素xに対して...する」と読める
- Streamは元のリストを変更せず、新しいリストを作る（イミュータブル）

## 参考文献

1. [Java Stream API - Oracle公式ドキュメント](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html)
2. [Baeldung - Java 8 Stream Tutorial](https://www.baeldung.com/java-8-streams)

## 更新履歴

- 2026-01-27：初版公開
