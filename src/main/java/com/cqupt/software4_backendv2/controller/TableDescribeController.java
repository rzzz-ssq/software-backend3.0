package com.cqupt.software4_backendv2.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.software4_backendv2.common.Result;
import com.cqupt.software4_backendv2.dao.CategoryMapper;
import com.cqupt.software4_backendv2.dao.TableDescribeMapper;
import com.cqupt.software4_backendv2.dao.UserMapper;
import com.cqupt.software4_backendv2.entity.CategoryEntity;
import com.cqupt.software4_backendv2.entity.TableDescribeEntity;
import com.cqupt.software4_backendv2.entity.User;
import com.cqupt.software4_backendv2.service.CategoryService;
import com.cqupt.software4_backendv2.service.TableDescribeService;
import com.cqupt.software4_backendv2.service.UserService;
import com.cqupt.software4_backendv2.vo.CheckApprovingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

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
    @Autowired
    CategoryMapper categoryMapper;

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
                                  @RequestParam("ids") String[] ids,
                                  @RequestParam("uid") String uid,   // 传表中涉及到的用户的uid
                                  @RequestParam("tableStatus") String tableStatus,
                                  @RequestParam("tableSize") Double tableSize,
                                  @RequestParam("current_uid") String current_uid, //操作用户的uid
                                  @RequestParam("uid_list") String uid_list
    ){
        // 保存表数据信息
        try {
//            String tableId="";
            // 管理员端-数据管理新更改
//            传入的是category的id集合，根据id获取labels拼接成classpath
            String classPath = "公共数据集";
            for (String id : ids){
                CategoryEntity categoryEntity = categoryMapper.selectById(id);
                classPath += "/" + categoryEntity.getLabel();
            }
            classPath += "/" + tableName;
            List<String> featureList = tableDescribeService.uploadDataTable(file, pid, tableName, userName, classPath, uid, tableStatus, tableSize, current_uid,uid_list);
            return Result.success("200",featureList); // 返回表头信息
        }catch (Exception e){
            e.printStackTrace();
            return Result.success(500,"文件上传异常");
        }
    }

    // 管理员端-数据管理新更改
    @GetMapping("/selectDataDiseases")
    public Result<TableDescribeEntity> selectDataDiseases(
//            @RequestParam("current_uid") String current_uid
    ){ // 参数表的Id
        List<CategoryEntity> res = categoryService.getLevel2Label();

        List<Object> retList = new ArrayList<>();
        for (CategoryEntity category : res) {
            Map<String, Object> ret =  new HashMap<>();
            ret.put("label", category.getLabel());
            ret.put("value", category.getId());
            if (selectCategoryDataDiseases(category.getId()).size() > 0) {
                ret.put("children", selectCategoryDataDiseases(category.getId()));
            }

            retList.add(ret);
        }
        System.out.println(retList);


        return Result.success("200",retList);
//        return Result.success("200",adminDataManages);
    }

    // 管理员端-数据管理新更改
    public List<Map<String, Object>> selectCategoryDataDiseases(String pid){
        List<Map<String, Object>> retList = new ArrayList<>();
        List<CategoryEntity> res = categoryService.getLabelsByPid(pid);
        for (CategoryEntity category : res) {
            Map<String, Object> ret =  new HashMap<>();
            ret.put("label", category.getLabel());
            ret.put("value", category.getId());
            if (selectCategoryDataDiseases(category.getId()).size() > 0) {
                ret.put("children", selectCategoryDataDiseases(category.getId()));
            }
            retList.add(ret);
        }
        return retList;
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
            @RequestParam("id") String id
    ){
        TableDescribeEntity adminDataManage = tableDescribeService.selectDataById(id); // 根据id获取table_describe表的那一行
        Map<String, Object> ret =  new HashMap<>();
        ret.put("object", adminDataManage);

        CategoryEntity categoryEntity = categoryMapper.selectById(adminDataManage.getTableId());// 根据id获取table_describe表的table_id与category形成映射
        List<String> pids = new ArrayList<>();
        while (!categoryEntity.getParentId().equals("1")){ // 筛选除疾病列表结点的下面结点
            categoryEntity = categoryMapper.selectById(categoryEntity.getParentId());
            pids.add(categoryEntity.getId()); // 迭代添加父节点id
        }
        Collections.reverse(pids); // 反转，使得父节点id在前面

        ret.put("ids", pids); // 包含疾病结点的id，不包含表id
        return Result.success("200",ret);
    }

    @PostMapping("/updateAdminDataManage")
    public Result<TableDescribeEntity> updateAdminDataManage(
            @RequestParam("id") String id,
            @RequestParam("tableid") String tableid,
            @RequestParam("oldTableName") String oldTableName,
            @RequestParam("tableName") String tableName,
            @RequestParam("tableStatus") String tableStatus,
            @RequestParam("pids") String[] pids,  // 父节点id列表
            @RequestParam("current_uid") String current_uid
    ){
        tableDescribeService.updateInfo(id, tableid, oldTableName, tableName, tableStatus, pids, current_uid);
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

    @GetMapping("/selectUsernamesById")
    public Result<TableDescribeEntity> selectUsernamesById(
            @RequestParam("id") String id
    ){
        TableDescribeEntity adminDataManage = tableDescribeService.selectDataById(id); // 根据id获取table_describe表的那一行
        String checkApproving = adminDataManage.getCheckApproving();
        if (checkApproving == null || checkApproving.length() == 0){
            return Result.fail(501, "没有申请人",null);
        }
        String[] approvingUsernames = checkApproving.split(",");

        List<CheckApprovingVo> checkApprovingVos = new ArrayList<>();


        for( String username: approvingUsernames){
            // 添加查询条件，假设我们要搜索 username 为 "testUser" 的用户
            // 使用 QueryWrapper 构建查询条件
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", username);
            User user = userMapper.selectOne(queryWrapper);

            CheckApprovingVo checkApprovingVo = new CheckApprovingVo();
            checkApprovingVo.setUsername(user.getUsername());
            checkApprovingVo.setUid(user.getUid());
            checkApprovingVo.setClasspath(adminDataManage.getClassPath());
            checkApprovingVos.add(checkApprovingVo);
        }

        System.out.println(checkApprovingVos);

        return Result.success("200",checkApprovingVos);
    }

    @GetMapping("/updateCheckApprove")
    public Result<TableDescribeEntity> updateCheckApprove(
            @RequestParam("id") String id,
            @RequestParam("username") String username,
            @RequestParam("type") int type
    ){
        TableDescribeEntity adminDataManage = tableDescribeService.selectDataById(id);
        // 根据id获取table_describe表的那一行
        String checkApproving = adminDataManage.getCheckApproving();
        String checkApproved = adminDataManage.getCheckApproved();
        String[] approvingUsernames = checkApproving.split(",");
        List<String> itemList = new ArrayList<>(Arrays.asList(approvingUsernames));

        itemList.remove(username);  // 不管同意还是拒绝下载，都要把他从申请列表中删除
        if (type == 1){   // 如果同意下载，还需要将他添加到已同意列表中去
            if (checkApproved == null || checkApproved.length() == 0){
                checkApproved = username;
            }else{
                checkApproved += ","+username;
            }

        }

        adminDataManage.setCheckApproving(String.join(",", itemList));
        adminDataManage.setCheckApproved(checkApproved);

        tableDescribeMapper.updateById(adminDataManage);
        return Result.success("200","成功更改");
    }

    @GetMapping("/getCheckApprove")
    public Result<TableDescribeEntity> getCheckApprove(
            @RequestParam("id") String id,
            @RequestParam("username") String username
    ){
        QueryWrapper<TableDescribeEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("table_id", id);
        List<TableDescribeEntity> list = tableDescribeService.list(wrapper);

        TableDescribeEntity adminDataManage = tableDescribeService.selectDataById(list.get(0).getId());
        // 根据id获取table_describe表的那一行
        String checkApproving = adminDataManage.getCheckApproving();
        String checkApproved = adminDataManage.getCheckApproved();

        if (checkApproving != null && checkApproving.length() != 0){
            String[] approvingUsernames = checkApproving.split(",");
            List<String> approvingItemList = new ArrayList<>(Arrays.asList(approvingUsernames));
            if (approvingItemList.contains(username)){
                return Result.success("200","1");
            }
        }
        if (checkApproved != null && checkApproved.length() != 0){
            String[] approvedUsernames = checkApproved.split(",");
            List<String> approvedItemList = new ArrayList<>(Arrays.asList(approvedUsernames));
            if (approvedItemList.contains(username)){
                return Result.success("200","2");
            }
        }
        return Result.success("500","0");


    }

    @GetMapping("/applyCheckApprove")
    public Result<TableDescribeEntity> applyCheckApprove(
            @RequestParam("id") String id,
            @RequestParam("username") String username
    ){
        QueryWrapper<TableDescribeEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("table_id", id);
        List<TableDescribeEntity> list = tableDescribeService.list(wrapper);

        TableDescribeEntity adminDataManage = tableDescribeService.selectDataById(list.get(0).getId());
        // 根据id获取table_describe表的那一行
        String checkApproving = adminDataManage.getCheckApproving();
        if (checkApproving == null || checkApproving.length() == 0){
            checkApproving = username;
        }else{
            checkApproving += ","+username;
        }
        adminDataManage.setCheckApproving(checkApproving);
        tableDescribeMapper.updateById(adminDataManage);
        return Result.success(username+"成功申请下载数据集"+adminDataManage.getTableName(),"1");
    }

    @GetMapping("/allowCheckApprove")
    public Result<TableDescribeEntity> allowCheckApprove(
            @RequestParam("id") String id,
            @RequestParam("username") String username
    ){
        QueryWrapper<TableDescribeEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("table_id", id);
        List<TableDescribeEntity> list = tableDescribeService.list(wrapper);

        TableDescribeEntity adminDataManage = tableDescribeService.selectDataById(list.get(0).getId());  // 根据id获取table_describe表的那一行
        // 根据id获取table_describe表的那一行
        String checkApproved = adminDataManage.getCheckApproved();
        String[] approvedUsernames = checkApproved.split(",");
        List<String> approvedItemList = new ArrayList<>(Arrays.asList(approvedUsernames));
        approvedItemList.remove(username);
        adminDataManage.setCheckApproved(String.join(",", approvedItemList));
        tableDescribeMapper.updateById(adminDataManage);
        return Result.success(username+"成功下载数据集"+adminDataManage.getTableName()+"并取消下次下载权限","0");
//        return Result.success("200",);
    }

}
