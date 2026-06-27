-- 利用形態マスタ追加マイグレーション（既存 DB 向け）
-- 実行例: psql -U dorm_app -d dormitory -f database/migration/V20260627_usage_type_master.sql

CREATE TABLE IF NOT EXISTS usage_type (
    usage_type_id VARCHAR(20)  NOT NULL,
    code          VARCHAR(30)  NOT NULL,
    name          VARCHAR(100) NOT NULL,
    display_order INT,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at    TIMESTAMPTZ,
    CONSTRAINT pk_usage_type PRIMARY KEY (usage_type_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_usage_type_code ON usage_type (code) WHERE deleted_at IS NULL;

COMMENT ON TABLE usage_type IS '利用形態マスタ';

INSERT INTO usage_type (usage_type_id, code, name, display_order, created_at, updated_at)
SELECT 'UT202606270001', 'NORMAL', '通常利用', 1, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM usage_type WHERE code = 'NORMAL' AND deleted_at IS NULL);

INSERT INTO usage_type (usage_type_id, code, name, display_order, created_at, updated_at)
SELECT 'UT202606270002', 'BUSINESS_TRIP', '出張利用', 2, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM usage_type WHERE code = 'BUSINESS_TRIP' AND deleted_at IS NULL);

INSERT INTO usage_type (usage_type_id, code, name, display_order, created_at, updated_at)
SELECT 'UT202606270003', 'LONG_TERM', '長期利用', 3, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM usage_type WHERE code = 'LONG_TERM' AND deleted_at IS NULL);
