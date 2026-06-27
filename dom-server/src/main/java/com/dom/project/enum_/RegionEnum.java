package com.dom.project.enum_;

/**
 * 地域列挙。
 */
public enum RegionEnum {

    TOKYO("TOKYO"),
    OSAKA("OSAKA"),
    NAGOYA("NAGOYA"),
    OTHER("OTHER");

    private final String code;

    RegionEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
