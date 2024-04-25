package com.cqupt.software4_backendv2.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.Date;

@TableName(value ="software4user",schema = "software4")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @TableId
    private String uid;

    private String username;

    private String password;

    private Integer role;

    private Date createTime;

    private Date updateTime;

    private String userStatus;

    @Column(name = "answer_1")
    private String answer_1;
    @Column(name = "answer_2")
    private String answer_2;
    @Column(name = "answer_3")
    private String answer_3;

    private Double uploadSize;

    private Double allSize;

    private static final long serialVersionUID = 1L;

    // 新增字段
    @TableField(exist = false)
    private String code;

}