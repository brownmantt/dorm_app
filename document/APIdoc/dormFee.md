# 寮費 API



> 呼び出し元: `views/dormFee/DormFeeList.vue` → `api/dormFee.js`



---



## 寮費一覧取得



**インターフェース名称：** 寮費一覧取得  

**機能説明：** 検索条件に基づき寮費レコードをページング取得する  

**インターフェースURL：** `/api/v1/dorm-fees`  

**リクエスト方式：** GET



---



### リクエストパラメータ



| パラメータ名 | 型 | 必須 | 説明 | 例 |

|--------------|------|------|------|------|

| employeeId | string | いいえ | 社員 ID（部分一致） | E00012 |

| targetYearMonth | string | いいえ | 対象年月 `YYYY-MM` | 2026-06 |

| status | string | いいえ | ステータス（`PROVISIONAL`/`ERROR`） | PROVISIONAL |

| page | int | いいえ | ページ番号（0 始まり） | 0 |

| size | int | いいえ | 1 ページあたり件数 | 20 |



---



### レスポンスパラメータ



```json

{

  "content": [

    {

      "dormFeeId": "DF001",

      "region": "TOKYO",

      "regionName": "東京",

      "dormitoryId": "D001",

      "dormitoryName": "男子寮A",

      "roomId": "R001",

      "roomName": "101号室",

      "employeeId": "E00005",

      "employeeName": "鈴木一郎",

      "targetYearMonth": "2026-06",

      "moveInDate": "2026-06-01",

      "moveOutDate": "2026-06-30",

      "residenceMoveOutDate": null,

      "usageTypeCode": "NORMAL",

      "usageTypeName": "通常利用",

      "usageDays": 30,

      "unitPriceId": "UP001",

      "unitPriceCode": "UC001",

      "dailyUnitPrice": 3500,

      "amount": 105000,

      "residenceHistoryId": "RH00002",

      "status": "PROVISIONAL"

    }

  ],

  "totalElements": 1

}

```



| パラメータ名 | 型 | 必須 | 説明 | 例 |

|--------------|------|------|------|------|

| content | object[] | はい | 寮費一覧 | — |

| content[].dormFeeId | string | はい | 寮費 ID | DF001 |

| content[].region | string | はい | 地域コード | TOKYO |

| content[].regionName | string | いいえ | 地域名称 | 東京 |

| content[].dormitoryId | string | はい | 寮 ID | D001 |

| content[].dormitoryName | string | いいえ | 寮名称 | 男子寮A |

| content[].roomId | string | はい | 部屋 ID | R001 |

| content[].roomName | string | いいえ | 部屋名称 | 101号室 |

| content[].employeeId | string | はい | 入居者（社員 ID） | E00005 |

| content[].employeeName | string | いいえ | 入居者名称 | 鈴木一郎 |

| content[].targetYearMonth | string | はい | 対象年月 | 2026-06 |

| content[].moveInDate | string | はい | 算定期間開始日 | 2026-06-01 |

| content[].moveOutDate | string | いいえ | 算定期間終了日（DB 保存値） | 2026-06-30 |

| content[].residenceMoveOutDate | string | いいえ | 入居履歴の退居日（未退居は null） | null |

| content[].usageTypeCode | string | はい | 利用形態コード | NORMAL |

| content[].usageTypeName | string | いいえ | 利用形態名称 | 通常利用 |

| content[].usageDays | int | いいえ | 利用日数 | 30 |

| content[].unitPriceId | string | いいえ | 単価 ID | UP001 |

| content[].unitPriceCode | string | いいえ | 単価コード | UC001 |

| content[].dailyUnitPrice | number | いいえ | 日単価 | 3500 |

| content[].amount | number | いいえ | 算出金額 | 105000 |

| content[].residenceHistoryId | string | はい | 入居履歴 ID | RH00002 |

| content[].status | string | はい | ステータス（`PROVISIONAL`=仮定 / `ERROR`=エラー） | PROVISIONAL |

| totalElements | int | いいえ | 総件数 | 1 |



---



## 寮費算定



**インターフェース名称：** 寮費算定  

**機能説明：** 対象月入居中の入居履歴から寮費を算出し `dorm_fee` に保存する  

**インターフェースURL：** `/api/v1/dorm-fees/calculate`  

**リクエスト方式：** POST



---



### 機能説明



対象年月と重なる入居履歴（入居中）を抽出する。入居履歴ごとに単価マスタを **A（地域・利用形態・寮・部屋）→ B（寮まで）→ C（地域のみ）** の順で検索する。一致した単価の日単価と、算定期間の利用日数（`MAX(入居日,月初)` 〜 `MIN(退居日,月末)`）から寮費を算出する。利用日数は利用形態マスタの最小/最大利用日数の**範囲内**であること（範囲外はエラー）。結果を `dorm_fee` に登録または更新する。成功時は **仮定（`PROVISIONAL`）**、失敗時は **エラー（`ERROR`）**。



---



### リクエストパラメータ



| パラメータ名 | 型 | 必須 | 説明 | 例 |

|--------------|------|------|------|------|

| employeeId | string | いいえ | 社員 ID（絞込用） | E00012 |

| dormitoryId | string | いいえ | 寮 ID（絞込用） | D001 |

| roomId | string | いいえ | 部屋 ID（絞込用） | R003 |

| targetYearMonth | string | はい | 対象年月 `YYYY-MM` | 2026-06 |

| moveInDate | string | いいえ | 入寮日（請求期間の下限で絞込） | 2026-06-01 |

| moveOutDate | string | いいえ | 退寮日（請求期間の上限で絞込） | 2026-06-30 |



---



### レスポンスパラメータ



```json

{

  "amount": 105000,

  "items": [

    {

      "dormFeeId": "DF001",

      "residenceHistoryId": "RH00002",

      "employeeId": "E00005",

      "employeeName": "鈴木一郎",

      "dormitoryId": "D001",

      "dormitoryName": "男子寮A",

      "roomId": "R001",

      "roomName": "101号室",

      "targetYearMonth": "2026-06",

      "moveInDate": "2026-06-01",

      "moveOutDate": "2026-06-30",

      "usageTypeCode": "NORMAL",

      "usageDays": 30,

      "unitPriceId": "UP001",

      "dailyUnitPrice": 3500,

      "amount": 105000,

      "status": "PROVISIONAL",

      "errorMessage": null

    }

  ]

}

```



| パラメータ名 | 型 | 必須 | 説明 | 例 |

|--------------|------|------|------|------|

| amount | number | はい | 算定合計金額（成功分の合計） | 105000 |

| items | object[] | はい | 算定・保存結果一覧 | — |

| items[].dormFeeId | string | はい | 保存された寮費 ID | DF001 |

| items[].status | string | はい | `PROVISIONAL` または `ERROR` | PROVISIONAL |

| items[].errorMessage | string | いいえ | エラー時の理由 | null |



---

