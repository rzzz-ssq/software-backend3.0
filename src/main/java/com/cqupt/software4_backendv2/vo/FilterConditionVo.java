package com.cqupt.software4_backendv2.vo;

import com.cqupt.software4_backendv2.entity.FilterDataCol;
import com.cqupt.software4_backendv2.entity.FilterDataInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterConditionVo {
    private FilterDataInfo filterDataInfo;
    private List<FilterDataCol> filterDataCols;
}
