package com.cqupt.software4_backendv2.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.software4_backendv2.entity.UserLog;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface UserLogService extends IService<UserLog> {
    int insertUserLog(UserLog userLog);


    PageInfo<UserLog> findByPageService(Integer pageNum, Integer pageSize, QueryWrapper<UserLog> queryWrapper);
}
