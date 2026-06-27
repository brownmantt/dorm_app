# 備品 API

> 呼び出し元: `views/equipment/*` → `api/equipment.js`

---

## 備品一覧取得

**インターフェース名称：** 備品一覧取得  
**機能説明：** 備品マスタ一覧をページング取得する  
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
      "name": "エアコン",
      "equipmentType": "家電"
    }
  ],
  "totalElements": 1
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| content | object[] | はい | 備品一覧 | — |
| content[].equipmentId | string | はい | 備品 ID | EQ001 |
| content[].name | string | はい | 名称 | エアコン |
| content[].equipmentType | string | はい | 種別 | 家電 |
| totalElements | int | いいえ | 総件数 | 1 |

---

## 備品登録

**インターフェース名称：** 備品登録  
**機能説明：** 備品マスタに新規備品を登録する  
**インターフェースURL：** `/api/v1/equipments`  
**リクエスト方式：** POST

---

### リクエストパラメータ

```json
{
  "name": "エアコン",
  "equipmentType": "家電"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| name | string | はい | 名称 | エアコン |
| equipmentType | string | はい | 種別 | 家電 |

---

### レスポンスパラメータ

フロントは成功後に一覧を再取得する。

---

## 備品更新

**インターフェース名称：** 備品更新  
**機能説明：** 既存備品マスタを更新する  
**インターフェースURL：** `/api/v1/equipments/{id}`  
**リクエスト方式：** PUT

---

### リクエストパラメータ

**パス:** `id` — 備品 ID

**ボディ:**

```json
{
  "name": "エアコン",
  "equipmentType": "家電"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| name | string | はい | 名称 | エアコン |
| equipmentType | string | はい | 種別 | 家電 |

---

### レスポンスパラメータ

フロントは成功後に一覧を再取得する。

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

## 備品保管一覧取得

**インターフェース名称：** 備品保管一覧取得  
**機能説明：** 保管中の備品一覧をページング取得する  
**インターフェースURL：** `/api/v1/equipment-storages`  
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
      "storageId": "ST001",
      "equipmentName": "エアコン",
      "storageLocation": "倉庫A-1",
      "status": "IN_STORAGE"
    }
  ],
  "totalElements": 1
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| content | object[] | はい | 保管一覧 | — |
| content[].storageId | string | はい | 保管 ID | ST001 |
| content[].equipmentName | string | はい | 備品名称 | エアコン |
| content[].storageLocation | string | はい | 保管場所 | 倉庫A-1 |
| content[].status | string | はい | ステータス（`IN_STORAGE`/`REUSED`） | IN_STORAGE |
| totalElements | int | いいえ | 総件数 | 1 |

---

## 備品保管登録

**インターフェース名称：** 備品保管登録  
**機能説明：** 備品の保管情報を新規登録する  
**インターフェースURL：** `/api/v1/equipment-storages`  
**リクエスト方式：** POST

---

### リクエストパラメータ

```json
{
  "equipmentId": "EQ001",
  "storageLocation": "倉庫A-1",
  "linkedMoveoutId": "MO001"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| equipmentId | string | はい | 備品 ID | EQ001 |
| storageLocation | string | はい | 保管場所 | 倉庫A-1 |
| linkedMoveoutId | string | いいえ | 関連退去処理 ID | MO001 |

---

### レスポンスパラメータ

フロントは成功後に一覧を再取得する。
