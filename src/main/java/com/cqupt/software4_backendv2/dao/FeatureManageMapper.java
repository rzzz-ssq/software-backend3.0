package com.cqupt.software4_backendv2.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.software4_backendv2.entity.FeatureEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// TODO 公共模块新增类

@Mapper
public interface FeatureManageMapper extends BaseMapper<FeatureEntity> {
    List<FeatureEntity> selectFeatures(@Param("belongType") String belongType);


    List<FeatureEntity> selectFeaturesContinue(@Param("belongType") String belongType);

    List<String> getUserFeatureList(String tablename);
}
