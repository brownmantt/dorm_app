---
name: 09-tester-agent
description: >-
  Excel/Markdown テストケースを Playwright で実行し、スクショ・DB差分・test-report.md・results.json を出力する自動化テスト実行エージェント。
  CRUD 100%カバレッジ、可視実行、P1→P4順。Use when the user says 09-tester-agent, tester-agent, テスト実行, Playwright実行, Excelテスト, 自動化テスト実行.
disable-model-invocation: true
---

# 09-tester-agent（自動化テスト実行エージェント）

Subagent 定義: [../../agents/09-tester-agent.md](../../agents/09-tester-agent.md)

## ミッション

**08-test-agent** が設計した Excel/Markdown テストケースを読み取り、Playwright で実行し、操作前後のスクリーンショット・DB 差分・日本語レポートを一括出力する。

## 入力

1. テストケースファイル（Excel `.xlsx` または Markdown `.md`）
2. （任意）URL / DB — 未指定時は `.trae/config/env.json`
3. （任意）モジュールフィルタ、優先度フィルタ（例：`P1` のみ）

## 出力

| ファイル | パス |
|---------|------|
| レポート | `.trae/test/test-report.md` |
| JSON | `.trae/test/results.json` |
| スクショ | `.trae/test/screenshots/` |

---

## 実行フロー（必須）

```
1. .trae/config/env.json 読込
2. @playwright-env-setup で Node/依存/ブラウザ/前後端確認
3. 前後端未起動 → バックグラウンド起動
4. テストケース 8列解析 → P1→P4 ソート
5. node run-tester.js "<ケースファイル>"
6. レポート・JSON・スクショ確認
7. チャットでサマリ報告
```

**エージェント実行原則：**

1. **必ず Shell でコマンドを実行する** — 手順説明だけで終わらせない
2. **headless=false（可視実行）をデフォルトとする**
3. **F列タグを厳密解析** — 捏造ステップ禁止
4. **増改削は DB 前後比較＋4段階スクショ必須**
5. 失敗時もレポート・JSON を出力する

---

## クイック実行

**Windows PowerShell：**

```powershell
Set-Location "${PROJECT_ROOT}\.trae\test"
npm install playwright-core mysql2 xlsx
node run-tester.js "D:\AItest\case\MyProject_67890.xlsx"
```

**Markdown 用例：**

```powershell
node run-tester.js "D:\AItest\case\MyProject_67890.md"
```

**優先度フィルタ（P1 のみ）：**

```powershell
$env:PRIORITY_FILTER="P1"; node run-tester.js "path\to\case.xlsx"
```

---

## 14種タグ（F列）

タグは **日本語で統一**。旧表記（中国語）も互換解析されるが、新規作成は日本語タグのみ。

| タグ（日本語・正） | 形式 | 旧表記（互換） |
|--------------------|------|----------------|
| `[遷移]` | `/path` | `[导航]` |
| `[入力]` | `selector::value` | `[输入]` |
| `[クリック]` | ボタンテキスト | `[点击]` |
| `[待機]` | `ms` または selector | `[等待]` |
| `[確認]` | `text 可視` | `[断言]` |
| `[URL確認]` | `/path` | `[断言URL]` |
| `[DB]` | `SELECT ...` | — |
| `[選択]` | `selector::option` | `[选择]` |
| `[撮影]` | 説明 | `[截图]` |
| `[送信]` | ボタン名 | `[提交]` |
| `[必須チェック]` | フィールド名 | `[校验必填]` |
| `[制約チェック]` | `説明::値::エラー` | `[校验约束]` |
| `[行確認]` | `text 存在/不存在` | `[断言行]` |
| `[閉じる]` | — | `[关闭]` |

詳細: [reference.md](reference.md)

---

## 環境変数

| 変数 | デフォルト | 説明 |
|------|------------|------|
| `HEADLESS` | `false` | `true` のみ非表示（本エージェントは可視が標準） |
| `SLOWMO` | `300` | ステップ間遅延(ms) |
| `BASE_URL` | env.json | フロント URL 上書き |
| `API_URL` | env.json | バック URL 上書き |
| `PRIORITY_FILTER` | — | `P1` 等、カンマ区切り可 |

---

## 呼び方

```
@09-tester-agent D:\AItest\case\MyProject_67890.xlsx を実行して
```

```
tester-agent Excel 用例で Playwright テストを走らせ、レポートを出して
```

```
@09-tester-agent P1 のみ実行、test-report.md を確認
```

---

## パイプライン

```
PRD → @08-test-agent（設計）→ Excel+MD → @09-tester-agent（実行）→ レポート
```

## 追加リソース

- タグ詳細・フレームワークセレクタ: [reference.md](reference.md)
- 環境セットアップ: `@playwright-env-setup`
- 実行スクリプト: [scripts/run-tester.js](scripts/run-tester.js)
