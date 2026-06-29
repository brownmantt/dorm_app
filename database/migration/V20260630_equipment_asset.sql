-- 備品（個体）管理テーブル
CREATE TABLE IF NOT EXISTS equipment_asset (
    equipment_asset_id       VARCHAR(20)    NOT NULL,
    equipment_id             VARCHAR(20)    NOT NULL,
    purchase_date            DATE           NOT NULL,
    purchase_amount          DECIMAL(12, 0) NOT NULL,
    purchase_store           VARCHAR(100),
    purchase_store_contact   VARCHAR(50),
    purchase_store_postal_code VARCHAR(7),
    purchase_store_address   VARCHAR(500),
    warranty_expiry_date     DATE,
    created_at               TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at               TIMESTAMPTZ    NOT NULL DEFAULT now(),
    deleted_at               TIMESTAMPTZ,
    CONSTRAINT pk_equipment_asset PRIMARY KEY (equipment_asset_id),
    CONSTRAINT fk_equipment_asset_item FOREIGN KEY (equipment_id) REFERENCES equipment (equipment_id)
);

COMMENT ON TABLE equipment_asset IS '備品（品目マスタに紐づく個体）';
COMMENT ON COLUMN equipment_asset.equipment_asset_id IS '備品番号（自動採番）';
COMMENT ON COLUMN equipment_asset.equipment_id IS '品目ID（品目マスタ）';
COMMENT ON COLUMN equipment_asset.purchase_date IS '購入日';
COMMENT ON COLUMN equipment_asset.purchase_amount IS '購入金額';
COMMENT ON COLUMN equipment_asset.purchase_store IS '購入店';
COMMENT ON COLUMN equipment_asset.purchase_store_contact IS '購入店連絡先';
COMMENT ON COLUMN equipment_asset.purchase_store_postal_code IS '購入店郵便番号';
COMMENT ON COLUMN equipment_asset.purchase_store_address IS '購入店住所';
COMMENT ON COLUMN equipment_asset.warranty_expiry_date IS '保証期限';

CREATE INDEX IF NOT EXISTS idx_equipment_asset_item ON equipment_asset (equipment_id) WHERE deleted_at IS NULL;
