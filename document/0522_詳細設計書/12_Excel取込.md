# Excel / CSV データ取込・出力（モジュール 6）

> 親文書: [README.md](./README.md)  
> 機能 ID: **F-11**  
> 要件参照: RFP 8.x  
> 主要 Service: `ExcelImportService`, `CsvExportService`  
> 参照サンプル: `document/RFP/寮割_サンプル_A.xlsx`

---

## 機能概要

| 機能 ID | 機能名 | 優先度 |
|---------|--------|--------|
| F-11 | Excel/CSV データ取込・エクスポート | Must |

RFP 8.x に基づき、Excel からのデータ移行と CSV エクスポートを提供する。

---

## 画面設計

| 画面 ID | 画面名 | 権限 |
|---------|--------|------|
| SC-13 | Excel/CSV 取込・出力 | 管理者 |

### SC-13 画面構成

| 区分 | 内容 |
|------|------|
| 取込 | ファイル選択 → **プレビュー** → ユーザー確認 → **取込実行**（2 段階フロー） |
| エクスポート | 同一画面内に **CSV エクスポートボタン**（入居履歴・寮費）。タブ分離は不要 |

---

## API 設計

| Method | Path | 機能 ID | 概要 |
|--------|------|---------|------|
| POST | `/api/v1/imports/excel/preview` | F-11 | 取込プレビュー |
| POST | `/api/v1/imports/excel/execute` | F-11 | 取込実行 |
| GET | `/api/v1/exports/csv/residences` | F-11 | 入居履歴 CSV 出力 |
| GET | `/api/v1/exports/csv/dorm-fees` | F-11 | 寮費 CSV 出力 |

**関連エラー**: `IMPORT_VALIDATION_FAILED` — [15_エラー・例外設計.md](./15_エラー・例外設計.md)

**トランザクション**: 1ジョブ = 1トランザクション（全件成功 or 全件ロールバック）

RFP 8.2 処理フロー:

```text
① ファイルアップロード
② 一時テーブル（staging）格納
③ バリデーション
④ 本テーブル登録
```

---

## データベース

### `excel_import_job` / `excel_import_error`

取込ジョブ ID、ファイル名、ステータス、実行者、エラー行番号・項目・メッセージを保持。

### `import_staging_*`（一時テーブル）

RFP 8.2 に従い、検証前データを staging テーブルに格納してから本テーブルへ反映する。

---

## 業務ロジック

### 7.6.1 処理フロー

```text
アップロード (.xlsx / .csv)
  → シート解析（寮割フォーマット自動認識）
  → 一時テーブル INSERT
  → 行単位バリデーション
  → プレビュー API（エラー一覧 + 登録予定件数）
  → ユーザー確認
  → トランザクション一括 INSERT/UPDATE
  → ジョブ結果保存
```

### 7.6.2 寮割サンプル Excel 列マッピング

`寮割_サンプル_A.xlsx`（シート `寮割2026_4月`）の列定義:

| Excel 列（論理名） | 取込先テーブル.カラム | 必須 | 備考 |
|--------------------|----------------------|------|------|
| 社員寮情報（寮名） | `dormitory.name` | ○ | ブロック先頭行から抽出 |
| 社員寮情報（住所） | `dormitory.address` | ○ | 複数行セルから結合 |
| 社員寮情報（郵便番号） | `dormitory.postal_code` | △ | `〒` 行から抽出 |
| 氏名 | `employee.name` | ○ | |
| 所属 | `affiliation.name` → `employee.affiliation_id` | ○ | マスタ未存在時は自動作成可（要設定） |
| 事業部 | `employee.business_division` | 任意 | 拡張列 |
| 部屋詳細 | `room.room_detail` | ○ | 部屋名としても利用可 |
| エアコン | `room.has_air_conditioner` | 任意 | 有/無/なし → boolean |
| 寮費(6月以降) | `room.monthly_fee` | 任意 | |
| 現場最寄駅 | `employee.nearest_station` | 任意 | 拡張列 |
| 入寮日 / 現在の部屋への入寮日 | `residence_history.move_in_date` | ○ | シートにより列名が異なる |
| 退寮日 | `residence_history.move_out_date` | 任意 | NULL 可 |
| カレンダー列（1〜31） | — | — | **取込対象外**（表示用。DB から再生成） |

**地域の導出**: 住所から都道府県を判定し `dormitory.region` を自動設定（東京/大阪/名古屋/その他）。

### 7.6.3 バリデーション

| チェック | 内容 |
|----------|------|
| 形式 | 日付・数値・列挙値 |
| 必須 | マッピング定義に基づく |
| 参照整合 | 寮→部屋、所属マスタ、社員存在 |
| 業務 | 日単位の部屋重複・定員（[08_入退寮管理.md](./08_入退寮管理.md) と同一） |

**実装**：Apache POI または uni Excel ライブラリ。ファイルサイズ上限 10MB（暫定）。

### 7.6.4 CSV エクスポート

| 出力 | 内容 |
|------|------|
| 入居履歴 | 寮名、氏名、所属、部屋、入寮日、退寮日 |
| 寮費 | 氏名、対象年月、金額、算定根拠 |

操作ログ: `EXCEL_IMPORT`, `CSV_EXPORT` — [13_操作ログ.md](./13_操作ログ.md)
