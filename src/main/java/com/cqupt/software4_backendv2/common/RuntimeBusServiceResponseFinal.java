package com.cqupt.software4_backendv2.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RuntimeBusServiceResponseFinal {

    private Double ratio;
    private String stu;
    private List<List<String>> res;
    private List<List<Double>> resWeights;
    private Integer ci;
    private Double time;
    private Map<Object,Object> treeRes;

}
