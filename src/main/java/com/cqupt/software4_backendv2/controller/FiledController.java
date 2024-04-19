package com.cqupt.software4_backendv2.controller;

import com.cqupt.software4_backendv2.common.Result;
import com.cqupt.software4_backendv2.entity.FieldManagementEntity;
import com.cqupt.software4_backendv2.service.*;
import com.cqupt.software4_backendv2.vo.QueryFiledVO;
import com.cqupt.software4_backendv2.vo.UpdateFiledVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/filed")
public class FiledController {

    @Autowired
    TableDataService tableDataService;
    @Autowired
    UserService userService;

    @Autowired
    CategoryService categoryService;
    @Autowired
    TableDescribeService tableDescribeService;

    @Autowired
    private FieldManagementService fieldManagementService;


    /**
     *
     * 通过关联疾病名称展示字段信息
     * @param
     * @return 字段信息表
     */
    @PostMapping("/getAllFiled")
    public Result getAllFiled(@RequestBody QueryFiledVO queryFiledVO){
        System.out.println(queryFiledVO.getDiseaseName());
        List<FieldManagementEntity> res = fieldManagementService.getFiledByDiseaseName(queryFiledVO.getDiseaseName());
        return Result.success(res);
    }


    /**
     *
     * 新建特征表 根据动态选择来更新字段表
     *
     * 接收病种名字 和 字段列表
     */
    @PostMapping("/updateFiled")
    public Result updateFiled(@RequestBody UpdateFiledVO updateFiledVO){
        String diseaseName = updateFiledVO.getDiseaseName();
        List<String> fields = updateFiledVO.getFileds();
        // 更新字段表信息
        fieldManagementService.updateFieldsByDiseaseName(diseaseName, fields);
        return Result.success(null);
    }









}
