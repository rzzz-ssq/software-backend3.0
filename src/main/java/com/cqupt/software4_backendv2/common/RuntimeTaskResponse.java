package com.cqupt.software4_backendv2.common;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class RuntimeTaskResponse {

    private Integer taskId;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Long taskStartTime;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Long taskFinishTime;
    private String taskType;
    private Integer bizId;
    private List<String> res=null;

}
