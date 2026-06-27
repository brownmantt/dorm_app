-- ============================================================
-- v2.0 マイグレーション（寮割カレンダー・所属・責任者）
-- 既存 DB に適用する場合は本ファイルを実行
-- ============================================================

-- 所属マスタ
CREATE TABLE IF NOT EXISTS affiliation (
    affiliation_id  VARCHAR(20)  NOT NULL,
    code            VARCHAR(30)  NOT NULL,
    name            VARCHAR(100) NOT NULL,
    display_order   INT,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT pk_affiliation PRIMARY KEY (affiliation_id)
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_affiliation_code
    ON affiliation (code) WHERE deleted_at IS NULL;
COMMENT ON TABLE affiliation IS '所属マスタ';

-- 寮責任者
CREATE TABLE IF NOT EXISTS dormitory_manager (
    dormitory_id          VARCHAR(20) NOT NULL,
    employee_id           VARCHAR(20) NOT NULL,
    residence_history_id  VARCHAR(20) NOT NULL,
    assigned_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    version               INT         NOT NULL DEFAULT 0,
    CONSTRAINT pk_dormitory_manager PRIMARY KEY (dormitory_id),
    CONSTRAINT fk_dm_dormitory FOREIGN KEY (dormitory_id) REFERENCES dormitory (dormitory_id),
    CONSTRAINT fk_dm_employee FOREIGN KEY (employee_id) REFERENCES employee (employee_id),
    CONSTRAINT fk_dm_residence FOREIGN KEY (residence_history_id) REFERENCES residence_history (residence_history_id)
);
COMMENT ON TABLE dormitory_manager IS '寮責任者（寮ごと最大1名）';

-- dormitory 拡張
ALTER TABLE dormitory ADD COLUMN IF NOT EXISTS region VARCHAR(20);
UPDATE dormitory SET region = 'OTHER' WHERE region IS NULL;
COMMENT ON COLUMN dormitory.region IS '地域コード：TOKYO/OSAKA/NAGOYA/OTHER';

-- room 拡張
ALTER TABLE room ADD COLUMN IF NOT EXISTS room_detail VARCHAR(100);
ALTER TABLE room ADD COLUMN IF NOT EXISTS has_air_conditioner BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE room ADD COLUMN IF NOT EXISTS monthly_fee DECIMAL(10,0);
COMMENT ON COLUMN room.room_detail IS '部屋詳細（洋室等）';
COMMENT ON COLUMN room.has_air_conditioner IS 'エアコン有無';
COMMENT ON COLUMN room.monthly_fee IS '寮費単価（月額）';

-- employee 拡張
ALTER TABLE employee ADD COLUMN IF NOT EXISTS affiliation_id VARCHAR(20);
ALTER TABLE employee ADD COLUMN IF NOT EXISTS business_division VARCHAR(30);
ALTER TABLE employee ADD COLUMN IF NOT EXISTS nearest_station VARCHAR(100);
ALTER TABLE employee ADD COLUMN IF NOT EXISTS contact_info JSONB;
COMMENT ON COLUMN employee.affiliation_id IS '所属マスタ FK';

-- インデックス
CREATE INDEX IF NOT EXISTS idx_dormitory_region_name ON dormitory (region, name);
CREATE INDEX IF NOT EXISTS idx_employee_affiliation ON employee (affiliation_id);
CREATE INDEX IF NOT EXISTS idx_residence_dormitory_period ON residence_history (dormitory_id, move_in_date);
