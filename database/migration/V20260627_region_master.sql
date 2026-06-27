-- 地域マスタ追加マイグレーション（既存 DB 向け）
-- 実行例: psql -U dorm_app -d dormitory -f database/migration/V20260627_region_master.sql

CREATE TABLE IF NOT EXISTS region (
    region_id     VARCHAR(20)  NOT NULL,
    code          VARCHAR(30)  NOT NULL,
    name          VARCHAR(100) NOT NULL,
    display_order INT,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at    TIMESTAMPTZ,
    CONSTRAINT pk_region PRIMARY KEY (region_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_region_code ON region (code) WHERE deleted_at IS NULL;

COMMENT ON TABLE region IS '地域マスタ';

INSERT INTO region (region_id, code, name, display_order, created_at, updated_at)
SELECT 'RG202606270001', 'TOKYO', '東京', 1, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM region WHERE code = 'TOKYO' AND deleted_at IS NULL);

INSERT INTO region (region_id, code, name, display_order, created_at, updated_at)
SELECT 'RG202606270002', 'OSAKA', '大阪', 2, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM region WHERE code = 'OSAKA' AND deleted_at IS NULL);

INSERT INTO region (region_id, code, name, display_order, created_at, updated_at)
SELECT 'RG202606270003', 'NAGOYA', '名古屋', 3, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM region WHERE code = 'NAGOYA' AND deleted_at IS NULL);

INSERT INTO region (region_id, code, name, display_order, created_at, updated_at)
SELECT 'RG202606270004', 'OTHER', 'その他', 4, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM region WHERE code = 'OTHER' AND deleted_at IS NULL);
