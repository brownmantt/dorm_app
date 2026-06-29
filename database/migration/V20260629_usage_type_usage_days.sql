-- Move min_usage_days / max_usage_days from unit_price to usage_type
-- Run: psql -h localhost -U dorm_app -d dormitory -f database/migration/V20260629_usage_type_usage_days.sql

SET client_encoding TO 'UTF8';

ALTER TABLE usage_type
    ADD COLUMN IF NOT EXISTS min_usage_days INT NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS max_usage_days INT NOT NULL DEFAULT -1;

ALTER TABLE usage_type DROP CONSTRAINT IF EXISTS ck_usage_type_min_days;
ALTER TABLE usage_type DROP CONSTRAINT IF EXISTS ck_usage_type_max_days;
ALTER TABLE usage_type ADD CONSTRAINT ck_usage_type_min_days CHECK (min_usage_days >= 1);
ALTER TABLE usage_type ADD CONSTRAINT ck_usage_type_max_days CHECK (max_usage_days >= -1);

UPDATE usage_type ut
SET min_usage_days = COALESCE(agg.min_days, 1),
    max_usage_days = COALESCE(agg.max_days, -1)
FROM (
    SELECT usage_type_code,
           MIN(min_usage_days) AS min_days,
           CASE
               WHEN BOOL_AND(max_usage_days = -1) THEN -1
               ELSE MAX(CASE WHEN max_usage_days = -1 THEN NULL ELSE max_usage_days END)
           END AS max_days
    FROM unit_price
    WHERE deleted_at IS NULL
    GROUP BY usage_type_code
) agg
WHERE ut.code = agg.usage_type_code;

ALTER TABLE unit_price DROP CONSTRAINT IF EXISTS ck_unit_price_min_days;
ALTER TABLE unit_price DROP CONSTRAINT IF EXISTS ck_unit_price_max_days;
ALTER TABLE unit_price DROP COLUMN IF EXISTS min_usage_days;
ALTER TABLE unit_price DROP COLUMN IF EXISTS max_usage_days;

COMMENT ON COLUMN usage_type.min_usage_days IS '最小利用日数（未指定登録時は1）';
COMMENT ON COLUMN usage_type.max_usage_days IS '最大利用日数（-1は制限なし）';
