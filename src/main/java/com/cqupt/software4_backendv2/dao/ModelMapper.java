package com.cqupt.software4_backendv2.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.software4_backendv2.entity.Model;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ModelMapper extends BaseMapper<Model> {
    List<String> getModelNames();
}
