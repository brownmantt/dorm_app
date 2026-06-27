-- 社員マスタの最寄駅を3項目に分割（nearest_station → nearest_station_1/2/3）
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'employee' AND column_name = 'nearest_station'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'employee' AND column_name = 'nearest_station_1'
    ) THEN
        ALTER TABLE employee RENAME COLUMN nearest_station TO nearest_station_1;
    END IF;
END $$;

ALTER TABLE employee
    ALTER COLUMN nearest_station_1 TYPE VARCHAR(20);

ALTER TABLE employee
    ADD COLUMN IF NOT EXISTS nearest_station_2 VARCHAR(20),
    ADD COLUMN IF NOT EXISTS nearest_station_3 VARCHAR(20);

COMMENT ON COLUMN employee.nearest_station_1 IS '最寄駅１（最大20文字）';
COMMENT ON COLUMN employee.nearest_station_2 IS '最寄駅２（最大20文字）';
COMMENT ON COLUMN employee.nearest_station_3 IS '最寄駅３（最大20文字）';
