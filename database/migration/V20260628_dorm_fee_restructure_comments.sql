-- Re-run COMMENT only (after V20260628_dorm_fee_restructure.sql encoding errors)
-- Run: psql -h localhost -U dorm_app -d dormitory -f database/migration/V20260628_dorm_fee_restructure_comments.sql

SET client_encoding TO 'UTF8';

-- amount nullable for ERROR status rows (safe if already nullable)
ALTER TABLE dorm_fee ALTER COLUMN amount DROP NOT NULL;

COMMENT ON COLUMN dorm_fee.region IS '地域コード';
COMMENT ON COLUMN dorm_fee.dormitory_id IS '寮ID';
COMMENT ON COLUMN dorm_fee.move_in_date IS '算定期間開始日（対象月内）';
COMMENT ON COLUMN dorm_fee.move_out_date IS '算定期間終了日（対象月内）';
COMMENT ON COLUMN dorm_fee.usage_type_code IS '利用形態コード';
COMMENT ON COLUMN dorm_fee.usage_days IS '利用日数';
COMMENT ON COLUMN dorm_fee.unit_price_id IS '単価ID';
COMMENT ON COLUMN dorm_fee.daily_unit_price IS '日単価';
COMMENT ON COLUMN dorm_fee.amount IS '算出金額';
COMMENT ON COLUMN dorm_fee.status IS 'PROVISIONAL=仮定 / ERROR=エラー';
