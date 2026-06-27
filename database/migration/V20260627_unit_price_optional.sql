-- 単価マスタ：寮・部屋任意化、最大利用日数 -1 対応（既存 DB 向け）
-- 実行例: psql -U dorm_app -d dormitory -f database/migration/V20260627_unit_price_optional.sql

ALTER TABLE unit_price ALTER COLUMN dormitory_id DROP NOT NULL;
ALTER TABLE unit_price ALTER COLUMN room_id DROP NOT NULL;

ALTER TABLE unit_price DROP CONSTRAINT IF EXISTS ck_unit_price_max_days;
ALTER TABLE unit_price ADD CONSTRAINT ck_unit_price_max_days CHECK (max_usage_days >= -1);
