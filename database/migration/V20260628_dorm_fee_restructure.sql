-- Dorm fee table restructure (PROVISIONAL/ERROR status)
-- Run: psql -h localhost -U dorm_app -d dormitory -f database/migration/V20260628_dorm_fee_restructure.sql

SET client_encoding TO 'UTF8';

DELETE FROM dorm_fee;

ALTER TABLE dorm_fee DROP CONSTRAINT IF EXISTS ck_dorm_fee_status;
ALTER TABLE dorm_fee DROP CONSTRAINT IF EXISTS fk_dorm_fee_unit_price;
ALTER TABLE dorm_fee DROP CONSTRAINT IF EXISTS fk_dorm_fee_dormitory;
DROP INDEX IF EXISTS uk_dorm_fee_emp_ym;

ALTER TABLE dorm_fee
    DROP COLUMN IF EXISTS basis_area_sqm,
    DROP COLUMN IF EXISTS basis_days,
    DROP COLUMN IF EXISTS basis_detail;

ALTER TABLE dorm_fee ADD COLUMN IF NOT EXISTS region VARCHAR(30);
ALTER TABLE dorm_fee ADD COLUMN IF NOT EXISTS dormitory_id VARCHAR(20);
ALTER TABLE dorm_fee ADD COLUMN IF NOT EXISTS move_in_date DATE;
ALTER TABLE dorm_fee ADD COLUMN IF NOT EXISTS move_out_date DATE;
ALTER TABLE dorm_fee ADD COLUMN IF NOT EXISTS usage_type_code VARCHAR(30);
ALTER TABLE dorm_fee ADD COLUMN IF NOT EXISTS usage_days INT;
ALTER TABLE dorm_fee ADD COLUMN IF NOT EXISTS unit_price_id VARCHAR(20);
ALTER TABLE dorm_fee ADD COLUMN IF NOT EXISTS daily_unit_price DECIMAL(10, 2);

ALTER TABLE dorm_fee ALTER COLUMN amount DROP NOT NULL;

UPDATE dorm_fee df
SET dormitory_id = rh.dormitory_id,
    region = d.region,
    move_in_date = rh.move_in_date,
    move_out_date = rh.move_out_date,
    usage_type_code = rh.usage_type_code
FROM residence_history rh
JOIN dormitory d ON d.dormitory_id = rh.dormitory_id
WHERE df.residence_history_id = rh.residence_history_id
  AND df.dormitory_id IS NULL;

ALTER TABLE dorm_fee
    ALTER COLUMN region SET NOT NULL,
    ALTER COLUMN dormitory_id SET NOT NULL,
    ALTER COLUMN move_in_date SET NOT NULL,
    ALTER COLUMN usage_type_code SET NOT NULL;

ALTER TABLE dorm_fee ALTER COLUMN residence_history_id SET NOT NULL;

ALTER TABLE dorm_fee ADD CONSTRAINT fk_dorm_fee_dormitory
    FOREIGN KEY (dormitory_id) REFERENCES dormitory (dormitory_id);

ALTER TABLE dorm_fee ADD CONSTRAINT fk_dorm_fee_unit_price
    FOREIGN KEY (unit_price_id) REFERENCES unit_price (unit_price_id);

ALTER TABLE dorm_fee ADD CONSTRAINT ck_dorm_fee_status
    CHECK (status IN ('PROVISIONAL', 'ERROR'));

DROP INDEX IF EXISTS uk_dorm_fee_residence_ym;
CREATE UNIQUE INDEX uk_dorm_fee_residence_ym
    ON dorm_fee (residence_history_id, target_year_month)
    WHERE deleted_at IS NULL;

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
