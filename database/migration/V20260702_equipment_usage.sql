-- 備品利用管理テーブル
CREATE TABLE IF NOT EXISTS equipment_usage (
    usage_id           VARCHAR(20) NOT NULL,
    equipment_asset_id VARCHAR(20) NOT NULL,
    dormitory_id       VARCHAR(20) NOT NULL,
    room_id            VARCHAR(20) NOT NULL,
    employee_id        VARCHAR(20) NOT NULL,
    usage_start_date   DATE          NOT NULL,
    usage_end_date     DATE,
    usage_quantity     INTEGER       NOT NULL DEFAULT 1,
    remarks            TEXT,
    created_at         TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ   NOT NULL DEFAULT now(),
    deleted_at         TIMESTAMPTZ,
    CONSTRAINT pk_equipment_usage PRIMARY KEY (usage_id),
    CONSTRAINT fk_equipment_usage_asset FOREIGN KEY (equipment_asset_id) REFERENCES equipment_asset (equipment_asset_id),
    CONSTRAINT fk_equipment_usage_dormitory FOREIGN KEY (dormitory_id) REFERENCES dormitory (dormitory_id),
    CONSTRAINT fk_equipment_usage_room FOREIGN KEY (room_id) REFERENCES room (room_id),
    CONSTRAINT fk_equipment_usage_employee FOREIGN KEY (employee_id) REFERENCES employee (employee_id),
    CONSTRAINT chk_equipment_usage_quantity CHECK (usage_quantity >= 1),
    CONSTRAINT chk_equipment_usage_date_range CHECK (usage_end_date IS NULL OR usage_end_date >= usage_start_date)
);

COMMENT ON TABLE equipment_usage IS '備品利用（寮・部屋・入居者への貸出）';
COMMENT ON COLUMN equipment_usage.usage_id IS '利用ID（EU + yyyyMMdd + 4桁連番）';
COMMENT ON COLUMN equipment_usage.equipment_asset_id IS '備品番号（FK）';
COMMENT ON COLUMN equipment_usage.dormitory_id IS '寮ID（FK）';
COMMENT ON COLUMN equipment_usage.room_id IS '部屋ID（FK）';
COMMENT ON COLUMN equipment_usage.employee_id IS '入居者（社員ID）';
COMMENT ON COLUMN equipment_usage.usage_start_date IS '利用開始日';
COMMENT ON COLUMN equipment_usage.usage_end_date IS '利用終了日（NULL＝利用中）';
COMMENT ON COLUMN equipment_usage.usage_quantity IS '利用個数';

CREATE INDEX IF NOT EXISTS idx_equipment_usage_asset ON equipment_usage (equipment_asset_id) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_equipment_usage_active ON equipment_usage (equipment_asset_id) WHERE deleted_at IS NULL AND usage_end_date IS NULL;
