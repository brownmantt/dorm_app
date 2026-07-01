-- 備品保管を備品（個体）参照・保管数量対応に変更

ALTER TABLE equipment_storage ADD COLUMN equipment_asset_id VARCHAR(20);
ALTER TABLE equipment_storage ADD COLUMN storage_quantity INTEGER NOT NULL DEFAULT 1;

UPDATE equipment_storage es
SET equipment_asset_id = sub.equipment_asset_id
FROM (
    SELECT DISTINCT ON (es2.storage_id)
        es2.storage_id,
        ea.equipment_asset_id
    FROM equipment_storage es2
    JOIN equipment_asset ea ON ea.equipment_id = es2.equipment_id AND ea.deleted_at IS NULL
    ORDER BY es2.storage_id, ea.created_at
) sub
WHERE es.storage_id = sub.storage_id
  AND es.equipment_asset_id IS NULL;

DELETE FROM equipment_storage WHERE equipment_asset_id IS NULL;

ALTER TABLE equipment_storage DROP CONSTRAINT IF EXISTS fk_storage_equipment;
ALTER TABLE equipment_storage DROP COLUMN equipment_id;

ALTER TABLE equipment_storage ALTER COLUMN equipment_asset_id SET NOT NULL;
ALTER TABLE equipment_storage
    ADD CONSTRAINT fk_storage_asset
        FOREIGN KEY (equipment_asset_id) REFERENCES equipment_asset (equipment_asset_id);

CREATE UNIQUE INDEX uk_storage_asset_location
    ON equipment_storage (equipment_asset_id, storage_location_id);
