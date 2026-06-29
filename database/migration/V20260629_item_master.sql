-- 品目マスタ: equipment_type を管理対象外とする（列は後方互換のため残置）
ALTER TABLE equipment ALTER COLUMN equipment_type DROP NOT NULL;

COMMENT ON TABLE equipment IS '品目マスタ（品目ID・品目名称）';
COMMENT ON COLUMN equipment.equipment_type IS '廃止（管理対象外・後方互換のため列のみ残置）';
