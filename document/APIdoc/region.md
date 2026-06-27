## 地域マスタ一覧

**インターフェース名称：** 地域マスタ一覧
**機能説明：** 地域マスタの一覧をページング取得する
**インターフェースURL：** `/regions`
**リクエスト方式：** GET

---

### 機能説明
地域名称による部分一致検索を行い、表示順・地域コード昇順で一覧を返却する。各画面の地域コンボボックスは本 API から取得したデータを表示する。

---

### リクエストパラメータ
```json
{
  "name": "東京",
  "page": 0,
  "size": 20
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| name | string | いいえ | 地域名称（部分一致） | 東京 |
| page | integer | いいえ | ページ番号（0 始まり） | 0 |
| size | integer | いいえ | 1 ページ件数 | 20 |

---

### レスポンスパラメータ
```json
{
  "content": [
    {
      "regionId": "RG202606270001",
      "code": "TOKYO",
      "name": "東京",
      "displayOrder": 1,
      "createdAt": "2026-06-27T10:00:00",
      "updatedAt": "2026-06-27T10:00:00"
    }
  ],
  "totalElements": 4,
  "totalPages": 1
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| content | array | はい | 地域一覧 | - |
| content.regionId | string | はい | 地域 ID | RG202606270001 |
| content.code | string | はい | 地域コード（寮 region 列と連携） | TOKYO |
| content.name | string | はい | 地域名称 | 東京 |
| content.displayOrder | integer | いいえ | 表示順 | 1 |
| content.createdAt | string | はい | 作成日時 | 2026-06-27T10:00:00 |
| content.updatedAt | string | はい | 更新日時 | 2026-06-27T10:00:00 |
| totalElements | integer | はい | 総件数 | 4 |
| totalPages | integer | はい | 総ページ数 | 1 |

---

## 地域マスタ登録

**インターフェース名称：** 地域マスタ登録
**機能説明：** 地域マスタを新規登録する
**インターフェースURL：** `/regions`
**リクエスト方式：** POST

---

### 機能説明
地域コードの重複をチェックし、地域マスタを登録する。管理者のみ実行可能。

---

### リクエストパラメータ
```json
{
  "code": "TOKYO",
  "name": "東京",
  "displayOrder": 1
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| code | string | はい | 地域コード（最大 30 文字） | TOKYO |
| name | string | はい | 地域名称（最大 100 文字） | 東京 |
| displayOrder | integer | はい | 表示順 | 1 |

---

### レスポンスパラメータ
```json
{
  "regionId": "RG202606270001",
  "code": "TOKYO",
  "name": "東京",
  "displayOrder": 1,
  "createdAt": "2026-06-27T10:00:00",
  "updatedAt": "2026-06-27T10:00:00"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| regionId | string | はい | 地域 ID | RG202606270001 |
| code | string | はい | 地域コード | TOKYO |
| name | string | はい | 地域名称 | 東京 |
| displayOrder | integer | いいえ | 表示順 | 1 |
| createdAt | string | はい | 作成日時 | 2026-06-27T10:00:00 |
| updatedAt | string | はい | 更新日時 | 2026-06-27T10:00:00 |

---

## 地域マスタ更新

**インターフェース名称：** 地域マスタ更新
**機能説明：** 地域マスタを更新する
**インターフェースURL：** `/regions/{id}`
**リクエスト方式：** PUT

---

### 機能説明
指定 ID の地域マスタを更新する。地域コード重複チェックを行う。管理者のみ実行可能。

---

### リクエストパラメータ
```json
{
  "code": "TOKYO",
  "name": "東京",
  "displayOrder": 1
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| code | string | はい | 地域コード | TOKYO |
| name | string | はい | 地域名称 | 東京 |
| displayOrder | integer | はい | 表示順 | 1 |

---

### レスポンスパラメータ
登録 API と同一構造。

---

## 地域マスタ削除

**インターフェース名称：** 地域マスタ削除
**機能説明：** 地域マスタを論理削除する
**インターフェースURL：** `/regions/{id}`
**リクエスト方式：** DELETE

---

### 機能説明
指定 ID の地域を論理削除する。寮マスタで使用中の地域コードは削除不可（`REGION_IN_USE`）。管理者のみ実行可能。

---

### リクエストパラメータ
パスパラメータ `id`（地域 ID）のみ。

---

### レスポンスパラメータ
レスポンスボディなし（HTTP 200）。
