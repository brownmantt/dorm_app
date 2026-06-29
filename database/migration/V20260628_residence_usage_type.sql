-- 入居履歴に利用形態コードを追加（既存 DB 向け）
-- 実行例: psql -h localhost -U dorm_app -d dormitory -f database/migration/V20260628_residence_usage_type.sql

ALTER TABLE residence_history
    ADD COLUMN IF NOT EXISTS usage_type_code VARCHAR(30);

UPDATE residence_history
SET usage_type_code = 'NORMAL'
WHERE usage_type_code IS NULL;

ALTER TABLE residence_history
    ALTER COLUMN usage_type_code SET NOT NULL;

COMMENT ON COLUMN residence_history.usage_type_code IS '利用形態コード（usage_type.code 参照）';
