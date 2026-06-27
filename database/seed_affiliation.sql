SET client_encoding TO 'UTF8';

INSERT INTO affiliation (affiliation_id, code, name, display_order, created_at, updated_at) VALUES
    ('AF00001', 'SHAIN', '社員', 1, '2024-01-01 09:00:00+09', '2024-01-01 09:00:00+09'),
    ('AF00002', 'CDXT', 'CDXT', 2, '2024-01-01 09:00:00+09', '2024-01-01 09:00:00+09'),
    ('AF00003', 'DALIAN', '大連', 3, '2024-01-01 09:00:00+09', '2024-01-01 09:00:00+09'),
    ('AF00004', 'SHENYANG', '瀋陽', 4, '2024-01-01 09:00:00+09', '2024-01-01 09:00:00+09')
ON CONFLICT (affiliation_id) DO NOTHING;
