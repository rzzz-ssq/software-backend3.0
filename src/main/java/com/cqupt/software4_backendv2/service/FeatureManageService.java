package com.cqupt.software4_backendv2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.software4_backendv2.entity.FeatureEntity;
import com.cqupt.software4_backendv2.vo.FeatureListVo;
import com.cqupt.software4_backendv2.vo.FeatureVo;

import java.util.List;
// TODO 公共模块新增类
public interface FeatureManageService extends IService<FeatureEntity> {
    List<FeatureVo> getFeatureList(String belongType);
    List<FeatureVo> selectFeaturesContinue(String belongType);
    void insertFeatures(FeatureListVo featureListVo);

    List<String> getUserFeatureList( String tablename);

}
