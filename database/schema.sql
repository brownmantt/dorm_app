SET client_encoding TO 'UTF8';

-- ============================================================
-- 寮管理システム データベース DDL
-- 対象 DBMS : PostgreSQL 15
-- 文字コード : UTF-8
-- 設計根拠   : 詳細設計書_0522/06_データベース設計.md ほか各モジュール設計書
-- 命名規約   : snake_case、論理削除は deleted_at(TIMESTAMPTZ)
-- ============================================================

-- 旧 EXCLUDE 制約用拡張（既存 DB 互換のため残置。新規は定員トリガーで検証）
CREATE EXTENSION IF NOT EXISTS btree_gist;

-- ============================================================
-- 1. 寮・部屋管理（F-01, F-02）
-- ============================================================

-- 1.1 寮マスタ
CREATE TABLE dormitory (
    dormitory_id  VARCHAR(20)  NOT NULL,
    name          VARCHAR(100) NOT NULL,
    region        VARCHAR(20),                           -- TOKYO / OSAKA / NAGOYA / OTHER
    postal_code   VARCHAR(7)   NOT NULL,                 -- 郵便番号（7桁、ハイフンなし）
    address       VARCHAR(200) NOT NULL,
    layout_type   VARCHAR(10)  NOT NULL,                 -- 間取り（3DK/2DK 等）
    gender_type   VARCHAR(10)  NOT NULL,                 -- MALE / FEMALE
    nearest_station_1 VARCHAR(20),                     -- 最寄駅１
    nearest_station_2 VARCHAR(20),                     -- 最寄駅２
    nearest_station_3 VARCHAR(20),                     -- 最寄駅３
    remarks       TEXT,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at    TIMESTAMPTZ,
    CONSTRAINT pk_dormitory PRIMARY KEY (dormitory_id),
    CONSTRAINT ck_dormitory_gender CHECK (gender_type IN ('MALE', 'FEMALE'))
);
COMMENT ON TABLE  dormitory IS '寮マスタ';
COMMENT ON COLUMN dormitory.postal_code IS '郵便番号（7桁、ハイフンなし）';
COMMENT ON COLUMN dormitory.gender_type IS 'MALE/FEMALE。変更はアプリ層で禁止（DORM_GENDER_CHANGE_FORBIDDEN）';
COMMENT ON COLUMN dormitory.nearest_station_1 IS '最寄駅１（最大20文字）';
COMMENT ON COLUMN dormitory.nearest_station_2 IS '最寄駅２（最大20文字）';
COMMENT ON COLUMN dormitory.nearest_station_3 IS '最寄駅３（最大20文字）';

-- 1.2 部屋マスタ
CREATE TABLE room (
    room_id              VARCHAR(20)  NOT NULL,
    dormitory_id         VARCHAR(20)  NOT NULL,
    room_name            VARCHAR(50)  NOT NULL,
    room_detail          VARCHAR(100),                   -- 部屋詳細（洋室等）
    has_air_conditioner  BOOLEAN      NOT NULL DEFAULT FALSE,
    monthly_fee          DECIMAL(10,0),                  -- 寮費単価
    area_sqm             DECIMAL(6,2) NOT NULL,          -- 面積（㎡）
    capacity             INT          NOT NULL DEFAULT 1, -- 定員（既定 1）
    room_type            VARCHAR(20)  NOT NULL,          -- STANDARD/SMALL/OTHER
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at    TIMESTAMPTZ,
    CONSTRAINT pk_room PRIMARY KEY (room_id),
    CONSTRAINT fk_room_dormitory FOREIGN KEY (dormitory_id) REFERENCES dormitory (dormitory_id),
    CONSTRAINT ck_room_area CHECK (area_sqm > 0),
    CONSTRAINT ck_room_capacity CHECK (capacity >= 1)
);
COMMENT ON TABLE room IS '部屋マスタ';

-- ============================================================
-- 2. 入退寮管理（F-03, F-04, F-05）
-- ============================================================

-- 2.1 社員／入居者
CREATE TABLE employee (
    employee_id        VARCHAR(20)  NOT NULL,
    name               VARCHAR(100) NOT NULL,
    gender             VARCHAR(10)  NOT NULL,            -- MALE / FEMALE
    employee_category  VARCHAR(20)  NOT NULL,            -- JAPAN / CHINA_ASSIGN
    affiliation_id     VARCHAR(20),                      -- FK → affiliation
    business_division  VARCHAR(30),
    nearest_station_1  VARCHAR(20),                      -- 最寄駅１
    nearest_station_2  VARCHAR(20),                      -- 最寄駅２
    nearest_station_3  VARCHAR(20),                      -- 最寄駅３
    contact_info       JSONB,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at         TIMESTAMPTZ,
    CONSTRAINT pk_employee PRIMARY KEY (employee_id),
    CONSTRAINT ck_employee_gender CHECK (gender IN ('MALE', 'FEMALE')),
    CONSTRAINT ck_employee_category CHECK (employee_category IN ('JAPAN', 'CHINA_ASSIGN'))
);
COMMENT ON TABLE employee IS '社員／入居者（SC-16 社員マスタ・Excel 取込・将来人事 API 連携）';
COMMENT ON COLUMN employee.nearest_station_1 IS '最寄駅１（最大20文字）';
COMMENT ON COLUMN employee.nearest_station_2 IS '最寄駅２（最大20文字）';
COMMENT ON COLUMN employee.nearest_station_3 IS '最寄駅３（最大20文字）';

-- 2.2 入居履歴
CREATE TABLE residence_history (
    residence_history_id  VARCHAR(20)  NOT NULL,
    employee_id           VARCHAR(20)  NOT NULL,
    dormitory_id          VARCHAR(20)  NOT NULL,         -- 非正規化（検索用）
    room_id               VARCHAR(20)  NOT NULL,
    move_in_date          DATE         NOT NULL,
    move_out_date         DATE,                          -- NULL = 入居中
    move_out_reason       VARCHAR(200),
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at            TIMESTAMPTZ,
    CONSTRAINT pk_residence_history PRIMARY KEY (residence_history_id),
    CONSTRAINT fk_residence_employee  FOREIGN KEY (employee_id)  REFERENCES employee (employee_id),
    CONSTRAINT fk_residence_dormitory FOREIGN KEY (dormitory_id) REFERENCES dormitory (dormitory_id),
    CONSTRAINT fk_residence_room      FOREIGN KEY (room_id)      REFERENCES room (room_id),
    CONSTRAINT ck_residence_period CHECK (move_out_date IS NULL OR move_out_date >= move_in_date)
);
COMMENT ON TABLE residence_history IS '入居履歴';

-- 同一部屋の定員超過防止（論理削除レコードは対象外）
-- 期間は [move_in_date, COALESCE(move_out_date, 'infinity')] の閉区間で判定
CREATE OR REPLACE FUNCTION fn_check_residence_room_capacity()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_capacity INTEGER;
    v_max_concurrent INTEGER;
    v_period_end DATE;
BEGIN
    IF NEW.deleted_at IS NOT NULL THEN
        RETURN NEW;
    END IF;

    SELECT COALESCE(NULLIF(r.capacity, 0), 1)
      INTO v_capacity
      FROM room r
     WHERE r.room_id = NEW.room_id
       AND r.deleted_at IS NULL;

    IF v_capacity IS NULL THEN
        RAISE EXCEPTION 'Room not found: %', NEW.room_id
            USING ERRCODE = 'foreign_key_violation';
    END IF;

    v_period_end := COALESCE(
        NEW.move_out_date,
        (
            SELECT MAX(COALESCE(rh.move_out_date, NEW.move_in_date))
              FROM residence_history rh
             WHERE rh.room_id = NEW.room_id
               AND rh.deleted_at IS NULL
               AND rh.residence_history_id IS DISTINCT FROM NEW.residence_history_id
               AND daterange(rh.move_in_date, COALESCE(rh.move_out_date, 'infinity'::date), '[]')
                   && daterange(NEW.move_in_date, COALESCE(NEW.move_out_date, 'infinity'::date), '[]')
        ),
        NEW.move_in_date
    );

    WITH overlapping AS (
        SELECT rh.move_in_date, rh.move_out_date
          FROM residence_history rh
         WHERE rh.room_id = NEW.room_id
           AND rh.deleted_at IS NULL
           AND rh.residence_history_id IS DISTINCT FROM NEW.residence_history_id
           AND daterange(rh.move_in_date, COALESCE(rh.move_out_date, 'infinity'::date), '[]')
               && daterange(NEW.move_in_date, COALESCE(NEW.move_out_date, 'infinity'::date), '[]')
    ),
    check_dates AS (
        SELECT NEW.move_in_date AS check_date
        UNION
        SELECT o.move_in_date
          FROM overlapping o
         WHERE o.move_in_date >= NEW.move_in_date
           AND o.move_in_date <= v_period_end
        UNION
        SELECT o.move_out_date + 1
          FROM overlapping o
         WHERE o.move_out_date IS NOT NULL
           AND o.move_out_date + 1 >= NEW.move_in_date
           AND o.move_out_date + 1 <= v_period_end
    )
    SELECT COALESCE(MAX(active_count), 0)
      INTO v_max_concurrent
      FROM (
        SELECT cd.check_date,
               (
                   SELECT COUNT(*)
                     FROM overlapping o
                    WHERE o.move_in_date <= cd.check_date
                      AND (o.move_out_date IS NULL OR o.move_out_date >= cd.check_date)
               ) AS active_count
          FROM check_dates cd
         WHERE cd.check_date >= NEW.move_in_date
           AND cd.check_date <= v_period_end
      ) counts;

    IF v_max_concurrent >= v_capacity THEN
        RAISE EXCEPTION 'Room capacity exceeded'
            USING ERRCODE = 'check_violation',
                  DETAIL = format('room_id=%s capacity=%s concurrent=%s', NEW.room_id, v_capacity, v_max_concurrent);
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_residence_room_capacity
    BEFORE INSERT OR UPDATE OF room_id, move_in_date, move_out_date, deleted_at
    ON residence_history
    FOR EACH ROW
    EXECUTE FUNCTION fn_check_residence_room_capacity();

-- 2.3 初回利用日（日本社員のみ）
CREATE TABLE employee_first_dorm_use (
    employee_id     VARCHAR(20) NOT NULL,
    first_use_date  DATE        NOT NULL,                -- 初めて寮を使用した入寮日（不変）
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT pk_employee_first_dorm_use PRIMARY KEY (employee_id),
    CONSTRAINT fk_first_use_employee FOREIGN KEY (employee_id) REFERENCES employee (employee_id)
);
COMMENT ON TABLE employee_first_dorm_use IS '初回利用日（employee_category=JAPAN のみ。既存は更新しない）';

-- ============================================================
-- 3. 寮費管理（F-06）
-- ============================================================

-- 3.1 寮費単価設定（パラメータ管理。application.yml 管理でも可）
CREATE TABLE fee_rate_config (
    room_type         VARCHAR(20)  NOT NULL,             -- STANDARD/SMALL/OTHER
    unit_rate_per_sqm DECIMAL(10,2) NOT NULL,            -- ㎡あたり月額単価
    valid_from        DATE         NOT NULL DEFAULT DATE '2000-01-01',
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT pk_fee_rate_config PRIMARY KEY (room_type, valid_from)
);
COMMENT ON TABLE fee_rate_config IS '寮費単価設定（room_type 別の㎡単価）';

-- 3.2 寮費
CREATE TABLE dorm_fee (
    dorm_fee_id           VARCHAR(20)   NOT NULL,
    employee_id           VARCHAR(20)   NOT NULL,
    room_id               VARCHAR(20)   NOT NULL,
    target_year_month     CHAR(7)       NOT NULL,        -- YYYY-MM
    amount                DECIMAL(10,0) NOT NULL,        -- 算出金額
    basis_area_sqm        DECIMAL(6,2)  NOT NULL,        -- 算出根拠：面積
    basis_days            INT           NOT NULL,        -- 算出根拠：日数
    basis_detail          JSONB,                         -- 算定内訳
    status                VARCHAR(20)   NOT NULL DEFAULT 'DRAFT',   -- DRAFT / CONFIRMED
    residence_history_id  VARCHAR(20),
    created_at            TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ   NOT NULL DEFAULT now(),
    deleted_at            TIMESTAMPTZ,
    CONSTRAINT pk_dorm_fee PRIMARY KEY (dorm_fee_id),
    CONSTRAINT fk_dorm_fee_employee  FOREIGN KEY (employee_id) REFERENCES employee (employee_id),
    CONSTRAINT fk_dorm_fee_room      FOREIGN KEY (room_id)     REFERENCES room (room_id),
    CONSTRAINT fk_dorm_fee_residence FOREIGN KEY (residence_history_id) REFERENCES residence_history (residence_history_id),
    CONSTRAINT ck_dorm_fee_status CHECK (status IN ('DRAFT', 'CONFIRMED')),
    CONSTRAINT ck_dorm_fee_ym CHECK (target_year_month ~ '^[0-9]{4}-[0-9]{2}$')
);
COMMENT ON TABLE dorm_fee IS '寮費';

-- 重複登録防止：未削除レコードで (employee_id, target_year_month) を一意化
CREATE UNIQUE INDEX uk_dorm_fee_emp_ym
    ON dorm_fee (employee_id, target_year_month)
    WHERE deleted_at IS NULL;

-- ============================================================
-- 4. 備品管理（F-07, F-08, F-09）
-- ============================================================

-- 4.1 備品マスタ
CREATE TABLE equipment (
    equipment_id    VARCHAR(20)  NOT NULL,
    name            VARCHAR(100) NOT NULL,
    equipment_type  VARCHAR(30)  NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT pk_equipment PRIMARY KEY (equipment_id)
);
COMMENT ON TABLE equipment IS '備品マスタ';

-- 4.2 退去時備品処理
CREATE TABLE equipment_moveout (
    moveout_id            VARCHAR(20)  NOT NULL,
    residence_history_id  VARCHAR(20)  NOT NULL,
    equipment_id          VARCHAR(20)  NOT NULL,
    disposition           VARCHAR(20)  NOT NULL,         -- DISCARD / STORE / REUSE
    processed_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    remarks               TEXT,
    created_by            VARCHAR(50)  NOT NULL,
    CONSTRAINT pk_equipment_moveout PRIMARY KEY (moveout_id),
    CONSTRAINT fk_moveout_residence FOREIGN KEY (residence_history_id) REFERENCES residence_history (residence_history_id),
    CONSTRAINT fk_moveout_equipment FOREIGN KEY (equipment_id) REFERENCES equipment (equipment_id),
    CONSTRAINT ck_moveout_disposition CHECK (disposition IN ('DISCARD', 'STORE', 'REUSE'))
);
COMMENT ON TABLE equipment_moveout IS '退去時備品処理';

-- 4.3 備品保管
CREATE TABLE equipment_storage (
    storage_id        VARCHAR(20)  NOT NULL,
    equipment_id      VARCHAR(20)  NOT NULL,
    storage_location  VARCHAR(100) NOT NULL,
    status            VARCHAR(20)  NOT NULL DEFAULT 'IN_STORAGE',  -- IN_STORAGE / REUSED 等
    linked_moveout_id VARCHAR(20),
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT pk_equipment_storage PRIMARY KEY (storage_id),
    CONSTRAINT fk_storage_equipment FOREIGN KEY (equipment_id) REFERENCES equipment (equipment_id),
    CONSTRAINT fk_storage_moveout   FOREIGN KEY (linked_moveout_id) REFERENCES equipment_moveout (moveout_id)
);
COMMENT ON TABLE equipment_storage IS '備品保管';

-- ============================================================
-- 5. 操作ログ（F-12 / 共通）
-- ============================================================
CREATE TABLE operation_log (
    log_id         BIGSERIAL    NOT NULL,
    operation_type VARCHAR(30)  NOT NULL,                -- 例：RESIDENCE_CHECKIN
    target_table   VARCHAR(50)  NOT NULL,
    target_id      VARCHAR(20)  NOT NULL,
    before_value   JSONB,
    after_value    JSONB,
    operated_by    VARCHAR(50)  NOT NULL,
    operated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT pk_operation_log PRIMARY KEY (log_id)
);
COMMENT ON TABLE operation_log IS '操作ログ（監査）';

-- ============================================================
-- 6. Excel 取込（F-11）
-- ============================================================

-- 6.1 取込ジョブ
CREATE TABLE excel_import_job (
    job_id        VARCHAR(20)  NOT NULL,
    file_name     VARCHAR(255) NOT NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING',  -- PENDING/PREVIEWED/SUCCESS/FAILED
    executed_by   VARCHAR(50)  NOT NULL,
    total_count   INT,
    success_count INT,
    error_count   INT,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    finished_at   TIMESTAMPTZ,
    CONSTRAINT pk_excel_import_job PRIMARY KEY (job_id)
);
COMMENT ON TABLE excel_import_job IS 'Excel 取込ジョブ';

-- 6.2 取込エラー明細
CREATE TABLE excel_import_error (
    error_id    BIGSERIAL    NOT NULL,
    job_id      VARCHAR(20)  NOT NULL,
    row_number  INT          NOT NULL,
    field       VARCHAR(100),
    message     VARCHAR(500) NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT pk_excel_import_error PRIMARY KEY (error_id),
    CONSTRAINT fk_import_error_job FOREIGN KEY (job_id) REFERENCES excel_import_job (job_id)
);
COMMENT ON TABLE excel_import_error IS 'Excel 取込エラー明細';

-- ============================================================
-- 7. 所属・責任者（F-15, F-20）
-- ============================================================

CREATE TABLE region (
    region_id     VARCHAR(20)  NOT NULL,
    code          VARCHAR(30)  NOT NULL,
    name          VARCHAR(100) NOT NULL,
    display_order INT,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at    TIMESTAMPTZ,
    CONSTRAINT pk_region PRIMARY KEY (region_id)
);
CREATE UNIQUE INDEX uk_region_code ON region (code) WHERE deleted_at IS NULL;
COMMENT ON TABLE region IS '地域マスタ';

INSERT INTO region (region_id, code, name, display_order) VALUES
('RG202606270001', 'TOKYO', '東京', 1),
('RG202606270002', 'OSAKA', '大阪', 2),
('RG202606270003', 'NAGOYA', '名古屋', 3),
('RG202606270004', 'OTHER', 'その他', 4);

CREATE TABLE usage_type (
    usage_type_id VARCHAR(20)  NOT NULL,
    code          VARCHAR(30)  NOT NULL,
    name          VARCHAR(100) NOT NULL,
    display_order INT,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at    TIMESTAMPTZ,
    CONSTRAINT pk_usage_type PRIMARY KEY (usage_type_id)
);
CREATE UNIQUE INDEX uk_usage_type_code ON usage_type (code) WHERE deleted_at IS NULL;
COMMENT ON TABLE usage_type IS '利用形態マスタ';

INSERT INTO usage_type (usage_type_id, code, name, display_order) VALUES
('UT202606270001', 'NORMAL', '通常利用', 1),
('UT202606270002', 'BUSINESS_TRIP', '出張利用', 2),
('UT202606270003', 'LONG_TERM', '長期利用', 3);

CREATE TABLE unit_price (
    unit_price_id    VARCHAR(20)    NOT NULL,
    code             VARCHAR(30)    NOT NULL,
    region           VARCHAR(30)    NOT NULL,
    dormitory_id     VARCHAR(20),
    room_id          VARCHAR(20),
    usage_type_code  VARCHAR(30)    NOT NULL,
    daily_unit_price DECIMAL(10, 2) NOT NULL,
    max_usage_days   INT            NOT NULL,
    created_at       TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ    NOT NULL DEFAULT now(),
    deleted_at       TIMESTAMPTZ,
    CONSTRAINT pk_unit_price PRIMARY KEY (unit_price_id),
    CONSTRAINT ck_unit_price_daily CHECK (daily_unit_price >= 0),
    CONSTRAINT ck_unit_price_max_days CHECK (max_usage_days >= -1)
);
CREATE UNIQUE INDEX uk_unit_price_code ON unit_price (code) WHERE deleted_at IS NULL;
COMMENT ON TABLE unit_price IS '単価マスタ';

CREATE TABLE affiliation (
    affiliation_id  VARCHAR(20)  NOT NULL,
    code          VARCHAR(30)  NOT NULL,
    name          VARCHAR(100) NOT NULL,
    display_order INT,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at    TIMESTAMPTZ,
    CONSTRAINT pk_affiliation PRIMARY KEY (affiliation_id)
);
CREATE UNIQUE INDEX uk_affiliation_code ON affiliation (code) WHERE deleted_at IS NULL;
COMMENT ON TABLE affiliation IS '所属マスタ';

CREATE TABLE dormitory_manager (
    dormitory_id          VARCHAR(20) NOT NULL,
    employee_id           VARCHAR(20) NOT NULL,
    residence_history_id  VARCHAR(20) NOT NULL,
    assigned_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    version               INT         NOT NULL DEFAULT 0,
    CONSTRAINT pk_dormitory_manager PRIMARY KEY (dormitory_id),
    CONSTRAINT fk_dm_dormitory FOREIGN KEY (dormitory_id) REFERENCES dormitory (dormitory_id),
    CONSTRAINT fk_dm_employee FOREIGN KEY (employee_id) REFERENCES employee (employee_id),
    CONSTRAINT fk_dm_residence FOREIGN KEY (residence_history_id) REFERENCES residence_history (residence_history_id)
);
COMMENT ON TABLE dormitory_manager IS '寮責任者';

-- ============================================================
-- 8. インデックス方針（詳細設計書 6.3）
-- ============================================================
CREATE INDEX idx_residence_room_period
    ON residence_history (room_id, move_in_date, move_out_date);

CREATE INDEX idx_residence_employee
    ON residence_history (employee_id, move_in_date);

CREATE INDEX idx_dorm_fee_ym_emp
    ON dorm_fee (target_year_month, employee_id);

CREATE INDEX idx_first_use_date
    ON employee_first_dorm_use (first_use_date);

-- 補助：論理削除を考慮した一覧検索
CREATE INDEX idx_dormitory_deleted ON dormitory (deleted_at);
CREATE INDEX idx_room_deleted      ON room (deleted_at);
CREATE INDEX idx_employee_deleted  ON employee (deleted_at);
CREATE INDEX idx_operation_log_type_at ON operation_log (operation_type, operated_at);
CREATE INDEX idx_dormitory_region_name ON dormitory (region, name);
CREATE INDEX idx_employee_affiliation ON employee (affiliation_id);
CREATE INDEX idx_residence_dormitory_period ON residence_history (dormitory_id, move_in_date);
