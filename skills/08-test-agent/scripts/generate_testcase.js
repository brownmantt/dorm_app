#!/usr/bin/env node
/**
 * 08-test-agent: JSON ドラフトから Excel + Markdown テストケースを生成
 *
 * Usage:
 *   node generate_testcase.js --input testcase/_draft.json --project MyProject --output-dir testcase
 *   node generate_testcase.js --input testcase/_draft.json --project MyProject --random 67890
 */

const fs = require('fs');
const path = require('path');

const HEADERS = [
  '所属モジュール',
  '関連要求',
  'テストケース名',
  '前提条件',
  'テストタイプ',
  'テスト手順',
  '期待結果',
  '優先度',
];

const OP_TYPES = '遷移|入力|クリック|待機|確認|URL確認|DB|選択|撮影';

const COL_WIDTHS = [18, 25, 40, 35, 14, 60, 50, 8];

function parseArgs(argv) {
  const args = { input: '', project: 'Project', outputDir: 'testcase', random: '' };
  for (let i = 2; i < argv.length; i++) {
    const key = argv[i];
    const val = argv[i + 1];
    if (key === '--input' && val) { args.input = val; i++; }
    else if (key === '--project' && val) { args.project = val; i++; }
    else if (key === '--output-dir' && val) { args.outputDir = val; i++; }
    else if (key === '--random' && val) { args.random = val; i++; }
  }
  return args;
}

function resolveXlsx() {
  const candidates = [
    path.join(process.cwd(), '.trae', 'test', 'node_modules', 'xlsx'),
    path.join(__dirname, '..', '..', '..', '.trae', 'test', 'node_modules', 'xlsx'),
    'xlsx',
  ];
  for (const p of candidates) {
    try {
      return require(p);
    } catch {
      /* try next */
    }
  }
  throw new Error(
    'xlsx パッケージが見つかりません。以下を実行してください:\n' +
    '  cd .trae/test && npm install xlsx'
  );
}

function random5() {
  return String(Math.floor(10000 + Math.random() * 90000));
}

function toMdCell(text) {
  if (!text) return '';
  return String(text).replace(/\r\n/g, '\n').replace(/\n/g, '<br>');
}

function validateCase(c, index) {
  const required = ['module', 'requirement', 'name', 'preconditions', 'type', 'steps', 'expected', 'priority'];
  for (const field of required) {
    if (!c[field] && c[field] !== 0) {
      throw new Error(`cases[${index}]: "${field}" が未設定です`);
    }
  }
  if (!/^\[.+?\]/.test(String(c.steps).split('\n')[0]?.replace(/^\d+\.\s*/, ''))) {
    const first = String(c.steps).split('\n')[0];
    if (first && !new RegExp(`\\[(${OP_TYPES})\\]`).test(first)) {
      console.warn(`警告 cases[${index}]: ステップに [操作タイプ]（${OP_TYPES}）が含まれていない可能性があります`);
    }
  }
}

function casesToRows(cases) {
  return cases.map((c) => [
    c.module,
    c.requirement,
    c.name,
    c.preconditions,
    c.type,
    c.steps,
    c.expected,
    c.priority,
  ]);
}

function writeExcel(xlsx, outPath, rows) {
  const ws = xlsx.utils.aoa_to_sheet([HEADERS, ...rows]);
  ws['!cols'] = COL_WIDTHS.map((w) => ({ wch: w }));
  const wb = xlsx.utils.book_new();
  xlsx.utils.book_append_sheet(wb, ws, 'テストケース');
  xlsx.writeFile(wb, outPath);
}

function writeMarkdown(outPath, projectName, randomNum, rows) {
  const now = new Date();
  const ts = now.toISOString().slice(0, 16).replace('T', ' ');
  const header = `# ${projectName} テストケース

**生成日時：** ${ts}
**ファイル：** ${projectName}_${randomNum}
**総件数：** ${rows.length}件

`;

  const mdHeader = '| ' + HEADERS.join(' | ') + ' |';
  const mdSep = '| ' + HEADERS.map(() => '---').join(' | ') + ' |';
  const mdRows = rows.map((row) =>
    '| ' + row.map((cell) => toMdCell(cell)).join(' | ') + ' |'
  );

  fs.writeFileSync(outPath, header + mdHeader + '\n' + mdSep + '\n' + mdRows.join('\n') + '\n', 'utf8');
}

function main() {
  const args = parseArgs(process.argv);

  if (!args.input) {
    console.error('Usage: node generate_testcase.js --input <json> --project <name> [--output-dir testcase] [--random 12345]');
    process.exit(1);
  }

  const inputPath = path.resolve(args.input);
  if (!fs.existsSync(inputPath)) {
    console.error(`入力ファイルが見つかりません: ${inputPath}`);
    process.exit(1);
  }

  const data = JSON.parse(fs.readFileSync(inputPath, 'utf8'));
  const cases = data.cases || data;
  if (!Array.isArray(cases) || cases.length === 0) {
    console.error('cases 配列が空です');
    process.exit(1);
  }

  cases.forEach((c, i) => validateCase(c, i));

  const randomNum = args.random || random5();
  const baseName = `${args.project}_${randomNum}`;
  const outputDir = path.resolve(args.outputDir);
  fs.mkdirSync(outputDir, { recursive: true });

  const xlsxPath = path.join(outputDir, `${baseName}.xlsx`);
  const mdPath = path.join(outputDir, `${baseName}.md`);
  const rows = casesToRows(cases);

  const xlsx = resolveXlsx();
  writeExcel(xlsx, xlsxPath, rows);
  writeMarkdown(mdPath, args.project, randomNum, rows);

  console.log(JSON.stringify({
    ok: true,
    project: args.project,
    random: randomNum,
    count: rows.length,
    excel: xlsxPath,
    markdown: mdPath,
  }, null, 2));
}

main();
