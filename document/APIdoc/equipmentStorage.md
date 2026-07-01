## 備品保管一覧

**インターフェース名称：** 備品保管一覧
**機能説明：** 備品保管明細をページング取得する
**インターフェースURL：** `/equipment-storages`
**リクエスト方式：** GET

---

### 機能説明
備品（個体）番号で絞り込み可能。1備品が複数保管場所に分割されている場合は行ごとに返却する。

---

### リクエストパラメータ
```json
{
  "equipmentAssetId": "EB202606290001",
  "page": 0,
  "size": 20
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| equipmentAssetId | string | いいえ | 備品番号 | EB202606290001 |
| page | integer | いいえ | ページ番号（0 始まり） | 0 |
| size | integer | いいえ | 1 ページ件数 | 20 |

---

### レスポンスパラメータ
```json
{
  "content": [
    {
      "storageId": "ST00001",
      "equipmentAssetId": "EB00002",
      "equipmentName": "デスク",
      "purchaseQuantity": 2,
      "storageLocationId": "SL202606300003",
      "storageLocationName": "本社倉庫1階",
      "storageQuantity": 1,
      "status": "IN_STORAGE",
      "linkedMoveoutId": "MO00002"
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| content | array | はい | 保管一覧 | - |
| content.storageId | string | はい | 保管 ID | ST00001 |
| content.equipmentAssetId | string | はい | 備品番号 | EB00002 |
| content.equipmentName | string | はい | 品目名称 | デスク |
| content.purchaseQuantity | integer | はい | 購入数量 | 2 |
| content.storageLocationId | string | はい | 保管場所 ID | SL202606300003 |
| content.storageLocationName | string | はい | 保管場所名 | 本社倉庫1階 |
| content.storageQuantity | integer | はい | 保管数量 | 1 |
| content.status | string | はい | ステータス | IN_STORAGE |
| content.linkedMoveoutId | string | いいえ | 関連退去処理 ID | MO00002 |
| totalElements | integer | はい | 総件数 | 1 |
| totalPages | integer | はい | 総ページ数 | 1 |

---

## 備品別保管明細取得

**インターフェース名称：** 備品別保管明細取得
**機能説明：** 指定備品の保管明細をすべて取得する（編集ダイアログ用）
**インターフェースURL：** `/equipment-storages/by-asset/{equipmentAssetId}`
**リクエスト方式：** GET

---

### 機能説明
指定した備品（個体）に紐づく保管明細を返却する。

---

### リクエストパラメータ
```json
{}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| equipmentAssetId | string | はい | 備品番号（パス） | EB00002 |

---

### レスポンスパラメータ
```json
[
  {
    "storageId": "ST00001",
    "equipmentAssetId": "EB00002",
    "equipmentName": "デスク",
    "purchaseQuantity": 2,
    "storageLocationId": "SL202606300003",
    "storageLocationName": "本社倉庫1階",
    "storageQuantity": 1,
    "status": "IN_STORAGE",
    "linkedMoveoutId": "MO00002"
  }
]
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| body | array | はい | 保管明細一覧 | - |
| body.storageId | string | はい | 保管 ID | ST00001 |
| body.equipmentAssetId | string | はい | 備品番号 | EB00002 |
| body.equipmentName | string | はい | 品目名称 | デスク |
| body.purchaseQuantity | integer | はい | 購入数量 | 2 |
| body.storageLocationId | string | はい | 保管場所 ID | SL202606300003 |
| body.storageLocationName | string | はい | 保管場所名 | 本社倉庫1階 |
| body.storageQuantity | integer | はい | 保管数量 | 1 |
| body.status | string | はい | ステータス | IN_STORAGE |
| body.linkedMoveoutId | string | いいえ | 関連退去処理 ID | MO00002 |

---

## 備品保管一括保存

**インターフェース名称：** 備品保管一括保存
**機能説明：** 備品ごとの保管明細を一括保存する
**インターフェースURL：** `/equipment-storages/by-asset/{equipmentAssetId}`
**リクエスト方式：** PUT

---

### 機能説明
既存の保管明細を削除し、リクエストの明細で置換する。保管数量の合計が備品の購入数量と一致することを検証する。

---

### リクエストパラメータ
```json
{
  "lines": [
    {
      "storageLocationId": "SL202606300003",
      "storageQuantity": 1,
      "status": "IN_STORAGE",
      "linkedMoveoutId": "MO00002"
    },
    {
      "storageLocationId": "SL202606300004",
      "storageQuantity": 1,
      "status": "IN_STORAGE"
    }
  ]
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| equipmentAssetId | string | はい | 備品番号（パス） | EB00002 |
| lines | array | はい | 保管明細 | - |
| lines.storageLocationId | string | はい | 保管場所 ID | SL202606300003 |
| lines.storageQuantity | integer | はい | 保管数量（1 以上） | 1 |
| lines.status | string | いいえ | ステータス | IN_STORAGE |
| lines.linkedMoveoutId | string | いいえ | 関連退去処理 ID | MO00002 |

---

### レスポンスパラメータ
```json
null
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| body | null | はい | 成功時は空レスポンス | null |

---

## 備品保管削除

**インターフェース名称：** 備品保管削除
**機能説明：** 保管明細を1件削除する
**インターフェースURL：** `/equipment-storages/{storageId}`
**リクエスト方式：** DELETE

---

### 機能説明
指定 ID の保管明細を削除する。

---

### リクエストパラメータ
```json
{}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| storageId | string | はい | 保管 ID（パス） | ST00001 |

---

### レスポンスパラメータ
```json
null
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| body | null | はい | 成功時は空レスポンス | null |
