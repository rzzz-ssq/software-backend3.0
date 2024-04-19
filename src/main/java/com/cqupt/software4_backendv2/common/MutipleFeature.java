package com.cqupt.software4_backendv2.common;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_table_manager")
public class MutipleFeature {
    @TableField("field_name")
    private String field_name;
    private Double fill_rate;
    @TableField("is_label")
    private String is_label;
}
