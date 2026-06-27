# dorm_app

寮管理システム（Dormitory Management System）

## 構成

| ディレクトリ | 説明 |
|---|---|
| `dom-dev/` | フロントエンド（Vue 3 + Vite + Element Plus） |
| `dom-server/` | バックエンド（Spring Boot + MyBatis + PostgreSQL） |
| `database/` | DB スキーマ・マイグレーション |
| `document/` | 詳細設計書・API ドキュメント |

## ローカル開発

### フロントエンド

```bash
cd dom-dev
npm install
npm run dev
```

### バックエンド

```bash
cd dom-server
mvn spring-boot:run
```

### データベース

`database/01_データベース構築手順.md` を参照してください。

## Cloudflare デプロイ（フロントエンド）

フロントエンドは Cloudflare Workers（Static Assets）にデプロイします。

### Cloudflare ダッシュボード設定

| 項目 | 値 |
|---|---|
| ビルドコマンド | `npm run build` |
| デプロイコマンド | `npx wrangler deploy` |

またはデプロイコマンドのみ 1 行で:

```bash
npm run deploy
```

環境変数:

| 変数名 | 説明 | 例 |
|---|---|---|
| `VITE_API_BASE_URL` | バックエンド API の URL | `https://api.example.com/api/v1` |

> `npx wrangler pages deploy` ではなく `npx wrangler deploy` を使用してください（`wrangler.toml` の `[assets]` 設定に対応）。
>
> バックエンド（Spring Boot）は Cloudflare では動作しません。別途 VPS / コンテナ等で API をホストし、`VITE_API_BASE_URL` で接続してください。

## ライセンス

Private
