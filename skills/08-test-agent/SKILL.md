---
name: 08-test-agent
description: >-
  要求仕様書（PRD）から自動化テストエージェント向けテストケースを設計し、Excel（8列固定・日本語ヘッダ）と Markdown を同名で出力する。
  各モジュール最低5件（正常系・新規・異常・境界値・DB検証）。手順は [遷移][入力][クリック][確認] 等の日本語操作タイプ必須。
  Use when the user says 08-test-agent, test-agent, テストケース設計, テスト用例生成, PRDからテストケース, or needs Excel/Markdown test cases.
disable-model-invocation: true
---

# 08-test-agent（テストケース設計エージェント）

Subagent 定義: [../../agents/08-test-agent.md](../../agents/08-test-agent.md)

## ミッション

要求仕様書（PRD）を分析し、**自動化テストエージェントが直接実行できる形式**の高品質テストケースを Excel + Markdown で一括生成する。

## 入力

1. 要求仕様書（PRD）
2. プロジェクト基本情報（フロントエンド URL、バックエンド URL、DB 接続）— 未指定時は `.trae/config/env.json` を読む
3. （任意）補完対象モジュール

## 出力

| ファイル | パス | 形式 |
|---------|------|------|
| Excel | `{出力先}/{プロジェクト名}_{ランダム数}.xlsx` | 8列固定（日本語ヘッダ） |
| Markdown | `{出力先}/{プロジェクト名}_{ランダム数}.md` | 表形式、Excel と内容完全一致 |

既定出力先：`D:\AItest\case\` またはユーザー指定（本プロジェクト例：`document/test/`）

---

## 実行フロー（必須）

```
1. PRD 全文読込 → モジュール分解
2. 各モジュール最低5件のテストケース設計
3. Excel 生成（先）
4. 同名 Markdown 生成（後）
5. 納品サマリをチャット出力
```

**エージェント実行原則：**

1. **必ず PRD を read してから設計する** — 推測で機能を追加しない
2. **Excel → Markdown の順で生成する** — 内容完全一致を検証する
3. **Shell で生成スクリプトを実行する** — 手書き Excel よりスクリプトを優先する
4. 完了後 `@09-tester-agent` で実行、`@playwright-env-setup` で環境初期化を案内できる

---

## クイックリファレンス

### 8列ヘッダ（固定）

`所属モジュール | 関連要求 | テストケース名 | 前提条件 | テストタイプ | テスト手順 | 期待結果 | 優先度`

### テスト手順形式（F列）

```
番号. [操作タイプ] 操作内容
```

操作タイプ: `[遷移]` `[入力]` `[クリック]` `[待機]` `[確認]` `[URL確認]` `[DB]` `[選択]` `[撮影]`

### テストケース名

`[条件]_[動詞＋目的語]` — 例: `正常ログイン_ホーム画面へ遷移することを確認`

### 語尾統一

| 用途 | 表現 |
|------|------|
| 正常系 | 〜ことを確認する |
| 異常系 | 〜エラーが表示されることを確認する |
| DB検証 | 〜レコードが存在することを確認する |

---

## 生成スクリプト

JSON ドラフトから Excel + Markdown を一括生成：

```powershell
node .cursor/skills/08-test-agent/scripts/generate_testcase.js `
  --input document/test/_draft.json `
  --project MyProject `
  --output-dir document/test
```

**JSON ドラフト形式：**

```json
{
  "cases": [
    {
      "module": "認証・ログイン1.0",
      "requirement": "PRD 3.1",
      "name": "正常ログイン_ホーム画面へ遷移することを確認",
      "preconditions": "1. フロントエンド（http://localhost:5173）およびバックエンド（http://localhost:8080/api/v1）が起動している\n2. データベース dormitory に正常接続できる",
      "type": "機能",
      "steps": "1. [遷移] /login\n2. [入力] input[name=\"user\"]::admin",
      "expected": "1. ログインページが正常に表示される\n2. ユーザー名欄に admin が入力されている",
      "priority": "P1"
    }
  ]
}
```

---

## 呼び方

```
@08-test-agent document/詳細設計書_0522 を読んでテストケースを生成して
```

```
@08-test-agent 入退寮管理モジュールのテストケースを5件以上追加
```

---

## 追加リソース

- フォーマット詳細: [reference.md](reference.md)
- テスト実行: `@09-tester-agent` / `@playwright-env-setup`
