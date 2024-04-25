package com.cqupt.software4_backendv2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.software4_backendv2.entity.Task;
import com.cqupt.software4_backendv2.entity.User;
import com.cqupt.software4_backendv2.entity.UserLog;
import com.cqupt.software4_backendv2.dao.UserLogMapper;
import com.cqupt.software4_backendv2.dao.UserMapper;
import com.cqupt.software4_backendv2.service.UserLogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class UserLogServiceImpl extends ServiceImpl<UserLogMapper, UserLog>
        implements UserLogService {
    @Autowired
    private UserLogMapper userLogMapper;

    @Resource
    private UserMapper userMapper;
    @Override
    public int insertUserLog(UserLog userLog) {

        int insert = userLogMapper.insert(userLog);
        return insert;
    }


    @Override
    public PageInfo<UserLog> findByPageService(Integer pageNum, Integer pageSize, QueryWrapper<UserLog> queryWrapper) {
        PageHelper.startPage(pageNum,pageSize);
        List<UserLog> logInfos = userLogMapper.selectList(queryWrapper);
        return new PageInfo<>(logInfos);
    }
}
