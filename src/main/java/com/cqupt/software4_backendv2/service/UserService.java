package com.cqupt.software4_backendv2.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.software4_backendv2.entity.Task;
import com.cqupt.software4_backendv2.entity.User;
import com.github.pagehelper.PageInfo;
import com.cqupt.software4_backendv2.entity.RespBean;
public interface UserService extends IService<User> {
    PageInfo<User> findByPageService(Integer pageNum, Integer pageSize, QueryWrapper<User> queryWrapper);

    User getUserByUserName(String username);



}
