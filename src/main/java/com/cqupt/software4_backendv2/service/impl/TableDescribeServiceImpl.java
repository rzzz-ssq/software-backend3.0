package com.cqupt.software4_backendv2.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.software4_backendv2.dao.CategoryMapper;
import com.cqupt.software4_backendv2.dao.TableDescribeMapper;
import com.cqupt.software4_backendv2.dao.UserMapper;
import com.cqupt.software4_backendv2.entity.CategoryEntity;
import com.cqupt.software4_backendv2.entity.TableDescribeEntity;
import com.cqupt.software4_backendv2.service.TableDescribeService;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.transaction.annotation.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

// TODO 公共模块新增类

@Service
public class TableDescribeServiceImpl extends ServiceImpl<TableDescribeMapper, TableDescribeEntity> implements TableDescribeService {

    @Autowired
    private TableDescribeMapper tableDescribeMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    public List<String> storeTableData(MultipartFile file, String tableName) throws IOException {
        ArrayList<String> featureList = null;
        if (!file.isEmpty()) {
            // 使用 OpenCSV 解析 CSV 文件
            Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(),"UTF-8"));
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> csvData = csvReader.readAll();
            csvReader.close();
            // 获取表头信息
            String[] headers = csvData.get(0);
            featureList = new ArrayList<String>(Arrays.asList(headers));
            System.out.println("表头信息为："+ JSON.toJSONString(headers));
            // 删除表头行，剩余的即为数据行
            csvData.remove(0);
            // 创建表信息
            tableDescribeMapper.createTable(headers,tableName);
            // 保存表头信息和表数据到数据库中
            for (String[] row : csvData) { // 以此保存每行信息到数据库中
                tableDescribeMapper.insertRow(row,tableName);
            }
        }
        return featureList;
    }



    @Override
    @Transactional
    public List<String> uploadDataTable(MultipartFile file, String pid, String tableName, String userName, String classPath, String uid, String tableStatus, Double tableSize,String current_id,String uid_list) throws IOException, ParseException {
        // 封住表描述信息
        TableDescribeEntity adminDataManageEntity = new TableDescribeEntity();
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setCatLevel(4);
        categoryEntity.setLabel(tableName);
        categoryEntity.setParentId(pid);
        categoryEntity.setIsLeafs(1);
        categoryEntity.setIsDelete(0);
        categoryEntity.setUid(uid);
        categoryEntity.setStatus(tableStatus);
        categoryEntity.setUsername(userName);
        categoryEntity.setIsUpload("1");
        categoryEntity.setIsFilter("0");
        categoryEntity.setUidList(uid_list);
        System.out.println("==categoryEntity==" + categoryEntity );
        categoryMapper.insert(categoryEntity);



        adminDataManageEntity.setTableName(tableName);
        adminDataManageEntity.setTableId(categoryEntity.getId());
        adminDataManageEntity.setCreateUser(userName);
        // 解析系统当前时间
        adminDataManageEntity.setCreateTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        adminDataManageEntity.setClassPath(classPath);
        adminDataManageEntity.setUid(uid);
        adminDataManageEntity.setTableStatus(tableStatus);
        adminDataManageEntity.setTableSize(tableSize);
        tableDescribeMapper.insert(adminDataManageEntity);

        userMapper.decUpdateUserColumnById(uid, tableSize);

        List<String> featureList = storeTableData(file, tableName);
        // 保存数据库
        System.out.println("表描述信息插入成功, 动态建表成功");
        return featureList;
    }

    @Override
    public List<TableDescribeEntity> selectAllDataInfo() {
        return tableDescribeMapper.selectAllDataInfo();
    }

    @Override
    public List<TableDescribeEntity> selectDataByName(String searchType, String name) {
        return tableDescribeMapper.selectDataByName(searchType, name);
    }

    @Override
    public TableDescribeEntity selectDataById(String id) {
        return tableDescribeMapper.selectDataById(id);
    }

    @Override
    public void deleteByTableName(String tableName) {
        tableDescribeMapper.deleteByTableName(tableName);
    }
    @Override
    public void deleteByTableId(String tableId) {
        tableDescribeMapper.deleteByTableId(tableId);
    }

    @Override
    public void updateDataBaseTableName(String old_name, String new_name){
        tableDescribeMapper.updateDataBaseTableName(old_name, new_name);
    }

    @Override
    public void updateById(String id, String[] pids, String tableName, String tableStatus) {
        TableDescribeEntity adminDataManage = tableDescribeMapper.selectById(id);  // 在table_describe表中获取表id对应的那一行数据

//        设置class_path
        String classPath = "公共数据集";
        for (String pid : pids){
            CategoryEntity categoryEntity = categoryMapper.selectById(pid);
            classPath += "/" + categoryEntity.getLabel();
        }
        classPath += "/" + tableName;

        adminDataManage.setClassPath(classPath);
        adminDataManage.setTableName(tableName);
        adminDataManage.setTableStatus(tableStatus);

        CategoryEntity categoryEntity = categoryMapper.selectById(adminDataManage.getTableId());
        categoryEntity.setParentId(pids[pids.length-1]);
        categoryMapper.updateById(categoryEntity); // 更改category表
        tableDescribeMapper.updateById(adminDataManage);// 更改table_describe表
    }

    @Override
    @Transactional
    public void updateInfo(String id, String tableid, String oldTableName, String tableName, String tableStatus, String[] pids, String current_uid) {
        updateById(id, pids, tableName, tableStatus);
        categoryMapper.updateTableNameByTableId(tableid, tableName, tableStatus);
        if (!oldTableName.equals(tableName)){
            updateDataBaseTableName(oldTableName, tableName);

        }
    }

}
