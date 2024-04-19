package com.cqupt.software4_backendv2.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value ="software4task",schema = "software4")
public class Task implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String  taskname;
    private String  leader;
    private String  participant;

    private Date createtime;

    private String   disease;
    private String   model;
    private String   remark;
    private String   feature;
    private String   result;
    private String   parameters;
    private String   parametersvalues;
    private String   targetcolumn;
    private double  usetime;
    private int     ci;
    private double  ratio;
    private String dataset;
    private String tips;
    private Integer userid;
    private String tasktype;
}
