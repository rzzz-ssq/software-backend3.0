package com.cqupt.software4_backendv2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.software4_backendv2.dao.UserLogMapper;
import com.cqupt.software4_backendv2.dao.UserMapper;
import com.cqupt.software4_backendv2.entity.Task;
import com.cqupt.software4_backendv2.entity.User;
import com.cqupt.software4_backendv2.service.UserService;
import com.cqupt.software4_backendv2.vo.UserPwd;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserLogMapper userLogMapper;
    @Override
    public PageInfo<User> findByPageService(Integer pageNum, Integer pageSize, QueryWrapper<User> queryWrapper) {
        PageHelper.startPage(pageNum,pageSize);
        List<User> userInfos = userMapper.selectList(queryWrapper);
        return new PageInfo<>(userInfos);
    }

    @Override
    public User getUserByUserName(String username) {
        User user = userMapper.queryByUername(username);
        System.out.println(user);
        return user;
    }


    //yx  新增
    @Override
    public void saveUser(User user) {
        userMapper.saveUser(user);
    }

    @Override
    public Map<String, Object> getUserPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<User> userList = userMapper.selectUserPage(offset, pageSize);
        int total = userMapper.countUsers();

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("data", userList);
        return result;
    }

    @Override
    public List<User> querUser() {
        List<User> users = userMapper.selectList(null);
        return users;
    }

    @Override
    public boolean updateStatusById(String uid, Integer role, double allSize, String status) {
        QueryWrapper<User> wrapper = new QueryWrapper<User>();
        User user = userMapper.selectByUid(uid);
        Double uploadSize = user.getUploadSize();
        //upload_size = #{allSize} - all_size + upload_size
        uploadSize = allSize - user.getAllSize() + uploadSize;
        System.out.println(uploadSize);
        boolean b =  userMapper.updateStatusById(uid, role ,allSize,  status,uploadSize);
        if ( b )  return true;

        return false;
    }

    @Override
    public boolean removeUserById(String uid) {
        userMapper.removeUserById(uid);
        return true;
    }

    @Override
    public boolean updatePwd(UserPwd user) {
        userMapper.updatePwd(user);
        return false;
    }

}
