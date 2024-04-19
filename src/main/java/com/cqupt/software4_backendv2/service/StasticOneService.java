package com.cqupt.software4_backendv2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.software4_backendv2.common.Result;
import com.cqupt.software4_backendv2.entity.StasticOne;

import java.util.List;
import java.util.Map;


public interface StasticOneService extends IService<StasticOne> {
    StasticOne getStasticOne();

    Map<String,Integer> getDiseases();

    List<String> getAllTableNames();

    List<String> getAllUserBuiltTableNames();



    String getType(String tablename);

    Integer getPosNumber(String tablename, String type);

    Integer getNegNumber(String tablename, String type);

    List<String> getAllFilteredTableNames();
}
