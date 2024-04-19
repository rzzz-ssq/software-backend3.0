package com.cqupt.software4_backendv2.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlgorithmUsageDailyStats {
//    private String date;
    private String formattedDate;
    private List<AlgorithmUsage> usages;
    private int total;
}
