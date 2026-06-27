-- 同一部屋の EXCLUDE 制約（定員=1 相当）を廃止し、部屋定員に基づくトリガー検証へ置換する。
-- 適用: psql -h localhost -U dorm_app -d dormitory -f migration/V20260620_residence_capacity_constraint.sql

ALTER TABLE residence_history
    DROP CONSTRAINT IF EXISTS ex_residence_no_overlap;

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

DROP TRIGGER IF EXISTS trg_residence_room_capacity ON residence_history;

CREATE TRIGGER trg_residence_room_capacity
    BEFORE INSERT OR UPDATE OF room_id, move_in_date, move_out_date, deleted_at
    ON residence_history
    FOR EACH ROW
    EXECUTE FUNCTION fn_check_residence_room_capacity();
