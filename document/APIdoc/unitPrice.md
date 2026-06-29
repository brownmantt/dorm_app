## 単価マスタ一覧

**インターフェース名称：** 単価マスタ一覧
**機能説明：** 単価マスタの一覧をページング取得する
**インターフェースURL：** `/unit-prices`
**リクエスト方式：** GET

---

### 機能説明
単価コード（部分一致）、地域コード、寮ID、利用形態コードで絞り込み、一覧を返却する。地域名・寮名・部屋名・利用形態名称は JOIN で付与する。

---

### リクエストパラメータ
```json
{
  "code": "UP001",
  "region": "TOKYO",
  "dormitoryId": "D001",
  "usageTypeCode": "NORMAL",
  "page": 0,
  "size": 20
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| code | string | いいえ | 単価コード（部分一致） | UP001 |
| region | string | いいえ | 地域コード | TOKYO |
| dormitoryId | string | いいえ | 寮ID | D001 |
| usageTypeCode | string | いいえ | 利用形態コード | NORMAL |
| page | integer | いいえ | ページ番号（0 始まり） | 0 |
| size | integer | いいえ | 1 ページ件数 | 20 |

---

### レスポンスパラメータ
```json
{
  "content": [
    {
      "unitPriceId": "UP202606270001",
      "code": "UP001",
      "region": "TOKYO",
      "regionName": "東京",
      "dormitoryId": "D001",
      "dormitoryName": "東京寮A",
      "roomId": "R001",
      "roomName": "101",
      "usageTypeCode": "NORMAL",
      "usageTypeName": "通常利用",
      "dailyUnitPrice": 3500.00
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| content | array | はい | 単価一覧 | - |
| content.unitPriceId | string | はい | 単価ID | UP202606270001 |
| content.code | string | はい | 単価コード | UP001 |
| content.region | string | はい | 地域コード | TOKYO |
| content.regionName | string | いいえ | 地域名称 | 東京 |
| content.dormitoryId | string | いいえ | 寮ID | D001 |
| content.dormitoryName | string | いいえ | 寮名称 | 東京寮A |
| content.roomId | string | いいえ | 部屋ID | R001 |
| content.roomName | string | いいえ | 部屋名称 | 101 |
| content.usageTypeCode | string | はい | 利用形態コード | NORMAL |
| content.usageTypeName | string | いいえ | 利用形態名称 | 通常利用 |
| content.dailyUnitPrice | number | はい | 日単価 | 3500.00 |
| totalElements | integer | はい | 総件数 | 1 |
| totalPages | integer | はい | 総ページ数 | 1 |

---

## 単価マスタ登録

**インターフェース名称：** 単価マスタ登録
**機能説明：** 単価マスタを新規登録する
**インターフェースURL：** `/unit-prices`
**リクエスト方式：** POST

---

### 機能説明
地域・利用形態のマスタ参照整合を検証したうえで単価を登録する。単価コードはサーバー側で自動採番（`UC` プレフィックス）する。寮・部屋は任意。部屋を指定する場合は寮も必須。寮の地域と選択地域が一致することを確認する。

---

### リクエストパラメータ
```json
{
  "region": "TOKYO",
  "dormitoryId": "D001",
  "roomId": "R001",
  "usageTypeCode": "NORMAL",
  "dailyUnitPrice": 3500.00
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| region | string | はい | 地域コード（地域マスタ） | TOKYO |
| dormitoryId | string | いいえ | 寮ID（寮マスタ） | D001 |
| roomId | string | いいえ | 部屋ID（部屋マスタ・寮に属する。寮未指定時は不可） | R001 |
| usageTypeCode | string | はい | 利用形態コード（利用形態マスタ） | NORMAL |
| dailyUnitPrice | number | はい | 日単価（0以上） | 3500.00 |

---

### レスポンスパラメータ
登録された単価エンティティ（`unitPriceId`, `code`, `region`, `dormitoryId`, `roomId`, `usageTypeCode`, `dailyUnitPrice`, `createdAt`, `updatedAt`）。

---

## 単価マスタ更新

**インターフェース名称：** 単価マスタ更新
**機能説明：** 単価マスタを更新する
**インターフェースURL：** `/unit-prices/{id}`
**リクエスト方式：** PUT

---

### 機能説明
登録 API と同一の検証・パラメータ構造で更新する。単価コードはリクエストに含めず、既存値を維持する。

---

### リクエストパラメータ
登録 API と同一。

---

### レスポンスパラメータ
更新後の単価エンティティ。

---

## 単価マスタ削除

**インターフェース名称：** 単価マスタ削除
**機能説明：** 単価マスタを論理削除する
**インターフェースURL：** `/unit-prices/{id}`
**リクエスト方式：** DELETE

---

### 機能説明
指定 ID の単価を論理削除する。管理者のみ実行可能。

---

### リクエストパラメータ
パスパラメータ `id`（単価ID）のみ。

---

### レスポンスパラメータ
レスポンスボディなし（HTTP 200）。
