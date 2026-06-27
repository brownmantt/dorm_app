import XLSX from 'xlsx'
import { mkdirSync } from 'fs'
import { dirname, join } from 'path'
import { fileURLToPath } from 'url'

const __dirname = dirname(fileURLToPath(import.meta.url))
const outDir = __dirname
mkdirSync(outDir, { recursive: true })
const outFile = join(outDir, 'UIレビュー結果_20260530.xlsx')

const summary = [
  ['項目', '内容'],
  ['設計書バージョン', '詳細設計書 v1.0（dom-dev/doc/詳細設計書_0522/）'],
  ['対象', '全画面 SC-01〜SC-14 + 認証'],
  ['設計に準拠しているか', 'いいえ'],
  ['受け入れ判定', '否']
]

const ok = [
  ['画面ID', '画面名', '設計要件', '実装状況', '根拠ファイル'],
  ['SC-01', 'ダッシュボード', '空き室サマリ・長期利用警告件数', '実装済', 'views/dashboard/Dashboard.vue'],
  ['SC-02', '寮一覧', 'F-01 検索・一覧・CRUD', '実装済', 'views/dormitory/DormitoryList.vue'],
  ['SC-03', '寮詳細', '寮情報+部屋一覧+空き状況', '実装済', 'views/dormitory/DormitoryDetail.vue'],
  ['SC-04', '部屋編集', 'F-02 部屋更新（専用画面）', 'RoomEdit.vue 実装あり', 'views/dormitory/RoomEdit.vue'],
  ['SC-05', '入居履歴一覧', 'F-03 検索（社員・寮・期間）', '実装済', 'views/residence/ResidenceHistoryList.vue'],
  ['SC-06', '入居登録・退寮', '入居/退寮フォーム・validate/create/checkout API', '一部実装', 'views/residence/ResidenceRegister.vue'],
  ['SC-07', '初回利用日・長期利用', 'F-04/F-05 照会・警告一覧', '実装済', 'views/residence/FirstUseLongTerm.vue'],
  ['SC-08', '寮費一覧・算定', 'F-06 一覧・算定・登録・確定', '実装済', 'views/dormFee/DormFeeList.vue'],
  ['SC-09', '備品マスタ', 'F-07 一覧・登録', '一部実装', 'views/equipment/EquipmentMasterList.vue'],
  ['SC-10', '退去備品処理', 'F-08 退去備品処理', '実装済', 'views/equipment/MoveOutEquipment.vue'],
  ['SC-11', '備品保管', 'F-09 保管一覧・登録', '実装済', 'views/equipment/EquipmentStorage.vue'],
  ['SC-12', '空き室一覧', 'F-10 性別フィルタ・指定6列', '実装済', 'views/vacancy/VacancyList.vue'],
  ['SC-13', 'Excel取込', 'F-11 プレビュー→実行', '一部実装', 'views/import/ExcelImport.vue'],
  ['SC-14', '操作ログ', 'F-12 一覧・検索', '実装済', 'views/log/OperationLogList.vue'],
  ['—', '認証・権限', 'Bearer Token / ROLE_ADMIN・USER', '実装済', 'views/login/Login.vue, router/index.js']
]

const ng = [
  ['画面ID', '欠落項目', '設計根拠', '内容', '根拠ファイル'],
  ['SC-03→SC-04', '画面遷移', '04_画面設計 4.2', 'SC-03 から SC-04 への遷移リンクなし（部屋編集は SC-03 ダイアログで代替）', 'views/dormitory/DormitoryDetail.vue'],
  ['SC-06', '社員ID 検索選択', '04_画面設計 4.3', '手入力のみ。社員マスタ検索選択 UI 未実装', 'views/residence/ResidenceRegister.vue L9-10'],
  ['SC-06', '部屋の寮連動', '04_画面設計 4.3 備考', '選択寮に属さない部屋も選択可能（dormitoryId 未連動）', 'views/residence/ResidenceRegister.vue L151-161'],
  ['SC-06', '登録前業務検証', '04_画面設計 4.3', '登録ボタンが validate API を経由せず create を直接呼ぶ', 'views/residence/ResidenceRegister.vue L183-201'],
  ['SC-09', '備品編集保存', '10_備品管理 業務ロジック', '編集時も createEquipment(POST) のみ。更新処理なし', 'views/equipment/EquipmentMasterList.vue L100-104'],
  ['SC-13', '10MB 上限検証', '12_Excel取込 7.6.1', '表示文言のみ。クライアント側サイズ検証なし', 'views/import/ExcelImport.vue L16-17']
]

const proposal = [
  ['項目', '内容'],
  ['設計書矛盾 F-07', 'API設計は GET/POST のみだが業務ロジックは「標準 CRUD + 論理削除」。更新・削除の要否を設計書で明確化推奨（10_備品管理.md）'],
  ['SC-04 二重実装', 'SC-03 ダイアログと SC-04 専用画面が重複。保守性・遷移整合の整理を推奨'],
  ['SC-06 退寮対象選択', '入居履歴 ID 手入力のみ。SC-05 からの選択導線は設計未記載のため提案（UX）'],
  ['SC-01 警告件数', 'ダッシュボードは直近10件表示。カード件数が API total と一致しない可能性']
]

const wb = XLSX.utils.book_new()
XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(summary), 'サマリー')
XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(ok), '適合項目')
XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(ng), '欠落項目')
XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(proposal), '提案項目')
XLSX.writeFile(wb, outFile)
console.log(outFile)
