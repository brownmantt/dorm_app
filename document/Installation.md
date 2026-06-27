# 寮管理システム フロントエンド 本番環境構築手順（Installation Guide）

| 項目 | 内容 |
|------|------|
| 対象 | 寮管理システム フロントエンド（`dom-dev`） |
| 技術スタック | Vue3 + Vite + Element Plus + Echarts + Pinia + Vue Router |
| 文書版 | v1.0 |
| 最終更新 | 2026-05-30 |

---

## 0. はじめに：Node.js は本番に必要か？

**結論：本番の「ランタイムサーバー」には Node.js は不要です。**

フロントエンドは Vite によって静的ファイル（HTML / CSS / JavaScript）へ
ビルドされます。本番ではその静的ファイルを Web サーバー（Nginx 等）で
配信するだけなので、サーバー側に Node.js は不要です。

| 環境 | Node.js / npm | 用途 |
|------|---------------|------|
| 開発端末 | **必要** | `npm run dev`（開発サーバー） |
| ビルド環境（CI / ビルド端末） | **必要** | `npm install` / `npm run build` |
| 本番ランタイムサーバー | **不要** | Nginx 等で `dist/` を静的配信するのみ |

> つまり、先ほどインストールした Node.js は「開発・ビルド」のために必要であり、
> 本番の配信サーバーには入れる必要はありません。

---

## 1. システム全体構成

```text
[ブラウザ]
   │ HTTPS
   ▼
[Nginx]  ← フロント静的ファイル(dist/)を配信 + /api をバックエンドへリバースプロキシ
   │ /api/* → http://backend:8080
   ▼
[寮管理 API (Spring Boot / dom-server)]
   │
   ▼
[PostgreSQL]
```

- フロント（本書の対象）: 静的ファイルとして Nginx が配信
- バックエンド（`dom-server`）: 別途デプロイが必要（Java 17 + Spring Boot 3）
- DB: PostgreSQL 15

---

## 2. 前提（ビルド環境）

| ソフトウェア | バージョン | 備考 |
|--------------|------------|------|
| Node.js | 20 LTS 以上（推奨：22 LTS） | ビルド時のみ使用 |
| npm | Node.js 同梱版 | |
| Git | 任意 | ソース取得 |

### Node.js のインストール（ビルド端末のみ）

- Windows: `winget install OpenJS.NodeJS.LTS`
- macOS: `brew install node`
- Linux(Debian/Ubuntu): NodeSource または `nvm` を利用

確認:

```bash
node --version
npm --version
```

---

## 3. 本番ビルド手順

### 3.1 依存パッケージのインストール

```bash
cd dom-dev
npm ci   # package-lock.json に基づく再現性のあるインストール（CI 推奨）
# package-lock.json が無い場合は npm install
```

### 3.2 本番用環境変数の設定

プロジェクト直下に `.env.production` を作成します（リポジトリには
コミットしない運用を推奨）。

```dotenv
# 本番 API のベースURL（Nginx 経由で同一オリジン配信する場合は /api/v1）
VITE_API_BASE_URL=/api/v1

# 認証バイパスは本番では必ず false（または記載しない）
VITE_AUTH_BYPASS=false
```

> ⚠️ **重要**: 開発時に有効化した `VITE_AUTH_BYPASS=true` は
> 認証をスキップする開発専用フラグです。本番ビルドでは必ず `false` に
> してください。

### 3.3 ビルド実行

```bash
npm run build
```

成功すると `dom-dev/dist/` に静的ファイルが出力されます。

```text
dist/
├── index.html
└── assets/
    ├── index-xxxxxxxx.js
    ├── index-xxxxxxxx.css
    └── ...
```

### 3.4 ローカルでのビルド確認（任意）

```bash
npm run preview
```

---

## 4. 本番デプロイ（Nginx で静的配信）

### 4.1 成果物の配置

`dist/` の中身を Web サーバーの公開ディレクトリへ配置します。

```bash
# 例：Linux サーバー
sudo mkdir -p /var/www/dormitory
sudo cp -r dist/* /var/www/dormitory/
```

### 4.2 Nginx 設定例

`/etc/nginx/conf.d/dormitory.conf`:

```nginx
server {
    listen 80;
    server_name dormitory.example.com;

    root /var/www/dormitory;
    index index.html;

    # SPA: 直リンク・リロードでも index.html を返す（History モード対応）
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API リバースプロキシ（バックエンドへ転送）
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 静的アセットのキャッシュ
    location /assets/ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

### 4.3 反映

```bash
sudo nginx -t          # 構文チェック
sudo systemctl reload nginx
```

> **ポイント**：Vue Router は History モードのため、`try_files ... /index.html`
> の設定が無いとページ直アクセス・リロードで 404 になります。必須設定です。

---

## 5. HTTPS 化（推奨）

本番では HTTPS を推奨します。Let's Encrypt（certbot）の例:

```bash
sudo certbot --nginx -d dormitory.example.com
```

---

## 6. バックエンド・DB（参考）

フロント単体では業務データを表示できません。以下が別途必要です。
詳細は各設計書を参照してください。

| コンポーネント | 必要ソフト | 参照 |
|----------------|------------|------|
| API (`dom-server`) | Java 17, Spring Boot 3 | `詳細設計書_0522/02_システム構成.md` |
| DB | PostgreSQL 15 | `詳細設計書_0522/06_データベース設計.md` |
| 認証 | Bearer Token / 社内 IdP | `詳細設計書_0522/16_セキュリティ設計.md` |

API のエンドポイントは `/api/v1/...`（共通仕様：
`詳細設計書_0522/05_API共通仕様.md`）。

---

## 7. デプロイ後の動作確認チェックリスト

- [ ] `https://<ドメイン>/` でログイン画面が表示される
- [ ] ブラウザの開発者ツールで JS/CSS が 200 で読み込まれている
- [ ] ページをリロードしても 404 にならない（SPA ルーティング）
- [ ] `/api/v1/...` への通信がバックエンドに到達している（401/200 が返る）
- [ ] `VITE_AUTH_BYPASS` が無効（ログインに正規の認証が必要）
- [ ] HTTPS でアクセスできる

---

## 8. CI/CD 例（GitHub Actions）

```yaml
name: build-frontend
on:
  push:
    branches: [main]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 22
          cache: npm
          cache-dependency-path: dom-dev/package-lock.json
      - working-directory: dom-dev
        run: npm ci
      - working-directory: dom-dev
        run: npm run build
      - uses: actions/upload-artifact@v4
        with:
          name: frontend-dist
          path: dom-dev/dist
```

> ビルド成果物（`dist/`）を本番サーバーへ転送（rsync / scp / S3 等）し、
> Nginx で配信します。**本番サーバー側に Node.js は不要**です。

---

## 9. トラブルシューティング

| 症状 | 原因 | 対処 |
|------|------|------|
| リロードで 404 | SPA フォールバック未設定 | Nginx の `try_files ... /index.html` を追加 |
| API が CORS エラー | 別オリジン配信 | Nginx で `/api` を同一オリジンにプロキシ |
| 画面が真っ白 | アセットパス不一致 | `vite.config.js` の `base` 設定を確認 |
| ログインなしで入れてしまう | バイパス有効のままビルド | `VITE_AUTH_BYPASS=false` で再ビルド |
| 401 で弾かれる | バックエンド未起動/トークン無効 | `dom-server` の稼働・認証設定を確認 |
