# 備品（個体）API

> 呼び出し元: `views/equipment/EquipmentAssetList.vue` → `api/equipmentAsset.js`

---

## 備品一覧取得

**インターフェース名称：** 備品一覧取得  
**機能説明：** 備品（個体）一覧をページング取得する。品目マスタ名称を JOIN 表示する  
**インターフェースURL：** `/api/v1/equipment-assets`  
**リクエスト方式：** GET

---

### 機能説明

品目マスタに紐づく備品個体を一覧表示する。`equipmentId` 指定時は当該品目に限定する。

---

### リクエストパラメータ

```json
{
  "equipmentId": "EQ00001",
  "page": 0,
  "size": 20
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| equipmentId | string | いいえ | 品目 ID 絞込 | EQ00001 |
| page | int | いいえ | ページ番号（0 始まり） | 0 |
| size | int | いいえ | 1 ページあたり件数 | 20 |

---

### レスポンスパラメータ

```json
{
  "content": [
    {
      "equipmentAssetId": "EB00001",
      "equipmentId": "EQ00001",
      "equipmentName": "シングルベッド",
      "purchaseDate": "2023-04-01",
      "purchaseAmount": 45000,
      "purchaseStore": "ニトリ 新宿店",
      "purchaseStoreContact": "03-1234-5678",
      "purchaseStorePostalCode": "1600022",
      "purchaseStoreAddress": "東京都新宿区新宿3-1-1",
      "warrantyExpiryDate": "2026-03-31"
    }
  ],
  "totalElements": 1
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| content | object[] | はい | 備品一覧 | — |
| content[].equipmentAssetId | string | はい | 備品番号 | EB00001 |
| content[].equipmentId | string | はい | 品目 ID | EQ00001 |
| content[].equipmentName | string | はい | 品目名称 | シングルベッド |
| content[].purchaseDate | string | はい | 購入日（YYYY-MM-DD） | 2023-04-01 |
| content[].purchaseAmount | number | はい | 購入金額 | 45000 |
| content[].purchaseStore | string | いいえ | 購入店 | ニトリ 新宿店 |
| content[].purchaseStoreContact | string | いいえ | 購入店連絡先 | 03-1234-5678 |
| content[].purchaseStorePostalCode | string | いいえ | 購入店郵便番号（7桁） | 1600022 |
| content[].purchaseStoreAddress | string | いいえ | 購入店住所 | 東京都新宿区… |
| content[].warrantyExpiryDate | string | いいえ | 保証期限（YYYY-MM-DD） | 2026-03-31 |
| totalElements | int | いいえ | 総件数 | 1 |

---

## 備品登録

**インターフェース名称：** 備品登録  
**機能説明：** 備品（個体）を新規登録する。備品番号はサーバ側で自動採番（プレフィックス `EB`）  
**インターフェースURL：** `/api/v1/equipment-assets`  
**リクエスト方式：** POST

---

### 機能説明

品目マスタの存在を検証後、`equipment_asset` に INSERT する。

---

### リクエストパラメータ

```json
{
  "equipmentId": "EQ00001",
  "purchaseDate": "2023-04-01",
  "purchaseAmount": 45000,
  "purchaseStore": "ニトリ 新宿店",
  "purchaseStoreContact": "03-1234-5678",
  "purchaseStorePostalCode": "1600022",
  "purchaseStoreAddress": "東京都新宿区新宿3-1-1",
  "warrantyExpiryDate": "2026-03-31"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| equipmentId | string | はい | 品目 ID | EQ00001 |
| purchaseDate | string | はい | 購入日（YYYY-MM-DD） | 2023-04-01 |
| purchaseAmount | number | はい | 購入金額（0 以上） | 45000 |
| purchaseStore | string | いいえ | 購入店 | ニトリ 新宿店 |
| purchaseStoreContact | string | いいえ | 購入店連絡先 | 03-1234-5678 |
| purchaseStorePostalCode | string | いいえ | 購入店郵便番号（7桁） | 1600022 |
| purchaseStoreAddress | string | いいえ | 購入店住所 | 東京都新宿区… |
| warrantyExpiryDate | string | いいえ | 保証期限（YYYY-MM-DD） | 2026-03-31 |

---

### レスポンスパラメータ

登録された `EquipmentAsset` エンティティ（`equipmentAssetId` を含む）を返す。

---

## 備品更新

**インターフェース名称：** 備品更新  
**機能説明：** 既存備品（個体）を更新する  
**インターフェースURL：** `/api/v1/equipment-assets/{id}`  
**リクエスト方式：** PUT

---

### リクエストパラメータ

**パス:** `id` — 備品番号

**ボディ:** 備品登録と同一

---

### レスポンスパラメータ

更新後の `EquipmentAsset` エンティティを返す。

| エラーコード | 説明 |
|--------------|------|
| EQUIPMENT_ASSET_NOT_FOUND | 備品が存在しない |
| EQUIPMENT_NOT_FOUND | 品目が存在しない |

---

## 備品削除

**インターフェース名称：** 備品削除  
**機能説明：** 備品（個体）を論理削除する  
**インターフェースURL：** `/api/v1/equipment-assets/{id}`  
**リクエスト方式：** DELETE

---

### リクエストパラメータ

**パス:** `id` — 備品番号

---

### レスポンスパラメータ

成功時はボディなし（HTTP 200）。

| エラーコード | 説明 |
|--------------|------|
| EQUIPMENT_ASSET_NOT_FOUND | 備品が存在しない |
