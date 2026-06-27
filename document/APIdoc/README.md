# 寮管理システム API ドキュメント

フロントエンドソース（`dom-dev/src/api/`）および各 View の呼び出し実装に基づく API 仕様書。

- **ベース URL:** `import.meta.env.VITE_API_BASE_URL` または `/api/v1`
- **認証:** `Authorization: Bearer {token}`（`utils/request.js` インターセプタで付与）
- **Content-Type:** `application/json`（Excel 取込を除く）
- **日付形式:** `YYYY-MM-DD` / 年月 `YYYY-MM`（ISO 8601 準拠）
- **ページング:** クエリ `page`（0 始まり）・`size`。UI 側は 1 始まりページを `page - 1` に変換して送信

## レスポンス形式

Axios インターセプタが `response.data` をそのまま返却する。一覧系は次のいずれか：

```json
{
  "content": [],
  "totalElements": 0,
  "totalPages": 0
}
```

または配列 `[...]`（`utils/pagination.js` の `normalizePageResponse` が正規化）。

## エラー応答

HTTP 4xx/5xx 時、`error.response.data` から `detail` / `title` / `message` を表示（`utils/request.js`）。

| HTTP | フロント挙動 |
|------|-------------|
| 401 | ログアウト → ログイン画面へ |
| 403 | 「権限がありません」表示 |
| その他 | メッセージを ElMessage で表示 |

## 呼び出しチェーン（全体）

```
views/{domain}/*.vue
 ↓
api/{domain}.js
 ↓
utils/request.js（axios + Bearer）
 ↓
Backend /api/v1/*
```

## API 一覧

| ドメイン | ファイル | エンドポイント数 |
|---------|---------|----------------|
| 認証 | [auth.md](./auth.md) | 1 |
| 社員 | [employee.md](./employee.md) | 1 |
| 寮・部屋 | [dormitory.md](./dormitory.md) | 9 |
| 郵便番号 | [postalCode.md](./postalCode.md) | 1 |
| 入退寮 | [residence.md](./residence.md) | 7 |
| 寮費 | [dormFee.md](./dormFee.md) | 4 |
| 備品 | [equipment.md](./equipment.md) | 6 |
| 空き室 | [vacancy.md](./vacancy.md) | 2 |
| Excel 取込 | [import.md](./import.md) | 2 |
| 操作ログ | [operationLog.md](./operationLog.md) | 1 |

**合計: 34 エンドポイント**

## ソース対応表

| api ファイル | 呼び出し元 View |
|-------------|----------------|
| `api/auth.js` | `views/login/Login.vue` |
| `api/employee.js` | `views/residence/ResidenceRegister.vue` |
| `api/dormitory.js` | `views/dormitory/DormitoryList.vue`, `DormitoryDetail.vue`, `RoomEdit.vue`, `views/residence/ResidenceRegister.vue` |
| `api/postalCode.js` | `views/dormitory/DormitoryList.vue` |
| `api/residence.js` | `views/residence/ResidenceHistoryList.vue`, `ResidenceRegister.vue`, `FirstUseLongTerm.vue` |
| `api/dormFee.js` | `views/dormFee/DormFeeList.vue` |
| `api/equipment.js` | `views/equipment/EquipmentMasterList.vue`, `MoveOutEquipment.vue`, `EquipmentStorage.vue` |
| `api/vacancy.js` | `views/vacancy/VacancyList.vue`, `views/residence/ResidenceRegister.vue` |
| `api/import.js` | `views/import/ExcelImport.vue` |
| `api/operationLog.js` | `views/log/OperationLogList.vue` |
