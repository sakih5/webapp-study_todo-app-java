# Claude Codeで画像読み込みエラーの解決方法 - HEIC形式と大容量PNG対策

## 本記事を作成した背景

UIイメージを紙に書いて**iPhoneで写真を撮った**もの（**`.HEIC`ファイル**の拡張子をエクスプローラー上で`.png`に変えたもの）をClaude Codeに読み込ませようとしたところ、`Could not process image`（**400エラー**）が発生しました。画像そのものは正常に見えるのに、なぜClaude APIが受け付けないのか？その原因究明と解決までの試行錯誤をまとめます。

画像エラーのトラブルシューティングは一見シンプルですが、実は「ファイル形式」「エンコード方式」「サイズ制限」「ツールのバージョン違い」「セッション状態」など、複数の要因が絡み合います。この記事では、それらを機械的に切り分けていく方法を学びました。

## 本記事で取り組んだこと

- Claude Code（CLI）で画像を読み込もうとして400エラーが発生
- `file`コマンドで画像の実体を確認したところ、拡張子は`.png`だが中身は`HEIF（HEIC）`形式だった
- HEIF → PNG/JPEG への変換を試行（`heif-convert`、`ffmpeg`、`ImageMagick`）
- 変換後もエラーが続いたため、画像サイズ（19MB、3024×4032、RGBA）が原因と特定
- ImageMagickで軽量化・JPEG変換（550KB、1500×2000、RGB）に成功
- それでもエラーが続いたため、Claude Codeのセッションが壊れていることに気づく
- Claude Code再起動で最終的に解決

## 手順

### 前提

- **環境**: Windows 11 + WSL2 (Ubuntu 24.04)
- **前提知識**: Linuxの基本コマンド（`file`, `ls`, `xxd`など）、画像形式の基礎知識
- **前提状態**: Claude Code（CLI）がインストール済み
- **使用ツール**: ImageMagick v6系（Ubuntu標準）

### 1. エラーの発生と初期診断

#### 🎯 目的

Claude Codeで画像を読み込もうとして発生した400エラーの原因を特定します。

#### 🛠️ 手順詳細

最初に以下のコマンドで画像を読み込もうとしました：

```bash
claude "この画像読める？" /home/sakih/projects/webapp_study_todo-app-java/docs/01_specs/wireframes/01_login_screen.png
```

エラーメッセージ

```
⎿  [Image #1]
⎿  Loaded docs/CLAUDE.md
⎿  API Error: 400 {"type":"error","error":{"type":"invalid_request_error","message":"Could not process image"},"request_id":"req_011CWoEqDMpBfwmPJCTWW9WK"}
```

エラーメッセージから、Claude APIが画像を処理できないことがわかりました。次に、画像ファイルの実体を確認します：

```bash
file 01_login_screen.png
```

出力結果

```
01_login_screen.png: ISO Media, HEIF Image HEVC Main or Main Still Picture Profile
```

#### 💡 理解ポイント

**拡張子と実体の不一致**を確認するのが最重要です。

- 拡張子は `.png` だが、中身は `HEIF（HEIC）`形式
- HEIF/HEICは iPhone/iPad/macOS で使われる高効率画像形式（動画コーデックHEVCベース）
- Claude API は HEIF/HEIC を処理できない
- **エクスプローラやFinderで拡張子だけ変更しても、中身は変わらない**

WSL環境では、Windows側で作成したファイルの「見た目の拡張子」と「実体」が異なることがよくあります。`file`コマンドは実体を確認する最初のステップです。

#### 📝 補足

**fileコマンドの使い方**

- `file <ファイル名>`: ファイルの実体を判定
- `file -k <ファイル名>`: 詳細情報も表示

**Claude APIが対応している画像形式**

- ✅ PNG（8bit RGB推奨）
- ✅ JPEG / JPG
- ❌ HEIF / HEIC
- ❌ WebP（環境によって不安定）
- ❌ SVG
- ❌ PDF

### 2. HEIF → PNG 変換の試行（失敗）

#### 🎯 目的

HEIF形式の画像を、Claude APIが受け付けるPNGまたはJPEG形式に変換します。

#### 🛠️ 手順詳細

まず、HEIFを扱うツールをインストールして変換を試みます：

```bash
sudo apt update
sudo apt install -y libheif-examples imagemagick
```

変換実行：

```bash
heif-convert 01_login_screen.png 01_login_screen_real.png
```

エラーメッセージ：

```bash
Could not read HEIF/AVIF file: Invalid input: Unspecified: Too many auxiliary image references
```

#### 💡 理解ポイント

**HEIF形式の種類**によって、ツールが読めないケースがあります。

- iPhone系が生成するHEIFには「補助画像（depth、alpha、preview等）」が含まれることがある
- `Too many auxiliary image references` は、libheifがその構造を処理できない時のエラー
- 単純なHEIF変換ツールでは対応できない特殊なファイル

#### 📝 補足

**HEIF/HEICとは**

- High Efficiency Image Format（高効率画像形式）
- 動画コーデックHEVCを画像に応用したもの
- JPEGと比べて高画質・小サイズだが、対応ツールが限られる

### 3. ffmpegでの変換試行（失敗）

#### 🎯 目的

`heif-convert`で失敗したため、より汎用的な`ffmpeg`での変換を試します。

#### 🛠️ 手順詳細

```bash
sudo apt install -y ffmpeg
ffmpeg -i 01_login_screen.png 01_login_screen_real.png
```

エラーメッセージ：

```bash
[mov,mp4,m4a,3gp,3g2,mj2 @ 0x64afc3baef40] moov atom not found
[in#0 @ 0x64afc3baee40] Error opening input: Invalid data found when processing input
Error opening input file 01_login_screen.png.
```

#### 💡 理解ポイント

`moov atom not found` は、MP4系コンテナとして不完全/破損している状態を示します。

- HEIF形式はMP4コンテナベースだが、特殊な構造のため標準的なffmpegでは読めないケースがある
- ファイル生成・保存・ダウンロードのどこかで破損している可能性も

この時点で、「WSL上での変換は困難」と判断し、作成元での対処が必要と気づきました。

#### 📝 補足

**元ファイルの確認が重要**

```bash
ls -lh 01_login_screen.png
stat 01_login_screen.png
xxd -l 64 01_login_screen.png  # 先頭バイトを16進数で表示
```

今回のケースでは、元ファイルが `01_login_screen.HEIC` であることが判明しました。

### 4. Windows側でPNG変換（成功）

#### 🎯 目的

WSL上での変換が困難なため、Windows標準アプリ（Photos）でHEIC → PNG変換を行います。

#### 🛠️ 手順詳細

1. エクスプローラで `01_login_screen.HEIC` を開く（Photosアプリ）
2. 右上「…」→「名前を付けて保存」
3. ファイル形式を **PNG** に指定して保存
4. WSLにコピー：

```bash
cp /mnt/c/Users/<username>/Desktop/01_login_screen.png /home/sakih/projects/webapp_study_todo-app-java/docs/01_specs/wireframes/
```

1. 実体を確認：

```bash
file 01_login_screen.png
```

出力結果

```
01_login_screen.png: PNG image data, 3024 x 4032, 8-bit/color RGBA, non-interlaced
```

正常なPNG形式になりました！

#### 💡 理解ポイント

**HEICはネイティブアプリで変換するのが最短**

- Windows 11 / macOS はHEIC/HEIFをネイティブサポート
- WSL上でコマンドライン変換にこだわると、依存関係やバージョン問題で沼る
- 「適材適所」で使い分ける

#### 📝 補足

**macOSの場合**

1. プレビューで開く
2. 「書き出す…」→ PNG / JPEG を選択

### 5. PNGサイズ問題の発見と対処

#### 🎯 目的

PNG形式に変換したにもかかわらず、まだClaude APIがエラーを返すため、画像サイズを確認します。

#### 🛠️ 手順詳細

画像の詳細を確認：

```bash
ls -lh 01_login_screen.png
file -k 01_login_screen.png
xxd -l 16 01_login_screen.png
```

出力結果

```
-rw-r--r-- 1 sakih sakih 19M Jan  5 09:05 01_login_screen.png

01_login_screen.png: PNG image data, 3024 x 4032, 8-bit/color RGBA, non-interlaced\012- data

00000000: 8950 4e47 0d0a 1a0a 0000 000d 4948 4452  .PNG........IHDR
```

**問題点**

- ファイルサイズ：**19MB**（大きすぎる）
- 解像度：**3024 × 4032**（約1200万画素）
- 色：**RGBA**（透明度付き）

Claude APIは「PNG + 高解像度 + アルファチャンネル」の組み合わせに弱く、このサイズでは処理できません。

#### 💡 理解ポイント

**Claude APIの画像制限（非公式だが経験則）**

- サイズ：〜3MB推奨（〜2MBが安全）
- 解像度：最大辺 〜2000px推奨
- 色：RGB（アルファなし）推奨
- 形式：JPEG が最も安定

「表示できる」と「APIが処理できる」は別物です。画像ビューアはストリーミング/遅延デコードできますが、APIは一括デコード＋内部変換のため、メモリ/時間制限に引っかかります。

#### 📝 補足

**xxdコマンドの見方**

```bash
xxd -l 16 01_login_screen.png
```

- PNGの先頭8バイトは必ず `89 50 4E 47 0D 0A 1A 0A`（PNGシグネチャ）
- JPEGなら `FF D8 FF`で始まる
- これで「本当にその形式か」を確認できる

### 6. ImageMagickでJPEG変換と軽量化

#### 🎯 目的

Claude APIが確実に処理できる「軽量JPEG」に変換します。

#### 🛠️ 手順詳細

まず、ImageMagickのバージョンを確認：

```bash
apt list --installed | grep imagemagick
```

出力

```
imagemagick/noble 8:6.9.12.98+dfsg1-5.2build2 amd64 [installed]
```

Ubuntu標準は **ImageMagick 6.x** のため、`magick`コマンドではなく`convert`コマンドを使います。

**安全なJPEG変換**（推奨）

```bash
convert 01_login_screen.png \
  -resize 2000x2000\> \
  -strip \
  -colorspace sRGB \
  -quality 85 \
  01_login_screen_small.jpg
```

**オプション説明**

- `-resize 2000x2000\>`: 最大辺2000pxに縮小（`\>`は「元より大きい場合のみ」）
- `-strip`: メタデータ除去（Exif、コメント、カラープロファイルなど）
- `-colorspace sRGB`: 色空間をsRGBに統一（APIが嫌う特殊色空間を回避）
- `-quality 85`: JPEG品質（85%で十分、ファイルサイズと画質のバランス）

確認：

```bash
file 01_login_screen_small.jpg
ls -lh 01_login_screen_small.jpg
identify 01_login_screen_small.jpg | head -n 1
```

出力結果

```
01_login_screen_small.jpg: JPEG image data, JFIF standard 1.01, resolution (DPCM), density 28x28, segment length 16, baseline, precision 8, 1500x2000, components 3

-rw-r--r-- 1 sakih sakih 550K Jan  5 09:21 01_login_screen_small.jpg

01_login_screen_small.jpg JPEG 1500x2000 1500x2000+0+0 8-bit sRGB 562646B 0.000u 0:00.001
```

**結果**

- サイズ：**550KB**（19MB → 550KB、約1/35に削減）
- 解像度：**1500 × 2000**
- 色：**sRGB、3コンポーネント（RGB）**
- 形式：**JPEG**

これでClaude APIが確実に処理できる形になりました。

#### 💡 理解ポイント

**ImageMagick v6とv7のコマンド違い**

| バージョン | コマンド |
|----------|---------|
| v6.x（Ubuntu標準） | `convert` |
| v7.x | `magick convert` または `magick` |

WSL2のUbuntu 24.04では、標準リポジトリから`apt install imagemagick`でインストールすると**v6系**が入ります。そのため`magick`コマンドは存在せず、`convert`を使います。

**透明度（alpha）の扱い**

- ワイヤーフレームやスクリーンショットなど、透明度が不要な画像は`-alpha remove`で削除すると安定します：

```bash
convert 01_login_screen.png \
  -resize 1600x1600\> \
  -strip \
  -alpha remove \
  -colorspace sRGB \
  -quality 80 \
  01_login_screen_safe.jpg
```

#### 📝 補足

**さらに軽量化したい場合**

```bash
convert 01_login_screen.png \
  -resize 1280x1280\> \
  -strip \
  -alpha remove \
  -colorspace sRGB \
  -interlace JPEG \
  -sampling-factor 4:2:0 \
  -quality 75 \
  01_login_screen_tiny.jpg
```

- `-interlace JPEG`: プログレッシブJPEG（Web表示が速くなる）
- `-sampling-factor 4:2:0`: 色情報を間引く（人間の目には気づきにくい）

**identifyコマンド**

```bash
identify -verbose 01_login_screen_small.jpg | head -n 40
```

画像の詳細情報（解像度、色空間、プロファイル、エンコード方式など）を確認できます。

### 7. それでもエラーが続く場合：Claude Codeセッション問題

#### 🎯 目的

画像が正常（550KB、JPEG、1500×2000、sRGB）になったにもかかわらず、まだ400エラーが出る場合、Claude Code側の問題を疑います。

#### 🛠️ 手順詳細

**現象**

```bash
claude "この画像読める？" ./01_login_screen_small.jpg
```

エラーメッセージ

```
⎿  [Image #4]
⎿ API Error: 400
  {"type":"error","error":{"type":"invalid_request_error","message":"Could not process image"}}
```

画像は正常なのに、何度やってもエラーが続く場合、**Claude Codeのセッションが壊れている**可能性が高いです。

**解決方法**

1. Claude Codeを終了：

```bash
/exit
```

1. 再起動：

```bash
cd ~/projects/webapp_study_todo-app-java
claude
```

1. 再度画像を読み込む：

```bash
claude "この画像読める？" ./01_login_screen_small.jpg
```

→ **成功！**

#### 💡 理解ポイント

**Claude Codeのセッション不具合（既知の問題）**

Claude Code（CLI）は、一度`Could not process image`エラーが連続で出ると、**そのセッションが回復不能**になることがあります。

- 画像参照情報が壊れた状態で会話ログに保存される
- 以降、正しい画像を渡しても400エラーを返し続ける
- `/exit` → 再起動で新しいセッションになれば解決

これは Claude Code 側の既知の不安定挙動で、GitHubのIssueでも報告されています：

- [anthropics/claude-code - Image upload issues](https://github.com/anthropics/claude-code/issues/16169)

**判断基準**

- 画像を`file`/`identify`で確認して正常
- 他のツール（ChatGPT、Windowsフォトビューアなど）では表示できる
- Claude Codeだけが400を返す
→ **セッション問題の可能性大**

#### 📝 補足

**セッションログの退避（確実に直したい場合）**

Claude Codeは会話を `~/.claude/projects/` に `.jsonl` 形式で保存しています。壊れた画像参照が残っている場合、このファイルを退避すると確実に直ります：

```bash
# 最新のセッションファイルを確認
ls -lt ~/.claude/projects/*/*.jsonl | head -n 5

# 一番新しいやつを退避（削除したくない場合）
mv ~/.claude/projects/<project-hash>/<session-id>.jsonl \
   ~/.claude/projects/<project-hash>/<session-id>.jsonl.bak

# Claude Code再起動
claude
```

**画像の渡し方（クォート混在を避ける）**

❌ 失敗しやすい例：

```bash
claude "この画像読める？'/path/to/image.png'"
```

✅ 正しい例：

```bash
claude "この画像読める？" ./image.png
```

引数として独立して渡すのが安全です。

### 8. 最終確認と成功

#### 🎯 目的

Claude Codeを再起動した状態で、変換した画像を読み込みます。

#### 🛠️ 手順詳細

```bash
cd /home/sakih/projects/webapp_study_todo-app-java/docs/01_specs/wireframes
claude "この画像読める？" ./01_login_screen_small.jpg
```

→ **成功！Claude Codeが画像を正常に読み込み、内容を認識しました。**

詳細内容（例）

```bash
はい、読めます！完璧に表示されていますね🎉

  画像の内容：Todo Appのログイン画面ワイヤーフレーム

  読み取れる要素：

  画面構成
  - タイトル：「Todo App」
  - 画面名：「Login」
  - メールアドレス入力欄（プレースホルダー：example.com）
  - パスワード入力欄（マスク表示 ○○○○○ + 表示/非表示切替アイコン）
  - ログインボタン

  エラーハンドリング
  左側のメモ：
  - エラーレベル
  - 状態
  - バリデーション

  エラーメッセージ例：
  - 「このメールアドレスでの登録はありません」
  - または「パスワードが間違っています」

  ソーシャルログイン
  - Googleアカウントでログイン
  - LINEアカウントでログイン
  - アカウント作成はこちら（リンク）
```

#### 💡 理解ポイント

今回のエラーは**複合的な原因**でした：

1. **形式問題**：HEIC → PNG（Windows側で変換）
2. **サイズ問題**：19MB/3024×4032/RGBA → 550KB/1500×2000/RGB（ImageMagickで軽量化）
3. **セッション問題**：壊れたセッション → 再起動で解決

このように、「画像が読めない」という一見シンプルな問題でも、複数のレイヤーで問題が発生していることがあります。**機械的に切り分ける**ことが重要です。

#### 📝 補足

**トラブルシューティングの順序（今後の鉄則）**

1. `file`で実体確認（HEIC/PNG/JPEGの確認）
2. `ls -lh`でサイズ確認（大きすぎないか）
3. `identify`で詳細確認（解像度、色空間、アルファの有無）
4. 形式変換（JPEG推奨）
5. 軽量化（〜2MB、〜2000px）
6. それでもダメならツール再起動

## 学び・次に活かせる知見

- **拡張子と実体は別物**：`file`コマンドで必ず実体を確認する習慣をつける
- **Claude APIの画像制約**：JPEG/PNG、〜2MB、〜2000px、RGB（アルファなし）が安全圏
- **HEICはネイティブアプリで変換**：WSLでのコマンドライン変換にこだわらず、Windows/macOSのネイティブ機能を使う方が速い
- **ImageMagickのバージョン違い**：Ubuntu標準はv6（`convert`）、v7は`magick`コマンド
- **セッション問題**：Claude Codeで画像エラーが2〜3回続いたら即再起動
- **機械的な切り分け**：感覚ではなく、`file`/`identify`/`xxd`などのコマンド出力で原因を特定する
- **最短ルート思考**：HEICをWSLで変換しようとして沼ったが、Windows側で変換すれば一発で解決した。「適材適所」の判断が重要

## 参考文献

1. [Claude Code 公式ドキュメント](https://claude.com/claude-code)
2. [ImageMagick 公式ドキュメント - Command-line Options](https://imagemagick.org/script/command-line-options.php)
3. [GitHub - anthropics/claude-code - Issues（画像関連）](https://github.com/anthropics/claude-code/issues)
4. [Reddit - Claude Code image upload issues](https://www.reddit.com/r/ClaudeAI/comments/claude_code_image_issues/)
5. [HEIF/HEIC 形式について - Wikipedia](https://en.wikipedia.org/wiki/High_Efficiency_Image_File_Format)

## もし時間があれば…Todo

- Windows Photosでの変換手順のスクリーンショット追加
- ImageMagick v6とv7の違いをより詳細に比較
- Claude Code以外のAI CLI（ChatGPT CLI、GitHub Copilot CLIなど）での画像対応状況を調査

## 更新履歴

- 2026-01-05：初版公開
