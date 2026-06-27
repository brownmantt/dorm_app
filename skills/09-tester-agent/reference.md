# 09-tester-agent リファレンス

## Excel / Markdown 8列

| 列 | 列名（中/日） | 説明 |
|----|--------------|------|
| A | 所属模块 / 所属モジュール | モジュール名 |
| B | 相关研发需求 / 関連要求 | PRD 章 |
| C | 用例名称 / テストケース名 | `条件_動詞＋目的語` |
| D | 前置条件 / 前提条件 | 番号＋改行 |
| E | 用例类型 / テストタイプ | 機能/性能等 |
| F | 测试步骤 / テスト手順 | `[タグ]パラメータ` 改行 |
| G | 预期结果 / 期待結果 | F と1対1 |
| H | 优先级 / 優先度 | P1〜P4 |

## ステップ解析規則

- 1行 = 1ステップ
- 形式：`番号. [タグ] パラメータ` または `番号.[タグ]パラメータ`
- タグは `[` `]` で囲む（全角半角混在可）
- G列の N 行目が F列 N 行目の期待結果

## 14種タグ実装詳細

タグは **日本語に統一**。旧表記（中国語）も互換解析される（各見出しのカッコ内）。

### `[遷移]`（旧 `[导航]`）

```
1. [遷移] /login
```

→ `page.goto(BASE_URL + '/login', { waitUntil: 'domcontentloaded' })`

### `[入力]`（旧 `[输入]`）

```
2. [入力] input[name="username"]::admin
```

→ `page.locator(selector).fill(value)` — `::` で分割

### `[クリック]`（旧 `[点击]`）

```
3. [クリック] ログイン
```

→ 優先順：`getByRole('button', { name })` → `getByText` → `getByRole('link')`

### `[待機]`（旧 `[等待]`）

```
4. [待機] 2000
4. [待機] .el-table
```

→ 数値のみなら `waitForTimeout`、それ以外は `waitForSelector`

### `[確認]`（旧 `[断言]`）

```
5. [確認] システム概要 可視
```

→ テキストを含む要素の `waitFor({ state: 'visible' })`

### `[URL確認]`（旧 `[断言URL]`）

```
6. [URL確認] /dashboard
```

→ `page.waitForURL('**' + path + '**')`

### `[DB]`

```
7. [DB] SELECT id, username FROM sys_user WHERE username='admin'
```

→ DB 接続実行、結果を actual に記録。前後スナップショットで差分比較。

### `[選択]`（旧 `[选择]`）

```
8. [選択] .el-select::第一倉庫
```

→ セレクタクリック → ドロップダウン項目クリック（framework 別）

### `[撮影]`（旧 `[截图]`）

```
9. [撮影] ログイン成功画面
```

→ `screenshots/{tcId}-s{stepNo}.png` フルページ保存

### `[送信]`（旧 `[提交]`）

```
10. [送信] 保存
```

→ ボタンクリック → `.el-message--success` / `.ant-message-success` 待機

### `[必須チェック]`（旧 `[校验必填]`）

```
11. [必須チェック] ユーザー名
```

→ 空のまま送信 → `.el-form-item__error` 等でエラー確認

### `[制約チェック]`（旧 `[校验约束]`）

```
12. [制約チェック] 数量::999999::在庫超過
```

→ 不正値入力 → エラーメッセージまたは送信ブロック確認

### `[行確認]`（旧 `[断言行]`）

```
13. [行確認] テストデータ001 存在
13. [行確認] 削除済みレコード 不存在
```

→ テーブル行 `tr` 内テキスト検索

### `[閉じる]`（旧 `[关闭]`）

```
14. [閉じる]
```

→ `.el-dialog__headerbtn` または `.ant-modal-close` クリック

---

## UI フレームワーク別セレクタ

### Element-Plus（`frontend.framework: element-plus`）

| 用途 | セレクタ |
|------|----------|
| 下拉項目 | `.el-select-dropdown__item` |
| ページタイトル | `.page-title` |
| テーブル | `.el-table` |
| カード | `.el-card` |
| フォームエラー | `.el-form-item__error` |
| 成功メッセージ | `.el-message--success` |
| ダイアログ閉じ | `.el-dialog__headerbtn` |

### Ant Design（`frontend.framework: ant-design`）

| 用途 | セレクタ |
|------|----------|
| 下拉項目 | `.ant-select-item` |
| テーブル | `.ant-table` |
| カード | `.ant-card` |
| フォームエラー | `.ant-form-item-explain-error` |
| 成功メッセージ | `.ant-message-success` |
| ダイアログ閉じ | `.ant-modal-close` |

---

## 4段階スクショ（増改削）

| 段階 |  suffix | タイミング |
|------|---------|-----------|
| ① before | `-1-before` | 操作前・一覧状態 |
| ② filling | `-2-filling` | 入力完了 |
| ③ submitted | `-3-submitted` | 送信直後・成功メッセージ |
| ④ reopen | `-4-reopen` | 一覧再表示で確認 |

テストケース名または `[截图]` タグの説明に `before`/`filling`/`submitted`/`reopen` が含まれる場合は自動マッピング。

---

## DB 前後比較

| CRUD | 前 | 後 | 判定 |
|------|-----|-----|------|
| CREATE | COUNT=0 or 不存在 | 1件 | PASS |
| UPDATE | 旧フィールド値 | 新値 | PASS |
| DELETE | delete_flag=0 | delete_flag=1 | PASS |
| LIST | — | COUNT=N | UI行数と一致 |

---

## 優先度実行順

```
P1 → P2 → P3 → P4
```

同一優先度内は Excel 行順。

---

## コンソール出力フォーマット

```
═══ TC-{NN} {用例名} (行{row}·{priority}) ═══
  [{i}/{total}] [{tag}] {param}
        期待: {expected}
        実際: {actual} → PASS|FAIL ({screenshot})
  ─── 用例結果: PASS|FAIL ({passed}/{total}) ───
```

---

## 品質チェックリスト（実行後）

- [ ] 全用例 P1→P4 順で実行
- [ ] 増改削4段階スクショあり
- [ ] DB ステップ前後比較記録あり
- [ ] test-report.md 日本語出力
- [ ] results.json 構造準拠
- [ ] 画面別 CRUD マトリクス記載
- [ ] headless=false（可視）で実行
