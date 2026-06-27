# 寮・部屋 API

> 呼び出し元: `views/dormitory/*`, `views/residence/ResidenceRegister.vue` → `api/dormitory.js`

---

## 寮一覧取得

**インターフェース名称：** 寮一覧取得  
**機能説明：** 検索条件に基づき寮一覧をページング取得する  
**インターフェースURL：** `/api/v1/dormitories`  
**リクエスト方式：** GET

---

### 機能説明

寮一覧画面（SC-02）の検索・ページング表示に使用。入居登録画面でも全寮取得（size=1000）に利用。

---

### リクエストパラメータ

```json
{
  "name": "第一寮",
  "genderType": "MALE",
  "address": "東京都",
  "page": 0,
  "size": 20
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| name | string | いいえ | 寮名称（部分一致） | 第一寮 |
| genderType | string | いいえ | 種別（`MALE` / `FEMALE`） | MALE |
| address | string | いいえ | 住所（部分一致） | 東京都 |
| page | int | いいえ | ページ番号（0 始まり） | 0 |
| size | int | いいえ | 1 ページあたり件数 | 20 |

---

### レスポンスパラメータ

```json
{
  "content": [
    {
      "dormitoryId": "D001",
      "name": "第一寮",
      "postalCode": "1000001",
      "address": "東京都千代田区...",
      "layoutType": "3DK",
      "genderType": "MALE",
      "nearestStation1": "東京駅",
      "nearestStation2": null,
      "nearestStation3": null,
      "remarks": null
    }
  ],
  "totalElements": 1
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| content | object[] | はい | 寮一覧 | — |
| content[].dormitoryId | string | はい | 寮 ID | D001 |
| content[].name | string | はい | 寮名称 | 第一寮 |
| content[].postalCode | string | はい | 郵便番号（7桁） | 1000001 |
| content[].address | string | はい | 住所 | 東京都千代田区... |
| content[].layoutType | string | はい | 間取り（`3DK` 等） | 3DK |
| content[].genderType | string | はい | 種別（`MALE` / `FEMALE`） | MALE |
| content[].nearestStation1 | string | いいえ | 最寄駅１（最大20文字） | 東京駅 |
| content[].nearestStation2 | string | いいえ | 最寄駅２（最大20文字） | null |
| content[].nearestStation3 | string | いいえ | 最寄駅３（最大20文字） | null |
| content[].remarks | string | いいえ | 備考 | null |
| totalElements | int | いいえ | 総件数 | 1 |

---

## 寮詳細取得

**インターフェース名称：** 寮詳細取得  
**機能説明：** 指定 ID の寮詳細情報を取得する  
**インターフェースURL：** `/api/v1/dormitories/{id}`  
**リクエスト方式：** GET

---

### 機能説明

寮詳細画面（SC-03）で寮の基本情報を表示する。

---

### リクエストパラメータ

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| id | string | はい | パスパラメータ：寮 ID | D001 |

---

### レスポンスパラメータ

```json
{
  "dormitoryId": "D001",
  "name": "第一寮",
  "postalCode": "1000001",
  "address": "東京都千代田区...",
  "layoutType": "3DK",
  "genderType": "MALE",
  "nearestStation1": "東京駅",
  "nearestStation2": null,
  "nearestStation3": null,
  "remarks": null
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| dormitoryId | string | はい | 寮 ID | D001 |
| name | string | はい | 寮名称 | 第一寮 |
| postalCode | string | はい | 郵便番号（7桁） | 1000001 |
| address | string | はい | 住所 | 東京都千代田区... |
| layoutType | string | はい | 間取り | 3DK |
| genderType | string | はい | 種別 | MALE |
| nearestStation1 | string | いいえ | 最寄駅１（最大20文字） | 東京駅 |
| nearestStation2 | string | いいえ | 最寄駅２（最大20文字） | null |
| nearestStation3 | string | いいえ | 最寄駅３（最大20文字） | null |
| remarks | string | いいえ | 備考 | null |

---

## 寮登録

**インターフェース名称：** 寮登録  
**機能説明：** 新規寮を登録する  
**インターフェースURL：** `/api/v1/dormitories`  
**リクエスト方式：** POST

---

### 機能説明

寮一覧画面の新規登録ダイアログから送信。管理者（`ROLE_ADMIN`）のみ操作可能。

---

### リクエストパラメータ

```json
{
  "name": "第一寮",
  "postalCode": "1000001",
  "address": "東京都千代田区...",
  "layoutType": "3DK",
  "genderType": "MALE",
  "nearestStation1": "東京駅",
  "nearestStation2": null,
  "nearestStation3": null,
  "remarks": null
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| name | string | はい | 寮名称 | 第一寮 |
| postalCode | string | はい | 郵便番号（7桁、ハイフンなし） | 1000001 |
| address | string | はい | 住所 | 東京都千代田区... |
| layoutType | string | はい | 間取り（`3DK`/`2DK`/`1K`/`1DK`/`OTHER`） | 3DK |
| genderType | string | はい | 種別（`MALE` / `FEMALE`） | MALE |
| nearestStation1 | string | いいえ | 最寄駅１（最大20文字） | 東京駅 |
| nearestStation2 | string | いいえ | 最寄駅２（最大20文字） | null |
| nearestStation3 | string | いいえ | 最寄駅３（最大20文字） | null |
| remarks | string | いいえ | 備考 | null |

---

### レスポンスパラメータ

フロントは成功メッセージ表示後に一覧を再取得する。レスポンスボディのフィールド参照はなし。

---

## 寮更新

**インターフェース名称：** 寮更新  
**機能説明：** 既存寮の情報を更新する  
**インターフェースURL：** `/api/v1/dormitories/{id}`  
**リクエスト方式：** PUT

---

### リクエストパラメータ

**パス:** `id` — 寮 ID

**ボディ:**

```json
{
  "name": "第一寮",
  "postalCode": "1000001",
  "address": "東京都千代田区...",
  "layoutType": "3DK",
  "genderType": "MALE",
  "nearestStation1": "東京駅",
  "nearestStation2": null,
  "nearestStation3": null,
  "remarks": null
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| name | string | はい | 寮名称 | 第一寮 |
| postalCode | string | はい | 郵便番号（7桁、ハイフンなし） | 1000001 |
| address | string | はい | 住所 | 東京都千代田区... |
| layoutType | string | はい | 間取り | 3DK |
| genderType | string | はい | 種別（編集時 UI では disabled） | MALE |
| nearestStation1 | string | いいえ | 最寄駅１（最大20文字） | 東京駅 |
| nearestStation2 | string | いいえ | 最寄駅２（最大20文字） | null |
| nearestStation3 | string | いいえ | 最寄駅３（最大20文字） | null |
| remarks | string | いいえ | 備考 | null |

---

### レスポンスパラメータ

フロントは成功メッセージ表示後に一覧を再取得する。

---

## 寮削除

**インターフェース名称：** 寮削除  
**機能説明：** 指定寮を論理削除する  
**インターフェースURL：** `/api/v1/dormitories/{id}`  
**リクエスト方式：** DELETE

---

### リクエストパラメータ

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| id | string | はい | パスパラメータ：寮 ID | D001 |

---

### レスポンスパラメータ

フロントは成功メッセージ表示後に一覧を再取得する。一括削除時は複数 ID に対し並列 DELETE を実行。

**エラー**

| code | HTTP | 条件 |
|------|------|------|
| `DORMITORY_IN_USE` | 400 | 当該寮に現在有効入居者が1名以上いる |

---

## 部屋一覧取得

**インターフェース名称：** 部屋一覧取得  
**機能説明：** 指定寮に属する部屋一覧を取得する  
**インターフェースURL：** `/api/v1/dormitories/{dormId}/rooms`  
**リクエスト方式：** GET

---

### 機能説明

寮詳細画面の部屋一覧、部屋編集画面の部屋情報取得に使用。

---

### リクエストパラメータ

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| dormId | string | はい | パスパラメータ：寮 ID | D001 |
| page | int | いいえ | ページ番号（0 始まり） | 0 |
| size | int | いいえ | 1 ページあたり件数 | 1000 |

---

### レスポンスパラメータ

```json
{
  "content": [
    {
      "roomId": "R003",
      "roomName": "301",
      "areaSqm": 12.5,
      "capacity": 1,
      "roomType": "STANDARD",
      "vacancyStatus": "VACANT"
    }
  ],
  "totalElements": 1
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| content | object[] | はい | 部屋一覧 | — |
| content[].roomId | string | はい | 部屋 ID | R003 |
| content[].roomName | string | はい | 部屋名称 | 301 |
| content[].areaSqm | number | はい | 面積（㎡） | 12.5 |
| content[].capacity | int | はい | 定員 | 1 |
| content[].roomType | string | はい | 部屋種別（`STANDARD`/`SMALL`/`OTHER`） | STANDARD |
| content[].vacancyStatus | string | いいえ | 空き状況（`VACANT`/`OCCUPIED`） | VACANT |
| totalElements | int | いいえ | 総件数 | 1 |

---

## 部屋登録

**インターフェース名称：** 部屋登録  
**機能説明：** 指定寮に新規部屋を登録する  
**インターフェースURL：** `/api/v1/rooms`  
**リクエスト方式：** POST

---

### リクエストパラメータ

```json
{
  "dormitoryId": "D001",
  "roomName": "301",
  "areaSqm": 12.5,
  "capacity": 1,
  "roomType": "STANDARD"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| dormitoryId | string | はい | 所属寮 ID | D001 |
| roomName | string | はい | 部屋名称 | 301 |
| areaSqm | number | はい | 面積（㎡） | 12.5 |
| capacity | int | はい | 定員 | 1 |
| roomType | string | はい | 部屋種別 | STANDARD |

---

### レスポンスパラメータ

フロントは成功後に部屋一覧を再取得する。

---

## 部屋更新

**インターフェース名称：** 部屋更新  
**機能説明：** 既存部屋の情報を更新する  
**インターフェースURL：** `/api/v1/rooms/{id}`  
**リクエスト方式：** PUT

---

### リクエストパラメータ

**パス:** `id` — 部屋 ID

**ボディ:**

```json
{
  "dormitoryId": "D001",
  "roomName": "301",
  "areaSqm": 12.5,
  "capacity": 1,
  "roomType": "STANDARD"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| dormitoryId | string | はい | 所属寮 ID | D001 |
| roomName | string | はい | 部屋名称 | 301 |
| areaSqm | number | はい | 面積（㎡） | 12.5 |
| capacity | int | はい | 定員 | 1 |
| roomType | string | はい | 部屋種別 | STANDARD |

---

### レスポンスパラメータ

フロントは成功後に寮詳細画面へ戻る。

---

## 部屋削除

**インターフェース名称：** 部屋削除  
**機能説明：** 指定部屋を論理削除する  
**インターフェースURL：** `/api/v1/rooms/{id}`  
**リクエスト方式：** DELETE

---

### リクエストパラメータ

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|--------------|------|------|------|------|
| id | string | はい | パスパラメータ：部屋 ID | R003 |

---

### レスポンスパラメータ

フロントは成功後に部屋一覧を再取得する。

**エラー**

| code | HTTP | 条件 |
|------|------|------|
| `ROOM_IN_USE` | 400 | 当該部屋に現在有効入居者が1名以上いる |
