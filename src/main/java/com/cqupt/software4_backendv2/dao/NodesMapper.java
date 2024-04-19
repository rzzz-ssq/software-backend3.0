package com.cqupt.software4_backendv2.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.software4_backendv2.entity.Nodes;
import com.cqupt.software4_backendv2.entity.Relationships;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NodesMapper extends BaseMapper<Nodes> {
    List<Nodes> getAllNodes();

    List<Relationships> getRelationships();
}
