## 寮割カレンダー取得

**インターフェース名称：** 寮割カレンダー取得  
**機能説明：** 対象年月の寮割カレンダーデータを取得する（F-14）  
**インターフェースURL：** `/dorm-allocation`  
**リクエスト方式：** GET

---

### 機能説明

指定した `yearMonth` の月初〜月末を基準に、寮単位ブロック・入居者行・日別在籍（黄色セル相当）・部屋重複（赤背景相当）・退寮警告フラグを組み立てて返却する。  
部屋ごとに定員（`capacity`、未設定時 1）分の行を返す。入居者がいる行は入居者情報・カレンダーを表示し、空きスロット行は `vacant=true` とし入居者情報・カレンダーは空。  
地域フィルタ（`regions`・単一地域コード）、寮名称（`dormitoryId`）、種別（`genderType`）、入居者氏名部分一致（`name`）に対応する。

---

### リクエストパラメータ

```json
{
  "yearMonth": "2026-04",
  "regions": "TOKYO",
  "dormitoryId": "D001",
  "genderType": "FEMALE",
  "name": "田中",
  "sort": "dormitoryName,asc"
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| yearMonth | string | はい | 対象年月（YYYY-MM） | 2026-04 |
| regions | string | いいえ | 地域コード（単一指定） | TOKYO |
| dormitoryId | string | いいえ | 寮ID（寮名称コンボ選択値） | D001 |
| genderType | string | いいえ | 寮種別（MALE / FEMALE） | FEMALE |
| name | string | いいえ | 入居者氏名部分一致 | 田中 |
| sort | string | いいえ | ソート（将来拡張） | dormitoryName,asc |

---

### レスポンスパラメータ

```json
{
  "yearMonth": "2026-04",
  "daysInMonth": 30,
  "firstDayOfWeek": "水",
  "weekdayLabels": ["水", "木", "金"],
  "blocks": [
    {
      "dormitoryId": "D001",
      "dormitoryName": "豊洲C寮",
      "address": "東京都江東区…",
      "postalCode": "1350061",
      "region": "TOKYO",
      "rows": [
        {
          "residenceHistoryId": "RH001",
          "employeeId": "E001",
          "employeeName": "A",
          "affiliationName": "瀋陽",
          "roomName": "洋室",
          "roomId": "R001",
          "vacant": false,
          "isManager": false,
          "moveInDate": "2025-11-30",
          "moveOutDate": "2026-04-22",
          "moveOutInMonth": true,
          "moveOutWarning": false,
          "occupiedDays": [1, 2, 3],
          "conflictDays": []
        },
        {
          "roomName": "洋室",
          "roomId": "R001",
          "vacant": true,
          "isManager": false,
          "moveOutInMonth": false,
          "moveOutWarning": false,
          "occupiedDays": [],
          "conflictDays": []
        }
      ]
    }
  ]
}
```

| パラメータ名 | 型 | 必須 | 説明 | 例 |
|---|---|---|---|---|
| yearMonth | string | はい | 対象年月 | 2026-04 |
| daysInMonth | number | はい | 対象月の日数 | 30 |
| firstDayOfWeek | string | はい | 月初曜日（日本語） | 水 |
| weekdayLabels | array | はい | 各日の曜日ラベル | ["水","木"] |
| blocks | array | はい | 寮ブロック一覧 | — |
| blocks.dormitoryId | string | はい | 寮ID | D001 |
| blocks.dormitoryName | string | はい | 寮名称 | 豊洲C寮 |
| blocks.address | string | はい | 住所 | 東京都… |
| blocks.postalCode | string | はい | 郵便番号 | 1350061 |
| blocks.region | string | はい | 地域コード | TOKYO |
| blocks.rows | array | はい | 入居者行（定員分。入居者行＋空きスロット行） | — |
| blocks.rows.residenceHistoryId | string | いいえ | 入居履歴ID（空きスロット行は省略） | RH001 |
| blocks.rows.employeeId | string | いいえ | 社員ID（空きスロット行は省略） | E001 |
| blocks.rows.employeeName | string | いいえ | 氏名（空きスロット行は省略） | A |
| blocks.rows.affiliationName | string | はい | 所属名称 | 瀋陽 |
| blocks.rows.roomName | string | はい | 部屋表示名 | 洋室 |
| blocks.rows.roomId | string | はい | 部屋ID | R001 |
| blocks.rows.vacant | boolean | はい | 空きスロット行フラグ（true 時「入」ボタン表示） | false |
| blocks.rows.isManager | boolean | はい | 責任者フラグ（★） | false |
| blocks.rows.moveInDate | string | はい | 入居日 | 2025-11-30 |
| blocks.rows.moveOutDate | string | いいえ | 退居日 | 2026-04-22 |
| blocks.rows.moveOutInMonth | boolean | はい | 当月退寮フラグ | true |
| blocks.rows.moveOutWarning | boolean | はい | 14日以内退寮警告 | false |
| blocks.rows.occupiedDays | array | はい | 在籍日（1〜月末） | [1,2,3] |
| blocks.rows.conflictDays | array | はい | 部屋重複日 | [] |
