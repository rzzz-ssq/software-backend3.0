package com.cqupt.software4_backendv2.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO 公共模块新增类

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("field_management")
public class FieldManagementEntity {
    @TableId
    private Integer characterId;
    private String featureName;
    private String chName;
    private Boolean diseaseStandard;

    // 人口学
    private Boolean population;
    // 生理指标
    private Boolean physiology;

    // 行为学
    private Boolean society;
    private String tableName;
    private String unit;
    private Boolean isLabel;
    private Boolean discrete;
    private String range;
    private String disease;
}
