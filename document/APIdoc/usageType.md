## 利用形態マスタ一覧

**インターフェース名称：** 利用形態マスタ一覧
**機能説明：** 利用形態マスタの一覧をページング取得する
**インターフェースURL：** `/usage-types`
**リクエスト方式：** GET

---

### 機能説明
名称による部分一致検索を行い、表示順・コード値昇順で一覧を返却する。

---

### リクエストパラメータ
```json
{
  "name": "通常",
  "page": 0,
  "size": 20
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| name | string | いいえ | 名称（部分一致） | 通常 |
| page | integer | いいえ | ページ番号（0 始まり） | 0 |
| size | integer | いいえ | 1 ページ件数 | 20 |

---

### レスポンスパラメータ
```json
{
  "content": [
    {
      "usageTypeId": "UT202606270001",
      "code": "NORMAL",
      "name": "通常利用",
      "displayOrder": 1,
      "minUsageDays": 1,
      "maxUsageDays": -1,
      "createdAt": "2026-06-27T10:00:00",
      "updatedAt": "2026-06-27T10:00:00"
    }
  ],
  "totalElements": 3,
  "totalPages": 1
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| content | array | はい | 利用形態一覧 | - |
| content.usageTypeId | string | はい | 利用形態 ID | UT202606270001 |
| content.code | string | はい | コード値 | NORMAL |
| content.name | string | はい | 名称 | 通常利用 |
| content.displayOrder | integer | いいえ | 表示順 | 1 |
| content.minUsageDays | integer | はい | 最小利用日数 | 1 |
| content.maxUsageDays | integer | はい | 最大利用日数（-1 は制限なし） | -1 |
| content.createdAt | string | はい | 作成日時 | 2026-06-27T10:00:00 |
| content.updatedAt | string | はい | 更新日時 | 2026-06-27T10:00:00 |
| totalElements | integer | はい | 総件数 | 3 |
| totalPages | integer | はい | 総ページ数 | 1 |

---

## 利用形態マスタ登録

**インターフェース名称：** 利用形態マスタ登録
**機能説明：** 利用形態マスタを新規登録する
**インターフェースURL：** `/usage-types`
**リクエスト方式：** POST

---

### 機能説明
コード値の重複をチェックし、利用形態マスタを登録する。最小利用日数は未指定時 1、最大利用日数は未指定時 -1（制限なし）とする。管理者のみ実行可能。

---

### リクエストパラメータ
```json
{
  "code": "NORMAL",
  "name": "通常利用",
  "displayOrder": 1,
  "minUsageDays": 1,
  "maxUsageDays": 365
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| code | string | はい | コード値（最大 30 文字） | NORMAL |
| name | string | はい | 名称（最大 100 文字） | 通常利用 |
| displayOrder | integer | はい | 表示順 | 1 |
| minUsageDays | integer | いいえ | 最小利用日数（1以上。未指定時は 1） | 1 |
| maxUsageDays | integer | いいえ | 最大利用日数（1以上。未指定時は -1） | 365 |

---

### レスポンスパラメータ
```json
{
  "usageTypeId": "UT202606270001",
  "code": "NORMAL",
  "name": "通常利用",
  "displayOrder": 1,
  "minUsageDays": 1,
  "maxUsageDays": -1,
  "createdAt": "2026-06-27T10:00:00",
  "updatedAt": "2026-06-27T10:00:00"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| usageTypeId | string | はい | 利用形態 ID | UT202606270001 |
| code | string | はい | コード値 | NORMAL |
| name | string | はい | 名称 | 通常利用 |
| displayOrder | integer | いいえ | 表示順 | 1 |
| minUsageDays | integer | いいえ | 最小利用日数 | 1 |
| maxUsageDays | integer | いいえ | 最大利用日数（-1 は制限なし） | -1 |
| createdAt | string | はい | 作成日時 | 2026-06-27T10:00:00 |
| updatedAt | string | はい | 更新日時 | 2026-06-27T10:00:00 |

---

## 利用形態マスタ更新

**インターフェース名称：** 利用形態マスタ更新
**機能説明：** 利用形態マスタを更新する
**インターフェースURL：** `/usage-types/{id}`
**リクエスト方式：** PUT

---

### 機能説明
指定 ID の利用形態マスタを更新する。コード値重複チェックを行う。管理者のみ実行可能。

---

### リクエストパラメータ
```json
{
  "code": "NORMAL",
  "name": "通常利用",
  "displayOrder": 1,
  "minUsageDays": 1,
  "maxUsageDays": 365
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| code | string | はい | コード値 | NORMAL |
| name | string | はい | 名称 | 通常利用 |
| displayOrder | integer | はい | 表示順 | 1 |
| minUsageDays | integer | いいえ | 最小利用日数（1以上。未指定時は 1） | 1 |
| maxUsageDays | integer | いいえ | 最大利用日数（1以上。未指定時は -1） | 365 |

---

### レスポンスパラメータ
登録 API と同一構造。

---

## 利用形態マスタ削除

**インターフェース名称：** 利用形態マスタ削除
**機能説明：** 利用形態マスタを論理削除する
**インターフェースURL：** `/usage-types/{id}`
**リクエスト方式：** DELETE

---

### 機能説明
指定 ID の利用形態を論理削除する。管理者のみ実行可能。

---

### リクエストパラメータ
パスパラメータ `id`（利用形態 ID）のみ。

---

### レスポンスパラメータ
レスポンスボディなし（HTTP 200）。
