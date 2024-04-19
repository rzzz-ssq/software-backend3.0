package com.cqupt.software4_backendv2.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.software4_backendv2.common.Result;
import com.cqupt.software4_backendv2.dao.TableDescribeMapper;
import com.cqupt.software4_backendv2.dao.UserMapper;
import com.cqupt.software4_backendv2.entity.CategoryEntity;
import com.cqupt.software4_backendv2.entity.TableDescribeEntity;
import com.cqupt.software4_backendv2.service.CategoryService;
import com.cqupt.software4_backendv2.service.TableDescribeService;
import com.cqupt.software4_backendv2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO 公共模块新增类

@RestController
@RequestMapping("/api")
public class TableDescribeController {
    @Autowired
    TableDescribeService tableDescribeService;
    @Autowired
    TableDescribeMapper tableDescribeMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    CategoryService categoryService;
    @Autowired
    UserService userService;

    @GetMapping("/tableDescribe")
    public Result<TableDescribeEntity> getTableDescribe(@RequestParam("id") String id){ // 参数表的Id
        TableDescribeEntity tableDescribeEntity = tableDescribeService.getOne(new QueryWrapper<TableDescribeEntity>().eq("table_id", id));
        System.out.println("数据为："+ JSON.toJSONString(tableDescribeEntity));

        return Result.success("200",JSON.toJSONString(tableDescribeEntity));
    }

    @GetMapping("/getTableNumber")
    public Result getTableNumber(){ // 参数表的Id
        QueryWrapper<TableDescribeEntity> queryWrapper = new QueryWrapper<>();

        int count = Math.toIntExact(tableDescribeMapper.selectCount(queryWrapper));

        return Result.success("200",JSON.toJSONString(count));
    }

    // 文件上传
    @PostMapping("/uploadDataTable")
    public Result uploadDataTable(@RequestParam("file") MultipartFile file,
//                             @RequestParam("tableId") String tableId,
                                  @RequestParam("pid") String pid,
                                  @RequestParam("tableName") String tableName,
                                  @RequestParam("userName") String userName,
                                  @RequestParam("classPath") String classPath,
                                  @RequestParam("uid") String uid,
                                  @RequestParam("tableStatus") String tableStatus,
                                  @RequestParam("tableSize") Double tableSize
    ){
        // 保存表数据信息
        try {

            List<String> featureList = tableDescribeService.uploadDataTable(file, pid, tableName, userName, classPath, uid, tableStatus, tableSize);
            return Result.success("200",featureList); // 返回表头信息
        }catch (Exception e){
            e.printStackTrace();
            return Result.success(500,"文件上传异常");
        }
    }

    @GetMapping("/selectAdminDataManage")
    public Result<TableDescribeEntity> selectAdminDataManage(){ // 参数表的Id
        List<TableDescribeEntity> adminDataManages = tableDescribeService.selectAllDataInfo();
//        System.out.println("数据为："+ JSON.toJSONString(tableDescribeEntity));
        Map<String, Object> ret =  new HashMap<>();
        ret.put("list", adminDataManages);
        ret.put("total", adminDataManages.size());

        return Result.success("200",ret);
//        return Result.success("200",adminDataManages);
    }


    @GetMapping("/selectDataByName")
    public Result<TableDescribeEntity> selectDataByName(
            @RequestParam("searchType") String searchType,
            @RequestParam("name") String name){
        List<TableDescribeEntity> adminDataManages = tableDescribeService.selectDataByName(searchType, name);
//        System.out.println("数据为："+ JSON.toJSONString(tableDescribeEntity));
        Map<String, Object> ret =  new HashMap<>();
        ret.put("list", adminDataManages);
        ret.put("total", adminDataManages.size());

        return Result.success("200",ret);
    }

    @GetMapping("/selectDataById")
    public Result<TableDescribeEntity> selectDataById(
            @RequestParam("id") String id){
        TableDescribeEntity adminDataManage = tableDescribeService.selectDataById(id);
//        System.out.println("数据为："+ JSON.toJSONString(tableDescribeEntity));

        return Result.success("200",adminDataManage);
    }


    @GetMapping("/updateAdminDataManage")
    public Result<TableDescribeEntity> updateAdminDataManage(
            @RequestParam("id") String id,
            @RequestParam("tableid") String tableid,
            @RequestParam("oldTableName") String oldTableName,
            @RequestParam("tableName") String tableName,
            @RequestParam("tableStatus") String tableStatus
    ){
        tableDescribeService.updateInfo(id, tableid, oldTableName, tableName, tableStatus);

        return Result.success("200","已经更改到数据库");
    }



    @GetMapping("/getLevel2Label")
    public Result<List<CategoryEntity>> getLevel2Label(
    ){
        List<CategoryEntity> res = categoryService.getLevel2Label();
        return Result.success("200",res);
    }
    @GetMapping("/getLabelByPid")
    public Result<List<CategoryEntity>> getLabelsByPid(
            @RequestParam("pid") String pid
    ){
        List<CategoryEntity> res = categoryService.getLabelsByPid(pid);
        return Result.success("200",res);
    }


    @GetMapping("/deleteByTableName")
    public Result<TableDescribeEntity> deleteByTableName(
            @RequestParam("id") String id,
            @RequestParam("uid") String uid,
            @RequestParam("tableSize") Double tableSize,
            @RequestParam("tableId") String tableId,
            @RequestParam("tableName") String tableName
    ){
//        System.out.println();
        tableDescribeService.deleteByTableName(tableName);// 【因为数据库中表名是不能重名的】
        tableDescribeService.deleteByTableId(tableId);// 在table_describe中删除记录
        categoryService.removeNode(tableId);// 在category中设置is_delete为1

//        float tableSize = adminDataManage.getTableSize();
        userMapper.recoveryUpdateUserColumnById(uid, tableSize);
        return Result.success("200","已在数据库中删除了"+tableName+"表");
    }
}
