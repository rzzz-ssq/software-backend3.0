package com.cqupt.software4_backendv2.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.cqupt.software4_backendv2.entity.Task;
import com.cqupt.software4_backendv2.entity.User;
import com.cqupt.software4_backendv2.common.Result;
import com.cqupt.software4_backendv2.service.UserService;
import com.cqupt.software4_backendv2.tool.SecurityUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Map;

/**
 *
 * 用户管理模块
 *
 * 用户注册
 * 用户登录
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;



    @PostMapping("/signUp")
    public Result signUp(@RequestBody User user) {

        // 检查用户名是否已经存在
//        user.setUid(0);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username",user.getUsername());

        User existUser = userService.getOne(queryWrapper);

        if (existUser != null){
            return Result.fail(500,"用户名已存在");
        }
        String pwd = user.getPassword();
        // 对密码进行加密处理
        String password = SecurityUtil.hashDataSHA256(pwd);
        user.setPassword(password);
        user.setCreateTime(new Date());
        user.setUpdateTime(null);
        System.out.println(user);
        userService.save(user);
        return Result.success(200,"注册成功");

    }

    @PostMapping("/login")
    public Result login(@RequestBody User user, HttpServletResponse response, HttpServletRequest request){

        String userName = user.getUsername();

        QueryWrapper queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("username",user.getUsername());

        User getUser = userService.getOne(queryWrapper);


        if (getUser != null){
            String password = getUser.getPassword();
            // 进行验证密码
            String pwd = user.getPassword();
            String sha256 = SecurityUtil.hashDataSHA256(pwd);
            if (sha256.equals(password)){
                // session认证
                HttpSession session = request.getSession();
                session.setAttribute("username",user.getUsername());
                session.setAttribute("userId",getUser.getUid());

                String uid = getUser.getUid().toString();
                Cookie cookie = new Cookie("userId",uid );
                response.addCookie(cookie);

                //封装user对象返回
                User user1 = new User();
                user1.setUid(getUser.getUid());
                user1.setUsername(getUser.getUsername());
                user1.setRole(getUser.getRole());

                return Result.success(200,"登录成功",user1);
            }else {
                return Result.fail(500,"密码错误请重新输入",null);
            }

        }else {
            return Result.fail(500,"用户不存在",null);
        }
    }


    @PostMapping("/logout")
    public Result logout(HttpServletRequest request,HttpServletResponse response){

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        session.invalidate();
        Cookie cookie = new Cookie("userId", userId.toString());
        cookie.setMaxAge(0); // 设置过期时间为0，表示立即过期
        cookie.setPath("/"); // 设置Cookie的作用路径，保持与之前设置Cookie时的路径一致
        response.addCookie(cookie); // 添加到HTTP响应中
        return Result.success(200,"退出成功",null);
    }


    @GetMapping("/getUserList")
    public Result getUserList() {
        return Result.success(200,"注册成功",userService.list());
    }

    @GetMapping("/selectByPage")
    public Result selectByPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam String searchUser
                               ){
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.like(StringUtils.isNotBlank(searchUser),"username",searchUser);
        PageInfo<User> pageInfo = userService.findByPageService(pageNum, pageSize,queryWrapper);
        return Result.success(pageInfo);
    }

    @PostMapping("/addUser")
    public Result addUser(@RequestBody Map<String,String> user) {


        QueryWrapper queryWrapper = new QueryWrapper();
        String username = user.get("username");
        queryWrapper.eq("username",username);

        User existUser = userService.getOne(queryWrapper);

        if (existUser != null){
            return Result.fail(500,"用户名已存在");
        }
        String pwd = user.get("password");
        // 对密码进行加密处理
        String password = SecurityUtil.hashDataSHA256(pwd);
        Date tempDate= new Date();
        User tempUser = new User();
        tempUser.setUsername(username);
        tempUser.setPassword(password);
        tempUser.setCreateTime(tempDate);
        tempUser.setUpdateTime(null);
        tempUser.setRole(1);
        userService.save(tempUser);
        return Result.success(200,"新增用户成功！");

    }

    @GetMapping("/delete/{uid}")
    public Result deleteUser(@PathVariable int uid) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid",uid);
        userService.remove(queryWrapper);
        return Result.success(200,"删除用户成功！");
    }

    @GetMapping("/getInfo/{uid}")
    public Result getUserInfo(@PathVariable int uid) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid",uid);
        User tempuser =  userService.getOne(queryWrapper);

        return Result.success(200,"获取用户信息成功！",tempuser);
    }

    @PostMapping("/edit")
    public Result getUserInfo(@RequestBody User user) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid",user.getUid());
        user.setPassword(SecurityUtil.hashDataSHA256(user.getPassword()));
        userService.update(user,queryWrapper);
        return Result.success(200,"更新用户信息成功！");
    }


}
