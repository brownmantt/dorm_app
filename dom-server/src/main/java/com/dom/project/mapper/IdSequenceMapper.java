package com.dom.project.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 業務 ID 連番の DB 同期用 Mapper。
 */
public interface IdSequenceMapper {

    /**
     * 指定日付（yyyyMMdd）で採番済みの最大連番（末尾4桁）を取得する。
     *
     * @param datePart yyyyMMdd
     * @return 最大連番。該当なしは null
     */
    Integer selectMaxSequenceForDate(@Param("datePart") String datePart);
}
