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

## Cloudflare Pages（フロントエンド）

フロントエンドは Cloudflare Pages にデプロイします。

- ビルドコマンド: `npm run build`
- ビルド出力: `dist`
- ルートディレクトリ: `dom-dev`

環境変数（Cloudflare Pages ダッシュボードで設定）:

| 変数名 | 説明 | 例 |
|---|---|---|
| `VITE_API_BASE_URL` | バックエンド API の URL | `https://api.example.com/api/v1` |

> バックエンド（Spring Boot）は Cloudflare Pages では動作しません。別途 VPS / コンテナ等で API をホストし、`VITE_API_BASE_URL` で接続してください。

## ライセンス

Private
