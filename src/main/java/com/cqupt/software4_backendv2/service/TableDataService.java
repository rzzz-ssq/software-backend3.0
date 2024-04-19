package com.cqupt.software4_backendv2.service;

import com.cqupt.software4_backendv2.entity.CategoryEntity;
import com.cqupt.software4_backendv2.vo.CreateTableFeatureVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// TODO 公共模块新增类
public interface TableDataService {
    List<LinkedHashMap<String,Object>> getTableData(String TableId, String tableName);

    List<String> uploadFile(MultipartFile file, String tableName, String type, String user, String userId, String parentId, String parentType,String status,Double size,String is_upload,String is_filter) throws IOException, ParseException;

    void createTable(String tableName, List<CreateTableFeatureVo> characterList, String createUser, CategoryEntity nodeData,String uid,String username,String IsFilter,String IsUpload);

    List<LinkedHashMap<String, Object>> getFilterDataByConditions(List<CreateTableFeatureVo> characterList,CategoryEntity nodeData);

    List<Map<String, Object>> getInfoByTableName(String tableName);

    List<String> ParseFileCol(MultipartFile file, String tableName) throws IOException;

    Integer getCountByTableName(String tableName);
}
