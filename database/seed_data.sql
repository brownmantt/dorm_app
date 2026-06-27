SET client_encoding TO 'UTF8';

INSERT INTO affiliation (affiliation_id, code, name, display_order, created_at, updated_at) VALUES
    ('AF00001', 'SHAIN', '社員', 1, '2024-01-01 09:00:00+09', '2024-01-01 09:00:00+09'),
    ('AF00002', 'CDXT', 'CDXT', 2, '2024-01-01 09:00:00+09', '2024-01-01 09:00:00+09'),
    ('AF00003', 'DALIAN', '大連', 3, '2024-01-01 09:00:00+09', '2024-01-01 09:00:00+09'),
    ('AF00004', 'SHENYANG', '瀋陽', 4, '2024-01-01 09:00:00+09', '2024-01-01 09:00:00+09');

INSERT INTO dormitory (dormitory_id, name, postal_code, address, layout_type, gender_type, nearest_station_1, nearest_station_2, nearest_station_3, remarks, created_at, updated_at) VALUES
    ('D001', '男子寮A', '1400002', '東京都品川区東品川1-1-1', '3DK', 'MALE', NULL, NULL, NULL, '研修用データ', '2024-01-10 09:00:00+09', '2024-01-10 09:00:00+09'),
    ('D002', '女子寮A', '1400002', '東京都品川区東品川2-2-2', '2DK', 'FEMALE', NULL, NULL, NULL, NULL, '2024-01-10 09:00:00+09', '2024-01-10 09:00:00+09'),
    ('D003', '男子寮B', '5300001', '大阪府大阪市北区3-3-3', '3DK', 'MALE', NULL, NULL, NULL, NULL, '2024-02-01 09:00:00+09', '2024-02-01 09:00:00+09'),
    ('D004', '女子寮B', '4600008', '愛知県名古屋市中区4-4-4', '2DK', 'FEMALE', NULL, NULL, NULL, NULL, '2024-02-15 09:00:00+09', '2024-02-15 09:00:00+09'),
    ('D005', '男子寮C', '8120011', '福岡県福岡市博多区5-5-5', '3DK', 'MALE', NULL, NULL, NULL, '空き室確認用', '2024-03-01 09:00:00+09', '2024-03-01 09:00:00+09');

INSERT INTO room (room_id, dormitory_id, room_name, area_sqm, capacity, room_type, created_at, updated_at) VALUES
    ('R001', 'D001', '101号室', 25.50, 1, 'STANDARD', '2024-01-11 09:00:00+09', '2024-01-11 09:00:00+09'),
    ('R002', 'D001', '102号室', 18.00, 1, 'SMALL', '2024-01-11 09:00:00+09', '2024-01-11 09:00:00+09'),
    ('R003', 'D002', '201号室', 22.00, 1, 'STANDARD', '2024-01-12 09:00:00+09', '2024-01-12 09:00:00+09'),
    ('R004', 'D002', '202号室', 15.50, 1, 'SMALL', '2024-01-12 09:00:00+09', '2024-01-12 09:00:00+09'),
    ('R005', 'D003', '301号室', 20.00, 1, 'OTHER', '2024-02-02 09:00:00+09', '2024-02-02 09:00:00+09');

INSERT INTO employee (employee_id, name, gender, employee_category, created_at, updated_at) VALUES
    ('E00001', '田中太郎', 'MALE', 'JAPAN', '2024-01-15 09:00:00+09', '2024-01-15 09:00:00+09'),
    ('E00002', '佐藤花子', 'FEMALE', 'JAPAN', '2024-01-15 09:00:00+09', '2024-01-15 09:00:00+09'),
    ('E00003', '王偉', 'MALE', 'CHINA_ASSIGN', '2024-01-16 09:00:00+09', '2024-01-16 09:00:00+09'),
    ('E00004', '李芳', 'FEMALE', 'CHINA_ASSIGN', '2024-01-16 09:00:00+09', '2024-01-16 09:00:00+09'),
    ('E00005', '鈴木一郎', 'MALE', 'JAPAN', '2024-01-17 09:00:00+09', '2024-01-17 09:00:00+09');

INSERT INTO residence_history (residence_history_id, employee_id, dormitory_id, room_id, move_in_date, move_out_date, move_out_reason, created_at, updated_at) VALUES
    ('RH00001', 'E00001', 'D001', 'R001', DATE '2024-04-01', DATE '2025-03-31', '転勤', '2024-04-01 10:00:00+09', '2025-03-31 18:00:00+09'),
    ('RH00002', 'E00005', 'D001', 'R001', DATE '2025-06-01', NULL, NULL, '2025-06-01 10:00:00+09', '2025-06-01 10:00:00+09'),
    ('RH00003', 'E00002', 'D002', 'R003', DATE '2025-01-15', NULL, NULL, '2025-01-15 10:00:00+09', '2025-01-15 10:00:00+09'),
    ('RH00004', 'E00003', 'D001', 'R002', DATE '2024-06-01', DATE '2024-12-31', '出張終了', '2024-06-01 10:00:00+09', '2024-12-31 18:00:00+09'),
    ('RH00005', 'E00004', 'D002', 'R004', DATE '2023-08-01', DATE '2024-07-31', '帰国', '2023-08-01 10:00:00+09', '2024-07-31 18:00:00+09');

INSERT INTO employee_first_dorm_use (employee_id, first_use_date, created_at, updated_at) VALUES
    ('E00001', DATE '2024-04-01', '2024-04-01 10:00:00+09', '2024-04-01 10:00:00+09'),
    ('E00002', DATE '2025-01-15', '2025-01-15 10:00:00+09', '2025-01-15 10:00:00+09'),
    ('E00005', DATE '2025-06-01', '2025-06-01 10:00:00+09', '2025-06-01 10:00:00+09');

INSERT INTO fee_rate_config (room_type, unit_rate_per_sqm, valid_from, created_at, updated_at) VALUES
    ('STANDARD', 3350.00, DATE '2000-01-01', '2024-01-01 09:00:00+09', '2024-01-01 09:00:00+09'),
    ('SMALL', 3000.00, DATE '2000-01-01', '2024-01-01 09:00:00+09', '2024-01-01 09:00:00+09'),
    ('OTHER', 2800.00, DATE '2000-01-01', '2024-01-01 09:00:00+09', '2024-01-01 09:00:00+09'),
    ('STANDARD', 3500.00, DATE '2025-01-01', '2025-01-01 09:00:00+09', '2025-01-01 09:00:00+09'),
    ('SMALL', 3200.00, DATE '2025-01-01', '2025-01-01 09:00:00+09', '2025-01-01 09:00:00+09')
ON CONFLICT (room_type, valid_from) DO NOTHING;

INSERT INTO dorm_fee (dorm_fee_id, employee_id, room_id, target_year_month, amount, basis_area_sqm, basis_days, basis_detail, status, residence_history_id, created_at, updated_at) VALUES
    ('DF00001', 'E00001', 'R001', '2024-04', 85425, 25.50, 30, '{"monthlyRate":85425,"billableDays":30}'::jsonb, 'CONFIRMED', 'RH00001', '2024-05-01 09:00:00+09', '2024-05-01 09:00:00+09'),
    ('DF00002', 'E00005', 'R001', '2025-06', 85425, 25.50, 30, '{"monthlyRate":85425,"billableDays":30}'::jsonb, 'DRAFT', 'RH00002', '2025-06-05 09:00:00+09', '2025-06-05 09:00:00+09'),
    ('DF00003', 'E00002', 'R003', '2025-01', 40700, 22.00, 17, '{"monthlyRate":73700,"billableDays":17}'::jsonb, 'CONFIRMED', 'RH00003', '2025-02-01 09:00:00+09', '2025-02-01 09:00:00+09'),
    ('DF00004', 'E00003', 'R002', '2024-06', 54000, 18.00, 30, '{"monthlyRate":54000,"billableDays":30}'::jsonb, 'DRAFT', 'RH00004', '2024-07-01 09:00:00+09', '2024-07-01 09:00:00+09'),
    ('DF00005', 'E00004', 'R004', '2023-08', 46500, 15.50, 31, '{"monthlyRate":46500,"billableDays":31}'::jsonb, 'CONFIRMED', 'RH00005', '2023-09-01 09:00:00+09', '2023-09-01 09:00:00+09');

INSERT INTO equipment (equipment_id, name, equipment_type, created_at, updated_at) VALUES
    ('EQ00001', 'シングルベッド', 'FURNITURE', '2024-01-20 09:00:00+09', '2024-01-20 09:00:00+09'),
    ('EQ00002', 'デスク', 'FURNITURE', '2024-01-20 09:00:00+09', '2024-01-20 09:00:00+09'),
    ('EQ00003', 'オフィスチェア', 'FURNITURE', '2024-01-21 09:00:00+09', '2024-01-21 09:00:00+09'),
    ('EQ00004', 'エアコン', 'APPLIANCE', '2024-01-21 09:00:00+09', '2024-01-21 09:00:00+09'),
    ('EQ00005', 'カーテン', 'OTHER', '2024-01-22 09:00:00+09', '2024-01-22 09:00:00+09');

INSERT INTO equipment_moveout (moveout_id, residence_history_id, equipment_id, disposition, processed_at, remarks, created_by) VALUES
    ('MO00001', 'RH00001', 'EQ00001', 'DISCARD', '2025-03-31 19:00:00+09', '老朽化', 'admin'),
    ('MO00002', 'RH00001', 'EQ00002', 'STORE', '2025-03-31 19:00:00+09', '保管へ', 'admin'),
    ('MO00003', 'RH00004', 'EQ00003', 'REUSE', '2024-12-31 19:00:00+09', '他部屋へ', 'admin'),
    ('MO00004', 'RH00005', 'EQ00004', 'DISCARD', '2024-07-31 19:00:00+09', NULL, 'admin'),
    ('MO00005', 'RH00005', 'EQ00005', 'STORE', '2024-07-31 19:00:00+09', '倉庫B', 'admin');

INSERT INTO equipment_storage (storage_id, equipment_id, storage_location, status, linked_moveout_id, created_at, updated_at) VALUES
    ('ST00001', 'EQ00002', '本社倉庫1階', 'IN_STORAGE', 'MO00002', '2025-04-01 09:00:00+09', '2025-04-01 09:00:00+09'),
    ('ST00002', 'EQ00005', '本社倉庫2階', 'IN_STORAGE', 'MO00005', '2024-08-01 09:00:00+09', '2024-08-01 09:00:00+09'),
    ('ST00003', 'EQ00003', '女子寮A倉庫', 'REUSED', 'MO00003', '2025-01-10 09:00:00+09', '2025-01-10 09:00:00+09'),
    ('ST00004', 'EQ00004', '本社倉庫1階', 'IN_STORAGE', NULL, '2024-08-02 09:00:00+09', '2024-08-02 09:00:00+09'),
    ('ST00005', 'EQ00001', '廃棄待ちエリア', 'IN_STORAGE', NULL, '2025-04-02 09:00:00+09', '2025-04-02 09:00:00+09');

INSERT INTO operation_log (operation_type, target_table, target_id, before_value, after_value, operated_by, operated_at) VALUES
    ('RESIDENCE_CHECKIN', 'residence_history', 'RH00002', NULL, '{"employeeId":"E00005","roomId":"R001"}'::jsonb, 'admin', '2025-06-01 10:00:00+09'),
    ('RESIDENCE_CHECKOUT', 'residence_history', 'RH00001', '{"moveOutDate":null}'::jsonb, '{"moveOutDate":"2025-03-31"}'::jsonb, 'admin', '2025-03-31 18:00:00+09'),
    ('DORM_FEE_CONFIRM', 'dorm_fee', 'DF00001', '{"status":"DRAFT"}'::jsonb, '{"status":"CONFIRMED"}'::jsonb, 'admin', '2024-05-02 09:00:00+09'),
    ('EQUIPMENT_MOVEOUT', 'equipment_moveout', 'MO00001', NULL, '{"disposition":"DISCARD"}'::jsonb, 'admin', '2025-03-31 19:00:00+09'),
    ('EXCEL_IMPORT', 'excel_import_job', 'JOB00003', NULL, '{"status":"SUCCESS"}'::jsonb, 'admin', '2024-06-10 14:00:00+09');

INSERT INTO excel_import_job (job_id, file_name, status, executed_by, total_count, success_count, error_count, created_at, finished_at) VALUES
    ('JOB00001', 'dormitory_import_202401.xlsx', 'PENDING', 'admin', NULL, NULL, NULL, '2024-01-25 10:00:00+09', NULL),
    ('JOB00002', 'employee_import_202402.xlsx', 'PREVIEWED', 'admin', 120, NULL, 3, '2024-02-20 10:00:00+09', NULL),
    ('JOB00003', 'full_import_202406.xlsx', 'SUCCESS', 'admin', 50, 50, 0, '2024-06-10 13:00:00+09', '2024-06-10 14:00:00+09'),
    ('JOB00004', 'residence_import_202407.xlsx', 'FAILED', 'admin', 30, 0, 30, '2024-07-15 11:00:00+09', '2024-07-15 11:30:00+09'),
    ('JOB00005', 'room_import_202501.xlsx', 'SUCCESS', 'admin', 15, 15, 0, '2025-01-20 09:00:00+09', '2025-01-20 09:15:00+09');

INSERT INTO excel_import_error (job_id, row_number, field, message, created_at) VALUES
    ('JOB00004', 2, 'move_in_date', '日付形式が不正です', '2024-07-15 11:30:00+09'),
    ('JOB00004', 5, 'gender_type', '列挙値が不正です', '2024-07-15 11:30:00+09'),
    ('JOB00004', 8, 'room_id', '参照先の部屋が存在しません', '2024-07-15 11:30:00+09'),
    ('JOB00004', 12, 'employee_id', '社員IDが重複しています', '2024-07-15 11:30:00+09'),
    ('JOB00004', 15, 'dormitory_id', '性別と寮種別が不一致です', '2024-07-15 11:30:00+09');
