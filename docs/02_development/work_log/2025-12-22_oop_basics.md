# 作業ログ: 2025-12-21 (OOP編)

## 学習情報

- **日付**: 2025-12-21
- **学習フェーズ**: WBS 1.0.2 オブジェクト指向プログラミング
- **学習時間**: 約2-3時間
- **ステータス**: オブジェクト指向の主要概念をすべて完了

---

## 完了したタスク

### プロジェクト情報

- **プロジェクト名**: OOPBasics (Object-Oriented Programming Basics)
- **プロジェクトパス**: Windows: C:\Users\sakih\IdeaProjects\OOPBasics
- **ビルドシステム**: Maven
- **JDK**: Microsoft OpenJDK 17

---

## オブジェクト指向プログラミングの学習

### 1. クラスとオブジェクトの概念

#### 学んだ概念

**オブジェクト指向とは？**
- プログラムを「モノ（オブジェクト）」の集まりとして考える方法
- 現実世界のモノをプログラムで表現する

**クラスとオブジェクトの関係：**
- **クラス** = 設計図（データと動作を定義）
- **オブジェクト（インスタンス）** = 設計図から作られた実物

**現実世界の例：**
- 車の設計図（クラス） → 実際の車（オブジェクト）
- 1つの設計図から複数の車を作れる
- 各車は独立している（色や速度が異なる）

---

#### 1.1 Carクラスの作成 (`Car.java`)

**学んだ要素：**
1. **フィールド（データ）** - クラスが持つ変数
2. **メソッド（動作）** - クラスが持つ機能

**最初のバージョン：**

```java
public class Car {
    // フィールド（データ）
    String color;
    int speed;

    // メソッド（動作）
    void drive() {
        System.out.println("車が走ります");
    }

    void stop() {
        System.out.println("車が止まります");
    }

    void showInfo() {
        System.out.println("色: " + color);
        System.out.println("速度: " + speed);
    }
}
```

**つまずいた点と解決：**

1. **問題**: 最初メソッドをセミコロン `;` で終わらせた
   ```java
   void drive();  // ❌ 処理の本体がない
   ```
   - **解決**: メソッドには必ずブロック `{ }` が必要
   ```java
   void drive() {  // ✅ 正しい
       System.out.println("車が走ります");
   }
   ```

---

#### 1.2 オブジェクトの作成と使用 (`Main.java`)

**学んだ概念：**
- `new` キーワードでオブジェクトを作成
- `.` （ドット）でフィールドやメソッドにアクセス
- 1つのクラスから複数のオブジェクトを作成可能
- 各オブジェクトは独立している

**作成したコード：**

```java
public class Main {
    public static void main(String[] args) {
        // オブジェクトの作成
        Car car1 = new Car();
        car1.color = "赤";
        car1.speed = 60;
        car1.showInfo();
        car1.drive();
        car1.stop();

        // 2台目の車
        Car car2 = new Car();
        car2.color = "青";
        car2.speed = 80;
        car2.showInfo();
    }
}
```

**実行結果：**

```
色: 赤
速度: 60
車が走ります
車が止まります
色: 青
速度: 80
```

**理解したポイント：**
- car1とcar2は独立したオブジェクト
- car1の色を変えてもcar2には影響しない
- これがオブジェクトの独立性

---

### 2. コンストラクタ

#### 学んだ概念

**コンストラクタとは：**
- オブジェクト作成時に自動的に呼ばれる特別なメソッド
- クラス名と同じ名前
- 戻り値の型を書かない（`void` も書かない）
- 主な目的：フィールドの初期化

**コンストラクタの利点：**
- オブジェクト作成時に値を設定できる
- コードがシンプルになる
- 値の設定忘れを防げる

---

#### 2.1 Car.javaにコンストラクタを追加

**追加したコード：**

```java
public class Car {
    String color;
    int speed;

    // コンストラクタ
    Car(String c, int s) {
        color = c;
        speed = s;
    }

    // メソッドは同じ
    void drive() { ... }
    void stop() { ... }
    void showInfo() { ... }
}
```

**Main.javaの変更：**

**修正前（3行）：**
```java
Car car1 = new Car();
car1.color = "赤";
car1.speed = 60;
```

**修正後（1行）：**
```java
Car car1 = new Car("赤", 60);
```

**実行結果：**
- 以前と同じ結果
- コードがシンプルになった

---

#### 重要な質問と理解

**Q: 変数を宣言した時点で初期値が入るのか？**

A: その通りです！完璧な理解です。

**フィールドのデフォルト値：**

| 型 | デフォルト値 |
|---|---|
| int | 0 |
| double | 0.0 |
| boolean | false |
| String (参照型) | null |
| その他のオブジェクト型 | null |

**コンストラクタの役割：**
1. オブジェクト作成時（`new`）、フィールドはデフォルト値で初期化される
2. その直後にコンストラクタが呼ばれる
3. コンストラクタでデフォルト値を更新する

```java
String color;  // 自動的に null が入る
int speed;     // 自動的に 0 が入る

Car(String c, int s) {
    color = c;  // null を "赤" に更新
    speed = s;  // 0 を 60 に更新
}
```

**重要な補足：**
- **フィールド（クラスの変数）** → 自動的に初期化される
- **ローカル変数（メソッド内の変数）** → 自動初期化されない（初期化必須）

---

#### コンストラクタの配置場所

**推奨されるクラス構造：**

```java
public class Car {
    // 1. フィールド（変数）
    private String color;
    private int speed;

    // 2. コンストラクタ
    Car(String c, int s) {
        ...
    }

    // 3. getter/setter

    // 4. その他のメソッド
}
```

**理由：**
- 読みやすい - 最初にどんなデータを持つか分かる
- 慣習 - Javaプログラマーの多くがこの順序
- 論理的 - コンストラクタはフィールドを初期化するので、フィールドを先に見せる

---

### 3. カプセル化（Encapsulation）

#### 学んだ概念

**カプセル化とは：**
- データを保護する仕組み
- フィールドを `private` にして外部から直接アクセスできなくする
- getter/setterメソッドで値の取得・設定を制御する

**カプセル化の目的：**
- 不正な値の設定を防ぐ
- データの整合性を保つ
- 内部実装を隠蔽する

---

#### 3.1 Car.javaをカプセル化

**変更内容：**

1. **フィールドをprivateにする**
   ```java
   private String color;
   private int speed;
   ```

2. **getter/setterメソッドを追加**
   ```java
   // getter - 値を取得
   String getColor() {
       return color;
   }

   int getSpeed() {
       return speed;
   }

   // setter - 値を設定（バリデーション付き）
   void setSpeed(int s) {
       if (s >= 0) {  // 負の値をチェック
           speed = s;
       } else {
           System.out.println("速度は0以上にしてください");
       }
   }
   ```

---

#### 3.2 privateの効果を確認

**テストコード（Main.java）：**

```java
Car car1 = new Car("赤", 60);
car1.color = "緑";  // ← privateなのでエラー
```

**エラーメッセージ：**
```
java: colorはCarでprivateアクセスされます
```

**これがカプセル化の目的！**
- フィールドに直接アクセスできない
- 不正な値を設定できない

---

#### 3.3 正しい方法で値を変更

**Main.java：**

```java
Car car1 = new Car("赤", 60);
car1.showInfo();

// 正しい方法：setter を使う
car1.setSpeed(100);
car1.showInfo();

// 不正な値を設定しようとする
car1.setSpeed(-50);  // エラーメッセージが表示される
car1.showInfo();
```

**実行結果：**

```
色: 赤
速度: 60
色: 赤
速度: 100
速度は0以上にしてください
色: 赤
速度: 100  ← 不正な値は設定されず、100のまま
```

**カプセル化の効果を確認：**

**❌ できなくなること（良いこと）：**
```java
car1.color = "緑";     // エラー！直接変更できない
car1.speed = -100;     // エラー！不正な値を設定できない
```

**✅ できること（正しい方法）：**
```java
car1.getColor();       // OK！getter で値を取得
car1.setSpeed(80);     // OK！setter で値を設定（チェック付き）
```

---

### 4. 継承（Inheritance）

#### 学んだ概念

**継承とは：**
- 既存のクラスを拡張して新しいクラスを作る仕組み
- 親クラス（スーパークラス）の機能を子クラス（サブクラス）が受け継ぐ
- コードの再利用が可能

**継承の例：**
- **Car（車）** - 基本クラス
- **ElectricCar（電気自動車）** - Carを継承
- ElectricCarはCarのすべての機能を持ち、さらにバッテリー機能を追加

---

#### 4.1 ElectricCarクラスの作成 (`ElectricCar.java`)

**作成したコード：**

```java
public class ElectricCar extends Car {
    private int battery;  // バッテリー残量（追加のフィールド）

    // コンストラクタ
    ElectricCar(String c, int s, int b) {
        super(c, s);  // 親クラス（Car）のコンストラクタを呼ぶ
        battery = b;
    }

    // 追加のメソッド
    void charge() {
        battery = 100;
        System.out.println("充電完了！バッテリー: " + battery + "%");
    }

    void showBattery() {
        System.out.println("バッテリー残量: " + battery + "%");
    }
}
```

**重要なキーワード：**
- **extends Car** - Carクラスを継承する
- **super(c, s)** - 親クラスのコンストラクタを呼び出す

---

#### 4.2 継承の動作確認

**Main.java：**

```java
// 通常の車
Car car1 = new Car("赤", 60);
car1.showInfo();
car1.drive();

// 電気自動車
ElectricCar eCar = new ElectricCar("青", 80, 50);
eCar.showInfo();      // ← Carから継承したメソッド
eCar.drive();         // ← Carから継承したメソッド
eCar.showBattery();   // ← ElectricCar独自のメソッド
eCar.charge();        // ← ElectricCar独自のメソッド
eCar.showBattery();
```

**実行結果：**

```
色: 赤
速度: 60
車が走ります
---
色: 青
速度: 80
車が走ります        ← Carから継承！
バッテリー残量: 50%   ← ElectricCar独自
充電完了！バッテリー: 100%
バッテリー残量: 100%
```

**ElectricCarが持っているもの：**

1. **Carから継承したもの（自動的に使える）：**
   - フィールド: `color`, `speed`
   - メソッド: `drive()`, `stop()`, `showInfo()`

2. **ElectricCar独自のもの：**
   - フィールド: `battery`
   - メソッド: `charge()`, `showBattery()`

**継承の利点：**
- コードの再利用（Carの機能を書き直す必要がない）
- 拡張が簡単（新しい機能だけ追加すればいい）

---

### 5. インターフェース（Interface）

#### 学んだ概念

**インターフェースとは：**
- 「こういうメソッドを持つべき」という**契約**を定義するもの
- 実装の詳細は含まず、メソッドの宣言だけ
- クラスは `implements` でインターフェースを実装する

**インターフェースの目的：**
- 共通の「できること」を定義
- 異なるクラスを統一的に扱える（ポリモーフィズム）

**例：**
- 車（Car）、飛行機（Airplane）、ボート（Boat）
- すべて「乗り物（Vehicle）」として共通の動作（start, stop）を持つ

---

#### 5.1 Vehicleインターフェースの作成 (`Vehicle.java`)

**作成したコード：**

```java
public interface Vehicle {
    void start();     // 開始する
    void stop();      // 停止する
    void showInfo();  // 情報を表示
}
```

**重要なポイント：**
- メソッドは宣言だけ（処理の本体 `{ }` がない）
- これは「このメソッドを実装してください」という契約

---

#### 5.2 CarクラスでVehicleインターフェースを実装

**Car.javaの変更：**

**変更1: インターフェースの実装宣言**
```java
public class Car implements Vehicle {
```

**変更2: start()メソッドを追加**
```java
public void start() {
    System.out.println("エンジンをかけます");
}
```

**変更3: 既存のメソッドにpublicを追加**
```java
public void stop() {
    System.out.println("車が止まります");
}

public void showInfo() {
    System.out.println("色: " + color);
    System.out.println("速度: " + speed);
}
```

---

#### 重要な質問と理解

**Q: なぜインターフェースを実装するメソッドには public を付ける必要があるのか？**

A: インターフェースのメソッドはデフォルトで `public` だから。

**詳細な理由：**

1. **インターフェースのメソッドは必ず public**
   ```java
   public interface Vehicle {
       void start();  // 実際は public abstract void start();
   }
   ```
   - インターフェースは**契約**
   - 契約は**誰でもアクセスできる**必要がある

2. **オーバーライドのルール**
   - 「オーバーライドするメソッドは、元のメソッドよりアクセスを制限できない」

   **アクセスレベル（広い → 狭い）：**
   ```
   public > protected > (何もなし) > private
   ```

3. **契約の整合性**
   ```java
   public interface Vehicle {
       void start();  // 「外部から呼べる」という契約
   }

   public class Car implements Vehicle {
       private void start() {  // もしこれが許されたら...
           // 外部から呼べない！契約違反！
       }
   }
   ```

**まとめ：**
- インターフェースのメソッドは必ず public（契約は公開）
- 実装クラスのメソッドも public にする必要がある（契約を守るため）

---

#### 5.3 インターフェースの動作確認

**Main.java：**

```java
// Vehicleインターフェース型で宣言
Vehicle v1 = new Car("赤", 60);
Vehicle v2 = new ElectricCar("青", 80, 50);

// どちらもVehicleとして扱える
v1.start();
v1.showInfo();
v1.stop();

v2.start();
v2.showInfo();
v2.stop();
```

**実行結果：**

```
エンジンをかけます
色: 赤
速度: 60
車が止まります
---
エンジンをかけます
色: 青
速度: 80
車が止まります
```

**重要なポイント：**
- `Vehicle v1 = new Car(...)` - インターフェース型の変数に実装クラスのオブジェクトを代入できる
- CarもElectricCarも**Vehicleとして扱える**
- ElectricCarはCarを継承しているので、自動的にVehicleも実装している

**継承とインターフェースの関係：**
```
Vehicle (インターフェース)
   ↑ implements
  Car
   ↑ extends
ElectricCar
```

---

### 6. ポリモーフィズム（Polymorphism）

#### 学んだ概念

**ポリモーフィズム（多態性）とは：**
- **1つのインターフェース型で、異なる実装クラスを扱える**こと
- 「多態性」= 多様な形態を持つ性質

**ポリモーフィズムの例：**

```java
Vehicle v1 = new Car(...);           // CarをVehicleとして扱う
Vehicle v2 = new ElectricCar(...);   // ElectricCarをVehicleとして扱う

// どちらも同じように扱える
v1.start();  // Carのstart()が呼ばれる
v2.start();  // ElectricCarのstart()が呼ばれる
```

**ポリモーフィズムの力：**
- 型は同じ（Vehicle）
- 実際のオブジェクトは違う（CarかElectricCar）
- でも同じように扱える！

---

#### 6.1 ポリモーフィズムの実用例

**Main.java：**

```java
// 配列で複数のVehicleを管理
Vehicle[] vehicles = new Vehicle[3];
vehicles[0] = new Car("赤", 60);
vehicles[1] = new ElectricCar("青", 80, 50);
vehicles[2] = new Car("緑", 70);

// すべてのVehicleを同じように扱える！
for (int i = 0; i < vehicles.length; i++) {
    System.out.println("--- Vehicle " + (i + 1) + " ---");
    vehicles[i].start();
    vehicles[i].showInfo();
    vehicles[i].stop();
}
```

**実行結果：**

```
--- Vehicle 1 ---
エンジンをかけます
色: 赤
速度: 60
車が止まります
--- Vehicle 2 ---
エンジンをかけます
色: 青
速度: 80
車が止まります
--- Vehicle 3 ---
エンジンをかけます
色: 緑
速度: 70
車が止まります
```

**ポリモーフィズムの実用的な利点：**

1. **統一的な管理**
   - `Vehicle[]` 配列に、CarもElectricCarも入れられる
   - 異なる型を1つの配列で管理できる

2. **コードの再利用**
   - for ループで同じコードですべてを処理
   - 型ごとに別々のコードを書く必要がない

3. **拡張性**
   - 将来、Airplane や Boat を追加しても、同じコードで扱える
   - 新しいクラスを追加してもメインコードの変更が不要

---

## 作成したファイル一覧

プロジェクト: `OOPBasics` (Windows: C:\Users\sakih\IdeaProjects\OOPBasics)

| # | ファイル名 | 種類 | 目的 | 主な内容 |
|---|-----------|------|------|---------|
| 1 | Vehicle.java | インターフェース | 乗り物の契約定義 | start(), stop(), showInfo() |
| 2 | Car.java | クラス | 基本的な車 | フィールド、コンストラクタ、getter/setter、Vehicleの実装 |
| 3 | ElectricCar.java | クラス | 電気自動車（継承） | Carを継承、battery追加、charge()追加 |
| 4 | Main.java | メインクラス | 実行プログラム | オブジェクト作成、ポリモーフィズムの実践 |

**合計**: 4ファイル

---

## オブジェクト指向の4大原則まとめ

### 1. カプセル化（Encapsulation）

**概念:**
- データを保護する
- `private` フィールド + getter/setter

**コード例:**
```java
private int speed;

void setSpeed(int s) {
    if (s >= 0) {
        speed = s;
    }
}
```

**利点:**
- 不正な値を防げる
- 内部実装を隠蔽できる

---

### 2. 継承（Inheritance）

**概念:**
- 既存クラスを拡張
- `extends` キーワード

**コード例:**
```java
public class ElectricCar extends Car {
    private int battery;
}
```

**利点:**
- コードの再利用
- 階層的な設計

---

### 3. ポリモーフィズム（Polymorphism）

**概念:**
- 同じインターフェースで異なる実装を扱う
- `implements` + インターフェース型変数

**コード例:**
```java
Vehicle v1 = new Car(...);
Vehicle v2 = new ElectricCar(...);
```

**利点:**
- 統一的な処理
- 拡張性

---

### 4. 抽象化（Abstraction）

**概念:**
- インターフェースで契約を定義
- 実装の詳細を隠す

**コード例:**
```java
public interface Vehicle {
    void start();
    void stop();
}
```

**利点:**
- 設計の明確化
- 実装の自由度

---

## 学習の進捗状況

### WBS 1.0.2 オブジェクト指向プログラミングの進捗

**完了項目**:
- ✅ クラスとオブジェクトの概念
- ✅ フィールドとメソッド
- ✅ コンストラクタ
- ✅ カプセル化（private, public, protected）
- ✅ 継承（extends）
- ✅ インターフェース（implements）
- ✅ ポリモーフィズム
- ✅ 簡単なクラス設計の練習

**進捗率**: 100%（WBS 1.0.2を完全に完了）

---

## 次回の学習予定

### WBS 1.0.3 Maven/Gradle基礎（0.8日）

**学習予定の内容**:
- Mavenとは何か（ビルドツール、依存関係管理）
- pom.xml の基本構造
- 依存関係の追加方法
- Mavenコマンド（mvn clean, mvn install, mvn package）
- Gradleとの違い（選択する場合）

---

### WBS 1.0.4 簡単なJavaプログラム作成（0.8日）

**学習予定の内容**:
- Hello World プログラム（Mavenプロジェクトとして）
- 計算機プログラム（クラスとメソッドを使用）
- Todoリストプログラム（簡易版、コンソール出力）
- 動作確認

---

## 重要な学習ポイント

### 1. staticとの違いが明確に

**今まで（基本文法）:**
```java
public static void main(String[] args) {
    sayHello();  // static メソッド
}

public static void sayHello() {
    // オブジェクト不要
}
```

**今回（OOP）:**
```java
public static void main(String[] args) {
    Car car1 = new Car("赤", 60);  // オブジェクト作成
    car1.drive();  // インスタンスメソッド
}
```

**理解したポイント:**
- **staticメソッド**: クラスに属する（オブジェクト不要）
- **インスタンスメソッド**: オブジェクトに属する（`new` で作成が必要）

---

### 2. フィールドの初期値

**重要な発見:**
- フィールド（クラスの変数）は自動的に初期化される
- int → 0、String → null、boolean → false
- コンストラクタでこれらの初期値を更新する

**コード例:**
```java
String color;  // 自動的に null
int speed;     // 自動的に 0

Car(String c, int s) {
    color = c;  // null を "赤" に更新
    speed = s;  // 0 を 60 に更新
}
```

---

### 3. アクセス修飾子の意味

**理解したアクセスレベル:**

| 修飾子 | 同じクラス | 同じパッケージ | サブクラス | すべて |
|--------|-----------|---------------|-----------|--------|
| private | ✅ | ❌ | ❌ | ❌ |
| (なし) | ✅ | ✅ | ❌ | ❌ |
| protected | ✅ | ✅ | ✅ | ❌ |
| public | ✅ | ✅ | ✅ | ✅ |

**使い分け:**
- **private**: フィールド（データ保護）
- **public**: メソッド（外部に公開）
- **(なし)**: 通常はあまり使わない
- **protected**: 継承で使う（今回は未使用）

---

### 4. クラス設計の順序

**推奨される順序（ベストプラクティス）:**

```java
public class ClassName {
    // 1. フィールド
    private String field1;
    private int field2;

    // 2. コンストラクタ
    ClassName(...) { }

    // 3. getter/setter
    public String getField1() { }
    public void setField1(String f) { }

    // 4. その他のメソッド
    public void method1() { }
    public void method2() { }
}
```

---

## つまずいた点とその解決

### 1. メソッド宣言の誤り

**問題:**
```java
void drive();  // セミコロンで終わらせた
```

**解決:**
```java
void drive() {  // ブロック { } が必要
    System.out.println("車が走ります");
}
```

**学び:** Javaのメソッドには必ず処理の本体が必要（インターフェースの抽象メソッドを除く）

---

### 2. コンストラクタの配置場所

**疑問:** コンストラクタはフィールドの前？後？

**回答:** フィールドの後が推奨
- 読みやすい
- 慣習
- 論理的（フィールドを先に見せる）

---

### 3. インターフェースメソッドのpublic

**疑問:** なぜインターフェースを実装するメソッドに `public` が必要？

**回答:**
1. インターフェースのメソッドはデフォルトで `public`
2. オーバーライドのルール：元のメソッドよりアクセスを制限できない
3. 契約の整合性：契約は公開されているべき

**学び:** インターフェースは「公開API」の契約

---

## メモ・気づき

### 良かった点

1. **段階的な学習**
   - 基本（クラス・オブジェクト）→ 発展（継承・インターフェース）
   - 各概念を実際に手を動かして確認

2. **エラーから学べた**
   - privateフィールドへのアクセスエラー
   - カプセル化の重要性を体感

3. **実用的な例**
   - 車（Car）、電気自動車（ElectricCar）
   - 現実世界と対応させやすい

4. **質問を通じた深い理解**
   - staticの意味
   - 初期値の仕組み
   - publicの必要性

### 理解が深まった概念

1. **オブジェクト指向の本質**
   - データと動作をまとめる
   - 現実世界をモデル化する

2. **継承の力**
   - コードの再利用
   - 拡張性

3. **ポリモーフィズムの実用性**
   - 異なる型を統一的に扱う
   - 配列やリストで管理

4. **カプセル化の重要性**
   - データ保護
   - バリデーション

---

## Spring Bootへの応用

### 今日学んだ概念がSpring Bootでどう使われるか

1. **クラスとオブジェクト**
   - Entity（データベースのテーブル）
   - DTO（データ転送オブジェクト）
   - Controller、Service、Repository（各レイヤー）

2. **カプセル化**
   - privateフィールド + getter/setter
   - データの整合性保証

3. **継承**
   - 共通の処理を持つ基底クラス
   - 例: BaseEntity（id, createdAt, updatedAtを持つ）

4. **インターフェース**
   - Repository interface（JpaRepository）
   - Service interface

5. **ポリモーフィズム**
   - 依存性注入（DI）
   - インターフェースに対してプログラミング

**次回以降の学習で、これらがどう使われるか実際に見ていきます。**

---

## 学習時間の記録

- **プロジェクト作成**: 5分
- **クラスとオブジェクト**: 30分
- **コンストラクタ**: 20分
- **カプセル化**: 30分
- **継承**: 25分
- **インターフェース**: 30分
- **ポリモーフィズム**: 20分

**合計学習時間**: 約2.5時間

---

## 参考にしたリソース

- WBS Phase1 計画書
- Java基本文法の学習（前回セッション）
- 質問を通じた深い理解

---

## 所感

オブジェクト指向プログラミングの主要概念をすべて学ぶことができた。特に、カプセル化、継承、インターフェース、ポリモーフィズムという4つの重要な概念を、実際に手を動かして理解できたのが大きい。

最初は抽象的に感じた概念も、Car と ElectricCar という具体例を通じて理解が深まった。特にポリモーフィズムの実用例（配列で異なる型を統一的に扱う）は、実際のプログラミングでの有用性がよく分かった。

staticとインスタンスメソッドの違い、フィールドの初期値、publicの必要性など、疑問に思ったことを質問して深く理解できたのも良かった。

次回からMaven/Gradleを学び、その後Spring Bootに進む準備が整った。今日学んだOOPの概念は、Spring Boot開発の基礎となる重要な知識だと理解している。

---

**作成日**: 2025-12-21
**次回学習予定日**: 未定
**WBS進捗**: 1.0.2 オブジェクト指向プログラミング 100%完了
