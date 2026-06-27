## 寮責任者取得

**インターフェース名称：** 寮責任者取得  
**機能説明：** 指定寮の責任者（★表示対象）を取得する（F-15）  
**インターフェースURL：** `/dormitories/{dormId}/manager`  
**リクエスト方式：** GET

---

### 機能説明

寮に責任者が未設定の場合、各フィールドは null または空で返却する。

---

### リクエストパラメータ

```json
{}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| dormId | string | はい | 寮ID（パス） | D001 |

---

### レスポンスパラメータ

```json
{
  "employeeId": "E00012",
  "employeeName": "山田太郎",
  "residenceHistoryId": "RH10045"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| employeeId | string | いいえ | 責任者社員ID | E00012 |
| employeeName | string | いいえ | 責任者氏名 | 山田太郎 |
| residenceHistoryId | string | いいえ | 紐づく入居履歴ID | RH10045 |

---

## 寮責任者設定

**インターフェース名称：** 寮責任者設定  
**機能説明：** 指定寮の責任者を設定・更新する  
**インターフェースURL：** `/dormitories/{dormId}/manager`  
**リクエスト方式：** PUT

---

### 機能説明

`residenceHistoryId` が当該寮の現在有効入居であることを検証し、UPSERT する。  
エラー: `MANAGER_NOT_RESIDENT`（現入居者でない場合）

---

### リクエストパラメータ

```json
{
  "employeeId": "E00012",
  "residenceHistoryId": "RH10045"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| dormId | string | はい | 寮ID（パス） | D001 |
| employeeId | string | いいえ | 社員ID（履歴から補完可） | E00012 |
| residenceHistoryId | string | はい | 入居履歴ID | RH10045 |

---

### レスポンスパラメータ

```json
{
  "employeeId": "E00012",
  "employeeName": "山田太郎",
  "residenceHistoryId": "RH10045"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| employeeId | string | はい | 責任者社員ID | E00012 |
| employeeName | string | はい | 責任者氏名 | 山田太郎 |
| residenceHistoryId | string | はい | 入居履歴ID | RH10045 |
