# 操作ログ API

> 呼び出し元: `views/log/OperationLogList.vue` → `api/operationLog.js`

---

## 操作ログ一覧取得

**インターフェース名称：** 操作ログ一覧取得  
**機能説明：** 検索条件に基づき操作ログをページング取得する  
**インターフェースURL：** `/api/v1/operation-logs`  
**リクエスト方式：** GET

---

### 機能説明

操作ログ一覧画面（SC-14）でシステム操作履歴を検索・表示する。管理者（`ROLE_ADMIN`）のみアクセス可能。

---

### リクエストパラメータ

```json
{
  "operationType": "CREATE",
  "operatedBy": "admin",
  "operatedAtFrom": "2026-06-01",
  "operatedAtTo": "2026-06-30",
  "page": 0,
  "size": 20
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| operationType | string | いいえ | 操作種別 | CREATE |
| operatedBy | string | いいえ | 操作者 | admin |
| operatedAtFrom | string | いいえ | 操作日（From）`YYYY-MM-DD` | 2026-06-01 |
| operatedAtTo | string | いいえ | 操作日（To）`YYYY-MM-DD` | 2026-06-30 |
| page | int | いいえ | ページ番号（0 始まり） | 0 |
| size | int | いいえ | 1 ページあたり件数 | 20 |

---

### レスポンスパラメータ

```json
{
  "content": [
    {
      "logId": "LOG001",
      "operationType": "CREATE",
      "targetTable": "dormitory",
      "targetId": "D001",
      "operatedBy": "admin",
      "operatedAt": "2026-06-01T10:30:00"
    }
  ],
  "totalElements": 1
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| content | object[] | はい | 操作ログ一覧 | — |
| content[].logId | string | はい | ログ ID | LOG001 |
| content[].operationType | string | はい | 操作種別 | CREATE |
| content[].targetTable | string | はい | 対象テーブル | dormitory |
| content[].targetId | string | はい | 対象 ID | D001 |
| content[].operatedBy | string | はい | 操作者 | admin |
| content[].operatedAt | string | はい | 操作日時 | 2026-06-01T10:30:00 |
| totalElements | int | いいえ | 総件数 | 1 |
