## 社員一覧取得

**インターフェース名称：** 社員一覧取得  
**機能説明：** 社員マスタを条件検索・ページング取得する（F-21 / SC-16）  
**インターフェースURL：** `/employees`  
**リクエスト方式：** GET

---

### 機能説明

社員ID・氏名のキーワード部分一致、性別・入居者区分・所属による絞り込みに対応する。`notResidingOnly=true` の場合、当日時点で入居中の社員を除外し、社員マスタの `updated_at` 降順で返却する。論理削除済みレコードは返却しない。所属名称は LEFT JOIN で補完する。

---

### リクエストパラメータ

```json
{
  "keyword": "佐藤",
  "gender": "FEMALE",
  "employeeCategory": "JAPAN",
  "affiliationId": "AF001",
  "notResidingOnly": true,
  "page": 0,
  "size": 20
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| keyword | string | いいえ | 社員ID / 氏名部分一致 | 佐藤 |
| gender | string | いいえ | 性別 MALE / FEMALE | FEMALE |
| employeeCategory | string | いいえ | 入居者区分 JAPAN / CHINA_ASSIGN | JAPAN |
| affiliationId | string | いいえ | 所属ID | AF001 |
| notResidingOnly | boolean | いいえ | 未入居者のみ（当日時点で有効入居履歴なし） | true |
| page | number | いいえ | ページ番号（0始まり） | 0 |
| size | number | いいえ | ページサイズ | 20 |

---

### レスポンスパラメータ

```json
{
  "content": [
    {
      "employeeId": "E001",
      "name": "佐藤花子",
      "gender": "FEMALE",
      "employeeCategory": "JAPAN",
      "affiliationId": "AF001",
      "affiliationName": "日本社員",
      "businessDivision": "開発",
      "nearestStation1": "渋谷",
      "nearestStation2": null,
      "nearestStation3": null,
      "contactInfo": "{\"mobilePhone\":\"090-0000-0000\",\"email\":\"hanako@example.com\"}"
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| content | array | はい | 社員一覧 | — |
| content.employeeId | string | はい | 社員ID | E001 |
| content.name | string | はい | 氏名 | 佐藤花子 |
| content.gender | string | はい | 性別 | FEMALE |
| content.employeeCategory | string | はい | 入居者区分 | JAPAN |
| content.affiliationId | string | いいえ | 所属ID | AF001 |
| content.affiliationName | string | いいえ | 所属名称 | 日本社員 |
| content.businessDivision | string | いいえ | 事業部 | 開発 |
| content.nearestStation1 | string | いいえ | 最寄駅１（最大20文字） | 渋谷 |
| content.nearestStation2 | string | いいえ | 最寄駅２（最大20文字） | null |
| content.nearestStation3 | string | いいえ | 最寄駅３（最大20文字） | null |
| content.contactInfo | string | いいえ | 連絡先 JSON 文字列 | {"mobilePhone":"..."} |
| totalElements | number | はい | 総件数 | 1 |
| totalPages | number | はい | 総ページ数 | 1 |

---

## 社員詳細取得

**インターフェース名称：** 社員詳細取得  
**機能説明：** 社員IDで社員詳細を取得する  
**インターフェースURL：** `/employees/{empId}`  
**リクエスト方式：** GET

---

### 機能説明

未削除の社員を1件取得する。存在しない場合は `EMPLOYEE_NOT_FOUND`。

---

### リクエストパラメータ

```json
{}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| empId | string | はい | 社員ID（パス） | E001 |

---

### レスポンスパラメータ

```json
{
  "employeeId": "E001",
  "name": "佐藤花子",
  "gender": "FEMALE",
  "employeeCategory": "JAPAN",
  "affiliationId": "AF001",
  "businessDivision": "開発",
  "nearestStation1": "渋谷",
  "nearestStation2": null,
  "nearestStation3": null,
  "contactInfo": "{\"mobilePhone\":\"090-0000-0000\",\"email\":\"hanako@example.com\"}"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| employeeId | string | はい | 社員ID | E001 |
| name | string | はい | 氏名 | 佐藤花子 |
| gender | string | はい | 性別 | FEMALE |
| employeeCategory | string | はい | 入居者区分 | JAPAN |
| affiliationId | string | いいえ | 所属ID | AF001 |
| businessDivision | string | いいえ | 事業部 | 開発 |
| nearestStation1 | string | いいえ | 最寄駅１（最大20文字） | 渋谷 |
| nearestStation2 | string | いいえ | 最寄駅２（最大20文字） | null |
| nearestStation3 | string | いいえ | 最寄駅３（最大20文字） | null |
| contactInfo | string | いいえ | 連絡先 JSON 文字列 | {"mobilePhone":"..."} |

---

## 社員登録

**インターフェース名称：** 社員登録  
**機能説明：** 社員マスタを新規登録する  
**インターフェースURL：** `/employees`  
**リクエスト方式：** POST

---

### 機能説明

社員IDの一意性・所属の存在・性別・入居者区分を検証し、社員を登録する。連絡先は JSONB に格納する。

---

### リクエストパラメータ

```json
{
  "employeeId": "E001",
  "name": "佐藤花子",
  "gender": "FEMALE",
  "employeeCategory": "JAPAN",
  "affiliationId": "AF001",
  "businessDivision": "開発",
  "nearestStation1": "渋谷",
  "nearestStation2": null,
  "nearestStation3": null,
  "mobilePhone": "090-0000-0000",
  "email": "hanako@example.com"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| employeeId | string | はい | 社員ID（20文字以内） | E001 |
| name | string | はい | 氏名 | 佐藤花子 |
| gender | string | はい | 性別 MALE / FEMALE | FEMALE |
| employeeCategory | string | はい | 入居者区分 | JAPAN |
| affiliationId | string | いいえ | 所属ID | AF001 |
| businessDivision | string | いいえ | 事業部 | 開発 |
| nearestStation1 | string | いいえ | 最寄駅１（最大20文字） | 渋谷 |
| nearestStation2 | string | いいえ | 最寄駅２（最大20文字） | null |
| nearestStation3 | string | いいえ | 最寄駅３（最大20文字） | null |
| mobilePhone | string | いいえ | 携帯電話 | 090-0000-0000 |
| email | string | いいえ | メール | hanako@example.com |

---

### レスポンスパラメータ

```json
{
  "employeeId": "E001",
  "name": "佐藤花子",
  "gender": "FEMALE",
  "employeeCategory": "JAPAN",
  "affiliationId": "AF001",
  "businessDivision": "開発",
  "nearestStation1": "渋谷",
  "nearestStation2": null,
  "nearestStation3": null,
  "contactInfo": "{\"mobilePhone\":\"090-0000-0000\",\"email\":\"hanako@example.com\"}"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| employeeId | string | はい | 社員ID | E001 |
| name | string | はい | 氏名 | 佐藤花子 |
| gender | string | はい | 性別 | FEMALE |
| employeeCategory | string | はい | 入居者区分 | JAPAN |
| affiliationId | string | いいえ | 所属ID | AF001 |
| businessDivision | string | いいえ | 事業部 | 開発 |
| nearestStation1 | string | いいえ | 最寄駅１（最大20文字） | 渋谷 |
| nearestStation2 | string | いいえ | 最寄駅２（最大20文字） | null |
| nearestStation3 | string | いいえ | 最寄駅３（最大20文字） | null |
| contactInfo | string | いいえ | 連絡先 JSON 文字列 | {"mobilePhone":"..."} |

---

## 社員更新

**インターフェース名称：** 社員更新  
**機能説明：** 社員マスタを更新する  
**インターフェースURL：** `/employees/{empId}`  
**リクエスト方式：** PUT

---

### 機能説明

社員IDは変更不可。所属・連絡先を含む各項目を更新する。

---

### リクエストパラメータ

```json
{
  "name": "佐藤花子",
  "gender": "FEMALE",
  "employeeCategory": "JAPAN",
  "affiliationId": "AF001",
  "businessDivision": "開発",
  "nearestStation1": "渋谷",
  "nearestStation2": null,
  "nearestStation3": null,
  "mobilePhone": "090-0000-0000",
  "email": "hanako@example.com"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| empId | string | はい | 社員ID（パス） | E001 |
| name | string | はい | 氏名 | 佐藤花子 |
| gender | string | はい | 性別 | FEMALE |
| employeeCategory | string | はい | 入居者区分 | JAPAN |
| affiliationId | string | いいえ | 所属ID | AF001 |
| businessDivision | string | いいえ | 事業部 | 開発 |
| nearestStation1 | string | いいえ | 最寄駅１（最大20文字） | 渋谷 |
| nearestStation2 | string | いいえ | 最寄駅２（最大20文字） | null |
| nearestStation3 | string | いいえ | 最寄駅３（最大20文字） | null |
| mobilePhone | string | いいえ | 携帯電話 | 090-0000-0000 |
| email | string | いいえ | メール | hanako@example.com |

---

### レスポンスパラメータ

社員登録と同一構造。

---

## 社員削除

**インターフェース名称：** 社員削除  
**機能説明：** 社員マスタを論理削除する  
**インターフェースURL：** `/employees/{empId}`  
**リクエスト方式：** DELETE

---

### 機能説明

入居履歴が存在する社員は削除不可（`EMPLOYEE_IN_USE`）。論理削除のみ行う。

---

### リクエストパラメータ

```json
{}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| empId | string | はい | 社員ID（パス） | E001 |

---

### レスポンスパラメータ

```json
null
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| — | — | — | 204 相当（ボディなし） | — |
