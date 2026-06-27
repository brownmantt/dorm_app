---
name: playwright-env-setup
description: >-
  Playwright テスト環境の一括インストールおよび実行を行うエージェント。ユーザーが『playwright環境をインストール』『テスト環境を初期化』『自動化テストを実行』『テストを走らせる』などと言ったときに起動する。Node.js 依存関係のインストール、Chromium ブラウザのダウンロード、MySQL 接続ライブラリの導入、テストスクリプトの生成と実行を担当する。移植性が高く、任意のマシン・任意のプロジェクトに対応可能。
---

# Playwright テスト環境インストール＆実行エージェント（移植可能版）

Playwright 自動化テスト環境をワンストップで構築し、テストを実行する。  
**すべてのパス・プロジェクト情報はハードコードせず**、設定から自動取得するため、どのマシン・どの前後分離プロジェクトでも動作可能。

## エージェント実行原則

1. **必ず Shell ツールでコマンドを実行する** — 手順の説明だけで終わらせない
2. **失敗時は再試行または代替手段を試す** — 1回の失敗で諦めない
3. **各ステップの結果を確認してから次へ進む**
4. **OS に応じてコマンドを切り替える** — Windows は PowerShell、Mac/Linux は bash

---

## 0. まずプロジェクト設定を読み込む（重要）

何を実行する前にも、まず共有設定ファイル `.trae/config/env.json`（`project-structure-init` により生成）を読み込む。  
存在しない場合は、以下の情報をユーザーに確認するか、プロジェクトファイルから自動検出する：

| パラメータ | 取得元 | 検出方法 |
|-----------|--------|----------|
| プロジェクトルート | 現在の作業ディレクトリ | `git rev-parse --show-toplevel` またはカレントディレクトリ |
| フロントエンドディレクトリ / URL | env.json | `vite.config.*` / `package.json` を含むディレクトリを探索 |
| バックエンドディレクトリ / URL / 起動コマンド | env.json | `pom.xml` / `package.json` を含むディレクトリを探索 |
| DB 接続情報 | env.json または `application.properties` | `spring.datasource.*` を読み取る |
| テストディレクトリ | `{プロジェクトルート}/.trae/test` | 固定相対パス |

**変数規約**（以下 `${VAR}` は実行時に実値へ置換される）：

- `${PROJECT_ROOT}` = プロジェクトルート
- `${TEST_DIR}` = `${PROJECT_ROOT}/.trae/test`
- `${FRONTEND_DIR}` / `${BACKEND_DIR}` = 前後端ディレクトリ
- `${FRONTEND_URL}` / `${BACKEND_URL}` = アクセス URL
- `${MVN}` = Maven 実行ファイル（§1 第5歩で自動検出）

---

## 1. 環境チェックとインストール

### ① Node.js の確認

```powershell
node --version    # バージョン >= 16 必須
npm --version
```

Mac/Linux:

```bash
node --version
npm --version
```

### ② テストディレクトリへ移動し npm 初期化

```powershell
New-Item -Path "${TEST_DIR}" -ItemType Directory -Force
Set-Location "${TEST_DIR}"

if (-not (Test-Path package.json)) {
  '{ "name": "autotest", "version": "1.0.0" }' | Out-File package.json -Encoding UTF8
}
```

Mac/Linux:

```bash
mkdir -p "${TEST_DIR}"
cd "${TEST_DIR}"
[ -f package.json ] || echo '{ "name": "autotest", "version": "1.0.0" }' > package.json
```

### ③ 必須 npm パッケージのインストール

```powershell
npm install playwright-core mysql2 xlsx

node -e "require('playwright-core'); require('mysql2/promise'); require('xlsx'); console.log('deps OK')"
```

### ④ ブラウザ準備（可能ならシステム Chrome を優先）

```powershell
$chrome = @(
  "$env:ProgramFiles\Google\Chrome\Application\chrome.exe",
  "${env:ProgramFiles(x86)}\Google\Chrome\Application\chrome.exe",
  "$env:LOCALAPPDATA\Google\Chrome\Application\chrome.exe"
) | Where-Object { Test-Path $_ } | Select-Object -First 1

if ($chrome) { "Chrome found: $chrome  → channel:'chrome' を使用" }
else { "Chrome 未検出 → npx playwright install chromium を実行" }
```

起動時は **Chrome → Edge → Chromium** の順でフォールバック：

```javascript
let browser;
try {
  browser = await chromium.launch({ headless, channel: 'chrome', slowMo });
} catch {
  try { browser = await chromium.launch({ headless, channel: 'msedge', slowMo }); }
  catch { browser = await chromium.launch({ headless, slowMo }); }
}
```

Chrome 未検出時は `${TEST_DIR}` で `npx playwright install chromium` を実行する。

### ⑤ Maven の自動検出（バックエンドが Java/SpringBoot の場合）

```powershell
$mvn = (Get-Command mvn -ErrorAction SilentlyContinue).Source

if (-not $mvn -and $env:MAVEN_HOME) { $mvn = "$env:MAVEN_HOME\bin\mvn.cmd" }
if (-not $mvn -and $env:M2_HOME)    { $mvn = "$env:M2_HOME\bin\mvn.cmd" }

if ($mvn) { "Maven: $mvn" } else { "Maven 未検出。インストールするか env.json に backend.maven_path を設定してください" }
```

Mac/Linux:

```bash
mvn=$(command -v mvn 2>/dev/null || echo "${MAVEN_HOME}/bin/mvn")
```

> バックエンドが Node の場合は `npm run start` または env.json の `backend.start_cmd` を使用。

### ⑥ 前後端の起動と確認

ポートは env.json から取得（例: 3000 / 8081）：

```powershell
netstat -ano | Select-String ":3000|:8081"
```

フロントエンド（未起動の場合）:

```powershell
Set-Location "${FRONTEND_DIR}"; npm run dev
```

バックエンド（Java）:

```powershell
Set-Location "${BACKEND_DIR}"; & "$mvn" spring-boot:run -DskipTests
```

バックエンド（Node）:

```powershell
Set-Location "${BACKEND_DIR}"; npm run start
```

起動コマンドは env.json の `backend.start_cmd` があればそれを優先する。  
前後端は **バックグラウンド** で起動し、疎通確認後にテストへ進む。

### ⑦ バックエンド疎通確認

```powershell
try { Invoke-WebRequest "${BACKEND_URL}/" -TimeoutSec 5 | Out-Null; "後端可達" }
catch { "後端未响应" }
```

Mac/Linux:

```bash
curl -sf --max-time 5 "${BACKEND_URL}/" && echo "後端可達" || echo "後端未响应"
```

---

## 2. テストスクリプト

`${TEST_DIR}` に以下が存在しない場合は生成する：

| ファイル | 用途 |
|---------|------|
| `run-playwright.js` | 基本 Playwright テスト実行 |
| `run-from-excel.js` | Excel 用例ファイルからテスト実行（レガシー） |
| `run-tester.js` | **09-tester-agent** 用 — 8列Excel/MD・14タグ・DB・レポート |

生成時の要件：

- `BASE_URL` / `API_URL` は env.json または環境変数から読み込む
- ブラウザ起動は §1 ④ の Chrome → Edge → Chromium フォールバックを使用
- 失敗時スクリーンショットを `${TEST_DIR}/screenshots/` に保存
- 実行結果を `${TEST_DIR}/test-report.md` に出力

---

## 3. 自動化テストの実行

```powershell
Set-Location "${TEST_DIR}"

node run-playwright.js

$env:HEADLESS="true"; node run-playwright.js

node run-tester.js "<用例ファイルパス>"
node run-from-excel.js "<用例ファイルパス>"
```

Mac/Linux:

```bash
cd "${TEST_DIR}"
node run-playwright.js
HEADLESS=true node run-playwright.js
node run-tester.js "<用例ファイルパス>"
node run-from-excel.js "<用例ファイルパス>"
```

---

## 4. 環境変数

| 変数 | デフォルト | 説明 |
|------|------------|------|
| `HEADLESS` | `false` | `true`=非表示実行 |
| `SLOWMO` | `300` | ステップ間遅延(ms) |
| `BASE_URL` | env.json | フロントエンド URL |
| `API_URL` | env.json | バックエンド URL |

---

## 5. ワンストップ実行フロー

```
1. env.json を読み込む（なければ検出/質問）
2. node --version を確認
3. cd ${TEST_DIR} && npm install playwright-core mysql2 xlsx
4. Chrome を検出（なければ chromium をインストール）
5. Maven を検出（Java バックエンド）
6. 前後端の起動確認
7. テストスクリプトがなければ生成
8. node run-playwright.js
9. test-report.md + screenshots/ を出力
```

実行後、ユーザーに以下を報告する：

- インストール結果（Node / npm / 依存パッケージ / ブラウザ）
- 前後端の起動状態
- テスト成功/失敗件数
- `test-report.md` とスクリーンショットのパス

---

## 6. マシン移行チェックリスト（PC を変えるとき）

| 項目 | 依存 | 対応 |
|------|------|------|
| プロジェクトパス | あり | `${PROJECT_ROOT}` で動的取得 |
| Maven パス | あり | 自動検出 |
| Chrome | なし | システム Chrome 優先 |
| Node/npm | なし | 共通 |
| ポート/URL | あり | env.json から取得 |
| DB 接続 | あり | env.json / application.properties |
| テストケース | あり | 実行時に指定 |
| OS | 一部 | Windows=PowerShell、Mac/Linux=bash |

---

## 7. よくある問題

| 問題 | 解決策 |
|------|--------|
| `Executable doesn't exist` | `npx playwright install chromium` または Chrome をインストール |
| 前端ポート接続拒否 | `npm run dev` |
| 后端ポート接続拒否 | Maven/npm で起動 |
| `mvn が見つからない` | Maven を PATH に追加 or env.json に設定 |
| MySQL 接続失敗 | env.json / application.properties を確認 |

---

## 呼び方

```
playwright環境をインストール
```

```
テスト環境を初期化して自動化テストを実行
```

```
@playwright-env-setup Excel 用例でテストを走らせる
```
