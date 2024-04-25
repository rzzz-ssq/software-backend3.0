package com.cqupt.software4_backendv2.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@TableName(schema = "software4",value ="log")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLog {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String uid;
    private String operation;
    private Date time;
    private Integer role;
    private static final long serialVersionUID = 1L;
}
