package com.cqupt.software4_backendv2.controller;

import com.cqupt.software4_backendv2.common.Result;
import com.cqupt.software4_backendv2.dao.CategoryMapper;
import com.cqupt.software4_backendv2.dao.TableDescribeMapper;
import com.cqupt.software4_backendv2.dao.UserMapper;
import com.cqupt.software4_backendv2.entity.tTable;
import com.cqupt.software4_backendv2.service.tTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("/tTable")
public class tTableController {

    @Autowired
    private tTableService tTableServcie;

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TableDescribeMapper tableDescribeMapper;

    private final ApplicationContext context;

    @Autowired
    public tTableController(ApplicationContext context) {
        this.context = context;
    }

    @Transactional
    @PostMapping("/insertTableManager")
    public Result insertTableManager(@RequestBody tTable tTable) {
        try {
            System.out.println(tTable);
            tTableServcie.insertTable(tTable);
           return Result.success(200,"插入成功");
        } catch (Exception e) {
            categoryMapper.deleteById(tTable.getNode().getId());
            tableDescribeMapper.deleteByTableId(tTable.getTableDescribe().getTableId());
            userMapper.recoveryUpdateUserColumnById(tTable.getUserId(),tTable.getSize());
           return Result.fail(500,"插入失败"+e);
        }
    }
}
