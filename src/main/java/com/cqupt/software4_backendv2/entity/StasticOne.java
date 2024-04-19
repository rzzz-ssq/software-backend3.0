package com.cqupt.software4_backendv2.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StasticOne {
    // 统计信息1
    private Integer specialDiseaseNumber;
    private Integer  itemNumber;
    private String startTime;
    private String endTime;
    private Integer  taskNumber;

}
