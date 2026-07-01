package com.dom.project.enum_;

/**
 * 操作ログ対象テーブル列挙。
 */
public enum TargetTableEnum {

    DORMITORY("dormitory"),
    AFFILIATION("affiliation"),
    REGION("region"),
    USAGE_TYPE("usage_type"),
    UNIT_PRICE("unit_price"),
    EMPLOYEE("employee"),
    DORMITORY_MANAGER("dormitory_manager"),
    ROOM("room"),
    RESIDENCE_HISTORY("residence_history"),
    DORM_FEE("dorm_fee"),
    EQUIPMENT("equipment"),
    EQUIPMENT_MOVEOUT("equipment_moveout"),
    EQUIPMENT_STORAGE("equipment_storage"),
    STORAGE_LOCATION("storage_location"),
    EQUIPMENT_ASSET("equipment_asset"),
    EQUIPMENT_USAGE("equipment_usage"),
    EXCEL_IMPORT_JOB("excel_import_job"),
    AUTH("auth");

    private final String code;

    TargetTableEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
