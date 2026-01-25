# 作業ログ: 2025-12-21 (Maven編)

## 学習情報

- **日付**: 2025-12-21
- **学習フェーズ**: WBS 1.0.3 Maven/Gradle基礎
- **学習時間**: 約1時間
- **ステータス**: Maven基礎を完了

---

## 完了したタスク

### プロジェクト情報

- **プロジェクト名**: OOPBasics（継続使用）
- **プロジェクトパス**: Windows: C:\Users\sakih\IdeaProjects\OOPBasics
- **ビルドシステム**: Maven
- **JDK**: Microsoft OpenJDK 17

---

## Mavenの学習

### 1. Mavenとは何か

#### 学んだ概念

**Maven（メイヴン）とは：**
- Javaプロジェクトの**ビルドツール**
- Apache Softwareが提供する標準的なプロジェクト管理ツール

**Mavenができること：**

1. **ビルド管理**
   - コンパイル
   - テスト実行
   - パッケージング（JAR/WAR作成）

2. **依存関係管理**
   - 必要なライブラリを自動でダウンロード
   - バージョン管理
   - 依存関係の依存関係も自動解決

3. **プロジェクト構造の標準化**
   - どのMavenプロジェクトも同じディレクトリ構造
   - チーム開発がスムーズ

---

#### Mavenの必要性

**Mavenなしの場合（昔のやり方）：**
- ライブラリを手動でダウンロード
- プロジェクトごとに保存
- バージョン管理が大変
- ビルド手順を毎回書く
- チームメンバー間で環境が異なる

**Mavenありの場合：**
- 必要なライブラリを設定ファイル（pom.xml）に書くだけ
- 自動でダウンロード
- 標準的な構造で管理
- コマンド一発でビルド
- どの環境でも同じようにビルドできる

---

### 2. pom.xmlの基本構造

#### pom.xmlとは

**POM (Project Object Model)**
- Mavenプロジェクトの設定ファイル
- XML形式
- プロジェクトのルートに配置

---

#### 2.1 初期状態のpom.xml

**OOPBasicsプロジェクトのpom.xml：**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>OOPBasics</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

</project>
```

---

#### 2.2 pom.xmlの各要素の意味

**1. modelVersion**
```xml
<modelVersion>4.0.0</modelVersion>
```
- Mavenのバージョン
- 固定値、通常変更しない

---

**2. プロジェクトの座標（識別情報）**

```xml
<groupId>org.example</groupId>
<artifactId>OOPBasics</artifactId>
<version>1.0-SNAPSHOT</version>
```

**各要素の意味：**

| 要素 | 意味 | 例 |
|------|------|-----|
| groupId | プロジェクトのグループ（会社やプロジェクトのドメイン） | com.example, org.springframework |
| artifactId | プロジェクト名 | OOPBasics, spring-boot-starter |
| version | バージョン | 1.0-SNAPSHOT, 2.3.4.RELEASE |

**SNAPSHOTとは：**
- 開発中のバージョンを意味する
- 正式リリース版は `1.0.0`、`2.0.0` など

---

**3. properties（プロパティ）**

```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

**各プロパティの意味：**

| プロパティ | 意味 |
|-----------|------|
| maven.compiler.source | ソースコードのJavaバージョン |
| maven.compiler.target | コンパイル後のJavaバージョン |
| project.build.sourceEncoding | ファイルの文字エンコーディング |

---

### 3. 依存関係の追加

#### 3.1 依存関係（dependencies）とは

**依存関係：**
- プロジェクトが使用する外部ライブラリ
- Mavenが自動的にダウンロードして管理

**例：**
- JSON処理 → Gson、Jackson
- ログ出力 → Logback、Log4j
- テスト → JUnit、Mockito
- Webフレームワーク → Spring Boot

---

#### 3.2 Gson（JSON処理ライブラリ）の追加

**追加した依存関係：**

```xml
<dependencies>
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>
</dependencies>
```

**依存関係の構造：**
- **groupId**: ライブラリの提供元（com.google.code.gson）
- **artifactId**: ライブラリ名（gson）
- **version**: バージョン（2.10.1）

---

#### 3.3 依存関係のダウンロード

**手順：**

1. pom.xmlに依存関係を追加
2. ファイルを保存
3. Mavenプロジェクトを再ロード
   - 方法1: Mavenウィンドウの「Reload All Maven Projects」ボタン
   - 方法2: pom.xml右クリック → Maven → Reload project

**結果：**
- Mavenが自動的にGson 2.10.1をダウンロード
- Maven Central Repository（https://repo.maven.apache.org/maven2/）から取得
- ローカルリポジトリ（C:\Users\sakih\.m2\repository）に保存

**確認方法：**
- External Libraries → Maven: com.google.code.gson:gson:2.10.1

---

#### つまずいた点と解決

**問題：**
- pom.xmlに依存関係を追加したが、Dependenciesが表示されない

**解決方法：**
- Mavenウィンドウで「すべてのMavenプロジェクトを再ロード」を実行
- External Librariesにgson:2.10.1が表示されるようになった

**学び：**
- pom.xmlを編集したら必ず再ロードが必要
- IntelliJ IDEAが自動で再ロードすることもあるが、手動実行が確実

---

### 4. 依存関係を使ったプログラム作成

#### 4.1 JsonExampleクラスの作成

**目的：**
- Gsonライブラリが正しく動作するか確認
- JSONの変換処理を体験

**作成したコード（JsonExample.java）：**

```java
import com.google.gson.Gson;

public class JsonExample {
    public static void main(String[] args) {
        // CarオブジェクトをJSONに変換
        Car car = new Car("赤", 60);

        Gson gson = new Gson();
        String json = gson.toJson(car);

        System.out.println("JSONに変換:");
        System.out.println(json);

        // JSONをCarオブジェクトに変換
        String jsonString = "{\"color\":\"青\",\"speed\":80}";
        Car car2 = gson.fromJson(jsonString, Car.class);

        System.out.println("\nJSONから変換:");
        car2.showInfo();
    }
}
```

**コードの説明：**

1. **import com.google.gson.Gson;**
   - Gsonライブラリをインポート
   - Mavenで追加した依存関係が使える

2. **gson.toJson(car)**
   - Javaオブジェクト → JSON文字列

3. **gson.fromJson(jsonString, Car.class)**
   - JSON文字列 → Javaオブジェクト

---

#### 4.2 実行結果

```
JSONに変換:
{"color":"赤","speed":60}

JSONから変換:
色: 青
速度: 80
```

**確認できたこと：**

1. **依存関係が正しく読み込まれた**
   - クラスパスに gson-2.10.1.jar が含まれている
   - `C:\Users\sakih\.m2\repository\com\google\code\gson\gson\2.10.1\gson-2.10.1.jar`

2. **オブジェクト → JSON変換**
   - CarオブジェクトがJSON形式の文字列になった
   - `{"color":"赤","speed":60}`

3. **JSON → オブジェクト変換**
   - JSON文字列からCarオブジェクトが作成された
   - showInfo()が正しく動作

**重要なポイント：**
- Mavenで依存関係を追加するだけで、外部ライブラリが使える
- 手動でjarファイルをダウンロードする必要がない

---

### 5. Mavenコマンド（ライフサイクル）

#### 5.1 Mavenのライフサイクルとは

**ライフサイクル：**
- ビルドプロセスの各段階
- 決まった順序で実行される

**主なライフサイクル：**

| フェーズ | 説明 |
|---------|------|
| validate | プロジェクトが正しいか検証 |
| compile | ソースコードをコンパイル |
| test | テストを実行 |
| package | コンパイル済みコードをパッケージング（JAR作成） |
| verify | 統合テストの結果を検証 |
| install | パッケージをローカルリポジトリにインストール |
| deploy | パッケージをリモートリポジトリにデプロイ |

**特殊なフェーズ：**
- **clean**: targetディレクトリを削除（クリーンアップ）

---

#### 5.2 コマンド実行方法

**コマンドラインからの実行を試みたがエラー：**

```
mvn clean
```

**エラーメッセージ：**
```
mvn : 用語 'mvn' は、コマンドレット、関数、スクリプト ファイル、または操作可能なプログラムの名前として認識されません。
```

**原因：**
- Mavenがシステムのパスに登録されていない
- IntelliJ IDEAには組み込みMavenがあるが、コマンドラインからは使えない

**解決方法：**
- IntelliJ IDEAのMavenウィンドウから実行

---

#### 5.3 Maven clean の実行

**実行方法：**
1. 右側の「Maven」タブをクリック
2. OOPBasics → Lifecycle → **clean** をダブルクリック

**実行結果：**

```
[INFO] Scanning for projects...
[INFO]
[INFO] -----------------------< org.example:OOPBasics >------------------------
[INFO] Building OOPBasics 1.0-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- clean:3.2.0:clean (default-clean) @ OOPBasics ---
Downloading from central: https://repo.maven.apache.org/maven2/...
Downloaded from central: https://repo.maven.apache.org/maven2/...
[INFO] Deleting C:\Users\sakih\IdeaProjects\OOPBasics\target
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.565 s
[INFO] Finished at: 2025-12-23T15:05:29+09:00
[INFO] ------------------------------------------------------------------------
```

**実行内容：**

1. **プロジェクトのスキャン**
   - pom.xmlを読み込み
   - プロジェクト情報を取得

2. **必要なプラグインのダウンロード**
   - maven-clean-plugin
   - 関連する依存関係（commons-ioなど）

3. **targetディレクトリの削除**
   - コンパイル済みファイルをクリーンアップ
   - `C:\Users\sakih\IdeaProjects\OOPBasics\target` を削除

4. **成功**
   - BUILD SUCCESS
   - 実行時間: 1.565秒

---

#### 5.4 Maven compile の実行

**実行方法：**
- Lifecycle → **compile** をダブルクリック

**実行結果：**

```
[INFO] --- resources:3.3.1:resources (default-resources) @ OOPBasics ---
[INFO] Copying 0 resource from src\main\resources to target\classes
[INFO]
[INFO] --- compiler:3.13.0:compile (default-compile) @ OOPBasics ---
[INFO] Recompiling the module because of changed source code.
[INFO] Compiling 5 source files with javac [debug target 17] to target\classes
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.329 s
[INFO] Finished at: 2025-12-23T15:05:56+09:00
[INFO] ------------------------------------------------------------------------
```

**実行内容：**

1. **resources フェーズ**
   - `src\main\resources` から `target\classes` へリソースをコピー
   - 今回は0個（リソースファイルがない）

2. **compile フェーズ**
   - **5つのソースファイル**をコンパイル：
     1. Vehicle.java
     2. Car.java
     3. ElectricCar.java
     4. Main.java
     5. JsonExample.java
   - Java 17でコンパイル
   - `target\classes` に .class ファイルを出力

3. **成功**
   - BUILD SUCCESS
   - 実行時間: 1.329秒

**成果物の確認：**
- `target/classes` ディレクトリに .class ファイルが生成された
- Car.class、ElectricCar.class、Main.class など

---

#### 5.5 Maven package の実行

**実行方法：**
- Lifecycle → **package** をダブルクリック

**実行結果：**

```
[INFO] --- resources:3.3.1:resources (default-resources) @ OOPBasics ---
[INFO] Copying 0 resource from src\main\resources to target\classes
[INFO]
[INFO] --- compiler:3.13.0:compile (default-compile) @ OOPBasics ---
[INFO] Nothing to compile - all classes are up to date.
[INFO]
[INFO] --- resources:3.3.1:testResources (default-testResources) @ OOPBasics ---
[INFO] skip non existing resourceDirectory C:\Users\sakih\IdeaProjects\OOPBasics\src\test\resources
[INFO]
[INFO] --- compiler:3.13.0:testCompile (default-testCompile) @ OOPBasics ---
[INFO] Nothing to compile - all classes are up to date.
[INFO]
[INFO] --- surefire:3.2.5:test (default-test) @ OOPBasics ---
[INFO] No tests to run.
[INFO]
[INFO] --- jar:3.4.1:jar (default-jar) @ OOPBasics ---
[INFO] Building jar: C:\Users\sakih\IdeaProjects\OOPBasics\target\OOPBasics-1.0-SNAPSHOT.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.776 s
[INFO] Finished at: 2025-12-23T15:07:32+09:00
[INFO] ------------------------------------------------------------------------
```

**実行内容（ライフサイクルの順序）：**

1. **resources** - リソースのコピー
2. **compile** - すでにコンパイル済みなのでスキップ
3. **testResources** - テストリソースがないのでスキップ
4. **testCompile** - テストコードがないのでスキップ
5. **test** - テストなし
6. **jar** - **JARファイル作成！**

**重要な成果物：**
```
Building jar: C:\Users\sakih\IdeaProjects\OOPBasics\target\OOPBasics-1.0-SNAPSHOT.jar
```

**JARファイルとは：**
- Java ARchive
- コンパイル済みクラスファイルをまとめたもの
- 配布や実行に使用
- ZIPファイルのような構造

**確認方法：**
- `target/OOPBasics-1.0-SNAPSHOT.jar` が作成されている

---

### 6. Mavenのディレクトリ構造

#### 標準的なMavenプロジェクト構造

```
OOPBasics/
├── pom.xml                    # Maven設定ファイル
├── src/
│   ├── main/
│   │   ├── java/             # Javaソースコード
│   │   │   ├── Car.java
│   │   │   ├── ElectricCar.java
│   │   │   ├── Vehicle.java
│   │   │   ├── Main.java
│   │   │   └── JsonExample.java
│   │   └── resources/        # リソースファイル（設定ファイルなど）
│   └── test/
│       ├── java/             # テストコード
│       └── resources/        # テスト用リソース
└── target/                   # ビルド成果物（自動生成）
    ├── classes/              # コンパイル済みクラス
    └── OOPBasics-1.0-SNAPSHOT.jar  # パッケージ
```

**この構造の利点：**
- どのMavenプロジェクトも同じ構造
- チーム開発がスムーズ
- IDEが自動認識
- ビルドツールが標準的に処理

---

### 6.5 targetフォルダとJARファイルの詳細

学習中に出た質問：
- 「targetフォルダって何ですか？」
- 「compileでtargetフォルダが作られますか？」
- 「packageで作られるjarファイルって何ですか？」

---

#### targetフォルダとは

**target フォルダ = ビルド成果物の出力先**

**Pythonとの対応：**
- Python: `__pycache__/`（.pycファイルが入る）
- Python: `dist/`（ビルドしたパッケージが入る）
- Java/Maven: `target/`（コンパイル済み.classファイルやJARが入る）

**targetフォルダの中身：**

```
target/
├── classes/                    # コンパイル済みの.classファイル
│   ├── Car.class
│   ├── ElectricCar.class
│   ├── Vehicle.class
│   ├── Main.class
│   └── JsonExample.class
├── maven-status/               # Mavenの状態情報
├── OOPBasics-1.0-SNAPSHOT.jar # packageで作られるJARファイル
└── その他のビルド関連ファイル
```

**重要なポイント：**
- **ソースコードではない**（自動生成される）
- **Gitにコミットしない**（.gitignoreに入れる）
- **いつでも削除して再生成できる**
- `mvn clean` で削除される

---

#### compileでtargetフォルダが作られる

**はい、`mvn compile` で target フォルダが作られます。**

**ビルドプロセスの流れ：**

**1. 最初の状態（targetなし）：**
```
OOPBasics/
├── pom.xml
└── src/
    └── main/java/
        ├── Car.java
        ├── Main.java
        └── ...
```

**2. `mvn compile` を実行：**
- targetフォルダが**自動的に作成**される
- .javaファイルがコンパイルされて.classファイルになる
- .classファイルが `target/classes/` に保存される

**3. compile後の状態：**
```
OOPBasics/
├── pom.xml
├── src/
│   └── main/java/
│       ├── Car.java      # ソースコード（変更なし）
│       ├── Main.java
│       └── ...
└── target/               # ← 新しく作られた！
    └── classes/
        ├── Car.class     # コンパイル済み
        ├── Main.class
        └── ...
```

**4. `mvn clean` を実行：**
- targetフォルダ全体が**削除**される
- ソースコード（src/）は影響を受けない
- 元の状態1に戻る

---

#### JARファイルとは

**JAR = Java ARchive（Javaアーカイブ）**

**Pythonとの対応：**
- Python: `.whl`（ホイールファイル） - パッケージ化されたPythonライブラリ
- Python: `.egg` - 古いパッケージ形式
- Java: `.jar` - パッケージ化されたJavaアプリケーション/ライブラリ

**JARファイルの正体：**

**実は、JARファイルはZIPファイル！**

試しに：
1. `OOPBasics-1.0-SNAPSHOT.jar` の拡張子を `.zip` に変更
2. 解凍してみる

**中身：**
```
OOPBasics-1.0-SNAPSHOT.jar
├── Car.class              # コンパイル済みクラス
├── ElectricCar.class
├── Vehicle.class
├── Main.class
├── JsonExample.class
└── META-INF/
    └── MANIFEST.MF        # メタ情報
```

---

#### JARファイルの種類

**1. ライブラリJAR（今回作成したもの）**
- クラスファイルだけ
- 他のプロジェクトから使われる
- 例: `gson-2.10.1.jar`（Gsonライブラリ）

**2. 実行可能JAR（Executable JAR）**
- mainメソッドを含む
- `java -jar アプリ名.jar` で実行できる
- Spring Bootアプリはこれ

---

#### JARファイルの使い方

**配布：**
```bash
# このJARを他の人に渡せば、そのまま使える
OOPBasics-1.0-SNAPSHOT.jar
```

**実行（実行可能JARの場合）：**
```bash
java -jar OOPBasics-1.0-SNAPSHOT.jar
```

**ライブラリとして使用：**
- 他のプロジェクトのpom.xmlに依存関係として追加
- または直接クラスパスに追加

---

#### Mavenのビルドプロセス全体像

```
ソースコード        target/classes/      target/
(.java)         →   (.class)         →  (.jar)

               mvn compile          mvn package
```

**各段階：**

**1. mvn clean**
- targetフォルダを削除
- クリーンな状態にリセット

**2. mvn compile**
- targetフォルダを作成
- .java → .class（コンパイル）
- target/classes/ に保存

**3. mvn package**
- compileを実行（まだの場合）
- testを実行（テストがある場合）
- .classファイルをJARにまとめる
- target/プロジェクト名-バージョン.jar を作成

---

#### Pythonとの比較

| 概念 | Python | Java/Maven |
|------|--------|-----------|
| ソースコード | .py | .java |
| コンパイル済み | .pyc (__pycache__/) | .class (target/classes/) |
| パッケージ | .whl (dist/) | .jar (target/) |
| ビルドコマンド | uv build | mvn package |
| クリーン | rm -rf dist/ __pycache__/ | mvn clean |
| 配布形式 | .whl | .jar |

**大きな違い：**
- **Python**: .pyファイルを直接実行できる（インタープリタ）
- **Java**: .classまたは.jarが必要（コンパイル必須）

---

#### JARファイルからコードの復元は可能か？

学習中に出た質問：「jarファイルからコードの復元は可能？」

**答え：はい、ほぼ復元できます（ただし完璧ではない）**

---

**デコンパイルの仕組み：**

```
jar ファイル
  ↓ 解凍
.class ファイル（Javaバイトコード）
  ↓ デコンパイル
.java ファイル（復元されたソースコード）
```

**デコンパイルツール：**
- JD-GUI（Java Decompiler GUI）
- IntelliJ IDEA（組み込みデコンパイラ）
- CFR、Procyon など

**IntelliJ IDEAで試す方法：**
1. External Libraries → gson-2.10.1.jar を展開
2. 任意の.classファイルをダブルクリック
3. デコンパイルされたコードが表示される

---

**何が復元されて、何が失われるか？**

**✅ 復元できるもの：**

1. **クラス構造**
   - クラス名
   - メソッド名
   - フィールド名

2. **ロジック**
   - if文、for文などの制御構造
   - メソッドの処理内容
   - アルゴリズム

3. **アクセス修飾子**
   - public、private、protected

**❌ 失われるもの：**

1. **コメント**
   - すべてのコメントが消える
   - JavaDocも消える

2. **ローカル変数名**
   - `userName` → `var1`
   - `totalPrice` → `var2`
   - 意味のある名前が失われる

3. **フォーマット**
   - インデント、改行などが変わる
   - 元のコードスタイルは失われる

4. **ジェネリクスの一部**
   - 型情報の一部が失われることがある

---

**デコンパイル前後の比較例：**

オリジナルのソースコード:
```java
public class Calculator {
    /**
     * 2つの数を足し算する
     * @param firstNumber 最初の数
     * @param secondNumber 2番目の数
     * @return 合計
     */
    public int add(int firstNumber, int secondNumber) {
        int result = firstNumber + secondNumber;
        return result;
    }
}
```

デコンパイル後:
```java
public class Calculator {
    public int add(int var1, int var2) {
        int var3 = var1 + var2;
        return var3;
    }
}
```

**失われたもの：**
- コメント（JavaDoc）
- 意味のある変数名（`firstNumber` → `var1`）

**復元されたもの：**
- ロジック（足し算の処理）
- メソッド名（`add`）
- アクセス修飾子（`public`）

---

**Pythonとの比較：**

| 項目 | Python (.pyc) | Java (.class) |
|------|--------------|---------------|
| 元ファイル | .py | .java |
| コンパイル済み | .pyc（バイトコード） | .class（バイトコード） |
| デコンパイル | 可能（uncompyle6など） | 可能（JD-GUIなど） |
| 復元度 | 高い（変数名もある程度残る） | 中程度（ローカル変数名は失われる） |
| ソース配布 | .pyファイルをそのまま配布が一般的 | .jarファイル（.classのみ）が一般的 |

**Pythonの特徴：**
- ソースコード（.py）をそのまま配布することが多い
- .pycは実行速度向上のためのキャッシュ

**Javaの特徴：**
- ソースコード（.java）は配布しない
- .jar（.classのみ）を配布
- でもデコンパイル可能

---

**対策：難読化（Obfuscation）**

商用ソフトウェアでは、デコンパイルされても読めないように**難読化**を使います。

変換前:
```java
public class UserService {
    public void registerUser(String email, String password) {
        // 登録処理
    }
}
```

難読化後:
```java
public class a {
    public void a(String a, String b) {
        // 処理は同じだが、すべて意味不明な名前に
    }
}
```

**難読化ツール：**
- ProGuard（Android開発でよく使われる）
- R8（Android）
- yGuard
- Zelix KlassMaster

---

**セキュリティ上の考慮事項**

**❌ 絶対にやってはいけない：**

```java
public class Config {
    private static final String API_KEY = "sk-1234567890abcdef";  // ← 危険！
    private static final String DB_PASSWORD = "password123";      // ← 危険！
}
```

**理由：**
- デコンパイルすれば、この文字列が見える
- ハードコードされた秘密情報は必ず漏洩する

**✅ 正しい方法：**

```java
public class Config {
    // 環境変数から読み込む
    private static final String API_KEY = System.getenv("API_KEY");

    // 設定ファイルから読み込む
    private static final String DB_PASSWORD = loadFromConfig("db.password");
}
```

---

**オープンソース vs クローズドソース**

**オープンソース（例：Spring Boot、Gson）：**
- ソースコード（.java）を公開
- GitHubなどで誰でも見られる
- JARファイルもMaven Centralで配布
- デコンパイルの必要なし

**クローズドソース（例：商用ソフトウェア）：**
- JARファイル（.class）のみ配布
- ソースコード（.java）は非公開
- デコンパイルは可能だが、ライセンス違反の可能性
- 難読化で対策

---

**ライセンスと法律**

**重要：**
- デコンパイル自体は技術的に可能
- しかし、**ライセンス違反**になる可能性が高い
- 商用ソフトウェアのデコンパイルは通常禁止されている
- リバースエンジニアリングの法的扱いは国によって異なる

**合法的な用途：**
- 自分のコードのデバッグ
- オープンソースライブラリの調査
- セキュリティ研究（許可を得て）

---

**まとめ：JARファイルからコードは復元できるか？**

**✅ 技術的には可能**
- デコンパイルツールで.class → .javaに変換できる
- ロジックは復元される

**❌ 完全には復元できない**
- コメントが失われる
- 変数名が失われる
- 元のコードと完全に同じにはならない

**🛡️ 対策**
- 難読化（ProGuardなど）
- 機密情報をハードコードしない
- ライセンスで保護

**Pythonとの違い：**
- Pythonは.pyファイル（ソース）を配布することが多い
- Javaは.jarファイル（バイトコード）を配布するが、デコンパイル可能

---

### 7. Mavenのリポジトリ

#### 3種類のリポジトリ

**1. ローカルリポジトリ**
- 場所: `C:\Users\sakih\.m2\repository`
- 自分のPCにダウンロードされた依存関係を保存
- 一度ダウンロードすれば再利用

**2. Central Repository（中央リポジトリ）**
- URL: https://repo.maven.apache.org/maven2/
- Maven公式の公開リポジトリ
- ほとんどのOSSライブラリが登録されている

**3. Remote Repository（リモートリポジトリ）**
- 会社やプロジェクト独自のリポジトリ
- プライベートなライブラリを管理

**依存関係の解決順序：**
1. ローカルリポジトリを確認
2. なければCentral Repositoryからダウンロード
3. ローカルリポジトリに保存
4. プロジェクトで使用

---

## 学習の進捗状況

### WBS 1.0.3 Maven/Gradle基礎の進捗

**完了項目**:
- ✅ Mavenとは何か（ビルドツール、依存関係管理）
- ✅ pom.xml の基本構造
- ✅ 依存関係の追加方法（Gsonを追加）
- ✅ Mavenコマンド（clean, compile, package）
- ✅ Mavenプロジェクト構造の理解
- ✅ Mavenリポジトリの理解

**進捗率**: 100%（WBS 1.0.3を完全に完了）

**Gradleについて：**
- 今回はMavenを学習
- Gradleは別のビルドツール（Groovy/Kotlinベース）
- Spring BootプロジェクトではMavenまたはGradleを選択可能
- 基本概念は同じ

---

## 次回の学習予定

### WBS 1.0.4 簡単なJavaプログラム作成（0.8日）

**学習予定の内容**:
- Hello World プログラム（Mavenプロジェクトとして）
- 計算機プログラム（クラスとメソッドを使用）
- Todoリストプログラム（簡易版、コンソール出力）
- 動作確認

---

## 作成したファイル一覧

プロジェクト: `OOPBasics` (Windows: C:\Users\sakih\IdeaProjects\OOPBasics)

### 追加・変更したファイル

| # | ファイル名 | 種類 | 目的 | 変更内容 |
|---|-----------|------|------|---------|
| 1 | pom.xml | 設定ファイル | Maven設定 | Gson依存関係を追加 |
| 2 | JsonExample.java | クラス | JSON処理の実践 | GsonライブラリでJSON変換 |

**成果物:**
- `target/OOPBasics-1.0-SNAPSHOT.jar` - パッケージ化されたJARファイル

---

## 重要な学習ポイント

### 1. Mavenの役割

**依存関係管理の自動化：**
- pom.xmlに書くだけで自動ダウンロード
- バージョン管理が簡単
- チーム全員が同じ環境

**ビルドの標準化：**
- どのプロジェクトも同じコマンド
- `mvn clean package` で誰でもビルドできる

---

### 2. pom.xmlの重要性

**プロジェクトの設計図：**
- プロジェクト情報（groupId, artifactId, version）
- 依存関係
- ビルド設定
- プラグイン設定

**バージョン管理：**
- pom.xmlをGitで管理
- チームメンバーが同じ依存関係を使用

---

### 3. ライフサイクルの理解

**順序が重要：**
- compile → test → package の順に実行
- packageを実行すると、自動的にcompileとtestも実行される

**clean の使い方：**
- 古いビルド成果物を削除
- クリーンな状態からビルドし直す

---

### 4. 依存関係の追加方法

**3つの情報が必要：**
- groupId（提供元）
- artifactId（ライブラリ名）
- version（バージョン）

**どこで調べる？**
- Maven Central Repository: https://search.maven.org/
- ライブラリの公式ドキュメント

**例（Gson）：**
```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>
```

---

## つまずいた点とその解決

### 1. 依存関係が表示されない

**問題:**
- pom.xmlに依存関係を追加したが、Dependenciesが表示されない

**原因:**
- Mavenプロジェクトが自動再ロードされなかった

**解決:**
- Mavenウィンドウの「Reload All Maven Projects」を実行
- External Librariesに表示されるようになった

**学び:**
- pom.xml編集後は必ず再ロードを確認
- IntelliJ IDEAが自動でやることもあるが、手動が確実

---

### 2. mvnコマンドがコマンドラインで使えない

**問題:**
```
mvn : 用語 'mvn' は...認識されません
```

**原因:**
- MavenがシステムのPATHに登録されていない
- IntelliJ IDEAの組み込みMavenはGUIからのみ使用可能

**解決:**
- IntelliJ IDEAのMavenウィンドウから実行
- Lifecycleのフェーズをダブルクリック

**学び:**
- 開発環境によって実行方法が異なる
- GUIからの実行で十分（コマンドラインは不要）

---

## メモ・気づき

### 良かった点

1. **実践的な学習**
   - 実際に依存関係を追加してライブラリを使用
   - Gsonで実際のJSON処理を体験

2. **ビルドプロセスの理解**
   - clean, compile, packageの違いが明確に
   - 各フェーズで何が起きているか確認できた

3. **成果物の確認**
   - JARファイルが作成されるのを確認
   - 配布可能なパッケージができた

### 理解が深まった概念

1. **依存関係管理の重要性**
   - 手動管理の大変さ
   - Mavenの自動化の便利さ

2. **標準化の価値**
   - どのプロジェクトも同じ構造
   - チーム開発での一貫性

3. **ビルドツールの役割**
   - コンパイルだけでなく、テスト、パッケージングまで
   - 一貫したプロセス

---

## Spring Bootへの応用

### 今日学んだ概念がSpring Bootでどう使われるか

1. **pom.xml での依存関係管理**
   - Spring Bootの依存関係
   - データベースドライバ
   - セキュリティライブラリ
   - テストライブラリ

**Spring Bootのpom.xml例：**
```xml
<dependencies>
    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
</dependencies>
```

2. **Mavenコマンドでの実行**
   - `mvn spring-boot:run` でアプリケーション起動
   - `mvn clean package` でデプロイ用JARを作成

3. **標準的なプロジェクト構造**
   - Spring BootもMavenの標準構造に従う
   - どのSpring Bootプロジェクトも同じ構造

---

## Gradle との比較（参考）

### Mavenと Gradleの違い

| 項目 | Maven | Gradle |
|------|-------|--------|
| 設定ファイル | pom.xml (XML) | build.gradle (Groovy/Kotlin) |
| 記述量 | やや多い | 少ない |
| ビルド速度 | 普通 | 速い（インクリメンタルビルド） |
| 学習コスト | 低い（XMLは読みやすい） | やや高い（Groovy/Kotlin） |
| 採用 | レガシーシステムに多い | モダンプロジェクトに多い |

**どちらを使うべきか：**
- Spring Bootはどちらも対応
- 今回はMavenで学習
- 基本概念（依存関係管理、ライフサイクル）は同じ

---

## Pythonツール（uv）との比較

### Maven は Python でいう uv のようなツール

学習中に出た質問：「mavenってPythonでいうuvみたいなやつってこと？」

**答え：はい、基本的にその理解で正しいです！**

---

### 共通点

**1. 依存関係管理**
- **Maven**: pom.xmlに依存関係を書く → 自動ダウンロード
- **uv**: pyproject.tomlに依存関係を書く → 自動ダウンロード

**2. プロジェクト管理**
- **Maven**: プロジェクトの構造を標準化
- **uv**: Pythonプロジェクトの構造を管理

**3. ビルド/実行**
- **Maven**: `mvn compile`, `mvn package`
- **uv**: `uv run`, `uv build`

---

### より正確な対応関係

**Pythonのツールとの対応：**

| Java | Python | 役割 |
|------|--------|------|
| **Maven** | **uv** | パッケージ管理 + プロジェクト管理 + ビルド |
| Maven | pip + virtualenv | 依存関係管理（従来） |
| Maven | Poetry | モダンなプロジェクト管理 |
| pom.xml | pyproject.toml | 設定ファイル |
| Maven Central | PyPI | パッケージリポジトリ |

---

### 具体的な比較

**依存関係の追加：**

Maven (pom.xml):
```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>
```

uv (pyproject.toml):
```toml
[project.dependencies]
requests = "^2.31.0"
```

---

**プロジェクト構造：**

Maven:
```
project/
├── pom.xml
├── src/
│   ├── main/java/
│   └── test/java/
└── target/
```

uv/Poetry:
```
project/
├── pyproject.toml
├── src/
│   └── package/
└── .venv/
```

---

**コマンド比較：**

| 操作 | Maven | uv |
|------|-------|-----|
| 依存関係インストール | mvn install | uv sync |
| ビルド | mvn package | uv build |
| 実行 | mvn exec:java | uv run python main.py |
| クリーン | mvn clean | (手動削除) |

---

### 主な違い

**1. ビルド vs インタープリタ**
- **Java/Maven**: コンパイル必要 → JARファイル作成
- **Python/uv**: インタープリタ言語 → 直接実行

**2. 仮想環境**
- **Java/Maven**: 仮想環境の概念なし（JARに全部入れる）
- **Python/uv**: 仮想環境（.venv）が重要

**3. 型システム**
- **Java**: 静的型付け → コンパイル時エラー検出
- **Python**: 動的型付け → 実行時エラー検出

---

### まとめ

**「プロジェクトを管理して依存関係を自動で解決する」という本質は同じです。**

- どちらも**依存関係管理 + プロジェクト管理**
- どちらも**設定ファイル**（pom.xml vs pyproject.toml）
- どちらも**標準的なプロジェクト構造**を提供

**細かい違い:**
- Mavenはビルドツール（コンパイル→JAR作成）も含む
- uvはパッケージマネージャー（依存関係管理中心）

Python経験者にとって、Mavenは「uvのJava版＋ビルド機能」と理解すると分かりやすい。

---

## 学習時間の記録

- **Mavenとは何か**: 10分
- **pom.xmlの理解**: 15分
- **依存関係の追加**: 15分
- **JSONプログラム作成**: 10分
- **Mavenコマンド実行**: 15分

**合計学習時間**: 約1時間

---

## 参考にしたリソース

- WBS Phase1 計画書
- IntelliJ IDEA の Maven統合機能
- Gsonライブラリ公式ドキュメント

---

## 所感

Maven の基礎を学ぶことができた。特に、依存関係管理の自動化がどれほど便利かを実感できた。pom.xmlに数行書くだけでライブラリがダウンロードされ、すぐに使える仕組みは素晴らしい。

Gsonを使った実践的なプログラム作成を通じて、外部ライブラリの使い方も理解できた。JSON処理は実際のWeb開発で頻繁に使うので、良い経験になった。

clean, compile, packageといったMavenのライフサイクルを実際に実行してみて、各フェーズで何が起きているかが明確になった。特にpackageでJARファイルが作成されるのを見て、配布可能な成果物ができるプロセスが理解できた。

次回からは簡単なJavaプログラム作成に進み、その後いよいよSpring Bootの学習に入る。今日学んだMavenの知識は、Spring Boot開発で必須なので、しっかり理解できて良かった。

---

**作成日**: 2025-12-21
**次回学習予定日**: 未定
**WBS進捗**: 1.0.3 Maven/Gradle基礎 100%完了
