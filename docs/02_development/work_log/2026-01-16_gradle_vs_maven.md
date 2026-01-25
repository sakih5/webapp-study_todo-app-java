# 作業ログ: 2026-01-16 (MavenとGradleの比較)

## 学習情報

- **日付**: 2026-01-16
- **学習フェーズ**: WBS 1.2 環境構築・Spring Boot学習
- **学習時間**: 約30分
- **ステータス**: ビルドツールの選定完了（Gradle - Kotlinに決定）

---

## 発生した疑問

### MavenとGradleの違いについて

**質問内容:**
> Mavenは依存ライブラリを再帰的に取得するため、再現性が悪くチーム開発に向かないと聞きました。
> 代わりにGradleは依存ライブラリを全て設定ファイルに記載するから再現性があってチーム開発に良いと聞きました。
> それは本当ですか？

---

## 学んだこと

### ❌ 誤解：「Mavenは再帰的、Gradleは全て記載」

この説明は**正確ではない**。

### ✅ 現実：両方とも推移的依存関係を自動解決する

**Maven:**
```xml
<!-- pom.xmlに直接依存だけ書く -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- ↑これだけで、spring-webmvc、tomcatなど関連ライブラリも自動で取得される -->
```

**Gradle:**
```groovy
// build.gradleに直接依存だけ書く
implementation 'org.springframework.boot:spring-boot-starter-web'
// ↑これだけで、関連ライブラリも自動で取得される（Mavenと同じ）
```

**結論**: どちらも「再帰的に依存関係を解決」する。

---

## MavenとGradleの比較

### 再現性について

#### Mavenの再現性

**pom.xmlでバージョンを明示すれば再現性がある:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>3.2.0</version>  <!-- バージョン固定 -->
</dependency>
```

**問題になるケース:**
- バージョンを`LATEST`や範囲指定すると再現性が落ちる
- でもそれは推奨されていない

#### Gradleの再現性

**Gradleも同様にバージョン固定で再現性がある:**
```groovy
implementation 'org.springframework.boot:spring-boot-starter-web:3.2.0'
```

**さらに厳密に管理する方法:**
- `gradle.lockfile`を使うと、推移的依存関係のバージョンも完全に固定できる
- これはMavenより厳密（ただし、Phase 1では不要）

---

### チーム開発への適性

| 項目 | Maven | Gradle |
|-----|-------|--------|
| **学習コスト** | 低い（シンプル） | 高め（柔軟だが複雑） |
| **ビルド速度** | 普通 | 速い（増分ビルド） |
| **エンタープライズ** | 非常に広く使われている | 最近増えている |
| **Spring Bootとの相性** | 良い | 良い |
| **設定ファイル** | XML（pom.xml） | Groovy/Kotlin DSL |
| **再現性** | 十分ある | より厳密に管理可能 |

**結論**: **どちらもチーム開発に適している**

---

## 選択の理由

### 当初の推奨：Maven

**理由:**
1. WBSでMavenを前提にしている
2. 学習目的に適している（シンプル）
3. Spring Bootの公式ドキュメントもMaven例が多い
4. SimpleTodoでMavenを使った経験がある
5. エンタープライズで広く使われている

### 最終決定：Gradle - Kotlin

**理由:**
1. **学習プロジェクトだから、新しいことを学ぶのは良い**
2. **特に急いでいない**（時間に余裕がある）
3. **モダンなツールに触れる機会**
4. **将来の選択肢が広がる**（Android開発、Kotlin言語への入り口）

---

## Gradle - Kotlinを選ぶメリット

### 学習面でのメリット

1. **モダンなビルドツールを学べる**
   - 最近のプロジェクトで採用が増えている
   - Kotlin DSLは型安全で補完が効く

2. **将来の選択肢が広がる**
   - Android開発（Gradle標準）
   - Kotlin言語への入り口にもなる

3. **ビルドスクリプト自体が理解しやすい**
   - XMLよりコードっぽくて読みやすい
   - 変数や関数が使える

### 実務面でのメリット

- ビルド速度が速い（増分ビルド、ビルドキャッシュ）
- 柔軟なカスタマイズが可能
- 大規模プロジェクトに強い

---

## Gradle - Kotlinのデメリットと対策

### デメリット

1. **学習コストがやや高い**
   - Maven（pom.xml）の方がシンプル

2. **WBSの想定との違い**
   - WBSではMaven前提の時間見積もり

3. **SimpleTodoとの違い**
   - SimpleTodoではMavenを使った

### 対策

- 最初は生成されたファイルをそのまま使う
- 必要に応じて学ぶ
- 困ったら質問する
- これも良い学習機会と捉える

---

## 重要な概念

### Gradle - Kotlin とは？

- **Gradle**: ビルドツール（Mavenの代替）
- **Kotlin**: プログラミング言語
- **Gradle - Kotlin**: ビルドスクリプト（`build.gradle.kts`）をKotlin言語で書く

**重要**: アプリケーション本体（Todoアプリ）は**Javaで書く**。Kotlinで書くのはビルドスクリプトだけ。

---

## まとめ

**誤解を解く:**
- ❌ Mavenは再現性が悪い → ✅ Mavenも再現性がある
- ❌ Gradleだけが全て記載 → ✅ 両方とも推移的依存を自動解決

**選択:**
- 特別な理由がなければMavenが安全
- 今回は学習目的で**Gradle - Kotlin**を選択

**判断基準:**
- 新しいことを学ぶのが楽しい
- 学習時間に余裕がある
- モダンな技術に興味がある

---

**作成日**: 2026-01-16
**WBS進捗**: 1.2 環境構築・Spring Boot学習 - ビルドツール選定完了
