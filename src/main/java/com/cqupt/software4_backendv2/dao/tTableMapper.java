package com.cqupt.software4_backendv2.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.software4_backendv2.common.MutipleFeature;
import com.cqupt.software4_backendv2.entity.tTable;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface tTableMapper  extends BaseMapper<tTable> {
    void insertTable(tTable tTable);

    List<Map<String, Object>> getMutipleList(String tablename);
}
