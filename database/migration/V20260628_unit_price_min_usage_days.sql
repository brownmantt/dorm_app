-- 単価マスタに最小利用日数を追加
-- 実行例: psql -U dorm_app -d dormitory -f database/migration/V20260628_unit_price_min_usage_days.sql

SET client_encoding TO 'UTF8';

ALTER TABLE unit_price
    ADD COLUMN IF NOT EXISTS min_usage_days INT NOT NULL DEFAULT 1;

ALTER TABLE unit_price DROP CONSTRAINT IF EXISTS ck_unit_price_min_days;
ALTER TABLE unit_price ADD CONSTRAINT ck_unit_price_min_days CHECK (min_usage_days >= 1);

COMMENT ON COLUMN unit_price.min_usage_days IS '最小利用日数（請求日数の下限。未指定登録時は1）';
