# 5. API 共通仕様

> 親文書: [README.md](./README.md)  
> 機能別 API 一覧は各モジュール設計書を参照。

## 5.1 共通仕様

| 項目 | 仕様 |
|------|------|
| プロトコル | HTTPS, REST, JSON |
| 認証 | `Authorization: Bearer {token}`（暫定） |
| 日付形式 | ISO 8601 `YYYY-MM-DD` |
| ページング | `?page=0&size=20`（一覧系） |
| エラー | RFC 7807 風 `application/problem+json` |
| 論理削除 | DELETE は `deleted_at` 設定。GET 一覧は `deleted_at IS NULL` 既定 |

## 5.2 エラー応答

業務エラーコードの一覧は [15_エラー・例外設計.md](./15_エラー・例外設計.md) を参照。
