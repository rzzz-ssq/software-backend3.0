package com.cqupt.software4_backendv2.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.software4_backendv2.entity.Task;
import com.cqupt.software4_backendv2.entity.User;
import com.cqupt.software4_backendv2.vo.UserPwd;
import com.github.pagehelper.PageInfo;
import com.cqupt.software4_backendv2.entity.RespBean;

import java.util.List;
import java.util.Map;

public interface UserService extends IService<User> {
    PageInfo<User> findByPageService(Integer pageNum, Integer pageSize, QueryWrapper<User> queryWrapper);

    User getUserByUserName(String username);

    //新加的
    void saveUser(User user);
    Map<String, Object> getUserPage(int pageNum, int pageSize);
    List<User> querUser();
    boolean updateStatusById(String uid, Integer role ,double allSize, String status);
    boolean removeUserById(String uid);
    boolean updatePwd(UserPwd user);

}
