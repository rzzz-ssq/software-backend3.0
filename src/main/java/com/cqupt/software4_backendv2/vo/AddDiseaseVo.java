package com.cqupt.software4_backendv2.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddDiseaseVo {
    private Integer catLevel;
    private String firstDisease;
    private String icdCode;
    //    private String secondDisease;
    private String parentId;
    private String username;
    private String uid;
}
