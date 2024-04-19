package com.cqupt.software4_backendv2.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BusResponse {
    private Integer busId;
    private String busName;
    private String busDescription;
    private String busState;
    private Timestamp taskCreateTime;
    private String icdCode;
    private int algorithmId;
    private String outputFilePath;
}
