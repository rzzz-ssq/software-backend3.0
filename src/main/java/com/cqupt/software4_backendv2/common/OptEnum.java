package com.cqupt.software4_backendv2.common;
//  TODO 公共模块新增
public enum OptEnum {
    AND(0, "AND"),
    OR(1, "OR"),
    NOT(2, "NOT");

    private final int code;
    private final String name;

    OptEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
