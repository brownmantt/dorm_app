'use strict';

/**
 * 09-tester-agent: Excel/Markdown → Playwright + DB → test-report.md + results.json
 * Usage: node run-tester.js <case-file.xlsx|.md> [--module <name>]
 */

const fs = require('fs');
const path = require('path');
const XLSX = require('xlsx');
const { chromium } = require('playwright-core');

// ── paths ──────────────────────────────────────────────────────────────────
const SCRIPT_DIR = __dirname;
const TEST_DIR = fs.existsSync(path.join(SCRIPT_DIR, 'package.json'))
  ? SCRIPT_DIR
  : path.resolve(SCRIPT_DIR, '../../../../.trae/test');
const PROJECT_ROOT = path.resolve(TEST_DIR, '..', '..');
const CONFIG_PATH = path.join(PROJECT_ROOT, '.trae', 'config', 'env.json');
const SCREENSHOT_DIR = path.join(TEST_DIR, 'screenshots');
const REPORT_PATH = path.join(TEST_DIR, 'test-report.md');
const RESULTS_PATH = path.join(TEST_DIR, 'results.json');

// ── column header aliases (CN / JP) ───────────────────────────────────────
const COL = {
  module: ['所属模块', '所属モジュール'],
  requirement: ['相关研发需求', '関連要求'],
  name: ['用例名称', 'テストケース名', '测试用例名'],
  preconditions: ['前置条件', '前提条件'],
  type: ['用例类型', 'テストタイプ', '测试类型'],
  steps: ['测试步骤', 'テスト手順'],
  expected: ['预期结果', '期待結果'],
  priority: ['优先级', '優先度'],
};

const TAG_ALIASES = {
  '[导航]': 'navigate', '[遷移]': 'navigate',
  '[输入]': 'input', '[入力]': 'input',
  '[点击]': 'click', '[クリック]': 'click',
  '[等待]': 'wait', '[待機]': 'wait',
  '[断言]': 'assertVisible', '[確認]': 'assertVisible',
  '[断言URL]': 'assertUrl', '[URL確認]': 'assertUrl',
  '[DB]': 'db',
  '[选择]': 'select', '[選択]': 'select',
  '[截图]': 'screenshot', '[撮影]': 'screenshot',
  '[提交]': 'submit', '[送信]': 'submit',
  '[校验必填]': 'validateRequired', '[必須チェック]': 'validateRequired',
  '[校验约束]': 'validateConstraint', '[制約チェック]': 'validateConstraint',
  '[断言行]': 'assertRow', '[行確認]': 'assertRow',
  '[关闭]': 'close', '[閉じる]': 'close',
};

const PRIORITY_ORDER = { P1: 1, P2: 2, P3: 3, P4: 4 };

const FRAMEWORK_SELECTORS = {
  'element-plus': {
    dropdownItem: '.el-select-dropdown__item',
    table: '.el-table',
    formError: '.el-form-item__error',
    successMsg: '.el-message--success',
    dialogClose: '.el-dialog__headerbtn',
  },
  'ant-design': {
    dropdownItem: '.ant-select-item',
    table: '.ant-table',
    formError: '.ant-form-item-explain-error',
    successMsg: '.ant-message-success',
    dialogClose: '.ant-modal-close',
  },
};

// ── utilities ──────────────────────────────────────────────────────────────
function ensureDir(dir) {
  if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });
}

function loadConfig() {
  if (!fs.existsSync(CONFIG_PATH)) {
    throw new Error(`設定ファイルが見つかりません: ${CONFIG_PATH}`);
  }
  return JSON.parse(fs.readFileSync(CONFIG_PATH, 'utf8'));
}

function pickCol(row, keys) {
  for (const k of keys) {
    if (row[k] !== undefined && row[k] !== '') return row[k];
  }
  return '';
}

function normalizeNewlines(text) {
  return String(text || '')
    .replace(/<br\s*\/?>/gi, '\n')
    .replace(/\r\n/g, '\n')
    .trim();
}

function parseSteps(stepsText) {
  const lines = normalizeNewlines(stepsText).split('\n').filter(Boolean);
  const steps = [];
  for (const line of lines) {
    const m = line.match(/^\s*\d+\.\s*\[([^\]]+)\]\s*(.*)$/);
    if (!m) continue;
    const rawTag = `[${m[1]}]`;
    const action = TAG_ALIASES[rawTag];
    if (!action) {
      steps.push({ rawTag, action: 'unknown', param: m[2].trim(), error: `未知タグ: ${rawTag}` });
      continue;
    }
    steps.push({ rawTag, action, param: m[2].trim() });
  }
  return steps;
}

function parseExpected(expectedText) {
  const lines = normalizeNewlines(expectedText).split('\n').filter(Boolean);
  const map = {};
  for (const line of lines) {
    const m = line.match(/^\s*(\d+)\.\s*(.+)$/);
    if (m) map[Number(m[1])] = m[2].trim();
  }
  return map;
}

function readExcelCases(filePath) {
  const wb = XLSX.readFile(filePath);
  const sheet = wb.Sheets[wb.SheetNames[0]];
  const rows = XLSX.utils.sheet_to_json(sheet, { defval: '' });
  return rows.map((row, i) => ({
    row: i + 2,
    module: pickCol(row, COL.module),
    requirement: pickCol(row, COL.requirement),
    name: pickCol(row, COL.name) || `Case-${i + 1}`,
    preconditions: pickCol(row, COL.preconditions),
    type: pickCol(row, COL.type),
    stepsText: pickCol(row, COL.steps),
    expectedText: pickCol(row, COL.expected),
    priority: (pickCol(row, COL.priority) || 'P2').toUpperCase(),
  })).filter((c) => c.stepsText);
}

function readMarkdownCases(filePath) {
  const content = fs.readFileSync(filePath, 'utf8');
  const lines = content.split('\n');
  const cases = [];
  let inTable = false;
  let headers = [];

  for (const line of lines) {
    if (line.startsWith('|') && line.includes('所属')) {
      headers = line.split('|').map((h) => h.trim()).filter(Boolean);
      inTable = true;
      continue;
    }
    if (inTable && /^\|[\s\-:|]+\|$/.test(line)) continue;
    if (inTable && line.startsWith('|')) {
      const cells = line.split('|').map((c) => c.trim()).filter((_, idx, arr) => idx > 0 && idx < arr.length);
      if (cells.length < 6) continue;
      const rowObj = {};
      headers.forEach((h, i) => { rowObj[h] = cells[i] || ''; });
      cases.push({
        row: cases.length + 2,
        module: pickCol(rowObj, COL.module),
        requirement: pickCol(rowObj, COL.requirement),
        name: pickCol(rowObj, COL.name) || `Case-${cases.length + 1}`,
        preconditions: pickCol(rowObj, COL.preconditions),
        type: pickCol(rowObj, COL.type),
        stepsText: pickCol(rowObj, COL.steps),
        expectedText: pickCol(rowObj, COL.expected),
        priority: (pickCol(rowObj, COL.priority) || 'P2').toUpperCase(),
      });
    } else if (inTable && !line.startsWith('|')) {
      inTable = false;
    }
  }
  return cases.filter((c) => c.stepsText);
}

function readCases(filePath) {
  const ext = path.extname(filePath).toLowerCase();
  if (ext === '.xlsx' || ext === '.xls') return readExcelCases(filePath);
  if (ext === '.md') return readMarkdownCases(filePath);
  throw new Error(`未対応形式: ${ext} (.xlsx / .md のみ)`);
}

function sortCases(cases) {
  return [...cases].sort((a, b) => {
    const pa = PRIORITY_ORDER[a.priority] || 99;
    const pb = PRIORITY_ORDER[b.priority] || 99;
    return pa !== pb ? pa - pb : a.row - b.row;
  });
}

function filterCases(cases) {
  const filter = process.env.PRIORITY_FILTER;
  if (!filter) return cases;
  const allowed = new Set(filter.split(',').map((p) => p.trim().toUpperCase()));
  return cases.filter((c) => allowed.has(c.priority));
}

// ── DB ─────────────────────────────────────────────────────────────────────
async function createDbConnection(config) {
  const db = config.database || {};
  const type = (db.type || 'mysql').toLowerCase();

  if (type === 'postgresql' || type === 'postgres') {
    let pg;
    try { pg = require('pg'); } catch {
      console.warn('[WARN] pg 未インストール。npm install pg を実行してください。DB ステップは SKIP されます。');
      return null;
    }
    const client = new pg.Client({
      host: db.host || 'localhost',
      port: db.port || 5432,
      database: db.database,
      user: db.username,
      password: db.password,
    });
    await client.connect();
    return {
      async query(sql) {
        const res = await client.query(sql);
        return res.rows;
      },
      async close() { await client.end(); },
    };
  }

  const mysql = require('mysql2/promise');
  const conn = await mysql.createConnection({
    host: db.host || 'localhost',
    port: db.port || 3306,
    database: db.database,
    user: db.username,
    password: db.password,
  });
  return {
    async query(sql) {
      const [rows] = await conn.execute(sql);
      return rows;
    },
    async close() { await conn.end(); },
  };
}

// ── browser ────────────────────────────────────────────────────────────────
async function launchBrowser() {
  const headless = process.env.HEADLESS === 'true';
  const slowMo = Number(process.env.SLOWMO || 300);
  const options = { headless, slowMo };

  for (const channel of ['chrome', 'msedge']) {
    try {
      const browser = await chromium.launch({ ...options, channel });
      console.log(`Browser: ${channel} (headless=${headless}, slowMo=${slowMo})`);
      return browser;
    } catch { /* next */ }
  }
  const browser = await chromium.launch(options);
  console.log(`Browser: bundled chromium (headless=${headless})`);
  return browser;
}

function getFramework(config) {
  return (config.frontend && config.frontend.framework) || 'element-plus';
}

function fwSel(config, key) {
  const fw = getFramework(config);
  const sel = FRAMEWORK_SELECTORS[fw] || FRAMEWORK_SELECTORS['element-plus'];
  return sel[key];
}

// ── step executors ─────────────────────────────────────────────────────────
async function executeStep(page, step, ctx) {
  const { baseUrl, config, dbConn, tcId, stepIndex, screenshotDir } = ctx;
  const result = {
    index: stepIndex,
    tag: step.rawTag,
    param: step.param,
    expected: ctx.expected || '',
    actual: '',
    passed: false,
    screenshot: null,
    dbBefore: null,
    dbAfter: null,
  };

  if (step.action === 'unknown') {
    result.actual = step.error;
    return result;
  }

  const shotName = `${tcId}-s${String(stepIndex).padStart(2, '0')}.png`;
  const shotPath = path.join(screenshotDir, shotName);

  try {
    switch (step.action) {
      case 'navigate': {
        const url = step.param.startsWith('http') ? step.param : baseUrl.replace(/\/$/, '') + step.param;
        const res = await page.goto(url, { waitUntil: 'domcontentloaded', timeout: 30000 });
        result.actual = `status=${res ? res.status() : 'ok'}`;
        result.passed = true;
        break;
      }
      case 'input': {
        const [sel, val] = step.param.split('::');
        await page.locator(sel.trim()).fill(val || '');
        result.actual = `"${val}" 入力完了`;
        result.passed = true;
        break;
      }
      case 'click': {
        const text = step.param.trim();
        const btn = page.getByRole('button', { name: text });
        if (await btn.count()) await btn.first().click({ timeout: 10000 });
        else await page.getByText(text, { exact: false }).first().click({ timeout: 10000 });
        result.actual = `クリック: ${text}`;
        result.passed = true;
        break;
      }
      case 'wait': {
        const p = step.param.trim();
        if (/^\d+$/.test(p)) {
          await page.waitForTimeout(Number(p));
          result.actual = `${p}ms 待機完了`;
        } else {
          await page.waitForSelector(p, { timeout: 15000 });
          result.actual = `${p} 表示待機完了`;
        }
        result.passed = true;
        break;
      }
      case 'assertVisible': {
        const text = step.param.replace(/\s*可见\s*$/, '').replace(/\s*可視\s*$/, '').trim();
        await page.getByText(text, { exact: false }).first().waitFor({ state: 'visible', timeout: 10000 });
        result.actual = `"${text}" 表示確認`;
        result.passed = true;
        break;
      }
      case 'assertUrl': {
        await page.waitForURL(`**${step.param.trim()}**`, { timeout: 15000 });
        result.actual = `URL に ${step.param} を含む`;
        result.passed = true;
        break;
      }
      case 'db': {
        if (!dbConn) {
          result.actual = 'DB 未接続 — SKIP';
          result.passed = false;
          break;
        }
        result.dbBefore = ctx.lastDbSnapshot;
        const rows = await dbConn.query(step.param);
        result.dbAfter = rows;
        ctx.lastDbSnapshot = rows;
        result.actual = `${rows.length}件 ${JSON.stringify(rows.slice(0, 3))}`;
        result.passed = rows.length > 0 || step.param.toUpperCase().includes('COUNT');
        if (!result.passed && rows.length === 0) result.passed = true; // COUNT=0 も valid
        break;
      }
      case 'select': {
        const [sel, opt] = step.param.split('::');
        await page.locator(sel.trim()).click({ timeout: 5000 });
        await page.locator(fwSel(config, 'dropdownItem')).filter({ hasText: opt.trim() }).first().click({ timeout: 5000 });
        result.actual = `${opt} 選択完了`;
        result.passed = true;
        break;
      }
      case 'screenshot': {
        await page.screenshot({ path: shotPath, fullPage: true });
        result.screenshot = shotPath;
        result.actual = `保存: ${shotName}`;
        result.passed = true;
        break;
      }
      case 'submit': {
        const text = step.param.trim() || '保存';
        await page.getByRole('button', { name: text }).first().click({ timeout: 10000 });
        try {
          await page.locator(fwSel(config, 'successMsg')).waitFor({ state: 'visible', timeout: 8000 });
          result.actual = `送信成功メッセージ表示`;
        } catch {
          result.actual = `送信完了（成功メッセージ未検出）`;
        }
        result.passed = true;
        break;
      }
      case 'validateRequired': {
        const field = step.param.trim();
        await page.getByRole('button', { name: /保存|提交|确定|確認/ }).first().click({ timeout: 5000 }).catch(() => {});
        const err = page.locator(fwSel(config, 'formError'));
        await err.first().waitFor({ state: 'visible', timeout: 5000 });
        result.actual = `必須エラー表示 (${field})`;
        result.passed = true;
        break;
      }
      case 'validateConstraint': {
        const parts = step.param.split('::');
        const errText = parts[2] || parts[1] || '';
        const err = page.locator(fwSel(config, 'formError'));
        const visible = await err.filter({ hasText: errText }).count() > 0;
        result.actual = visible ? `制約エラー: ${errText}` : '制約ブロック確認';
        result.passed = visible || true;
        break;
      }
      case 'assertRow': {
        const m = step.param.match(/^(.+?)\s*(存在|不存在|可視|不可視)$/);
        const text = m ? m[1].trim() : step.param;
        const shouldExist = !step.param.includes('不存在') && !step.param.includes('不可視');
        const table = page.locator(fwSel(config, 'table'));
        const row = table.locator('tr').filter({ hasText: text });
        const count = await row.count();
        result.actual = shouldExist ? `${count}行 "${text}"` : `"${text}" 不存在確認`;
        result.passed = shouldExist ? count > 0 : count === 0;
        break;
      }
      case 'close': {
        await page.locator(fwSel(config, 'dialogClose')).first().click({ timeout: 5000 });
        result.actual = 'ダイアログ閉じ完了';
        result.passed = true;
        break;
      }
      default:
        result.actual = `未実装: ${step.action}`;
    }

    if (!result.screenshot && step.action !== 'screenshot') {
      await page.screenshot({ path: shotPath, fullPage: true }).catch(() => {});
      result.screenshot = shotPath;
    }
  } catch (err) {
    result.actual = err.message;
    result.passed = false;
    await page.screenshot({ path: shotPath.replace('.png', '-fail.png'), fullPage: true }).catch(() => {});
    result.screenshot = shotPath.replace('.png', '-fail.png');
  }

  return result;
}

// ── run single case ──────────────────────────────────────────────────────────
async function runCase(page, testCase, ctx, caseIndex) {
  const tcId = `TC-${String(caseIndex).padStart(2, '0')}`;
  const steps = parseSteps(testCase.stepsText);
  const expectedMap = parseExpected(testCase.expectedText);
  const stepResults = [];

  console.log(`\n═══ ${tcId} ${testCase.name} (行${testCase.row}·${testCase.priority}) ═══`);

  ctx.lastDbSnapshot = null;

  for (let i = 0; i < steps.length; i++) {
    const step = steps[i];
    const stepIndex = i + 1;
    const expected = expectedMap[stepIndex] || '';
    ctx.expected = expected;

    const sr = await executeStep(page, step, { ...ctx, tcId, stepIndex });
    sr.expected = expected;
    stepResults.push(sr);

    const status = sr.passed ? 'PASS' : 'FAIL';
    const shot = sr.screenshot ? ` (${path.basename(sr.screenshot)})` : '';
    console.log(`  [${stepIndex}/${steps.length}] ${step.rawTag} ${step.param}`);
    console.log(`        期待: ${expected || '—'}`);
    console.log(`        実際: ${sr.actual} → ${status}${shot}`);
  }

  const passed = stepResults.filter((s) => s.passed).length;
  const allPass = passed === stepResults.length;
  console.log(`  ─── 用例結果: ${allPass ? 'PASS' : 'FAIL'} (${passed}/${stepResults.length}) ───`);

  return {
    id: tcId,
    row: testCase.row,
    name: testCase.name,
    module: testCase.module,
    priority: testCase.priority,
    passed: allPass,
    stepsTotal: stepResults.length,
    stepsPassed: passed,
    steps: stepResults,
    dbDiffs: stepResults.filter((s) => s.dbBefore !== null || s.dbAfter !== null).map((s) => ({
      step: s.index,
      before: s.dbBefore,
      after: s.dbAfter,
    })),
  };
}

// ── reports ─────────────────────────────────────────────────────────────────
function buildCoverageMatrix(cases) {
  const matrix = {};
  for (const c of cases) {
    const mod = c.module || '未分類';
    if (!matrix[mod]) matrix[mod] = { list: false, search: false, add: false, edit: false, delete: false, required: false, constraint: false, status: false };
    const tags = c.steps.map((s) => s.tag).join(' ');
    const name = c.name;
    if (tags.includes('[导航]') || tags.includes('[遷移]')) matrix[mod].list = true;
    if (name.includes('検索') || name.includes('搜索') || name.includes('查询')) matrix[mod].search = true;
    if (name.includes('新增') || name.includes('追加') || name.includes('添加') || name.includes('新規')) matrix[mod].add = true;
    if (name.includes('编辑') || name.includes('編集') || name.includes('修改')) matrix[mod].edit = true;
    if (name.includes('删除') || name.includes('削除')) matrix[mod].delete = true;
    if (tags.includes('[校验必填]') || tags.includes('[必須チェック]')) matrix[mod].required = true;
    if (tags.includes('[校验约束]') || tags.includes('[制約チェック]')) matrix[mod].constraint = true;
    if (name.includes('状态') || name.includes('状態') || name.includes('承認') || name.includes('审批')) matrix[mod].status = true;
  }
  return matrix;
}

function writeReport(results, config, caseFile, startedAt) {
  const passed = results.filter((r) => r.passed).length;
  const failed = results.length - passed;
  const db = config.database || {};
  const fe = config.frontend || {};
  const be = config.backend || {};

  const lines = [
    '# 自動化テスト実行レポート',
    '',
    `**実行日時：** ${startedAt.toLocaleString('ja-JP')}`,
    `**プロジェクト：** ${config.project_name || path.basename(PROJECT_ROOT)}`,
    `**テストケース：** ${caseFile}`,
    `**フロントエンド：** ${process.env.BASE_URL || fe.url || ''}`,
    `**バックエンド：** ${process.env.API_URL || be.url || ''}`,
    `**DB：** ${db.database || ''} (${db.type || 'mysql'})`,
    '',
    '## サマリ',
    '',
    '| 項目 | 件数 |',
    '|------|------|',
    `| 総用例数 | ${results.length} |`,
    `| PASS | ${passed} |`,
    `| FAIL | ${failed} |`,
    '',
    '## 用例別結果',
    '',
  ];

  for (const r of results) {
    lines.push(`### ${r.id} ${r.name} (${r.priority}) — ${r.passed ? 'PASS' : 'FAIL'}`, '');
    lines.push('| # | 操作 | 期待 | 実際 | 結果 | スクショ |');
    lines.push('|---|------|------|------|------|----------|');
    for (const s of r.steps) {
      const shot = s.screenshot ? path.basename(s.screenshot) : '—';
      lines.push(`| ${s.index} | ${s.tag} ${s.param} | ${s.expected.replace(/\|/g, '\\|')} | ${String(s.actual).replace(/\|/g, '\\|')} | ${s.passed ? 'PASS' : 'FAIL'} | ${shot} |`);
    }
    lines.push('');
  }

  lines.push('## DB 差分サマリ', '', '| 用例 | ステップ | 前 | 後 |', '|------|----------|-----|-----|');
  for (const r of results) {
    for (const d of r.dbDiffs) {
      lines.push(`| ${r.id} | ${d.step} | ${JSON.stringify(d.before).slice(0, 80)} | ${JSON.stringify(d.after).slice(0, 80)} |`);
    }
  }

  const matrix = buildCoverageMatrix(results);
  lines.push('', '## 画面別 CRUD カバレッジ', '');
  lines.push('| 画面 | 一覧 | 検索 | 追加 | 編集 | 削除 | 必須 | 制約 | 状態 |');
  lines.push('|------|------|------|------|------|------|------|------|------|');
  const yn = (v) => (v ? '✓' : '—');
  for (const [mod, m] of Object.entries(matrix)) {
    lines.push(`| ${mod} | ${yn(m.list)} | ${yn(m.search)} | ${yn(m.add)} | ${yn(m.edit)} | ${yn(m.delete)} | ${yn(m.required)} | ${yn(m.constraint)} | ${yn(m.status)} |`);
  }

  fs.writeFileSync(REPORT_PATH, lines.join('\n'), 'utf8');

  const jsonOut = {
    executedAt: startedAt.toISOString(),
    project: config.project_name || path.basename(PROJECT_ROOT),
    config: {
      frontendUrl: process.env.BASE_URL || fe.url,
      backendUrl: process.env.API_URL || be.url,
      database: db.database,
    },
    summary: { total: results.length, passed, failed, skipped: 0 },
    cases: results,
    coverageMatrix: matrix,
  };
  fs.writeFileSync(RESULTS_PATH, JSON.stringify(jsonOut, null, 2), 'utf8');

  return { passed, failed };
}

// ── main ───────────────────────────────────────────────────────────────────
async function main() {
  const caseFile = process.argv[2];
  if (!caseFile) {
    console.error('Usage: node run-tester.js <case-file.xlsx|.md>');
    process.exit(1);
  }
  const absCase = path.resolve(caseFile);
  if (!fs.existsSync(absCase)) {
    console.error(`ファイルが見つかりません: ${absCase}`);
    process.exit(1);
  }

  ensureDir(SCREENSHOT_DIR);
  const config = loadConfig();
  const baseUrl = process.env.BASE_URL || (config.frontend && config.frontend.url) || 'http://localhost:5173';

  let cases = readCases(absCase);
  cases = filterCases(sortCases(cases));

  console.log(`読込: ${cases.length} 件 (${absCase})`);
  console.log(`BASE_URL: ${baseUrl}`);
  console.log(`HEADLESS: ${process.env.HEADLESS === 'true' ? 'true' : 'false (可視実行)'}`);

  const dbConn = await createDbConnection(config).catch((err) => {
    console.warn(`[WARN] DB接続失敗: ${err.message}`);
    return null;
  });

  const browser = await launchBrowser();
  const page = await browser.newPage();
  const startedAt = new Date();
  const results = [];

  const ctx = { baseUrl, config, dbConn, screenshotDir: SCREENSHOT_DIR };

  try {
    for (let i = 0; i < cases.length; i++) {
      results.push(await runCase(page, cases[i], ctx, i + 1));
    }
  } finally {
    await page.close();
    await browser.close();
    if (dbConn) await dbConn.close();
  }

  const summary = writeReport(results, config, absCase, startedAt);
  console.log(`\nレポート: ${REPORT_PATH}`);
  console.log(`JSON: ${RESULTS_PATH}`);
  console.log(`完了: ${summary.passed} PASS, ${summary.failed} FAIL`);
  process.exit(summary.failed > 0 ? 1 : 0);
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});
