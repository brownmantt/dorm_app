## 所属一覧取得

**インターフェース名称：** 所属一覧取得  
**機能説明：** 所属マスタをページング取得する（F-20）  
**インターフェースURL：** `/affiliations`  
**リクエスト方式：** GET

---

### 機能説明

所属名称の部分一致検索とページングに対応する。論理削除済みレコードは返却しない。

---

### リクエストパラメータ

```json
{
  "name": "瀋陽",
  "page": 0,
  "size": 20
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| name | string | いいえ | 所属名称部分一致 | 瀋陽 |
| page | number | いいえ | ページ番号（0始まり） | 0 |
| size | number | いいえ | ページサイズ | 20 |

---

### レスポンスパラメータ

```json
{
  "content": [
    {
      "affiliationId": "AF202606070001",
      "code": "SY",
      "name": "瀋陽",
      "displayOrder": 1
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| content | array | はい | 所属一覧 | — |
| content.affiliationId | string | はい | 所属ID | AF202606070001 |
| content.code | string | はい | 所属コード | SY |
| content.name | string | はい | 所属名称 | 瀋陽 |
| content.displayOrder | number | いいえ | 表示順 | 1 |
| totalElements | number | はい | 総件数 | 1 |
| totalPages | number | はい | 総ページ数 | 1 |

---

## 所属登録

**インターフェース名称：** 所属登録  
**機能説明：** 所属マスタを新規登録する  
**インターフェースURL：** `/affiliations`  
**リクエスト方式：** POST

---

### 機能説明

所属コードの一意性を検証し、新規所属を登録する。

---

### リクエストパラメータ

```json
{
  "code": "SY",
  "name": "瀋陽",
  "displayOrder": 1
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| code | string | はい | 所属コード（一意） | SY |
| name | string | はい | 所属名称 | 瀋陽 |
| displayOrder | number | いいえ | 表示順 | 1 |

---

### レスポンスパラメータ

```json
{
  "affiliationId": "AF202606070001",
  "code": "SY",
  "name": "瀋陽",
  "displayOrder": 1
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| affiliationId | string | はい | 所属ID | AF202606070001 |
| code | string | はい | 所属コード | SY |
| name | string | はい | 所属名称 | 瀋陽 |
| displayOrder | number | いいえ | 表示順 | 1 |
