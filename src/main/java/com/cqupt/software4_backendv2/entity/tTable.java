package com.cqupt.software4_backendv2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_table_manager")
public class tTable {
    private String tableName;
    private List<TableHeader> tableHeaders;
}
