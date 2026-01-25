# Javaのジェネリクスと型消去 - TypeTokenの仕組みを理解する

## 本記事を作成した背景

TodoManagerクラスでGsonを使ってJSONからデータを読み込む際、以下のような謎のコードに遭遇しました。

```java
Type listType = new TypeToken<List<Todo>>(){}.getType();
todos = gson.fromJson(reader, listType);
```

**なぜこんな回りくどい書き方をするのか？**
**そもそも `new TypeToken<List<Todo>>(){}` の `{}` って何？**
**listTypeには何が入っているのか？**

この疑問を深掘りすることで、Javaの**ジェネリクス**と**型消去（Type Erasure）**という重要な概念、そしてそれを回避するための**TypeToken**という仕組みを理解できました。

## 本記事で取り組んだこと

- ジェネリクス（Generics）とは何かを理解
- Javaの型消去（Type Erasure）の仕組みを学習
- なぜGson/JacksonがTypeTokenを必要とするのかを理解
- TypeTokenの正体と仕組みを解明
- `Type` オブジェクトに何が入っているかを確認
- 実践での使い方を習得

## 手順

### 前提

- **環境**: Java 17以降
- **前提知識**: List、コレクションの基本的な使い方を理解している
- **前提記事**: [05_JavaのListとArrayListとArray](./05_JavaのListとArrayListとArray%20-%20コレクション設計の基本.md)
- **使用ライブラリ**: Gson（JSONライブラリ）

### 1. ジェネリクス（Generics）とは何か？

#### 🎯 目的

TypeTokenを理解する前に、まず**ジェネリクス**という仕組みを理解する。

#### 🛠️ ジェネリクスの定義

**ジェネリクス（Generics）** とは：

> 「型をあとから決められる仕組み」＋「コンパイル時に型の安全性を保証するための仕組み」

#### ジェネリクスがない世界（Java 5より前）

```java
// 昔のJava（ジェネリクスなし）
List list = new ArrayList();
list.add("hello");
list.add(123);       // ← これも入ってしまう
list.add(new Todo());  // ← これも入ってしまう

// 取り出すとき
String s = (String) list.get(0);  // キャスト必須
String x = (String) list.get(1);  // ❌ 実行時エラー！
```

**問題点**：

- 何が入っているか分からない
- キャストミスは**実行時エラー**
- IDEの補完が効かない

#### ジェネリクスがある世界（Java 5以降）

```java
// ジェネリクスあり
List<String> list = new ArrayList<>();
list.add("hello");
list.add(123);  // ❌ コンパイルエラー

// 取り出すとき
String s = list.get(0);  // キャスト不要
```

**メリット**：

- 型安全（間違いをコンパイル時に発見）
- キャスト不要
- IDE補完が効く

#### 💡 理解ポイント - ジェネリクスの本質

**ジェネリクスは「型のルールを先に宣言する」こと**

```java
List<Todo>
```

これは日本語で言うと：

> 「このリストには `Todo` しか入れない」という契約

#### よく見る `<T>` の意味

```java
class Box<T> {
    private T value;

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
```

この `T` は：

- **型のプレースホルダ（仮の型）**
- 使う側が決める

```java
Box<String> box1 = new Box<>();
box1.set("hello");

Box<Todo> box2 = new Box<>();
box2.set(new Todo("買い物"));
```

**同じクラスを型だけ変えて再利用できる**

#### 📝 補足 - ジェネリクスのメリット

| メリット | 説明 |
| --- | --- |
| **型安全** | バグを早期発見 |
| **キャスト不要** | `(String)` などが不要 |
| **再利用性** | 同じクラスを異なる型で使える |
| **ドキュメント性** | コードを読めば型が分かる |

### 2. Javaの型消去（Type Erasure）

#### 🎯 目的

ジェネリクスの「落とし穴」である**型消去**を理解する。

#### 🛠️ 型消去とは何か？

**型消去（Type Erasure）** とは：

> Javaのジェネリクスでは、コンパイル後（実行時）に型引数（`<Todo>` や `<String>`）の情報が消える仕組み

#### 具体例で確認

```java
List<String> listA = new ArrayList<>();
List<Todo> listB = new ArrayList<>();

// 実行時（JVMの目線）
listA.getClass() == listB.getClass();  // true !!!
```

どちらも実行時には：

```java
java.util.ArrayList
```

👉 **`String` / `Todo` の違いは消えている**

#### 何が残って、何が消える？

| 情報 | コンパイル時 | 実行時 |
| --- | --- | --- |
| List である | ✅ | ✅ |
| 要素が Todo | ✅ | ❌ |
| 型チェック | ✅ | ❌ |

#### 💡 理解ポイント - なぜこんな仕様？

##### 理由1：後方互換性

Java 5より前にはジェネリクスがなかった。

```java
// 昔のJava
List list = new ArrayList();
```

これらの古いコードを**壊さずに**ジェネリクスを導入するため、実行時には昔と同じ振る舞いをする設計になった。

##### 理由2：JVMを複雑にしないため

実行時に型を全部保持すると：

- JVM・バイトコード・メモリ管理が複雑になる

👉 **コンパイル時だけ型安全を保証**する設計にした。

#### 型消去の実例

```java
// コンパイル前
List<String> list = new ArrayList<>();
list.add("hello");

// コンパイル後（概念的）
List list = new ArrayList();
list.add("hello");
```

#### 📝 補足 - たとえ話

**コンパイル時**：

> 「この箱には Todo しか入れちゃダメ」というラベル付き

**実行時**：

> 「箱」だけ（ラベルが剥がされている）

### 3. なぜGson/Jacksonが困るのか？

#### 🎯 目的

型消去がJSON変換でどんな問題を引き起こすかを理解する。

#### 🛠️ 問題の発生

##### 本当はこう書きたい（でも書けない）

```java
List<Todo> todos = gson.fromJson(reader, List<Todo>.class);
//                                        ^^^^^^^^^^^^^^
//                                        ❌ 書けない！
```

##### なぜダメ？

```java
List<Todo>.class   // ❌ こんな構文は存在しない
List.class         // ✅ これしか書けない
```

でも `List.class` だと：

```java
// Gsonの気持ち
「Listなのは分かった... でも中身の型が分からない😢」
```

##### 実際に起きること

```java
List<Todo> todos = gson.fromJson(reader, List.class);

// todosの中身を見ると...
for (Object obj : todos) {
    System.out.println(obj.getClass());
    // com.google.gson.internal.LinkedTreeMap ← ？？？
}
```

👉 **よくわからないオブジェクト**になってしまう

#### 💡 理解ポイント - なぜこうなる？

**JSON → オブジェクト変換は実行時処理**

でも：

- JVMは「List」までしか知らない
- 「中身は Todo」という情報がない

👉 Gsonは正しく復元できない

#### 図解

```plaintext
JSON
 ↓
[List]      ← 型情報なし
 ↓
LinkedTreeMap 😢  ← よくわからないオブジェクト

JSON
 ↓
[List<Todo>]  ← 型情報あり
 ↓
Todo オブジェクト 🎉  ← 正しく復元
```

#### 📝 補足 - どうすればいい？

**「`List<Todo>` という完全な型情報をGsonに教える」**

そのための仕組みが **TypeToken** です。

### 4. TypeTokenの正体

#### 🎯 目的

TypeTokenがどのようにして型情報を保持しているかを理解する。

#### 🛠️ TypeTokenの使い方

```java
Type listType = new TypeToken<List<Todo>>(){}.getType();
todos = gson.fromJson(reader, listType);
```

#### なぜこの形？

##### ポイント1：匿名クラス

```java
new TypeToken<List<Todo>>(){}
//                         ^^
//                         これが匿名クラス
```

これは実際には：

```java
// 概念的には、こういうクラスを作っている
class AnonymousTypeToken extends TypeToken<List<Todo>> {
}
```

##### ポイント2：継承時には型情報が残る

Javaでは：

```java
class MyClass extends TypeToken<List<Todo>> {}
```

この `List<Todo>` の情報は：

- **クラス定義の一部**として残る
- **型消去されない**

#### 💡 理解ポイント - 型消去を回避するトリック

**普通のインスタンス**：

```java
List<Todo>  →  List   （Todo消失）
```

**匿名サブクラス**：

```java
TypeToken<List<Todo>>
        ↑
        Todo がクラス定義として残る
```

#### なぜ `{}` が必要？

```java
// ❌ これだと型情報が消える
TypeToken<List<Todo>> token = new TypeToken<>();

// ✅ これだと型情報が残る
TypeToken<List<Todo>> token = new TypeToken<List<Todo>>(){};
//                                                       ^^
//                                                       匿名サブクラス化
```

`{}` があると：

- **匿名サブクラス**を作る
- サブクラス化すると → ジェネリクス情報が保持される

#### 📝 補足 - たとえ話

**普通の List**：

> 段ボール箱（ラベルなし）

**TypeToken を継承した匿名クラス**：

> 「この箱には Todo を入れる」とクラス定義に印刷されている段ボール箱
> （ラベルが箱そのものに刻まれている）

### 5. listTypeに入っているもの

#### 🎯 目的

`Type` オブジェクトに実際に何が入っているかを確認する。

#### 🛠️ listTypeの正体

```java
Type listType = new TypeToken<List<Todo>>(){}.getType();
```

このとき `listType` に入っているものは：

> 「List であり、要素の型が Todo である」という実行時に保持された**型の設計図**

#### 型を正確に言うと

```java
listType instanceof java.lang.reflect.Type  // true
```

そして実体としては多くの場合：

```java
listType instanceof ParameterizedType  // true
```

##### ParameterizedTypeとは？

**「ジェネリクス付き型」を表すためのインターフェース**

今回の場合の中身（概念的）：

```plaintext
listType
 ├─ raw type        : java.util.List
 └─ type arguments  : [ Todo ]
```

つまりJVMから見ると：

> 「これは List で、型引数は Todo」

という情報を**オブジェクトとして持っている**状態。

#### 💡 理解ポイント - 実際に中身を覗く

```java
Type type = new TypeToken<List<Todo>>(){}.getType();

System.out.println(type.getTypeName());
// 出力：java.util.List<org.example.Todo>
```

👆 これが `listType` の正体です。

#### ClassとTypeの違い

| 観点 | `List.class` | `listType` |
| --- | --- | --- |
| 型 | Class | Type（ParameterizedType） |
| 入る情報 | 「これは List である」 | 「これは List\<Todo\> である」 |
| ジェネリクス | ❌ 不明 | ✅ 完全に分かる |

#### 📝 補足 - Gson目線での違い

```java
// ❌ 型情報がない場合
gson.fromJson(reader, List.class);
// Gsonの気持ち：
// 「Listなのは分かった... でも中に何を入れればいいか分からない😢」

// ✅ listTypeを渡した場合
gson.fromJson(reader, listType);
// Gsonの気持ち：
// 「Listで、中身は Todo ね。了解！🎉」
```

### 6. TypeTokenがなぜ型引数を保持できるのか？

#### 🎯 目的

TypeTokenの「仕組み」を深く理解する。

#### 🛠️ 型消去は回避できない

まず大前提として：

```java
List<Todo>
```

の `<Todo>` は**実行時には必ず消えます**。

👉 TypeTokenは**JVMのルールを破っていません**。

#### じゃあ、なぜTypeTokenで分かるのか？

**キーワード：「継承時にはジェネリクス情報が残る」**

##### クラス定義には型引数が残る

Javaでは：

```java
class MyClass extends TypeToken<List<Todo>> {}
```

この `List<Todo>` は：

- **クラスのメタ情報**として保存される
- `Class` オブジェクトから取得可能
- **型消去されない**

#### JVMの視点で見ると

```java
new TypeToken<List<Todo>>(){}
```

これで作られる匿名クラスの `.class` には：

```plaintext
TypeToken<java.util.List<org.example.Todo>>
```

という情報が**クラス定義として埋め込まれている**。

#### getType() は何をしている？

```java
Type type = new TypeToken<List<Todo>>(){}.getType();
```

内部では（概念的に）：

```java
this.getClass()                  // 匿名クラスのClass
    .getGenericSuperclass()      // スーパークラスの型情報
```

を呼んでいます。

これで取れるもの：

```plaintext
TypeToken<List<Todo>>
```

👉 ジェネリクス込みの型情報

#### 💡 理解ポイント - 図解

```plaintext
通常のインスタンス
List<Todo>  →  List   （Todo消失）

匿名サブクラス
new TypeToken<List<Todo>>(){}
        ↓
    クラス定義に List<Todo> が保存される
        ↓
    getType() で取り出せる
```

#### 超重要な一文でまとめると

> TypeTokenは「型消去される前に、継承という仕組みで型引数をクラス定義として保存するテクニック」

#### 📝 補足 - たとえ話

**普通の List**：

> 段ボール箱にメモ紙で「Todo」と書いてある
> → 実行時にメモが剥がれる

**TypeToken**：

> 段ボール箱に「Todo」と印刷されている
> → 箱そのものに刻まれているので消えない

### 7. 実践での使い方

#### 🎯 目的

TypeTokenを実際のコードで使えるようになる。

#### 🛠️ Gsonでの使用例

##### パターン1：List\<Todo\>

```java
Type listType = new TypeToken<List<Todo>>(){}.getType();
List<Todo> todos = gson.fromJson(reader, listType);
```

##### パターン2：Map\<String, Todo\>

```java
Type mapType = new TypeToken<Map<String, Todo>>(){}.getType();
Map<String, Todo> todoMap = gson.fromJson(reader, mapType);
```

##### パターン3：List\<List\<String\>\>（ネスト）

```java
Type nestedListType = new TypeToken<List<List<String>>>(){}.getType();
List<List<String>> nestedList = gson.fromJson(reader, nestedListType);
```

#### 💡 理解ポイント - 配列ならTypeToken不要

```java
// 配列は型消去されない
Todo[] todos = gson.fromJson(reader, Todo[].class);  // ✅ OK
```

なぜ？

- 配列の型情報は実行時にも保持される
- `Todo[].class` で完全な型情報が取れる

#### Jacksonでの同等機能

Jacksonでは `TypeReference` を使います：

```java
// Jackson
List<Todo> todos = objectMapper.readValue(
    json,
    new TypeReference<List<Todo>>(){}  // TypeTokenと同じ仕組み
);
```

#### 📝 補足 - 覚え方

**「ListやMapをGson/Jacksonで読むときはTypeToken/TypeReference」**

この2パターンを覚えればOK：

```java
// Gson
Type t = new TypeToken<List<Todo>>(){}.getType();
Type t = new TypeToken<Map<String, Todo>>(){}.getType();

// Jackson
new TypeReference<List<Todo>>(){}
new TypeReference<Map<String, Todo>>(){}
```

### 8. よくある疑問

#### 🎯 目的

TypeTokenに関するよくある疑問を解消する。

#### Q1. なぜ配列（Todo[]）は型消去されない？

**A. 配列は言語組み込み機能で、ジェネリクスとは別の仕組み**

```java
Todo[] array = new Todo[10];
array.getClass();  // class [Lorg.example.Todo;
```

配列の型情報は実行時にも残っています。

```java
// だからこれでOK
Todo[] todos = gson.fromJson(reader, Todo[].class);
```

#### Q2. KotlinやC#ではTypeTokenは要らない？

**A. 要らない（実行時にジェネリクス情報が残る仕組みがある）**

Kotlin：

```kotlin
// Kotlinでは reified を使う
inline fun <reified T> fromJson(json: String): T {
    // T の型情報が実行時にも残る
}
```

C#：

```csharp
// C#ではジェネリクスの型情報が実行時にも残る
List<Todo> todos = JsonSerializer.Deserialize<List<Todo>>(json);
```

👉 Javaの型消去は**Javaの歴史的制約**

#### Q3. `new TypeToken<List<Todo>>(){}` の `{}` を忘れたら？

**A. 型情報が消えて、LinkedTreeMapになる**

```java
// ❌ {} がない
Type type = new TypeToken<List<Todo>>().getType();

// 型情報が取れない
// → LinkedTreeMapの配列になる
```

**`{}` を忘れないこと！**

#### Q4. 毎回書くのが面倒なんだけど？

**A. ヘルパーメソッドを作る**

```java
public class GsonUtil {
    public static <T> List<T> fromJsonList(String json, Class<T> clazz) {
        Type type = TypeToken.getParameterized(List.class, clazz).getType();
        return new Gson().fromJson(json, type);
    }
}

// 使う側
List<Todo> todos = GsonUtil.fromJsonList(json, Todo.class);
```

#### 💡 理解ポイント - ここまで理解できたあなたは

**「TypeTokenはおまじない」❌**

**「TypeTokenは仕組み」✅**

の領域に来ています。

## 学び・次に活かせる知見

- **ジェネリクス** - 型をパラメータとして扱い、コンパイル時に型安全を保証する仕組み
- **型消去（Type Erasure）** - Javaでは実行時にジェネリクスの型引数が消える
- **TypeTokenの仕組み** - 継承を使って型情報をクラス定義として保存するテクニック
- **Type vs Class** - Classはジェネリクス情報なし、TypeはParameterizedTypeとして完全な型情報を保持
- **匿名クラスの活用** - `{}` で匿名サブクラスを作ることで型情報を保存
- **配列は型消去されない** - 配列とジェネリクスは別の仕組み

**実務での使い方**：

```java
// Gsonでの基本パターン
Type listType = new TypeToken<List<Todo>>(){}.getType();
List<Todo> todos = gson.fromJson(reader, listType);

Type mapType = new TypeToken<Map<String, Todo>>(){}.getType();
Map<String, Todo> todoMap = gson.fromJson(reader, mapType);
```

**次のステップ**：

- Java Reflectionの深掘り
- `Class<T>` と `Type` の違い
- Kotlinの `reified` とJavaの型消去の比較
- Stream APIとジェネリクス
- ワイルドカード（`? extends T`、`? super T`）

## 参考文献

1. [Java公式ドキュメント - Generics](https://docs.oracle.com/javase/tutorial/java/generics/)
2. [Java公式ドキュメント - Type Erasure](https://docs.oracle.com/javase/tutorial/java/generics/erasure.html)
3. [Gson User Guide - TypeToken](https://github.com/google/gson/blob/master/UserGuide.md#TOC-Serializing-and-Deserializing-Generic-Types)
4. [Effective Java 第3版](https://www.amazon.co.jp/dp/4621303252) - Item 33: Consider typesafe heterogeneous containers

## 更新履歴

- 2026-01-01：初版公開
