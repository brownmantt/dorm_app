-- ============================================================
-- 寮管理システム 初期設定データ（システム設定のみ）
-- 業務データ（寮・社員等）は含めない。寮費算定に必須の単価設定のみ投入する。
-- 対象 DBMS : PostgreSQL 15
-- 単価は研修用の暫定値。運用に合わせて調整すること。
-- ============================================================

INSERT INTO fee_rate_config (room_type, unit_rate_per_sqm, valid_from) VALUES
    ('STANDARD', 3350.00, DATE '2000-01-01'),
    ('SMALL',    3000.00, DATE '2000-01-01'),
    ('OTHER',    2800.00, DATE '2000-01-01')
ON CONFLICT (room_type, valid_from) DO NOTHING;
