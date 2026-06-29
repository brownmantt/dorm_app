# func_server.md（バックエンド機能定義書）

寮管理システム バックエンド（`dom-server`）の機能インベントリ・呼び出しチェーン定義書。
新規実装・変更の前に本ファイルを全文確認し、重複・類似・再利用可能な機能がないかをチェックすること。
新規追加・変更を行った場合は、本ファイルを必ず更新すること。

> プロジェクト: `dom-server`  
> ベース URL: `/api/v1`  
> 最終更新: 2026-06-07

---

## API 一覧（60 エンドポイント）

| # | メソッド | URL | Controller | Service |
|---|---------|-----|------------|---------|
| 1 | POST | `/auth/login` | AuthController | AuthService |
| 2 | GET | `/employees` | EmployeeController | EmployeeService |
| 3 | GET | `/employees/{empId}` | EmployeeController | EmployeeService |
| 4 | POST | `/employees` | EmployeeController | EmployeeService |
| 5 | PUT | `/employees/{empId}` | EmployeeController | EmployeeService |
| 6 | DELETE | `/employees/{empId}` | EmployeeController | EmployeeService |
| 7 | GET | `/employees/{empId}/first-use-date` | EmployeeController | EmployeeService |
| 8 | GET | `/employees/{empId}/total-usage-days` | EmployeeController | EmployeeService |
| 9 | GET | `/dormitories` | DormitoryController | DormitoryService |
| 10 | GET | `/dormitories/{id}` | DormitoryController | DormitoryService |
| 11 | POST | `/dormitories` | DormitoryController | DormitoryService |
| 12 | PUT | `/dormitories/{id}` | DormitoryController | DormitoryService |
| 13 | DELETE | `/dormitories/{id}` | DormitoryController | DormitoryService |
| 14 | GET | `/dormitories/{dormId}/rooms` | DormitoryController | DormitoryService |
| 15 | POST | `/rooms` | RoomController | DormitoryService |
| 16 | PUT | `/rooms/{id}` | RoomController | DormitoryService |
| 17 | DELETE | `/rooms/{id}` | RoomController | DormitoryService |
| 18 | GET | `/residences` | ResidenceController | ResidenceService |
| 19 | POST | `/residences` | ResidenceController | ResidenceService |
| 20 | POST | `/residences/validate` | ResidenceController | ResidenceService |
| 21 | PUT | `/residences/{id}/checkout` | ResidenceController | ResidenceService |
| 22 | GET | `/alerts/long-term-usage` | AlertController | VacancyService |
| 23 | GET | `/dorm-fees` | DormFeeController | DormFeeService |
| 24 | POST | `/dorm-fees/calculate` | DormFeeController | DormFeeService |
| 25 | GET | `/equipments` | EquipmentController | EquipmentService |
| 26 | POST | `/equipments` | EquipmentController | EquipmentService |
| 27 | PUT | `/equipments/{id}` | EquipmentController | EquipmentService |
| 28 | DELETE | `/equipments/{id}` | EquipmentController | EquipmentService |
| 30 | POST | `/equipment-moveouts` | EquipmentController | EquipmentService |
| 31 | GET | `/equipment-storages` | EquipmentController | EquipmentService |
| 32 | POST | `/equipment-storages` | EquipmentController | EquipmentService |
| 33 | GET | `/equipment-assets` | EquipmentAssetController | EquipmentAssetService |
| 34 | POST | `/equipment-assets` | EquipmentAssetController | EquipmentAssetService |
| 35 | PUT | `/equipment-assets/{id}` | EquipmentAssetController | EquipmentAssetService |
| 36 | DELETE | `/equipment-assets/{id}` | EquipmentAssetController | EquipmentAssetService |
| 37 | GET | `/vacancies` | VacancyController | VacancyService |
| 34 | GET | `/vacancies/assignable` | VacancyController | VacancyService |
| 35 | POST | `/imports/excel/preview` | ImportController | ExcelImportService |
| 36 | POST | `/imports/excel/execute` | ImportController | ExcelImportService |
| 37 | GET | `/operation-logs` | OperationLogController | OperationLogService |
| 38 | GET | `/postal-codes/{postalCode}/address` | PostalCodeController | PostalCodeService |
| 39 | GET | `/affiliations` | AffiliationController | AffiliationService |
| 40 | POST | `/affiliations` | AffiliationController | AffiliationService |
| 41 | PUT | `/affiliations/{id}` | AffiliationController | AffiliationService |
| 42 | DELETE | `/affiliations/{id}` | AffiliationController | AffiliationService |
| 43 | GET | `/dorm-allocation` | DormAllocationController | DormAllocationService |
| 44 | GET | `/dorm-allocation/print` | DormAllocationController | DormAllocationService |
| 45 | GET | `/alerts/move-out` | AlertController | DormAllocationService |
| 46 | GET | `/exports/csv/residences` | ExportController | CsvExportService |
| 47 | GET | `/exports/csv/dorm-fees` | ExportController | CsvExportService |
| 48 | GET | `/dormitories/{dormId}/manager` | DormitoryController | DormitoryService |
| 49 | PUT | `/dormitories/{dormId}/manager` | DormitoryController | DormitoryService |
| 50 | DELETE | `/dormitories/{dormId}/manager` | DormitoryController | DormitoryService |
| 51 | GET | `/regions` | RegionController | RegionService |
| 52 | POST | `/regions` | RegionController | RegionService |
| 53 | PUT | `/regions/{id}` | RegionController | RegionService |
| 54 | DELETE | `/regions/{id}` | RegionController | RegionService |
| 55 | GET | `/usage-types` | UsageTypeController | UsageTypeService |
| 56 | POST | `/usage-types` | UsageTypeController | UsageTypeService |
| 57 | PUT | `/usage-types/{id}` | UsageTypeController | UsageTypeService |
| 58 | DELETE | `/usage-types/{id}` | UsageTypeController | UsageTypeService |
| 59 | GET | `/unit-prices` | UnitPriceController | UnitPriceService |
| 60 | POST | `/unit-prices` | UnitPriceController | UnitPriceService |
| 61 | PUT | `/unit-prices/{id}` | UnitPriceController | UnitPriceService |
| 62 | DELETE | `/unit-prices/{id}` | UnitPriceController | UnitPriceService |

---

## Controller 層

### AuthController
- `POST /auth/login` — ユーザー名・パスワード認証、トークン返却

### EmployeeController
- `GET /employees` — 社員一覧（キーワード・性別・区分・所属・`notResidingOnly`・`targetYearMonth`・`dormitoryId`・`roomId`・ページング）。`notResidingOnly=true` 時は現在未入居の社員のみ、`updated_at` 降順。寮費算定コンボは `dormFeeComboSort=true` と `targetYearMonth` で対象月入居者を絞込
- `GET /employees/{empId}` — 社員詳細
- `POST /employees` — 社員新規登録（管理者）
- `PUT /employees/{empId}` — 社員更新（管理者）
- `DELETE /employees/{empId}` — 社員論理削除（入居履歴参照時は不可）
- `GET /employees/{empId}/first-use-date` — 初回利用日・入居者区分
- `GET /employees/{empId}/total-usage-days` — 通算利用日数

### DormitoryController
- 寮 CRUD + `GET /dormitories/{dormId}/rooms` 部屋一覧
- 寮登録・更新時 `postalCode`（7桁）必須
- `GET/PUT/DELETE /dormitories/{dormId}/manager` で責任者を取得・設定・解除

### PostalCodeController
- `GET /postal-codes/{postalCode}/address` — 郵便番号から住所検索（zipcloud 連携）

### RoomController
- 部屋登録・更新・削除

### ResidenceController
- 入居履歴一覧、入居登録、業務検証、退寮処理

### AlertController
- 長期利用警告一覧（閾値 10 年）
- 退寮予定警告一覧（閾値 14 日）

### AffiliationController
- 所属マスタ CRUD（一覧・登録・更新・論理削除）

### RegionController
- 地域マスタ CRUD（一覧・登録・更新・論理削除）
- 寮マスタ `region` 列は地域コード（`code`）と連携

### UsageTypeController
- 利用形態マスタ CRUD（一覧・登録・更新・論理削除）

### UnitPriceController
- 単価マスタ CRUD（一覧・登録・更新・論理削除）
- 地域・寮・部屋・利用形態のマスタ参照整合を業務層で検証

### DormAllocationController
- 寮割カレンダー取得、印刷用データ取得
- クエリ: `yearMonth`, `regions`, `dormitoryId`, `genderType`, `name`（入居者氏名）

### ExportController
- 入居履歴 CSV / 寮費 CSV エクスポート

### DormFeeController
- 寮費一覧、算定（入居履歴×単価マスタから算出・`dorm_fee` 保存）

### EquipmentController
- 品目マスタ CRUD（品目ID・品目名称・論理削除）、退去備品処理、備品保管一覧・登録

### EquipmentAssetController
- 備品（個体）一覧・登録・更新・削除（論理削除）
- 備品番号は `EB` プレフィックスで自動採番

### VacancyController
- 空き室一覧、割当可能部屋一覧

### ImportController
- Excel プレビュー・実行（スタブ）

### OperationLogController
- 操作ログ一覧

---

## Service 層

### AuthService / AuthServiceImpl
- 設定ファイル（`dom.auth.users`）による認証

### DormitoryService / DormitoryServiceImpl
- 寮 CRUD、部屋一覧・登録・更新・削除
- ID プレフィックス: `D`（寮）、`R`（部屋）
- `postal_code` カラムを永続化
- `nearest_station_1` / `nearest_station_2` / `nearest_station_3`（各 VARCHAR(20)、NULL 可）を永続化
- `dormitoryId` / `region` / `genderType` / `address` 条件付き一覧検索
- 寮責任者の取得・設定・解除（現入居者チェック）
- 寮・部屋削除時は現在有効入居者の有無を検証（`DORMITORY_IN_USE` / `ROOM_IN_USE`）

### AffiliationService / AffiliationServiceImpl
- 所属一覧（`name` 部分一致、`code` は固定で null）
- 所属登録・更新・論理削除
- 所属コード重複チェック、使用中所属削除禁止（`AFFILIATION_IN_USE`）
- ID プレフィックス: `AF`

### RegionService / RegionServiceImpl
- 地域一覧（`name` 部分一致）
- 地域登録・更新・論理削除
- 地域コード重複チェック、使用中地域削除禁止（`REGION_IN_USE`：寮 `region` 列参照）
- ID プレフィックス: `RG`

### UsageTypeService / UsageTypeServiceImpl
- 利用形態一覧（`name` 部分一致）
- 利用形態登録・更新・論理削除
- コード値重複チェック（`USAGE_TYPE_CODE_DUPLICATE`）
- 最小利用日数未指定時は 1、最大利用日数未指定時は -1（制限なし）
- 最小利用日数は 1 以上、最大利用日数は 1 以上または -1。min ≤ max（max ≠ -1 時）
- ID プレフィックス: `UT`

### UnitPriceService / UnitPriceServiceImpl
- 単価一覧（コード部分一致・地域・寮・利用形態で絞込、名称は JOIN 表示）
- 単価登録・更新・論理削除
- 登録時単価コード自動採番（プレフィックス `UC`）、更新時は既存コードを維持
- 寮・部屋は任意
- 地域/寮/部屋/利用形態の存在・整合検証（部屋指定時は寮必須）
- ID プレフィックス: `UP`

### DormAllocationService / DormAllocationServiceImpl
- 寮割カレンダー生成、印刷 VO 生成
- 退寮予定警告一覧（`PageResult<MoveOutWarningVO>`）
- `DormitoryMapper.listForCalendar` / `ResidenceHistoryMapper.findForCalendar` / `DormAllocationHelper` を利用
- `buildRoomRows`: 部屋ごとに定員（`room.capacity`、未設定時 1）分の行を生成。入居者行の後に空きスロット行（`vacant=true`）を追加

### CsvExportService / CsvExportServiceImpl
- 入居履歴 CSV（UTF-8 BOM）生成
- 寮費 CSV（UTF-8 BOM）生成

### PostalCodeService / PostalCodeServiceImpl
- 郵便番号（7桁）正規化・バリデーション
- zipcloud API から都道府県・市区町村・町域を取得

### EmployeeService / EmployeeServiceImpl
- 社員一覧（条件検索・所属名 JOIN）、詳細、新規・更新・論理削除
- `notResidingOnly=true` 時：当日時点で有効入居履歴がない社員のみ、`employee.updated_at` 降順
- `nearest_station_1` / `nearest_station_2` / `nearest_station_3`（各 VARCHAR(20)、NULL 可）を永続化
- 社員ID一意・所属存在・入居履歴参照チェック
- 初回利用日、通算利用日数

### ResidenceService / ResidenceServiceImpl
- 入居履歴一覧（利用形態名称は `usage_type` JOIN）、業務検証（性別・定員・期間内最大同時入居数・**利用形態マスタ存在**）、入居登録（`usage_type_code` 永続化）、退寮
- 日本社員の初回利用日自動登録
- ID プレフィックス: `RH`

### VacancyService / VacancyServiceImpl
- 空き室一覧、割当可能部屋、長期利用警告

### DormFeeService / DormFeeServiceImpl
- 寮費一覧、入居履歴×単価マスタによる算定・保存（日単価×利用日数）
- 入居履歴は対象月と重なる入居中データを抽出
- 単価は A（地域・利用形態・寮・部屋）→ B（寮まで）→ C（地域のみ）の順で検索
- 利用日数は利用形態マスタの min/max **範囲内**であること（範囲外は ERROR）
- 算定成功時ステータス `PROVISIONAL`（仮定）、失敗時 `ERROR`（エラー）
- ID プレフィックス: `DF`

### EquipmentService / EquipmentServiceImpl
- 品目マスタ CRUD（品目ID・品目名称・論理削除。退去処理・保管参照時は削除不可）、退去備品処理、備品保管
- ID プレフィックス: `EQ`、`MO`、`ST`

### EquipmentAssetService / EquipmentAssetServiceImpl
- 備品（個体）CRUD（品目マスタ参照・備品番号自動採番・論理削除）
- 購入日・購入金額必須。購入店・連絡先・郵便番号・住所・保証期限は任意
- ID プレフィックス: `EB`

### OperationLogService / OperationLogServiceImpl
- 操作ログ一覧、`writeLog()` ヘルパー

### ExcelImportService / ExcelImportServiceImpl
- ファイル検証 + スタブプレビュー/実行
- ID プレフィックス: `JOB`

---

## 呼び出しチェーン

### 認証
```
AuthController.login()
  ↓
AuthServiceImpl.login()
  ↓
AuthProperties（application.yml）
```

### 寮一覧
```
DormitoryController.list()
  ↓
DormitoryServiceImpl.list()
  ↓
DormitoryMapper.searchList() / countSearch()
  ↓
dormitory テーブル
```

### 寮責任者設定
```
DormitoryController.assignManager()
  ↓
DormitoryServiceImpl.assignManager()
  ↓
ResidenceHistoryMapper.findById()（現入居者チェック）
DormitoryManagerMapper.upsert()
  ↓
dormitory_manager テーブル
```

### 社員マスタ登録（SC-16）
```
EmployeeController.create()
  ↓
EmployeeServiceImpl.create()
  ↓
EmployeeMapper.findById()（重複チェック）
AffiliationMapper.findById()（所属存在チェック）
EmployeeMapper.insert()
  ↓
employee テーブル
```

### 社員削除
```
EmployeeController.delete()
  ↓
EmployeeServiceImpl.delete()
  ↓
ResidenceHistoryMapper.countByEmployeeId()（参照チェック）
EmployeeMapper.logicalDelete()
  ↓
employee テーブル
```

### 所属削除
```
AffiliationController.delete()
  ↓
AffiliationServiceImpl.delete()
  ↓
AffiliationMapper.countEmployeesByAffiliationId()（使用中チェック）
AffiliationMapper.logicalDelete()
  ↓
affiliation テーブル
```

### 地域マスタ CRUD
```
RegionController.list() / create() / update() / delete()
  ↓
RegionServiceImpl
  ↓
RegionMapper.searchList() / insert() / update() / logicalDelete()
RegionMapper.countDormitoriesByRegionCode()（削除時）
  ↓
region テーブル
```

### 利用形態マスタ CRUD
```
UsageTypeController.list() / create() / update() / delete()
  ↓
UsageTypeServiceImpl
  ↓
UsageTypeMapper.searchList() / insert() / update() / logicalDelete()
  ↓
usage_type テーブル
```

### 単価マスタ CRUD
```
UnitPriceController.list() / create() / update() / delete()
  ↓
UnitPriceServiceImpl
  ↓
UnitPriceMapper.searchList() / insert() / update() / logicalDelete()
RegionMapper / DormitoryMapper / RoomMapper / UsageTypeMapper（参照検証）
  ↓
unit_price テーブル
```

### 寮割カレンダー
```
DormAllocationController.getCalendar()
  ↓
DormAllocationServiceImpl.buildCalendar()
  ↓
DormitoryMapper.listForCalendar()
ResidenceHistoryMapper.findForCalendar()
DormAllocationHelper（在籍日/重複/退寮警告計算）
  ↓
dormitory / residence_history / room / employee / affiliation テーブル
```

### CSV エクスポート
```
ExportController.exportResidencesCsv() / exportDormFeesCsv()
  ↓
CsvExportServiceImpl
  ↓
ResidenceHistoryMapper.searchList() / DormFeeMapper.searchList()
  ↓
residence_history / dorm_fee テーブル
```

### 部屋登録
```
RoomController.create()
  ↓
DormitoryServiceImpl.createRoom()
  ↓
RoomMapper.insert()
  ↓
room テーブル
```

### 入居登録
```
ResidenceController.create()
  ↓
ResidenceServiceImpl.create()
  ↓ doValidate()
  │   EmployeeMapper.findById()
  │   DormitoryMapper.findById()
  │   RoomMapper.findById()
  │   UsageTypeMapper.findByCode()
  │   ResidenceHistoryMapper.findOverlappingInPeriod() → 期間内最大同時入居数と room.capacity 比較
  ↓
ResidenceHistoryMapper.insert()（usage_type_code 含む）
EmployeeFirstDormUseMapper.insert()（日本社員・初回のみ）
  ↓
residence_history / employee_first_dorm_use テーブル
```

### 寮費算定
```
DormFeeController.calculate()
  ↓
DormFeeServiceImpl.calculate()
  ↓
ResidenceHistoryMapper.findForDormFeeCalculation()
UnitPriceMapper.findRoomLevelMatch() / findDormitoryLevelMatch() / findRegionLevelMatch()
UsageTypeMapper.findByCode()
  ↓
residence_history / dormitory / room / unit_price / usage_type テーブル
```

### 空き室一覧
```
VacancyController.list()
  ↓
VacancyServiceImpl.listVacancies()
  ↓
RoomMapper.searchVacancyList() / countVacancyList()
  ↓
VacancyListView（dormitoryId, dormitoryName, roomId, roomName, status, residentName, residenceHistoryId, …）
  ↓
room / residence_history / dormitory / employee テーブル
```

### 操作ログ
```
OperationLogController.list()
  ↓
OperationLogServiceImpl.list()
  ↓
OperationLogMapper.searchList() / countSearch()
  ↓
operation_log テーブル
```

### Excel 取込
```
ImportController.preview() / execute()
  ↓
ExcelImportServiceImpl
  ↓
ExcelImportMapper.insertJob()
  ↓
excel_import_job テーブル
```

---

## Mapper 層

| Mapper | 対象テーブル |
|--------|-------------|
| DormitoryMapper | dormitory |
| RoomMapper | room |
| EmployeeMapper | employee |
| ResidenceHistoryMapper | residence_history |
| AffiliationMapper | affiliation |
| RegionMapper | region |
| UsageTypeMapper | usage_type |
| UnitPriceMapper | unit_price |
| DormitoryManagerMapper | dormitory_manager |
| EmployeeFirstDormUseMapper | employee_first_dorm_use |
| DormFeeMapper | dorm_fee |
| FeeRateConfigMapper | fee_rate_config |
| EquipmentMapper | equipment |
| EquipmentAssetMapper | equipment_asset |
| EquipmentMoveoutMapper | equipment_moveout |
| EquipmentStorageMapper | equipment_storage |
| OperationLogMapper | operation_log |
| ExcelImportMapper | excel_import_job, excel_import_error |

---

## 共通コンポーネント

| クラス | 説明 |
|--------|------|
| PageResult | ページング応答（content / totalElements / totalPages） |
| PageUtils | offset / limit 計算 |
| IdGenerator | 業務 ID 生成（D/R/RH/DF/EQ/EB/MO/ST/JOB） |
| JsonUtils | JSON シリアライズ（basisDetail 等） |
| DormAllocationHelper | 寮割カレンダー日付計算・重複判定ヘルパー |
| GlobalExceptionHandler | 業務例外・検証例外を統一処理 |
| BusinessException | 業務エラーコード + HTTP ステータス |
| SecurityUtils | 認証コンテキストから操作者名取得 |
| OperationLogWriter | 操作ログ書き込みヘルパー（各 Service から利用） |
| AppConstants | ロール・閾値・ページング等の定数 |
| AuthProperties | `application.yml` の `dom.auth.users` 設定 |

---

## Enum 層

| Enum | 値 | 用途 |
|------|-----|------|
| DormFeeStatusEnum | PROVISIONAL / ERROR | 寮費ステータス（仮定 / エラー） |
| EquipmentStorageStatusEnum | IN_STORAGE / REUSED | 備品保管ステータス |
| EmployeeCategoryEnum | JAPAN / CHINA_ASSIGN | 社員区分 |
| ExcelImportJobStatusEnum | PENDING / PREVIEWED / SUCCESS / FAILED | Excel 取込ジョブ |
| GenderEnum | MALE / FEMALE | 性別・寮種別判定 |
| RegionEnum | TOKYO / OSAKA / NAGOYA / OTHER | 寮地域分類（**レガシー**。地域マスタ `region.code` を正とする） |
| OperationTypeEnum | LOGIN / DORMITORY_CREATE / … | 操作ログ種別 |
| TargetTableEnum | dormitory / room / … | 操作ログ対象テーブル |

---

## 認証・権限

- 認証方式: Bearer トークン（`Authorization: Bearer {token}`）
- トークン形式: Base64(`username:uuid`) — `AuthServiceImpl.login()` で発行
- ロール: `ROLE_ADMIN` / `ROLE_USER`（`application.yml` の `dom.auth.users`）
- 設定クラス: `SecurityConfig` / `BearerTokenAuthenticationFilter`

### API 権限マトリクス

| 権限 | 対象 API |
|------|----------|
| 公開 | `POST /auth/login` |
| ROLE_ADMIN + ROLE_USER | GET `/employees/**`, `/dormitories/**`, `/dorm-allocation/**`, `/affiliations`, `/regions`, `/usage-types`, `/postal-codes/**`, `/residences`, `/alerts/**`, `/vacancies/**` |
| ROLE_ADMIN のみ | `/dorm-fees/**`, `/unit-prices/**`, `/equipments/**`, `/equipment-assets/**`, `/equipment-moveouts/**`, `/equipment-storages/**`, `/operation-logs/**`, `/imports/**`, `/exports/**`, POST/PUT `/residences/**`, POST/PUT/DELETE `/dormitories/**`, `/rooms/**`, POST/PUT/DELETE `/affiliations/**`, POST/PUT/DELETE `/regions/**`, POST/PUT/DELETE `/usage-types/**`, PUT/DELETE `/dormitories/*/manager` |

---

## 操作ログ連携

変更系 Service メソッドから `OperationLogWriter` 経由で `OperationLogService.writeLog()` を呼び出す。

| Service | 記録タイミング |
|---------|----------------|
| AuthServiceImpl | ログイン成功 |
| DormitoryServiceImpl | 寮 CRUD、部屋 CRUD |
| ResidenceServiceImpl | 入居登録、退寮 |
| DormFeeServiceImpl | 寮費算定・一覧 |
| EquipmentServiceImpl | 品目マスタ CRUD、退去処理、保管登録 |
| EquipmentAssetServiceImpl | 備品（個体）CRUD |
| ExcelImportServiceImpl | 取込実行 |

---

## DTO / VO / View

| 種別 | パッケージ | 例 |
|------|-----------|-----|
| DTO | `entity.dto` | LoginDTO, ResidenceSaveDTO, AffiliationSaveDTO, RegionSaveDTO, UsageTypeSaveDTO, UnitPriceSaveDTO, DormitoryManagerSaveDTO |
| VO | `entity.vo` | LoginVO, ValidateVO, DormFeeCalculateVO, DormAllocationCalendarVO, MoveOutWarningVO |
| View | `entity.view` | RoomListView, VacancyListView, AllocationResidenceView, UnitPriceListView, EquipmentAssetListView（一覧 JOIN 結果） |
