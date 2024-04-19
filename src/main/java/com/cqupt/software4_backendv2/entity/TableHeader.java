package com.cqupt.software4_backendv2.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableHeader {
    private String fieldName;
    private String isLabel;
}
