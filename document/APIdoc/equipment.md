# 備品 API

> 呼び出し元: `views/equipment/*` → `api/equipment.js`

---

## 品目一覧取得

**インターフェース名称：** 品目一覧取得  
**機能説明：** 品目マスタ一覧をページング取得する  
**インターフェースURL：** `/api/v1/equipments`  
**リクエスト方式：** GET

---

### リクエストパラメータ

```json
{
  "page": 0,
  "size": 20
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| page | int | いいえ | ページ番号（0 始まり） | 0 |
| size | int | いいえ | 1 ページあたり件数 | 20 |

---

### レスポンスパラメータ

```json
{
  "content": [
    {
      "equipmentId": "EQ001",
      "name": "エアコン"
    }
  ],
  "totalElements": 1
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| content | object[] | はい | 品目一覧 | — |
| content[].equipmentId | string | はい | 品目 ID | EQ001 |
| content[].name | string | はい | 品目名称 | エアコン |
| totalElements | int | いいえ | 総件数 | 1 |

---

## 品目登録

**インターフェース名称：** 品目登録  
**機能説明：** 品目マスタに新規品目を登録する  
**インターフェースURL：** `/api/v1/equipments`  
**リクエスト方式：** POST

---

### リクエストパラメータ

```json
{
  "name": "エアコン"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| name | string | はい | 品目名称 | エアコン |

---

### レスポンスパラメータ

フロントは成功後に一覧を再取得する。

---

## 品目更新

**インターフェース名称：** 品目更新  
**機能説明：** 既存品目マスタを更新する  
**インターフェースURL：** `/api/v1/equipments/{id}`  
**リクエスト方式：** PUT

---

### リクエストパラメータ

**パス:** `id` — 品目 ID

**ボディ:**

```json
{
  "name": "エアコン"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| name | string | はい | 品目名称 | エアコン |

---

### レスポンスパラメータ

フロントは成功後に一覧を再取得する。

---

## 品目削除

**インターフェース名称：** 品目削除  
**機能説明：** 品目マスタを論理削除する。退去備品処理・備品保管で参照されている品目は削除できない  
**インターフェースURL：** `/api/v1/equipments/{id}`  
**リクエスト方式：** DELETE

---

### リクエストパラメータ

**パス:** `id` — 品目 ID

---

### レスポンスパラメータ

成功時はボディなし（HTTP 200）。フロントは成功後に一覧を再取得する。

| エラーコード | 説明 |
|--------------|------|
| EQUIPMENT_NOT_FOUND | 品目が存在しない |
| EQUIPMENT_IN_USE | 退去処理または保管で参照中 |

---

## 退去備品処理

**インターフェース名称：** 退去備品処理  
**機能説明：** 退寮時の備品処分（廃棄・保管・再利用）を記録する  
**インターフェースURL：** `/api/v1/equipment-moveouts`  
**リクエスト方式：** POST

---

### リクエストパラメータ

```json
{
  "residenceHistoryId": "RH10045",
  "equipmentId": "EQ001",
  "disposition": "DISCARD",
  "remarks": "老朽化のため廃棄"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| residenceHistoryId | string | はい | 入居履歴 ID | RH10045 |
| equipmentId | string | はい | 備品 ID | EQ001 |
| disposition | string | はい | 処分方法（`DISCARD`/`STORE`/`REUSE`） | DISCARD |
| remarks | string | いいえ | 備考 | 老朽化のため廃棄 |

---

### レスポンスパラメータ

フロントは成功後にフォームをリセットする。

---

## 備品保管 API

備品保管の API 仕様は [equipmentStorage.md](./equipmentStorage.md) を参照。

- 一覧取得 `GET /equipment-storages`
- 備品別明細取得 `GET /equipment-storages/by-asset/{equipmentAssetId}`
- 一括保存 `PUT /equipment-storages/by-asset/{equipmentAssetId}`（保管数量合計 = 購入数量）
- 明細削除 `DELETE /equipment-storages/{storageId}`
