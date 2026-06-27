# 郵便番号 API

> 呼び出し元: `views/dormitory/DormitoryList.vue` → `api/postalCode.js`

---

## 郵便番号住所検索

**インターフェース名称：** 郵便番号住所検索  
**機能説明：** 郵便番号（7桁）から都道府県・市区町村・町域を取得し、住所の自動入力に利用する  
**インターフェースURL：** `/api/v1/postal-codes/{postalCode}/address`  
**リクエスト方式：** GET

---

### 機能説明

寮新規登録・編集ダイアログで郵便番号入力後に呼び出し、zipcloud API 経由で住所を取得する。  
取得した `address`（都道府県＋市区町村＋町域）をフロントの住所欄へ自動反映する。

---

### リクエストパラメータ

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| postalCode | string | はい | パスパラメータ：郵便番号（7桁、ハイフン可） | 1000001 |

---

### レスポンスパラメータ

```json
{
  "postalCode": "1000001",
  "prefecture": "東京都",
  "city": "千代田区",
  "town": "千代田",
  "address": "東京都千代田区千代田"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| postalCode | string | はい | 正規化後の郵便番号（7桁） | 1000001 |
| prefecture | string | はい | 都道府県 | 東京都 |
| city | string | はい | 市区町村 | 千代田区 |
| town | string | はい | 町域 | 千代田 |
| address | string | はい | 自動入力用の結合住所 | 東京都千代田区千代田 |

---

### エラー例

| HTTP | code | 説明 |
|------|------|------|
| 400 | INVALID_POSTAL_CODE | 郵便番号が7桁数字でない |
| 400 | POSTAL_CODE_NOT_FOUND | 該当住所なし |
| 400 | POSTAL_CODE_LOOKUP_FAILED | 外部 API 呼び出し失敗 |
