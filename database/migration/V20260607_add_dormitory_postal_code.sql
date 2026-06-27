-- 寮マスタに郵便番号カラムを追加
ALTER TABLE dormitory
    ADD COLUMN IF NOT EXISTS postal_code VARCHAR(7);

COMMENT ON COLUMN dormitory.postal_code IS '郵便番号（7桁、ハイフンなし）';

-- 既存データにサンプル郵便番号を設定（開発・検証用）
UPDATE dormitory SET postal_code = '1400002' WHERE dormitory_id = 'D001';
UPDATE dormitory SET postal_code = '1400002' WHERE dormitory_id = 'D002';
UPDATE dormitory SET postal_code = '5300001' WHERE dormitory_id = 'D003';
UPDATE dormitory SET postal_code = '4600008' WHERE dormitory_id = 'D004';
UPDATE dormitory SET postal_code = '8120011' WHERE dormitory_id = 'D005';

ALTER TABLE dormitory
    ALTER COLUMN postal_code SET NOT NULL;
