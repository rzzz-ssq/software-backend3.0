package com.cqupt.software4_backendv2.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.software4_backendv2.common.CommonEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommonEntityMapper extends BaseMapper<CommonEntity> {
    Integer findTargetColumnIndex(@Param("tablename") String tablename, @Param("target") String target);
}
