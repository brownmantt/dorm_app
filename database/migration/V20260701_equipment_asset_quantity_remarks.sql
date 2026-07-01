-- 備品（個体）: 購入数量・備考カラム追加
ALTER TABLE equipment_asset
    ADD COLUMN IF NOT EXISTS purchase_quantity INTEGER NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS remarks TEXT;

COMMENT ON COLUMN equipment_asset.purchase_quantity IS '購入数量（登録時の一括購入数。個体ごとに保持）';
COMMENT ON COLUMN equipment_asset.remarks IS '備考';
