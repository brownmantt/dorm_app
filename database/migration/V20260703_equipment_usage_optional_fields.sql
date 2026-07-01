-- 寮・部屋・入居者を任意化（寮または入居者のいずれかはアプリ層で検証）
ALTER TABLE equipment_usage ALTER COLUMN dormitory_id DROP NOT NULL;
ALTER TABLE equipment_usage ALTER COLUMN room_id DROP NOT NULL;
ALTER TABLE equipment_usage ALTER COLUMN employee_id DROP NOT NULL;

COMMENT ON COLUMN equipment_usage.dormitory_id IS '寮ID（FK・任意。入居者といずれか必須）';
COMMENT ON COLUMN equipment_usage.room_id IS '部屋ID（FK・任意）';
COMMENT ON COLUMN equipment_usage.employee_id IS '入居者（社員ID・任意。寮といずれか必須）';
