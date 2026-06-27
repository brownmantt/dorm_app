const fs = require('fs')
const path = require('path')
const { execSync } = require('child_process')

const xlsxPath = path.join(__dirname, '../../document/RFP/寮割_サンプル_A.xlsx')
const tmpDir = path.join(__dirname, '../_xlsx_extract')

if (fs.existsSync(tmpDir)) {
  fs.rmSync(tmpDir, { recursive: true, force: true })
}
fs.mkdirSync(tmpDir, { recursive: true })

const zipPath = path.join(tmpDir, 'workbook.zip')
fs.copyFileSync(xlsxPath, zipPath)
execSync(
  `powershell -NoProfile -Command "Expand-Archive -LiteralPath '${zipPath.replace(/'/g, "''")}' -DestinationPath '${tmpDir.replace(/'/g, "''")}' -Force"`,
  { stdio: 'inherit' }
)

function parseShared(xml) {
  const out = []
  const re = /<si[^>]*>([\s\S]*?)<\/si>/g
  let m
  while ((m = re.exec(xml))) {
    const t = m[1].match(/<t[^>]*>([\s\S]*?)<\/t>/g) || []
    out.push(
      t
        .map((x) =>
          x
            .replace(/<[^>]+>/g, '')
            .replace(/&amp;/g, '&')
            .replace(/&lt;/g, '<')
            .replace(/&gt;/g, '>')
        )
        .join('')
    )
  }
  return out
}

function colLetters(ref) {
  return ref.replace(/[0-9]/g, '')
}

function cellVal(c, shared) {
  const refMatch = c.match(/r="([^"]+)"/)
  const ref = refMatch ? refMatch[1] : ''
  const tMatch = c.match(/t="([^"]+)"/)
  const t = tMatch ? tMatch[1] : null
  const vMatch = c.match(/<v>([^<]*)<\/v>/)
  const v = vMatch ? vMatch[1] : undefined
  if (v === undefined) return { ref, val: '' }
  let val = v
  if (t === 's') val = shared[parseInt(v, 10)] || ''
  return { ref, val }
}

const sharedPath = path.join(tmpDir, 'xl/sharedStrings.xml')
const sheetPath = path.join(tmpDir, 'xl/worksheets/sheet1.xml')
const shared = fs.existsSync(sharedPath)
  ? parseShared(fs.readFileSync(sharedPath, 'utf8'))
  : []
const sheet = fs.readFileSync(sheetPath, 'utf8')
const cells = [...sheet.matchAll(/<c[^>]*>[\s\S]*?<\/c>/g)].map((x) => cellVal(x[0], shared))
const colD = cells.filter((c) => colLetters(c.ref) === 'D')
const unique = [
  ...new Set(colD.map((c) => String(c.val || '').trim()).filter(Boolean))
].sort((a, b) => a.localeCompare(b, 'ja'))

console.log(JSON.stringify({ column: 'D', count: colD.length, unique }, null, 2))
