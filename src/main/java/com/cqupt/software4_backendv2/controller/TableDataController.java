package com.cqupt.software4_backendv2.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.software4_backendv2.common.Result;
import com.cqupt.software4_backendv2.dao.StasticOneMapper;
import com.cqupt.software4_backendv2.entity.CategoryEntity;
import com.cqupt.software4_backendv2.service.*;
import com.cqupt.software4_backendv2.vo.FilterTableDataVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// TODO 公共模块新增类

@RestController()
@RequestMapping("/api")
public class TableDataController {

    @Autowired
    TableDataService tableDataService;
    @Autowired
    UserService userService;
    @Autowired
    StasticOneService stasticOneService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    TableDescribeService tableDescribeService;
    @GetMapping("/getTableData")
    public Result getTableData(@RequestParam("tableId") String tableId, @RequestParam("tableName") String tableName){
        System.out.println("tableId=="+tableId+"   tableName=="+tableName);
        List<LinkedHashMap<String, Object>> tableData = tableDataService.getTableData(tableId, tableName);
        return Result.success("200",tableData);
    }

    // 文件上传
    @PostMapping("/dataTable/upload")
    public Result uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("newName") String tableName,
                             @RequestParam("disease") String type,
                             @RequestParam("user") String user,
                             @RequestParam("uid") String userId,
                             @RequestParam("parentId") String parentId,
                             @RequestParam("parentType") String parentType,
                             @RequestParam("status") String status,
                             @RequestParam("size") Double size,
                             @RequestParam("is_upload") String is_upload,
                             @RequestParam("is_filter") String is_filter){
        // 保存表数据信息
        try {
            List<String> featureList = tableDataService.ParseFileCol(file,tableName);
            tableDataService.uploadFile(file, tableName, type, user, userId, parentId, parentType,status,size,is_upload,is_filter);
            return Result.success("200",featureList); // 返回表头信息
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail(500,"文件上传异常");
        }
    }

        // 解析文件列信息
        @PostMapping("/dataTable/parseAndUpload")
        public Result ParseFileCol(@RequestParam("file") MultipartFile file,
                                   @RequestParam("newName") String tableName,
                                   @RequestParam("disease") String type,
                                   @RequestParam("user") String user,
                                   @RequestParam("uid") String userId,
                                   @RequestParam("parentId") String parentId,
                                   @RequestParam("parentType") String parentType,@RequestParam("status") String status,@RequestParam("size") Double size,@RequestParam("is_upload") String is_upload,
                                   @RequestParam("is_filter") String is_filter){
            // 保存表数据信息
            try {
                List<String> featureList = tableDataService.uploadFile(file, tableName, type, user, userId, parentId, parentType,status,size,is_upload,is_filter);
                return Result.success("200",featureList); // 返回表头信息
            }catch (Exception e){
                e.printStackTrace();
                return Result.fail(500,"文件解析失败");
            }
        }



    // 检查上传文件是数据文件的表名在数据库中是否重复
    @GetMapping("/DataTable/inspection")
    public Result tableInspection(@RequestParam("newname") String name){
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("is_delete", 0);
        List<CategoryEntity> list = categoryService.list(wrapper);
        List<String>  nameList = new ArrayList<>();
        nameList = stasticOneService.getAllTableNames();
        for (String tempName :nameList) {
            CategoryEntity tempCategoryEntity = new CategoryEntity();
            tempCategoryEntity.setLabel(tempName);
            list.add(tempCategoryEntity);
        }
        boolean flag = true;
        for (CategoryEntity categoryEntity : list) {
            if(categoryEntity.getLabel().equals(name)) {
                flag = false;
                break;
            }
        }
        return Result.success("200",flag); // 判断文件名是否重复
    }


    // 筛选数据后，创建表保存筛选后的数据
    @PostMapping("/createTable")
    public Result createTable(@RequestBody FilterTableDataVo filterTableDataVo){

        tableDataService.createTable(filterTableDataVo.getAddDataForm().getDataName(),filterTableDataVo.getAddDataForm().getCharacterList(),
                filterTableDataVo.getAddDataForm().getCreateUser(),filterTableDataVo.getNodeData(),filterTableDataVo.getAddDataForm().getUid(),filterTableDataVo.getAddDataForm().getUsername(),filterTableDataVo.getAddDataForm().getIsFilter(),filterTableDataVo.getAddDataForm().getIsUpload());
        System.out.println("开始新建表："+JSON.toJSONString(filterTableDataVo));
        return Result.success(200,"SUCCESS");
    }

    // 根据条件筛选数据
    @PostMapping("/filterTableData")
    public Result<List<Map<String,Object>>> getFilterTableData(@RequestBody FilterTableDataVo filterTableDataVo){
        List<LinkedHashMap<String, Object>> filterDataByConditions = tableDataService.getFilterDataByConditions(filterTableDataVo.getAddDataForm().getCharacterList(), filterTableDataVo.getNodeData());
        System.out.println("筛选数据长度为："+filterDataByConditions.size());
        return Result.success("200",filterDataByConditions);
    }

    @GetMapping("/getInfoByTableName/{tableName}")
    public Result<List<Map<String,Object>>> getInfoByTableName(@PathVariable("tableName") String tableName){
        tableName = tableName.replace("\"", "");
        List<Map<String, Object>> res = tableDataService.getInfoByTableName(tableName);
        return Result.success(200, "成功", res);
    }

    @GetMapping("/getCountByTableName/{tableName}")
    public Result<List<Map<String,Object>>> getCountByTableName(@PathVariable("tableName") String tableName){
        tableName = tableName.replace("\"", "");
        Integer res = tableDataService.getCountByTableName(tableName);
        return Result.success(200, "成功", res);
    }

}
