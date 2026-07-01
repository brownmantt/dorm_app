-- 保管場所マスタ追加、備品保管の保管場所をマスタ参照に変更

CREATE TABLE storage_location (
    storage_location_id VARCHAR(20)  NOT NULL,
    name                VARCHAR(100) NOT NULL,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at          TIMESTAMPTZ,
    CONSTRAINT pk_storage_location PRIMARY KEY (storage_location_id)
);
CREATE UNIQUE INDEX uk_storage_location_name ON storage_location (name) WHERE deleted_at IS NULL;
COMMENT ON TABLE storage_location IS '保管場所マスタ';

INSERT INTO storage_location (storage_location_id, name) VALUES
    ('SL202606300001', '本社倉庫1階'),
    ('SL202606300002', '本社倉庫2階'),
    ('SL202606300003', '女子寮A倉庫');

ALTER TABLE equipment_storage ADD COLUMN storage_location_id VARCHAR(20);

UPDATE equipment_storage es
SET storage_location_id = sl.storage_location_id
FROM storage_location sl
WHERE es.storage_location = sl.name
  AND sl.deleted_at IS NULL;

INSERT INTO storage_location (storage_location_id, name)
SELECT 'SL' || to_char(now(), 'YYYYMMDD') || lpad((ROW_NUMBER() OVER (ORDER BY es.storage_location))::text, 4, '0'),
       es.storage_location
FROM equipment_storage es
WHERE es.storage_location_id IS NULL
  AND es.storage_location IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM storage_location sl
      WHERE sl.name = es.storage_location AND sl.deleted_at IS NULL
  )
GROUP BY es.storage_location;

UPDATE equipment_storage es
SET storage_location_id = sl.storage_location_id
FROM storage_location sl
WHERE es.storage_location_id IS NULL
  AND es.storage_location = sl.name
  AND sl.deleted_at IS NULL;

ALTER TABLE equipment_storage ALTER COLUMN storage_location_id SET NOT NULL;
ALTER TABLE equipment_storage DROP COLUMN storage_location;
ALTER TABLE equipment_storage
    ADD CONSTRAINT fk_storage_location
        FOREIGN KEY (storage_location_id) REFERENCES storage_location (storage_location_id);
