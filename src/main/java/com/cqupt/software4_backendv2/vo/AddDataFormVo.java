package com.cqupt.software4_backendv2.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// TODO 公共模块新增类
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddDataFormVo {
    private String dataName;
    private String createUser;
    private String uid;
    private String username;
    private String isUpload;
    private String isFilter;
    private List<CreateTableFeatureVo> characterList;
}
