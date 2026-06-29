# 社員 API

> 呼び出し元: `views/residence/ResidenceRegister.vue` → `api/employee.js`

---

## 社員検索

**インターフェース名称：** 社員検索  
**機能説明：** 入居登録画面の入居者コンボボックス用に、未入居の社員を社員マスタから取得する  
**インターフェースURL：** `/api/v1/employees`  
**リクエスト方式：** GET

---

### 機能説明

入居登録画面の入居者選択（`el-select`）で使用。`notResidingOnly=true` により当日時点で入居中の社員を除外し、社員マスタの更新日（`updated_at`）降順で最大 5000 件取得する。

---

### リクエストパラメータ

```json
{
  "notResidingOnly": true,
  "page": 0,
  "size": 5000
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| notResidingOnly | boolean | はい | 未入居者のみ取得 | true |
| page | int | いいえ | ページ番号（0 始まり） | 0 |
| size | int | いいえ | 1 ページあたり件数 | 5000 |

---

### レスポンスパラメータ

```json
{
  "content": [
    {
      "employeeId": "E00012",
      "name": "山田 太郎"
    }
  ],
  "totalElements": 1
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| content | object[] | はい | 社員一覧 | — |
| content[].employeeId | string | はい | 社員 ID | E00012 |
| content[].name | string | はい | 氏名 | 山田 太郎 |
| totalElements | int | いいえ | 総件数 | 1 |

---

## 寮費算定用社員一覧

**インターフェース名称：** 寮費算定用社員一覧  
**機能説明：** 寮費算定ダイアログの社員コンボボックス用に、社員マスタを取得する  
**インターフェースURL：** `/api/v1/employees`  
**リクエスト方式：** GET

---

### 機能説明

`dormFeeComboSort=true` を指定すると、表示順は **入居中 → 更新日降順 → 氏名昇順** となる。

`targetYearMonth`（`YYYY-MM`）を指定した場合、対象月と重なる入居履歴がある社員のみ返す。`dormitoryId` / `roomId` を追加指定すると、当該寮・部屋の入居履歴でさらに絞り込む。`residing` フラグは `targetYearMonth` 指定時は対象月時点の入居有無、未指定時は当日時点とする。

---

### リクエストパラメータ

```json
{
  "dormFeeComboSort": true,
  "targetYearMonth": "2026-06",
  "dormitoryId": "D001",
  "roomId": "R003",
  "page": 0,
  "size": 5000
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| dormFeeComboSort | boolean | はい | 寮費算定コンボ用ソートを適用 | true |
| targetYearMonth | string | いいえ | 対象年月（`YYYY-MM`）。指定時は当該月と重なる入居履歴がある社員のみ | 2026-06 |
| dormitoryId | string | いいえ | 寮 ID（`targetYearMonth` と併用で絞込） | D001 |
| roomId | string | いいえ | 部屋 ID（`targetYearMonth` と併用で絞込） | R003 |
| page | int | いいえ | ページ番号（0 始まり） | 0 |
| size | int | いいえ | 1 ページあたり件数 | 5000 |

---

### レスポンスパラメータ

```json
{
  "content": [
    {
      "employeeId": "E00012",
      "name": "山田 太郎",
      "residing": true,
      "updatedAt": "2026-06-28T10:00:00"
    }
  ],
  "totalElements": 1
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| content | object[] | はい | 社員一覧 | — |
| content[].employeeId | string | はい | 社員 ID | E00012 |
| content[].name | string | はい | 氏名 | 山田 太郎 |
| content[].residing | boolean | はい | 入居中フラグ | true |
| content[].updatedAt | string | いいえ | 更新日時 | 2026-06-28T10:00:00 |
| totalElements | int | いいえ | 総件数 | 1 |
