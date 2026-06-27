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
