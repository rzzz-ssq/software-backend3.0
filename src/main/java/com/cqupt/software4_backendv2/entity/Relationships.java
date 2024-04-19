package com.cqupt.software4_backendv2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value ="relationships",schema = "public")
public class Relationships {
    private Integer parentId;

    private Integer childId;
}
