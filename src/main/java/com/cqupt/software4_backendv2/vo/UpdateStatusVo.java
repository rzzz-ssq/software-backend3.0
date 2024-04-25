package com.cqupt.software4_backendv2.vo;


import lombok.Data;

@Data
public class UpdateStatusVo {
    private String uid ;
    private Integer role;
    private  String status;
    private double allSize;
}
