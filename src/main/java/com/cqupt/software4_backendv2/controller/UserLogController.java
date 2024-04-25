package com.cqupt.software4_backendv2.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqupt.software4_backendv2.common.Result;
import com.cqupt.software4_backendv2.entity.Task;
import com.cqupt.software4_backendv2.entity.UserLog;
import com.cqupt.software4_backendv2.dao.UserLogMapper;
import com.cqupt.software4_backendv2.service.UserLogService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 *
 * 用户管理模块
 *
 * 用户注册
 * 用户登录
 *
 */
@RestController
@RequestMapping("/userlog")
public class UserLogController {


    @Autowired
    private UserLogService userLogService;

    @Resource
    private UserLogMapper userLogMapper;

    @GetMapping("/allLog")
    public Result queryAllLog(){
        return Result.success(userLogMapper.selectList(null));
    }

    @GetMapping("/getLogByPage")
    public Result queryLogByPage(@RequestParam Integer pageNum,
                                 @RequestParam Integer pageSize,
                                 @RequestParam String username
                                 ){
        QueryWrapper<UserLog> queryWrapper = new QueryWrapper<UserLog>().orderByDesc("id");
        queryWrapper.like(StringUtils.isNotBlank(username),"username",username);

        PageInfo<UserLog> page = userLogService.findByPageService(pageNum, pageSize,queryWrapper);
        return Result.success(page);
    }
}
