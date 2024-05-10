package com.cqupt.software4_backendv2.vo;

import com.cqupt.software4_backendv2.entity.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO 公共模块新增类

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterTableDataVo {
    private AddDataFormVo addDataForm;
    private CategoryEntity nodeData;
    private String nodeid;
    private String status;
}
