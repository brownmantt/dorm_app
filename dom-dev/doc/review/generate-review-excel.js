const XLSX = require('xlsx')
const path = require('path')

const outPath = path.join(__dirname, 'UIレビュー結果_20260630.xlsx')

const summary = [
  ['項目', '内容'],
  ['設計書バージョン', 'v2.7（10_備品管理.md）/ README v2.6'],
  ['レビュー日', '2026-06-30'],
  ['対象', 'dom-dev/src/views 全画面（SC-01〜SC-22 相当）'],
  ['照合基準', 'document/0522_詳細設計書/ 分割版 + APIdoc'],
  ['設計準拠', 'いいえ'],
  ['受け入れ判定', '否'],
  ['欠落項目数', '4'],
  ['適合項目数（代表）', '40+'],
  ['備考', 'Must 画面の欠落 4 件を解消後に再レビュー推奨']
]

const compliant = [
  ['画面ID', '画面名', '項目', '実装ファイル', '備考'],
  ['SC-01', '寮割カレンダー', '地域フィルタ・月切替・氏名検索・印刷', 'DormAllocationCalendar.vue', ''],
  ['SC-01', '寮割カレンダー', '黄色在籍セル（cell-occupied）', 'index.css', ''],
  ['SC-01', '寮割カレンダー', '重複日赤背景＋ツールチップ', 'DormAllocationCalendar.vue', 'cell-conflict / dayCellTitle'],
  ['SC-01', '寮割カレンダー', '退寮日赤文字（対象月内）', 'DormAllocationCalendar.vue', 'text-move-out-month'],
  ['SC-01', '寮割カレンダー', '責任者★表示・所属列', 'DormAllocationCalendar.vue', ''],
  ['SC-01', '寮割カレンダー', '行クリック→入居登録/退居', 'DormAllocationCalendar.vue', 'goRegister / goCheckout'],
  ['SC-02', '寮一覧', '検索（寮・地域・種別・住所）', 'DormitoryList.vue', ''],
  ['SC-02', '寮一覧', '新規登録・一括削除・ページネーション同一行', 'DormitoryList.vue', 'table-card-toolbar'],
  ['SC-02', '寮一覧', '登録ダイアログ：郵便番号blur住所自動入力', 'DormitoryFormDialog.vue', ''],
  ['SC-03', '寮詳細', '責任者設定（現入居者選択・★表示）', 'DormitoryDetail.vue', ''],
  ['SC-04', '部屋編集', '部屋CRUD', 'RoomEdit.vue / DormitoryDetail.vue', ''],
  ['SC-05', '入居履歴一覧', '検索（社員・寮・期間）', 'ResidenceHistoryList.vue', '氏名・社員ID・寮名称・入居日範囲'],
  ['SC-05', '入居履歴一覧', '一覧列（利用形態・退寮理由含む）', 'ResidenceHistoryList.vue', ''],
  ['SC-06', '入居登録・退居', '業務検証 API 呼出', 'ResidenceRegister.vue', 'validateResidence'],
  ['SC-06', '入居登録・退居', '退居処理（退居日・退寮理由）', 'ResidenceRegister.vue', ''],
  ['SC-07', '初回利用日・長期利用', '社員照会・長期利用警告一覧', 'FirstUseLongTerm.vue', ''],
  ['SC-08', '寮費一覧・算定', '一覧列（地域寮部屋2行・退居日等）', 'DormFeeList.vue', ''],
  ['SC-08', '寮費一覧・算定', '算定ダイアログ連動・寮費算定ボタン', 'DormFeeList.vue', 'table-card-toolbar'],
  ['SC-09', '品目マスタ', 'CRUD・検索', 'EquipmentMasterList.vue', ''],
  ['SC-12', '空き室一覧', '性別フィルタ・一覧列', 'VacancyList.vue', ''],
  ['SC-13', 'Excel取込', 'プレビュー→実行2段階', 'ExcelImport.vue', ''],
  ['SC-13', 'Excel取込', 'CSVエクスポート（入居履歴・寮費）', 'ExcelImport.vue', ''],
  ['SC-14', '操作ログ', '検索（操作種別・操作者・操作日範囲）', 'OperationLogList.vue', 'APIdoc準拠'],
  ['SC-14', '操作ログ', '一覧列', 'OperationLogList.vue', ''],
  ['SC-15', '所属マスタ', '所属コード・名称・表示順 CRUD', 'AffiliationList.vue', ''],
  ['SC-15', '所属マスタ', '新規登録・ページネーション同一行', 'AffiliationList.vue', ''],
  ['SC-16', '社員マスタ', '検索・一覧・登録項目', 'EmployeeList.vue', ''],
  ['SC-17', '地域マスタ', 'CRUD', 'RegionList.vue', ''],
  ['SC-18', '利用形態マスタ', 'min/max利用日数', 'UsageTypeList.vue', ''],
  ['SC-19', '単価マスタ', '地域・寮・部屋・利用形態・日単価', 'UnitPriceList.vue', ''],
  ['SC-20', '備品管理', '登録項目・一覧列（連絡先含む）', 'EquipmentAssetList.vue', ''],
  ['SC-20', '備品管理', '品目コンボ検索', 'EquipmentAssetList.vue', ''],
  ['SC-21', '備品利用', '検索5条件＋利用中のみ', 'EquipmentUsageList.vue', ''],
  ['SC-21', '備品利用', '利用登録・利用解除', 'EquipmentUsageList.vue', ''],
  ['SC-11', '備品保管', '検索・一覧・保管登録ツールバー', 'EquipmentStorage.vue', ''],
  ['SC-11', '備品保管', '複数保管場所・数量整合ヒント', 'EquipmentStorage.vue', ''],
  ['SC-11', '備品保管', '購入数量1固定・行追加/削除条件', 'EquipmentStorage.vue', ''],
  ['SC-22', '保管場所マスタ', '検索・CRUD・ID表示', 'StorageLocationList.vue', '']
]

const missing = [
  ['画面ID', '画面名', '欠落項目', '設計根拠', '実装状況'],
  ['SC-15', '所属マスタ', '有効フラグ（チェックボックス・必須）', '21_責任者・所属管理.md SC-15', 'AffiliationList.vue ダイアログに未実装'],
  ['SC-06', '入居登録・退居', '所属（選択・必須）', '08_入退寮管理.md SC-06', 'ResidenceRegister.vue 入居登録タブに未実装'],
  ['SC-21', '備品利用・解除', '一覧列：利用ID', '10_備品管理.md SC-21 一覧表示列', 'EquipmentUsageList.vue テーブルに列なし'],
  ['SC-13', 'Excel/CSV取込・出力', 'エクスポートタブ', '12_Excel取込.md 画面設計', 'ExcelImport.vue は同一ページ内ボタン配置のみ']
]

const proposals = [
  ['区分', '内容', '備考'],
  ['設計矛盾', 'SC-15「有効フラグ」と affiliation テーブル（deleted_at のみ）の整合', 'UI実装時に設計書かDBのどちらを正とするか要確認'],
  ['設計版数', 'README v2.6 と 10_備品管理 v2.7 の版数不一致', '文書管理の整理推奨'],
  ['UX', '操作ログ・入居履歴のページネーションがテーブル下部', '他画面は table-card-toolbar 上部配置。設計未規定のため提案のみ'],
  ['UX', '備品利用検索が1行に項目多数', '横スクロール発生の可能性。設計未規定']
]

function sheetFromRows(rows) {
  const ws = XLSX.utils.aoa_to_sheet(rows)
  ws['!cols'] = rows[0].map((_, i) => ({ wch: i === 0 ? 12 : 36 }))
  return ws
}

const wb = XLSX.utils.book_new()
XLSX.utils.book_append_sheet(wb, sheetFromRows(summary), 'サマリー')
XLSX.utils.book_append_sheet(wb, sheetFromRows(compliant), '適合項目')
XLSX.utils.book_append_sheet(wb, sheetFromRows(missing), '欠落項目')
XLSX.utils.book_append_sheet(wb, sheetFromRows(proposals), '提案項目')

XLSX.writeFile(wb, outPath)
console.log('Written:', outPath)
