# 作業ログ: 2025-12-21

## 学習情報

- **日付**: 2025-12-21
- **学習フェーズ**: WBS 1.0.1 Java基本文法学習（初日）
- **学習時間**: 約3-4時間
- **ステータス**: Java基本文法の主要部分を完了

---

## 完了したタスク

### 1. 開発環境のセットアップ

#### 1.1 JDKのインストール
- **環境**: WSL2 (Ubuntu 24.04) + Windows
- **WSL2側**:
  - OpenJDK 17.0.17 をインストール
  - コマンド: `sudo apt install openjdk-17-jdk -y`
  - JAVA_HOME環境変数を設定
  - 確認コマンド: `java -version`

- **Windows側**:
  - IntelliJ IDEA経由でMicrosoft OpenJDK 17をインストール
  - 理由: Windows上のIntelliJ IDEAからWSL2のJDKは使用できないため

#### 1.2 IntelliJ IDEA Community Editionのインストール
- **バージョン**: IntelliJ IDEA Community Edition 2025.2.6
- **インストール先**: Windows
- **初期設定**:
  - テーマ選択
  - プラグイン設定（デフォルトで開始）
  - JDKの設定（Microsoft OpenJDK 17）

#### 1.3 最初のプロジェクト作成
- **プロジェクト名**: HelloWorld
- **ビルドシステム**: Maven
- **JDK**: Microsoft OpenJDK 17
- **プロジェクト構造**:
  ```
  HelloWorld
  ├── .idea
  ├── src
  │   ├── main
  │   │   └── java
  │   └── test
  │       └── java
  └── pom.xml
  ```

#### トラブルシューティング
- **問題**: 初回実行時にRunウィンドウが表示されない
- **解決方法**: Invalidate Caches / Restart を実行
- **学び**: IntelliJ IDEAのキャッシュ問題の対処法を習得

---

### 2. Java基本文法の学習

#### 2.1 Hello World プログラム (`HelloWorld.java`)

**学んだ概念**:
- Javaプログラムの基本構造
- `public class クラス名` の書き方
- `main` メソッド - プログラムのエントリーポイント
- `System.out.println()` - コンソール出力

**作成したコード**:
```java
public class HelloWorld {
    public static void main(String[] args){
        System.out.println("Hello, World!");
    }
}
```

**実行結果**:
```
Hello, World!

Process finished with exit code 0
```

**理解したポイント**:
- クラス名はファイル名と一致する必要がある
- `main` メソッドは `public static void` で定義
- 文の最後には必ずセミコロン `;` が必要
- 終了コード 0 は正常終了を意味する

---

#### 2.2 変数と型 (`Variables.java`)

**学んだ概念**:
- Javaの基本データ型
- 変数の宣言と代入
- 文字列と数値の違い

**習得したデータ型**:
1. **int** - 整数型（例: 25, 100, -5）
2. **String** - 文字列型（例: "太郎", "Hello"）※ダブルクォートで囲む
3. **double** - 小数型（例: 175.5, 3.14）※ダブルクォートなし
4. **boolean** - 真偽値型（`true` または `false`）

**作成したコード**:
```java
public class Variables {
    public static void main(String[] args){
        int age = 25;
        String name = "太郎";
        double height = 175.5;
        boolean isStudent = true;
        System.out.println(age);
        System.out.println(name);
        System.out.println(height);
        System.out.println(isStudent);
    }
}
```

**実行結果**:
```
25
太郎
175.5
true
```

**つまずいた点と解決**:
- **問題**: 最初 `double height = "175.5";` と書いてしまった
- **エラー理由**: 数値型に文字列（ダブルクォート付き）を代入しようとした
- **解決**: ダブルクォートを削除して `175.5` に修正
- **学び**: 文字列はダブルクォートで囲む、数値は囲まない

---

#### 2.3 演算子 (`Operators.java`)

**学んだ概念**:
- 算術演算子
- 比較演算子
- 論理演算子
- コメントの書き方（`//`）

**習得した演算子**:

1. **算術演算子**:
   - `+` (足し算)
   - `-` (引き算)
   - `*` (掛け算)
   - `/` (割り算) ※int同士の割り算は整数除算
   - `%` (余り)

2. **比較演算子**:
   - `==` (等しい)
   - `!=` (等しくない)
   - `>` (より大きい)
   - `<` (より小さい)

3. **論理演算子**:
   - `&&` (AND: 両方true)
   - `||` (OR: どちらかtrue)
   - `!` (NOT: 否定)

**作成したコード**:
```java
public class Operators {
    public static void main(String args[]){
        // 算術演算子
        int a = 10;
        int b = 3;
        System.out.println(a + b);  // 13
        System.out.println(a - b);  // 7
        System.out.println(a * b);  // 30
        System.out.println(a / b);  // 3
        System.out.println(a % b);  // 1

        // 比較演算子
        int x = 5;
        int y = 10;
        System.out.println(x == y);  // false
        System.out.println(x != y);  // true
        System.out.println(x > y);   // false
        System.out.println(x < y);   // true

        // 論理演算子
        boolean isAdult = true;
        boolean hasLicense = false;
        System.out.println(isAdult && hasLicense);  // false
        System.out.println(isAdult || hasLicense);  // true
        System.out.println(!isAdult);               // false
    }
}
```

**重要な発見**:
- **整数除算**: `10 / 3 = 3` （小数点以下切り捨て）
- **余り演算**: `10 % 3 = 1` （10 ÷ 3 = 3 余り 1）
- **比較・論理演算の結果**: `boolean` 型（`true` または `false`）が返される

---

#### 2.4 制御構文 (`ControlFlow.java`)

**学んだ概念**:
- 条件分岐（if-else）
- ループ（for, while）
- ブロック `{ }` の使い方

**習得した制御構文**:

1. **if-else文** - 条件による分岐
   ```java
   if (条件式) {
       // 条件がtrueのときの処理
   } else {
       // 条件がfalseのときの処理
   }
   ```

2. **for文** - 決まった回数繰り返す
   ```java
   for (初期化; 条件; 更新) {
       // 繰り返す処理
   }
   ```

3. **while文** - 条件が満たされている間繰り返す
   ```java
   while (条件式) {
       // 繰り返す処理
   }
   ```

**作成したコード**:
```java
public class ControlFlow {
    public static void main(String args[]){
        // if文
        int score = 85;
        if (score >= 80){
            System.out.println("合格");
        } else {
            System.out.println("不合格");
        }

        // for文
        for (int i = 1; i <= 5; i++) {
            System.out.println(i);
        }

        // while文
        int count = 0;
        while (count < 3) {
            System.out.println(count);
            count++;
        }
    }
}
```

**実行結果**:
```
合格
1
2
3
4
5
0
1
2
```

**重要なポイント**:
- **変数のスコープ**: for文内で宣言した `i` はfor文の中でのみ有効
- **インクリメント**: `count++` は `count = count + 1` と同じ
- **無限ループ防止**: while文では必ず条件を変化させる処理が必要

---

#### 2.5 配列とArrayList (`ArraysAndLists.java`)

**学んだ概念**:
- 配列（固定サイズ）
- ArrayList（可変サイズ）
- import文の使い方
- インデックスの概念（0から始まる）

**配列（Array）の基本**:
- **宣言と初期化**: `型[] 配列名 = {要素1, 要素2, ...};`
- **要素へのアクセス**: `配列名[インデックス]`
- **配列の長さ**: `配列名.length`
- **特徴**: サイズが固定、変更不可

**ArrayListの基本**:
- **import**: `import java.util.ArrayList;`
- **作成**: `ArrayList<型> 変数名 = new ArrayList<>();`
- **要素の追加**: `変数名.add(値);`
- **要素の取得**: `変数名.get(インデックス)`
- **サイズ**: `変数名.size()`
- **特徴**: サイズが可変、要素の追加・削除が可能

**作成したコード**:
```java
import java.util.ArrayList;

public class ArraysAndLists {
    public static void main(String Args[]) {
        // Array
        String[] names = {"太郎", "花子", "次郎"};
        System.out.println(names[0]);
        System.out.println(names[1]);
        System.out.println(names.length);

        for (int i = 0; i< names.length; i++) {
            System.out.println(names[i]);
        }

        // ArrayList
        ArrayList<Integer> numbers = new ArrayList<>();
        numbers.add(10);
        numbers.add(20);
        numbers.add(30);

        System.out.println(numbers.get(0));
        System.out.println(numbers.size());

        for (int i = 0; i < numbers.size(); i++){
            System.out.println(numbers.get(i));
        }
    }
}
```

**つまずいた点と解決**:

1. **問題**: ArrayListのコードをmainメソッドの外に書いた
   - **エラー理由**: 実行されるコードは`main`メソッド内に書く必要がある
   - **解決**: すべてのコードを`main`メソッド内に移動

2. **問題**: `new AllayList<>()` とタイプミス
   - **解決**: `new ArrayList<>()` に修正

3. **問題**: ArrayListの初期化で `{ }` を使おうとした
   - **誤**: `ArrayList<Integer> numbers = new ArrayList<>(){ ... }`
   - **正**: `ArrayList<Integer> numbers = new ArrayList<>();` の後に`add()`

4. **問題**: for文で `System.out.println(i)` としてしまった
   - **エラー理由**: インデックス番号を表示してしまう
   - **解決**: `System.out.println(numbers.get(i))` に修正

**実行結果**:
```
太郎
花子
3
太郎
花子
次郎
10
3
10
20
30
```

---

#### 2.6 メソッド (`Methods.java`)

**学んだ概念**:
- メソッドの定義と呼び出し
- 引数と戻り値
- `static` の意味
- メソッドの配置場所

**メソッドの構造**:
```java
修飾子 戻り値の型 メソッド名(引数の型 引数名) {
    // 処理
    return 戻り値;  // 戻り値がある場合
}
```

**習得したメソッドのパターン**:

1. **引数なし、戻り値なし**:
   ```java
   public static void sayHello() {
       System.out.println("Hello!");
   }
   ```

2. **引数あり、戻り値なし**:
   ```java
   public static void greet(String name) {
       System.out.println("こんにちは" + name + "さん！");
   }
   ```

3. **引数あり、戻り値あり**:
   ```java
   public static int add(int a, int b) {
       return a + b;
   }
   ```

**作成したコード**:
```java
public class Methods {
    public static void main(String[] args) {
        // ここでメソッドを呼び出す
        sayHello();
        greet("太郎");
        int result = add(5,3);
        System.out.println(result);
    }

    public static void sayHello() {
        System.out.println("Hello!");
    }

    public static void greet(String name) {
        System.out.println("こんにちは" + name + "さん！");
    }

    public static int add(int a, int b) {
        return a + b;
    }
}
```

**実行結果**:
```
Hello!
こんにちは太郎さん！
8
```

**重要な質問と回答**:

**Q: `static` とは何か？**

A: `static` は「クラスに属する」という意味。

- **staticメソッド**: オブジェクトを作らなくても呼び出せる
- **非staticメソッド**: オブジェクトを作ってから呼び出す必要がある
- **なぜ今までstaticを使ってきたか**: `main`メソッドが`static`なので、`main`から呼び出すメソッドも`static`にする必要がある（オブジェクト指向を学ぶまでは）

**例**:
```java
// staticメソッド → そのまま呼び出せる
staticMethod();  // ✅ OK

// 非staticメソッド → オブジェクトが必要
Example obj = new Example();  // オブジェクトを作成
obj.instanceMethod();  // ✅ OK
```

**学び**: オブジェクト指向（WBS 1.0.2）を学ぶと、`static`の使い分けが完全に理解できる

---

#### 2.7 例外処理 (`ExceptionHandling.java`)

**学んだ概念**:
- 例外（Exception）とは何か
- try-catch構文
- 例外処理の重要性
- プログラムのクラッシュ vs 安全な処理

**例外処理の構文**:
```java
try {
    // エラーが起きる可能性のあるコード
} catch (例外の型 変数名) {
    // エラーが起きたときの処理
}
```

**作成したコード**:
```java
public class ExceptionHandling {
    public static void main(String[] args){
        try {
            Integer[] numbers = {1, 2, 3};
            System.out.println(numbers[5]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("エラー: 配列の範囲外です");
            System.out.println("詳細: " + e.getMessage());
        }

        System.out.println("プログラムは続行されます");
    }
}
```

**実行結果の比較**:

**try-catch なし（クラッシュ）**:
```
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: Index 5 out of bounds for length 3
    at ExceptionHandling.main(ExceptionHandling.java:5)

プロセスは終了コード 1 で終了しました
```
- ❌ プログラムがクラッシュ
- ❌ 「プログラムは続行されます」が表示されない
- ❌ 終了コード 1（エラー終了）

**try-catch あり（安全な処理）**:
```
エラー: 配列の範囲外です
詳細: Index 5 out of bounds for length 3
プログラムは続行されます

プロセスは終了コード 0 で終了しました
```
- ✅ エラーは発生したが適切に処理された
- ✅ 「プログラムは続行されます」が表示された
- ✅ 終了コード 0（正常終了）

**理解したポイント**:
- **例外処理の目的**: エラーが起きてもプログラム全体をクラッシュさせない
- **実際のアプリケーションでの重要性**: ユーザーに適切なエラーメッセージを表示し、対応を促すことができる
- **終了コードの意味**: 0は正常終了、1はエラー終了

---

## 作成したファイル一覧

プロジェクト: `HelloWorld` (Windows: C:\Users\sakih\IdeaProjects\HelloWorld)

| # | ファイル名 | 目的 | 行数 |
|---|-----------|------|------|
| 1 | HelloWorld.java | Javaの基本構造、Hello World | 5 |
| 2 | Variables.java | 変数と型（int, String, double, boolean） | 11 |
| 3 | Operators.java | 演算子（算術、比較、論理） | 24 |
| 4 | ControlFlow.java | 制御構文（if-else, for, while） | 18 |
| 5 | ArraysAndLists.java | 配列とArrayList | 24 |
| 6 | Methods.java | メソッドの定義と呼び出し | 19 |
| 7 | ExceptionHandling.java | 例外処理（try-catch） | 12 |

**合計**: 7ファイル、約113行のコード

---

## 学習の進捗状況

### WBS 1.0.1 Java基本文法学習の進捗

**完了項目** (WBS 1.0.1より):
- ✅ JDKのインストール（Java 17以上推奨）
- ✅ IDEのセットアップ（IntelliJ IDEA）
- ✅ 変数と型（int, String, boolean等）
- ✅ 演算子（算術、比較、論理）
- ✅ 制御構文（if, switch, for, while）
- ✅ 配列とArrayList
- ✅ メソッドの定義と呼び出し
- ✅ 例外処理（try-catch）

**残りの項目**（次回以降）:
- ⬜ 公式チュートリアル、オンライン学習サイト（Udemy、Progate等）を活用
- ⬜ より複雑な練習問題

**進捗率**: 約80-90%（WBS 1.0.1の主要部分を完了）

---

## 次回の学習予定

### WBS 1.0.2 オブジェクト指向（3.0日）

**学習予定の内容**:

1. **クラスとオブジェクトの概念**
   - クラスとは何か
   - オブジェクト（インスタンス）とは何か
   - クラスとオブジェクトの関係

2. **フィールドとメソッド**
   - インスタンス変数（フィールド）
   - インスタンスメソッド
   - staticとの違いの理解

3. **コンストラクタ**
   - コンストラクタとは
   - オブジェクトの初期化
   - デフォルトコンストラクタ

4. **カプセル化（private, public, protected）**
   - アクセス修飾子の意味
   - getter/setter
   - データの隠蔽

5. **継承（extends）**
   - クラスの継承
   - スーパークラスとサブクラス
   - オーバーライド

6. **インターフェース（implements）**
   - インターフェースとは
   - 抽象メソッド
   - インターフェースの実装

7. **ポリモーフィズム**
   - 多態性の概念
   - オーバーライドとオーバーロード
   - 抽象クラスとインターフェースの違い

8. **簡単なクラス設計の練習**
   - 実践的なクラス設計
   - オブジェクト指向の設計思想

**準備**:
- 今日学んだ基本文法の復習
- `static` の意味を再確認（今日質問した内容）

---

## メモ・気づき・質問

### 良かった点
1. **手を動かして学べた**: すべてのコードを自分で書いたことで理解が深まった
2. **エラーから学べた**: ミスをして修正することで、なぜそうなるのかが理解できた
3. **段階的な学習**: 簡単なものから徐々に複雑になる構成で理解しやすかった
4. **実行結果の確認**: すべてのプログラムを実行して動作を確認できた

### つまずいた点とその解決
1. **数値型にダブルクォート**: 文字列と数値の違いを理解
2. **ArrayListの初期化構文**: 正しい書き方を習得
3. **mainメソッドの外にコード**: 実行コードはmain内に書く必要があることを理解
4. **IntelliJ IDEAのキャッシュ問題**: Invalidate Cachesで解決

### 理解を深めた概念
1. **static の意味**: クラスに属する vs オブジェクトに属する
2. **例外処理の重要性**: プログラムのクラッシュ vs 安全な処理
3. **終了コードの意味**: 0は正常、1はエラー

### 次回に向けての疑問
1. オブジェクト指向とはどういう考え方なのか？
2. なぜクラスとオブジェクトを分けて考える必要があるのか？
3. Spring Bootでどのようにオブジェクト指向が使われるのか？

---

## 学習時間の記録

- **開発環境セットアップ**: 約1時間
  - JDKインストール: 20分
  - IntelliJ IDEAインストール: 20分
  - プロジェクト作成: 20分

- **Java基本文法学習**: 約2-3時間
  - Hello World: 15分
  - 変数と型: 20分
  - 演算子: 20分
  - 制御構文: 25分
  - 配列とArrayList: 30分
  - メソッド: 25分
  - 例外処理: 25分

**合計学習時間**: 約3-4時間

---

## 参考にしたリソース

- IntelliJ IDEA公式ドキュメント
- Javaの基本文法（検索して調べた内容）
- WBS Phase1 計画書

---

## 所感

初めてのJavaプログラミングだったが、開発環境のセットアップから基本文法まで、1日で多くのことを学ぶことができた。特に、自分で手を動かしてコードを書き、エラーに遭遇して解決するプロセスが非常に有益だった。

`static`の概念など、完全には理解できていない部分もあるが、次回のオブジェクト指向の学習で深く理解できると思う。

明日以降もこの調子で学習を進めていきたい。

---

**作成日**: 2025-12-21
**次回学習予定日**: 未定
**WBS進捗**: 1.0.1 Java基本文法学習 約80-90%完了
