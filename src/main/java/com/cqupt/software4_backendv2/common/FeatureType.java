package com.cqupt.software4_backendv2.common;

// TODO 公共模块新增
public enum FeatureType {
    DIAGNOSIS(0, "population"),
    PATHOLOGY(2, "physiology"),
    VITAL_SIGNS(3, "society");

    private final int code;
    private final String name;

    FeatureType(int code, String name) {
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
