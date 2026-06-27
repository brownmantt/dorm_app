-- 寮マスタに最寄駅カラムを追加
ALTER TABLE dormitory
    ADD COLUMN IF NOT EXISTS nearest_station_1 VARCHAR(20),
    ADD COLUMN IF NOT EXISTS nearest_station_2 VARCHAR(20),
    ADD COLUMN IF NOT EXISTS nearest_station_3 VARCHAR(20);

COMMENT ON COLUMN dormitory.nearest_station_1 IS '最寄駅１（最大20文字）';
COMMENT ON COLUMN dormitory.nearest_station_2 IS '最寄駅２（最大20文字）';
COMMENT ON COLUMN dormitory.nearest_station_3 IS '最寄駅３（最大20文字）';
