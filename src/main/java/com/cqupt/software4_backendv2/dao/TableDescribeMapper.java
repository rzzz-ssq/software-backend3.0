package com.cqupt.software4_backendv2.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.software4_backendv2.entity.TableDescribeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// TODO 公共模块新增类

@Mapper
public interface TableDescribeMapper extends BaseMapper<TableDescribeEntity> {

    void createTable(@Param("headers") String[] headers, @Param("tableName") String tableName);
    void insertRow(@Param("row") String[] row, @Param("tableName") String tableName);
    List<String>  uploadDataTable(MultipartFile file, String tableId, String pid, String tableName, String userName, String classPath, String uid, String tableStatus);

    List<TableDescribeEntity> selectAllDataInfo();

    //根据searchType【表名、用户名、疾病名】搜索
    List<TableDescribeEntity> selectDataByName(@Param("searchType") String searchType,@Param("name") String name);
    TableDescribeEntity selectDataById(String id);

    //    void updateById(String id, String tableName, String tableStatus);
    void updateDataBaseTableName(@Param("old_name") String old_name, @Param("new_name")  String new_name);
    void deleteByTableName(String tableName);
    void deleteByTableId(String tableId);

}
