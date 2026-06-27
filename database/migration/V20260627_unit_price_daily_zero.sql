-- 単価マスタ：日単価 0 許可（既存 DB 向け）
-- 実行例: psql -U dorm_app -d dormitory -f database/migration/V20260627_unit_price_daily_zero.sql

ALTER TABLE unit_price DROP CONSTRAINT IF EXISTS ck_unit_price_daily;
ALTER TABLE unit_price ADD CONSTRAINT ck_unit_price_daily CHECK (daily_unit_price >= 0);
