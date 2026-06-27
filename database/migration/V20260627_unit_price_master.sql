-- 単価マスタ追加マイグレーション（既存 DB 向け）
-- 実行例: psql -U dorm_app -d dormitory -f database/migration/V20260627_unit_price_master.sql

CREATE TABLE IF NOT EXISTS unit_price (
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

CREATE UNIQUE INDEX IF NOT EXISTS uk_unit_price_code ON unit_price (code) WHERE deleted_at IS NULL;

COMMENT ON TABLE unit_price IS '単価マスタ';
