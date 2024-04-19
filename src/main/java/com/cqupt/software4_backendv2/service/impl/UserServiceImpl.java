package com.cqupt.software4_backendv2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.software4_backendv2.dao.UserMapper;
import com.cqupt.software4_backendv2.entity.Task;
import com.cqupt.software4_backendv2.entity.User;
import com.cqupt.software4_backendv2.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public PageInfo<User> findByPageService(Integer pageNum, Integer pageSize, QueryWrapper<User> queryWrapper) {
        PageHelper.startPage(pageNum,pageSize);
        List<User> userInfos = userMapper.selectList(queryWrapper);
        return new PageInfo<>(userInfos);
    }

    @Override
    public User getUserByUserName(String username) {
        User user = userMapper.queryByUername(username);
        return user;
    }

}
