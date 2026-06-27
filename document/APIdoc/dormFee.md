# 寮費 API

> 呼び出し元: `views/dormFee/DormFeeList.vue` → `api/dormFee.js`

---

## 寮費一覧取得

**インターフェース名称：** 寮費一覧取得  
**機能説明：** 検索条件に基づき寮費レコードをページング取得する  
**インターフェースURL：** `/api/v1/dorm-fees`  
**リクエスト方式：** GET

---

### リクエストパラメータ

```json
{
  "employeeId": "E00012",
  "targetYearMonth": "2026-06",
  "status": "DRAFT",
  "page": 0,
  "size": 20
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| employeeId | string | いいえ | 社員 ID | E00012 |
| targetYearMonth | string | いいえ | 対象年月 `YYYY-MM` | 2026-06 |
| status | string | いいえ | ステータス（`DRAFT`/`CONFIRMED`） | DRAFT |
| page | int | いいえ | ページ番号（0 始まり） | 0 |
| size | int | いいえ | 1 ページあたり件数 | 20 |

---

### レスポンスパラメータ

```json
{
  "content": [
    {
      "dormFeeId": "DF001",
      "employeeId": "E00012",
      "targetYearMonth": "2026-06",
      "amount": 45000,
      "status": "DRAFT"
    }
  ],
  "totalElements": 1
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| content | object[] | はい | 寮費一覧 | — |
| content[].dormFeeId | string | はい | 寮費 ID | DF001 |
| content[].employeeId | string | はい | 社員 ID | E00012 |
| content[].targetYearMonth | string | はい | 対象年月 | 2026-06 |
| content[].amount | number | はい | 金額 | 45000 |
| content[].status | string | はい | ステータス | DRAFT |
| totalElements | int | いいえ | 総件数 | 1 |

---

## 寮費算定

**インターフェース名称：** 寮費算定  
**機能説明：** 入居情報に基づき対象月の寮費を試算する  
**インターフェースURL：** `/api/v1/dorm-fees/calculate`  
**リクエスト方式：** POST

---

### 機能説明

算定ダイアログで入力された条件から金額と算定根拠を返却。結果を確認後、寮費登録 API で永続化する。

---

### リクエストパラメータ

```json
{
  "employeeId": "E00012",
  "roomId": "R003",
  "targetYearMonth": "2026-06",
  "moveInDate": "2026-06-01",
  "moveOutDate": "2026-06-30"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| employeeId | string | はい | 社員 ID | E00012 |
| roomId | string | はい | 部屋 ID | R003 |
| targetYearMonth | string | はい | 対象年月 `YYYY-MM` | 2026-06 |
| moveInDate | string | はい | 入寮日 | 2026-06-01 |
| moveOutDate | string | いいえ | 退寮日 | 2026-06-30 |

---

### レスポンスパラメータ

```json
{
  "amount": 45000,
  "basis": {
    "roomAreaSqm": 12.5,
    "roomType": "STANDARD",
    "billableDays": 30,
    "dailyRate": 1500,
    "formula": "面積 × 日額 × 請求日数"
  }
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| amount | number | はい | 算定金額 | 45000 |
| basis | object | いいえ | 算定根拠 | — |
| basis.roomAreaSqm | number | いいえ | 部屋面積（㎡） | 12.5 |
| basis.roomType | string | いいえ | 部屋種別 | STANDARD |
| basis.billableDays | int | いいえ | 請求日数 | 30 |
| basis.dailyRate | number | いいえ | 日額 | 1500 |
| basis.formula | string | いいえ | 算定式 | 面積 × 日額 × 請求日数 |

---

## 寮費登録

**インターフェース名称：** 寮費登録  
**機能説明：** 算定結果を下書き（DRAFT）状態で登録する  
**インターフェースURL：** `/api/v1/dorm-fees`  
**リクエスト方式：** POST

---

### リクエストパラメータ

```json
{
  "employeeId": "E00012",
  "roomId": "R003",
  "targetYearMonth": "2026-06",
  "amount": 45000,
  "basisDetail": {
    "roomAreaSqm": 12.5,
    "roomType": "STANDARD",
    "billableDays": 30,
    "dailyRate": 1500,
    "formula": "面積 × 日額 × 請求日数"
  }
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| employeeId | string | はい | 社員 ID | E00012 |
| roomId | string | はい | 部屋 ID | R003 |
| targetYearMonth | string | はい | 対象年月 | 2026-06 |
| amount | number | はい | 金額（算定 API の結果） | 45000 |
| basisDetail | object | いいえ | 算定根拠（算定 API の basis） | — |
| basisDetail.roomAreaSqm | number | いいえ | 部屋面積 | 12.5 |
| basisDetail.roomType | string | いいえ | 部屋種別 | STANDARD |
| basisDetail.billableDays | int | いいえ | 請求日数 | 30 |
| basisDetail.dailyRate | number | いいえ | 日額 | 1500 |
| basisDetail.formula | string | いいえ | 算定式 | 面積 × 日額 × 請求日数 |

---

### レスポンスパラメータ

フロントは成功後にダイアログを閉じ、一覧を再取得する。

---

## 寮費確定

**インターフェース名称：** 寮費確定  
**機能説明：** 下書き状態の寮費を確定（CONFIRMED）に変更する  
**インターフェースURL：** `/api/v1/dorm-fees/{id}/confirm`  
**リクエスト方式：** PUT

---

### リクエストパラメータ

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| id | string | はい | パスパラメータ：寮費 ID | DF001 |

---

### レスポンスパラメータ

フロントは成功後に一覧を再取得する。リクエストボディなし。
