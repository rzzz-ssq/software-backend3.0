package com.cqupt.software4_backendv2.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RuntimeBusResponse {

    private Double ratio;
    private String stu;
    private List<List<String>> res;
    private Integer ci;
    private Double time;
    private Map<Object,Object> treeRes;

}
