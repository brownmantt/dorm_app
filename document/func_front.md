# func_front.md（フロントエンド機能定義書）

寮管理システム フロントエンド（`dom-dev`）の機能インベントリ・呼び出しチェーン定義書。
新規実装・変更の前に本ファイルを全文確認し、重複・類似・再利用可能な機能がないかをチェックすること。
新規追加・変更を行った場合は、本ファイルを必ず更新すること。

- 技術スタック: Vue 3 (script setup) + Vue Router + Pinia + Element Plus + Axios + Vite
- ソースルート: `dom-dev/src/`
- API ベース URL: `import.meta.env.VITE_API_BASE_URL || '/api/v1'`

---

## 1. ディレクトリ構成と命名規約

| 階層 | パス | 命名規約 |
|---|---|---|
| Views | `src/views/{domain}/{Feature}.vue` | パスカルケースの画面名 |
| Components | `src/components/{Component}.vue` | 共通 UI コンポーネント |
| Api | `src/api/{domain}.js` | ドメイン単位のリクエスト関数群 |
| Store | `src/store/{name}.js` | Pinia ストア（`use{Name}Store`） |
| Utils | `src/utils/{function}.js` | 汎用関数・定数 |
| Router | `src/router/index.js` / `menus.js` | ルート定義・階層メニュー定義（`menuGroups`） |
| Layout | `src/layout/MainLayout.vue` | 共通レイアウト（固定ヘッダー＋左メニュー／明細部の独立スクロール＋フッター）。サイドナビ上部に「すべて開く」「すべて閉じる」。1階層：寮割カレンダー（ダッシュボード統合済み）。階層グループ：寮・部屋／入居管理／寮費／マスタ／備品／データ・ログ。ブランドロゴ／ログイン後リダイレクト先は `/allocation` |

> 注: API は設計書の `api/{domain}/index.js` ではなく `api/{domain}.js` 形式で統一されている。新規追加時も本形式に合わせること。

---

## 2. Views（画面一覧）

| ドメイン | ルートパス | ルート名 | ファイル | ロール | 概要 |
|---|---|---|---|---|---|
| login | `/login` | Login | `views/login/Login.vue` | public | ログイン |
| allocation | `/allocation` | DormAllocationCalendar | `views/allocation/DormAllocationCalendar.vue` | ADMIN/USER | **寮割カレンダー（メイン画面・ダッシュボード統合）** — `PageHeader` は廃止。画面上部に**ダッシュボード内容**（`no-print`）を**カラフルな丸角カード（`metric-card`・`border-radius:14px`・色別 `--indigo`/`--teal`/`--violet`/`--amber`/`--rose`）5枚を1行**で表示（`el-row` span **5/3/4/6/6**・内容量に応じた幅配分）：①寮数（`dormCountSummary`）②部屋数（`roomSummary.total`）③**入居人数**（`residentSummary`：当日時点の有効入居履歴数＋男性/女性内訳。`fetchResidentSummary()` → `getResidences` + `searchEmployeesSilent` で `employee.gender` を突合）④入居状況（円グラフ＋室数・割合）⑤長期利用中状況（件数＋直近一覧）。データ取得は `getVacancies` / `getLongTermUsageAlerts`。検索：寮名称コンボ・種別・地域・入居者氏名（＋印刷ボタン）。**対象月切替は検索条件カードに統合**（`allocation-month-bar`・`justify-content:space-between`：左＝前月ボタン・中央＝月度ラベル・右＝翌月ボタン）。**印刷ボタンは検索アクション欄（表示/リセットの右）**。印刷時は条件カード（`no-print`）が非表示のため、明細部に印刷専用の月度ラベル（`print-only`：画面非表示・`@media print` で表示）を出力。一体レイアウト：寮情報（入/退/寮詳細）＋入居者情報＋カレンダー。**明細密度向上のため、寮名称はヘッダ部「寮情報」グループセルに表示（`group-dorm-name`・寮詳細リンク）し、独立した寮名称メタ行・住所・「寮情報」見出し文言は廃止**。寮概要セル（`dormSummary`）は `間取り / 種別 / 空室有無 / 地域` 形式（例：`3DK / 女性寮 / 空室無 / 東京`・地域は `region || inferRegionFromAddress(address)`）。**明細ブロックの表示順は `sortedBlocks` で①入居者あり→なし ②部屋データあり→なし ③更新日（`block.updatedAt`）新しい順 ④寮名称昇順**（`buildCalendarFromData` がブロックに `updatedAt` を付与）。入居者情報列：入居日・退居日。責任者列は `isManager` 時に **★** 表示（最大1名/寮）。**部屋ごとに定員（`capacity`）数の行を表示**（入居者行＋空きスロット行）。**「入」ボタンは `row.vacant === true`（空きスロット行）のみ表示**。カレンダー列ヘッダーは土日・祝日を赤文字（`utils/calendarOffDay.js` + `@holiday-jp/holiday_jp`）。列幅：寮情報188px・入居者情報320px固定、カレンダーは `calc((100% - 508px) / 日数)` で可変。寮名称リンク→寮詳細（SC-03）、部屋名称リンク→部屋編集（SC-04）、氏名リンク→社員マスタ（SC-16・社員ID絞込） |
| dormitory | `/dormitories` | DormitoryList | `views/dormitory/DormitoryList.vue` | ADMIN/USER | 寮一覧（寮名称コンボ・地域・種別・住所検索・CRUD・最寄駅3項目）。新規/編集は `DormitoryFormDialog` |
| dormitory | `/dormitories/:id` | DormitoryDetail | `views/dormitory/DormitoryDetail.vue` | ADMIN/USER | 寮詳細（上部3カラム3行、値エリア列幅 **2:6:2**）。`toolbar-card` に一覧へ戻る・**編集**（`DormitoryFormDialog`・保存後 `fetchDetail` で反映）・部屋追加。部屋一覧・責任者設定 |
| dormitory | `/dormitories/:dormId/rooms/:roomId/edit` | RoomEdit | `views/dormitory/RoomEdit.vue` | ADMIN | 部屋編集。フォーム下部に「一覧へ戻る」（寮詳細へ）・「保存」 |
| residence | `/residences/register` | ResidenceRegister | `views/residence/ResidenceRegister.vue` | ADMIN | **入居登録・退居**（メニュー1番目）。タブ：入居登録・**退居処理**。入居登録：入居者は `getRegisterableEmployees()` コンボ（未入居者のみ・社員マスタ更新日降順）。入居者選択後、寮コンボは入居者性別の男性寮/女性寮かつ割当可能部屋がある寮のみ（`getAssignableRooms` + `getDormitories({ genderType })`）。入居日・退居日は `iso-date-editor`。寮割カレンダー「入」から遷移時は `query.dormitoryId` / `query.roomId` で寮・部屋をプリセットし、入居者変更時も固定（`calendarPreset`）。入居日未入力時は当日を自動設定（`ensureDefaultMoveInDate`） |
| residence | `/residences` | ResidenceHistoryList | `views/residence/ResidenceHistoryList.vue` | ADMIN/USER | **入居履歴**（メニュー2番目）。入居履歴一覧（検索：入居者氏名・社員ID・寮名称・入居日範囲）。入居日範囲は `iso-date-editor`（`calc(10ch + 72px)`・フォーカス時クリアアイコン込みで yyyy-MM-dd 全文表示）。一覧列：番号・社員ID・入居者氏名・寮名称・部屋・入居日・退居日・退寮理由（履歴IDは画面非表示）。入居日・退居日列は `col-iso-date`（120px・yyyy-MM-dd 10文字省略なし） |
| residence | `/first-use-long-term` | FirstUseLongTerm | `views/residence/FirstUseLongTerm.vue` | ADMIN/USER | 初回利用日・長期利用アラート |
| dormFee | `/dorm-fees` | DormFeeList | `views/dormFee/DormFeeList.vue` | ADMIN | 寮費一覧・算定・確定。算定ダイアログ：入居日・退居日は `form-inline-date-range` 横並び・`iso-date-editor` |
| equipment | `/equipments` | EquipmentMasterList | `views/equipment/EquipmentMasterList.vue` | ADMIN | 備品マスタ |
| equipment | `/equipment-moveouts` | MoveOutEquipment | `views/equipment/MoveOutEquipment.vue` | ADMIN | 退去備品処理 |
| equipment | `/equipment-storages` | EquipmentStorage | `views/equipment/EquipmentStorage.vue` | ADMIN | 備品保管 |
| vacancy | `/vacancies` | VacancyList | `views/vacancy/VacancyList.vue` | ADMIN/USER | 空き室一覧（検索：種別・基準日）。寮名称→寮詳細、部屋名称→部屋編集。入居者列右「入居」、退寮予定日列右「退居」（ADMIN） |
| affiliation | `/affiliations` | AffiliationList | `views/affiliation/AffiliationList.vue` | ADMIN | 所属マスタ CRUD |
| region | `/regions` | RegionList | `views/region/RegionList.vue` | ADMIN | **地域マスタ CRUD** — 地域コード・名称・表示順。初期データ：東京/大阪/名古屋/その他 |
| usageType | `/usage-types` | UsageTypeList | `views/usageType/UsageTypeList.vue` | ADMIN | **利用形態マスタ CRUD** — コード値・名称・表示順。初期データ：通常利用/出張利用/長期利用 |
| unitPrice | `/unit-prices` | UnitPriceList | `views/unitPrice/UnitPriceList.vue` | ADMIN | **単価マスタ CRUD** — 単価コード（自動採番・編集時参照のみ）・地域・寮（任意）・部屋（任意）・利用形態・日単価・最大利用日数（任意、未入力時 -1） |
| employee | `/employees` | EmployeeList | `views/employee/EmployeeList.vue` | ADMIN | **社員マスタ CRUD（SC-16）** — 検索・新規・編集・論理削除・最寄駅3項目。入居登録の社員検索データソース |
| import | `/import` | ExcelImport | `views/import/ExcelImport.vue` | ADMIN | Excel/CSV 取込・エクスポート |
| log | `/operation-logs` | OperationLogList | `views/log/OperationLogList.vue` | ADMIN | 操作ログ一覧 |
| error | `/403` | Forbidden | `views/error/Forbidden.vue` | public | 権限エラー |

---

## 3. Components（共通コンポーネント）

| コンポーネント | ファイル | Props | Slots | Events |
|---|---|---|---|---|
| PageHeader | `components/PageHeader.vue` | `title: String (required)`, `subtitle: String` | `extra` | なし |
| DormitoryFormDialog | `components/DormitoryFormDialog.vue` | `modelValue: Boolean`, `editData: Object \| null` | なし | `update:modelValue`, `saved`（保存成功後） |

> 寮新規/編集ダイアログは `DormitoryFormDialog` に共通化。住所入力は **textarea（3行）**。郵便番号 blur で `lookupAddressByPostalCode()` による住所自動入力（**郵便番号がダイアログ表示時／前回 lookup 時と同一の場合は住所を上書きしない**）。保存時 `createDormitory()` / `updateDormitory()`。

---

## 4. Api（リクエスト関数）

すべて `utils/request.js`（axios インスタンス）経由。一覧系は `buildQueryParams` でクエリ整形。

### `api/employee.js`
| 関数 | メソッド | エンドポイント | 概要 |
|---|---|---|---|
| `getEmployees(params)` | GET | `/employees` | 社員一覧（キーワード・性別・区分・所属・`notResidingOnly`・ページング） |
| `searchEmployees(params)` | GET | `/employees` | 社員検索（`getEmployees` エイリアス） |
| `getRegisterableEmployees(params)` | GET | `/employees` | 入居登録用社員一覧（`notResidingOnly=true`・更新日降順） |
| `searchEmployeesSilent(params)` | GET | `/employees` | 社員検索（エラー非表示・カレンダー補完用） |
| `getEmployee(employeeId)` | GET | `/employees/:id` | 社員詳細 |
| `createEmployee(data)` | POST | `/employees` | 社員登録 |
| `updateEmployee(id, data)` | PUT | `/employees/:id` | 社員更新 |
| `deleteEmployee(id)` | DELETE | `/employees/:id` | 社員論理削除 |

### `api/auth.js`
| 関数 | メソッド | エンドポイント | 概要 |
|---|---|---|---|
| `login(data)` | POST | `/auth/login` | ログイン（`{username, password}`） |

### `api/dormAllocation.js`
| 関数 | メソッド | エンドポイント | 概要 |
|---|---|---|---|
| `fetchDormAllocationCalendar(params)` | GET | `/dorm-allocation` | 寮割カレンダー（未実装時はクライアント組み立て） |
| `buildCalendarClientSide(params)` | — | — | 既存 API からカレンダー組み立て（所属・責任者補完含む） |
| `getDormAllocationPrint(params)` | GET | `/dorm-allocation/print` | 印刷用データ |
| `getMoveOutAlerts(params)` | GET | `/alerts/move-out` | 退寮予定警告 |

### `api/affiliation.js`
| 関数 | メソッド | エンドポイント | 概要 |
|---|---|---|---|
| `getAffiliations(params)` | GET | `/affiliations` | 所属一覧 |
| `getAffiliationsSilent(params)` | GET | `/affiliations` | 所属一覧（エラー非表示・カレンダー補完用） |
| `createAffiliation(data)` | POST | `/affiliations` | 所属登録 |
| `updateAffiliation(id, data)` | PUT | `/affiliations/:id` | 所属更新 |
| `deleteAffiliation(id)` | DELETE | `/affiliations/:id` | 所属削除 |

### `api/region.js`
| 関数 | メソッド | エンドポイント | 概要 |
|---|---|---|---|
| `getRegions(params)` | GET | `/regions` | 地域一覧（名称・ページング） |
| `getRegionsSilent(params)` | GET | `/regions` | 地域一覧（エラー非表示・コンボ用） |
| `createRegion(data)` | POST | `/regions` | 地域登録 |
| `updateRegion(id, data)` | PUT | `/regions/:id` | 地域更新 |
| `deleteRegion(id)` | DELETE | `/regions/:id` | 地域削除 |

### `api/usageType.js`
| 関数 | メソッド | エンドポイント | 概要 |
|---|---|---|---|
| `getUsageTypes(params)` | GET | `/usage-types` | 利用形態一覧（名称・ページング） |
| `getUsageTypesSilent(params)` | GET | `/usage-types` | 利用形態一覧（エラー非表示・コンボ用） |
| `createUsageType(data)` | POST | `/usage-types` | 利用形態登録 |
| `updateUsageType(id, data)` | PUT | `/usage-types/:id` | 利用形態更新 |
| `deleteUsageType(id)` | DELETE | `/usage-types/:id` | 利用形態削除 |

### `api/unitPrice.js`
| 関数 | メソッド | エンドポイント | 概要 |
|---|---|---|---|
| `getUnitPrices(params)` | GET | `/unit-prices` | 単価一覧（コード・地域・寮・利用形態・ページング） |
| `createUnitPrice(data)` | POST | `/unit-prices` | 単価登録 |
| `updateUnitPrice(id, data)` | PUT | `/unit-prices/:id` | 単価更新 |
| `deleteUnitPrice(id)` | DELETE | `/unit-prices/:id` | 単価削除 |

### `api/dormitory.js`
| 関数 | メソッド | エンドポイント | 概要 |
|---|---|---|---|
| `getDormitories(params)` | GET | `/dormitories` | 寮一覧（`dormitoryId` / `genderType` / `region` / `address`） |
| `getDormitoryNameOptions()` | — | `/dormitories` | 寮名称コンボボックス用選択肢 |
| `getDormitory(id)` | GET | `/dormitories/:id` | 寮詳細 |
| `createDormitory(data)` | POST | `/dormitories` | 寮登録（`postalCode` 必須） |
| `updateDormitory(id, data)` | PUT | `/dormitories/:id` | 寮更新 |
| `deleteDormitory(id)` | DELETE | `/dormitories/:id` | 寮削除 |
| `getRoomsByDormitory(dormId, params)` | GET | `/dormitories/:dormId/rooms` | 部屋一覧 |
| `createRoom(data)` | POST | `/rooms` | 部屋登録 |
| `updateRoom(id, data)` | PUT | `/rooms/:id` | 部屋更新 |
| `deleteRoom(id)` | DELETE | `/rooms/:id` | 部屋削除 |
| `getDormitoryManager(dormId)` | GET | `/dormitories/:dormId/manager` | 責任者取得 |
| `getDormitoryManagerSilent(dormId)` | GET | `/dormitories/:dormId/manager` | 責任者取得（エラー非表示・カレンダー補完用） |
| `setDormitoryManager(dormId, data)` | PUT | `/dormitories/:dormId/manager` | 責任者設定 |
| `removeDormitoryManager(dormId)` | DELETE | `/dormitories/:dormId/manager` | 責任者解除 |

### `api/postalCode.js`
| 関数 | メソッド | エンドポイント | 概要 |
|---|---|---|---|
| `lookupAddressByPostalCode(postalCode)` | GET | `/postal-codes/:postalCode/address` | 郵便番号から住所取得 |

### `api/residence.js`
| 関数 | メソッド | エンドポイント | 概要 |
|---|---|---|---|
| `getResidences(params)` | GET | `/residences` | 入居履歴一覧（`name` / `employeeId` / `dormitoryName` / 入居日範囲） |
| `createResidence(data)` | POST | `/residences` | 入居登録 |
| `validateResidence(data)` | POST | `/residences/validate` | 入居業務検証 |
| `checkoutResidence(id, data)` | PUT | `/residences/:id/checkout` | 退居処理 |
| `getFirstUseDate(empId)` | GET | `/employees/:empId/first-use-date` | 初回利用日取得 |
| `getTotalUsageDays(empId, params)` | GET | `/employees/:empId/total-usage-days` | 累計利用日数 |
| `getLongTermUsageAlerts(params)` | GET | `/alerts/long-term-usage` | 長期利用アラート |

### `api/dormFee.js`
| 関数 | メソッド | エンドポイント | 概要 |
|---|---|---|---|
| `getDormFees(params)` | GET | `/dorm-fees` | 寮費一覧 |
| `calculateDormFee(data)` | POST | `/dorm-fees/calculate` | 寮費算定 |
| `createDormFee(data)` | POST | `/dorm-fees` | 寮費登録 |
| `confirmDormFee(id)` | PUT | `/dorm-fees/:id/confirm` | 寮費確定 |

### `api/equipment.js`
| 関数 | メソッド | エンドポイント | 概要 |
|---|---|---|---|
| `getEquipments(params)` | GET | `/equipments` | 備品一覧 |
| `createEquipment(data)` | POST | `/equipments` | 備品登録 |
| `updateEquipment(id, data)` | PUT | `/equipments/:id` | 備品更新 |
| `processEquipmentMoveout(data)` | POST | `/equipment-moveouts` | 退去備品処理 |
| `getEquipmentStorages(params)` | GET | `/equipment-storages` | 保管一覧 |
| `createEquipmentStorage(data)` | POST | `/equipment-storages` | 保管登録 |

### `api/vacancy.js`
| 関数 | メソッド | エンドポイント | 概要 |
|---|---|---|---|
| `getVacancies(params)` | GET | `/vacancies` | 空き室一覧 |
| `getAssignableRooms(params)` | GET | `/vacancies/assignable` | 割当可能部屋一覧 |

### `api/import.js`
| 関数 | メソッド | エンドポイント | 概要 |
|---|---|---|---|
| `previewExcelImport(formData)` | POST | `/imports/excel/preview` | Excel取込プレビュー（multipart） |
| `executeExcelImport(formData)` | POST | `/imports/excel/execute` | Excel取込実行（multipart） |
| `exportResidencesCsv(params)` | GET | `/exports/csv/residences` | 入居履歴 CSV（blob） |
| `exportDormFeesCsv(params)` | GET | `/exports/csv/dorm-fees` | 寮費 CSV（blob） |

### `api/operationLog.js`
| 関数 | メソッド | エンドポイント | 概要 |
|---|---|---|---|
| `getOperationLogs(params)` | GET | `/operation-logs` | 操作ログ一覧 |

---

## 5. Store（Pinia）

### `store/user.js` — `useUserStore`
| 種別 | 名前 | 概要 |
|---|---|---|
| state | `token` / `username` / `roles` | localStorage と同期 |
| getter | `isLoggedIn` | トークン有無 |
| getter | `isAdmin` | `ROLE_ADMIN` 保持判定 |
| action | `setSession(payload)` | ログイン情報を保存 |
| action | `logout()` | セッション破棄 |
| action | `hasPermission(permission)` | ロール判定 |

> 業務データ用ストアは存在しない。各 View が `api/*` を直接呼ぶ方針で統一されている。

---

## 6. Utils（共通関数・定数）

### `utils/request.js`
- axios インスタンス。リクエスト時に JWT（`Bearer`）付与。
- レスポンスは `response.data` を返す。401 でログアウト＆ログイン遷移、403 で権限エラー通知。
- `VITE_AUTH_BYPASS=true` 時はバックエンド未接続のネットワークエラー通知を抑制。

### `utils/pagination.js`
- `normalizePageResponse(data)`: Spring Data 形式 or 配列を `{ list, total }` に正規化。
- `buildQueryParams(params)`: 空値を除外したクエリオブジェクトを生成。

### `utils/constants.js`
- 区分マスタ: `GENDER_TYPE` / `REGION` / `EMPLOYEE_GENDER` / `EMPLOYEE_CATEGORY` / `ROOM_TYPE` / `DORM_FEE_STATUS` / `EQUIPMENT_DISPOSITION` / `STORAGE_STATUS` / `VACANCY_STATUS`
- 選択肢: `REGION_OPTIONS`（**フォールバック用**。画面の地域コンボは `utils/region.js` → `api/region.js` から取得）
- ロール: `ROLE_ADMIN` / `ROLE_USER`
- 選択肢: `LAYOUT_TYPE_OPTIONS` / `WEEKDAY_JA` / `MOVE_OUT_WARNING_DAYS`
- 関数: `labelOf(map, code)`（コード→ラベル変換）

### `utils/postalCode.js`
- `normalizePostalCode(value)`: 7桁数字に正規化
- `formatPostalCode(value)`: `123-4567` 形式に整形
- `isCompletePostalCode(value)`: 7桁入力済み判定

### `utils/date.js`
- `formatDate(value)` / `formatDateTime(value)` / `formatYearMonth(yearMonth)` / `formatMonthDay(value)`（例: `1月1日`）
- `parseYearMonth` / `formatYearMonthLabel` / `shiftYearMonth` / `monthStartDate` / `monthEndDate` / `dateInMonth`
- `weekdayIndex` / `todayDateString` / `daysBetween` / `getDaysInMonth`

### `utils/employee.js`
- `parseContactInfo(contactInfo)`: contact_info JSON から携帯・メールを抽出

### `utils/region.js`
- `mapRegionListToOptions(list)`: API レスポンス → `{ value: code, label: name }[]`
- `loadRegionOptions({ silent })`: 地域マスタからコンボ用選択肢を取得（失敗時 `REGION_OPTIONS` にフォールバック）
- `toRegionLabelMap(regionOptions)`: コード → 名称マップ生成
- `resolveRegionLabel(code, regionLabelMap)`: 地域コード → 表示名称
- `regionLabelFromOptions(code, regionOptions)`: 選択肢配列から名称解決

### `utils/usageType.js`
- `mapUsageTypeListToOptions(list)`: API レスポンス → `{ value: code, label: name }[]`
- `loadUsageTypeOptions({ silent })`: 利用形態マスタからコンボ用選択肢を取得

### `utils/dormAllocation.js`
- `inferRegionFromAddress(address)`: 住所から地域コード推定
- `isVisibleInMonth` / `isOccupiedOnDate` / `getOccupiedDayNumbers`: カレンダー在籍判定
- `isMoveOutWarning` / `isMoveOutInMonth`: 退寮警告・当月退寮表示
- `resolveIsManager` / `resolveAffiliationName` / `resolveRoomConflictKey`: 所属・責任者・部屋重複キー解決
- `buildEmployeeAffiliationMap(employees, affiliations)`: 社員ID→所属名称マップ（所属マスタ未実装時は社員区分ラベルで補完）
- `buildCalendarFromData(dormitories, residences, yearMonth, filters, enrichment)`: 寮割カレンダーデータ組み立て（部屋定員分の行：入居者行＋空きスロット行・enrichment で所属・責任者を補完）
- `resolveRoomCapacity(room)`: 部屋定員（未設定時 1）
- `formatRoomNameLabel(row)`: 寮割カレンダー寮詳細列表示（`roomName` のみ）
- `formatRoomDetailLabel(row)`: 部屋詳細ラベル（`roomDetail` / `roomName` ＋ エアコン有無・他画面用）

---

## 7. 呼び出しチェーン

業務データ系（store を経由しない）:

```
views/{domain}/{Feature}.vue  onMounted / fetchList()
 ↓
api/{domain}.js  get{Domain}(params)
 ↓
utils/request.js  GET /{resource}
 ↓
backend
```

寮割カレンダー（SC-01・ダッシュボード統合）:

```
views/allocation/DormAllocationCalendar.vue  fetchCalendar()
 ↓
api/dormAllocation.js  fetchDormAllocationCalendar()
 ↓
GET /dorm-allocation（失敗時 api/dormAllocation.js buildCalendarClientSide()）
 ↓
api/dormitory.js getDormitories() + api/residence.js getResidences()
 ↓
api/employee.js searchEmployeesSilent() + api/affiliation.js getAffiliationsSilent()
 ↓
api/dormitory.js getDormitoryManagerSilent()（寮ごと）
 ↓
utils/dormAllocation.js buildCalendarFromData(..., enrichment)
 ↓
backend / クライアント組み立て

# 画面上部ダッシュボード（onMounted で並行取得）
views/allocation/DormAllocationCalendar.vue  fetchVacancies() / fetchAlerts()
 ↓
api/vacancy.js getVacancies()        → 空き室サマリ（円グラフ・統計カード）
api/residence.js getLongTermUsageAlerts() → 長期利用警告一覧・件数カード
```

寮割カレンダー部屋名称リンク → 部屋編集（SC-01 → SC-04）:

```
views/allocation/DormAllocationCalendar.vue  roomEditRoute(block, row)
 ↓
router  name: RoomEdit, params: { dormId, roomId }
 ↓
views/dormitory/RoomEdit.vue  fetchRoom() / updateRoom()
 ↓
api/dormitory.js  GET/PUT /rooms/:id
```

寮割カレンダー氏名リンク → 社員マスタ（社員ID絞込）（SC-01 → SC-16）:

```
views/allocation/DormAllocationCalendar.vue  residentDetailRoute(row)
 ↓
router  name: EmployeeList, query: { employeeId }
 ↓
views/employee/EmployeeList.vue  fetchList()（keyword=employeeId）
 ↓
api/employee.js getEmployees({ keyword })
 ↓
EmployeeController GET /employees
```

寮割カレンダー「入」ボタン → 入居登録（SC-01 → SC-06）※空室行（row.vacant）のみ表示:

```
views/allocation/DormAllocationCalendar.vue  goRegister(block, row)
 ↓
router  name: ResidenceRegister, query: { tab: register, dormitoryId, roomId }
 ↓
views/residence/ResidenceRegister.vue  applyRouteQuery()
 ↓
入居登録タブへ切替・寮・部屋プリセット（calendarPreset 有効・寮/部屋 select 無効化）
 ↓
api/dormitory.js getRoomsByDormitory()（プリセット部屋の表示用）
 ↓
入居者選択時: loadEmployeeInfo() → api/residence.js getFirstUseDate()
watch(employeeId, moveInDate) → refreshDormitoryOptions()
  → api/vacancy.js getAssignableRooms({ employeeId, asOfDate })
  → api/dormitory.js getDormitories({ genderType: 入居者性別 })
  → 割当可能寮のみ寮コンボ表示
```

寮割カレンダー「退」ボタン → 退居処理（SC-01 → SC-06）:

```
views/allocation/DormAllocationCalendar.vue  goCheckout(row)
 ↓
router  name: ResidenceRegister, query: { tab: checkout, residenceHistoryId }
 ↓
views/residence/ResidenceRegister.vue  applyRouteQuery()
 ↓
退居処理タブへ切替・履歴IDプリセット・対象者情報表示
 ↓
api/residence.js getResidences()（対象レコード照会）
```

例（寮一覧）:

```
views/dormitory/DormitoryList.vue  fetchList()
 ↓
api/dormitory.js  getDormitories(params)
 ↓
utils/request.js  GET /dormitories
 ↓
backend
```

寮新規登録（郵便番号→住所自動入力）:

```
views/dormitory/DormitoryList.vue  handlePostalCodeBlur()
 ↓
api/postalCode.js  lookupAddressByPostalCode(postalCode)
 ↓
utils/request.js  GET /postal-codes/{postalCode}/address
 ↓
PostalCodeController → PostalCodeServiceImpl → zipcloud API
 ↓
form.address へ自動反映 → createDormitory(payload)
 ↓
DormitoryController → DormitoryServiceImpl → DormitoryMapper → dormitory テーブル
```

寮新規登録ダイアログ項目: 寮名称 / 郵便番号 / 住所 / 地域 / 間取り / 種別 / **最寄駅（1行3テキスト: nearestStation1〜3、各20文字）** / 備考
```

認証系（store を経由する）:

```
views/login/Login.vue  handleLogin()
 ↓
api/auth.js  login(data)
 ↓
store/user.js  setSession(payload)
 ↓
router ガードで isLoggedIn / roles を参照
```

寮詳細 → 部屋編集（SC-03 → SC-04）:

```
views/dormitory/DormitoryDetail.vue  goRoomEdit(row)
 ↓
router  name: RoomEdit
 ↓
views/dormitory/RoomEdit.vue  updateRoom()
 ↓
api/dormitory.js  PUT /rooms/:id
```

寮詳細 → 寮編集（SC-03）:

```
views/dormitory/DormitoryDetail.vue  openEditDialog()
 ↓
components/DormitoryFormDialog.vue  updateDormitory()
 ↓
api/dormitory.js  PUT /dormitories/:id
 ↓
views/dormitory/DormitoryDetail.vue  fetchDetail()（@saved で詳細再表示）
```

社員マスタ（SC-16）:

```
views/employee/EmployeeList.vue  fetchList() / handleSubmit() / handleDelete()
 ↓
api/employee.js  getEmployees() / getEmployee() / createEmployee() / updateEmployee() / deleteEmployee()
 ↓
api/affiliation.js getAffiliations()（所属選択肢）
 ↓
GET/POST/PUT/DELETE /employees
 ↓
backend EmployeeController → EmployeeService → EmployeeMapper
```

社員新規登録ダイアログ項目: 社員ID / 氏名 / 性別 / 入居者区分 / 所属 / 事業部 / **最寄駅（1行3テキスト: nearestStation1〜3、各20文字）** / 携帯電話 / メール
```

入居登録（入居者コンボ・業務検証）:

```
views/residence/ResidenceRegister.vue  onMounted() → fetchEmployeeOptions()
 ↓
api/employee.js  getRegisterableEmployees({ notResidingOnly: true, page, size })
 ↓
EmployeeController GET /employees → EmployeeService.list() → EmployeeMapper.searchList()
 ↓
employee テーブル（未入居者のみ・updated_at 降順）

views/residence/ResidenceRegister.vue  handleRegister()
 ↓
api/vacancy.js  getAssignableRooms({ employeeId, dormitoryId, asOfDate })
api/residence.js  validateResidence() → createResidence()
 ↓
backend
```

退居処理（カレンダー遷移含む）:

```
views/residence/ResidenceRegister.vue  applyRouteQuery() / handleCheckout()
 ↓
route.query.residenceHistoryId で退居処理タブ・履歴IDをプリセット
 ↓
api/residence.js  getResidences()（対象照会）→ checkoutResidence()
 ↓
backend
```

空き室一覧 → 寮詳細（SC-12 → SC-03）:

```
views/vacancy/VacancyList.vue  寮名称 router-link
 ↓
router  name: DormitoryDetail, params: { id: dormitoryId }
 ↓
views/dormitory/DormitoryDetail.vue  fetchDetail()
 ↓
api/dormitory.js  getDormitory(id)
```

空き室一覧 → 部屋編集（SC-12 → SC-04）:

```
views/vacancy/VacancyList.vue  部屋名称 router-link
 ↓
router  name: RoomEdit, params: { dormId, roomId }
 ↓
views/dormitory/RoomEdit.vue  fetchRoom() / updateRoom()
 ↓
api/dormitory.js  getRoomsByDormitory() / updateRoom()
```

空き室一覧 → 入居登録（SC-12 → SC-06）:

```
views/vacancy/VacancyList.vue  goRegister(row)  ※assignable かつ ADMIN
 ↓
router  name: ResidenceRegister, query: { tab: register, dormitoryId, roomId }
 ↓
views/residence/ResidenceRegister.vue  applyRouteQuery()
 ↓
入居登録タブ・寮・部屋プリセット（calendarPreset）
```

空き室一覧 → 退居処理（SC-12 → SC-06）:

```
views/vacancy/VacancyList.vue  goCheckout(row)  ※residenceHistoryId あり かつ ADMIN
 ↓
router  name: ResidenceRegister, query: { tab: checkout, residenceHistoryId }
 ↓
views/residence/ResidenceRegister.vue  applyRouteQuery()
 ↓
退居処理タブ・履歴IDプリセット
```

地域マスタ（コンボ連携）:

```
views/region/RegionList.vue  fetchList() / handleSubmit() / handleDelete()
 ↓
api/region.js  getRegions() / createRegion() / updateRegion() / deleteRegion()
 ↓
RegionController → RegionServiceImpl → RegionMapper → region テーブル

# 各画面の地域コンボ（寮一覧・寮割カレンダー・寮登録ダイアログ等）
views/* / components/DormitoryFormDialog.vue  onMounted / handleOpen
 ↓
utils/region.js  loadRegionOptions()
 ↓
api/region.js  getRegions({ page: 0, size: 500 })
 ↓
GET /regions
```

利用形態マスタ:

```
views/usageType/UsageTypeList.vue  fetchList() / handleSubmit() / handleDelete()
 ↓
api/usageType.js  getUsageTypes() / createUsageType() / updateUsageType() / deleteUsageType()
 ↓
UsageTypeController → UsageTypeServiceImpl → UsageTypeMapper → usage_type テーブル
```

単価マスタ:

```
views/unitPrice/UnitPriceList.vue  fetchList() / handleSubmit() / handleDelete()
 ↓
api/unitPrice.js  getUnitPrices() / createUnitPrice() / updateUnitPrice() / deleteUnitPrice()
api/region.js getRegions() / api/dormitory.js getDormitories() + getRoomsByDormitory() / api/usageType.js getUsageTypes()（ダイアログ選択肢）
 ↓
UnitPriceController → UnitPriceServiceImpl → UnitPriceMapper → unit_price テーブル
（参照整合：RegionMapper / DormitoryMapper / RoomMapper / UsageTypeMapper）
```

---

## 8. 共通実装パターン（一覧画面）

一覧系 View は以下の定型構成を持つ（`DormitoryList` / `ResidenceHistoryList` / `DormFeeList` / `EquipmentMasterList` / `EquipmentStorage` / `VacancyList` / `OperationLogList` / `FirstUseLongTerm`）:

- `query`（検索条件 reactive）/ `pagination`（page・size・total）/ `loading`
- `fetchList()` → `api.get*()` → `normalizePageResponse()` → `tableData` / `pagination.total`
- UI: `el-card.search-card` → `toolbar-card` → `table-card`（`el-table.data-table` + `el-pagination`）
- 一覧テーブル（`.table-card .data-table`）は横スクロールバーを表示しない（`assets/styles/index.css`）
- ロール制御は `userStore.isAdmin` でボタン表示を切替

> 共通化候補（未実装）: `composables/useTableList.js`（または `usePagination`）を新設し、上記の重複ロジックを集約することを推奨。`composables/` フォルダは現状未作成。

---

## 9. 更新ルール

新規追加・変更時は、本ファイルの該当セクションを必ず更新すること。

- Views: §2 にページ構造（ルートパス・ロール・概要）を追記
- Components: §3 に Props / Slots / Events を記載
- Api: §4 にパラメータ・エンドポイントを記載
- Store: §5 に state / getter / action を記載
- Utils: §6 に関数・定数を記載
- 呼び出しチェーン: §7 に `View → Api → (Store) → Backend` を追記
