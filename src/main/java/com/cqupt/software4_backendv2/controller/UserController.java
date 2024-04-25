package com.cqupt.software4_backendv2.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.cqupt.software4_backendv2.dao.UserLogMapper;
import com.cqupt.software4_backendv2.dao.UserMapper;
import com.cqupt.software4_backendv2.entity.Task;
import com.cqupt.software4_backendv2.entity.User;
import com.cqupt.software4_backendv2.common.Result;
import com.cqupt.software4_backendv2.entity.UserLog;
import com.cqupt.software4_backendv2.service.UserLogService;
import com.cqupt.software4_backendv2.service.UserService;
import com.cqupt.software4_backendv2.tool.SecurityUtil;
import com.cqupt.software4_backendv2.vo.UpdateStatusVo;
import com.cqupt.software4_backendv2.vo.UserPwd;
import com.cqupt.software4_backendv2.vo.VerifyUserQ;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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


    @Autowired
    private UserLogMapper userLogMapper;

    @Autowired
    private UserMapper userMapper;

//    @Autowired
//    private UserDetailsService userDetailsService;
    @Transactional
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

    //新增  yx
    // 判断用户名是否可用
    @GetMapping("/querUserNameExist")
    public Result querUserNameExist(@RequestParam String userName){
        User existUser = userService.getUserByUserName(userName);
        if (existUser != null){
            return Result.fail(500,"用户已经存在",null);
        }
        return Result.success(200, "用户名可用" , null);
    }
    //新注册
    @Transactional
    @PostMapping("/signUp")
    public Result signUp(@RequestBody User user) throws ParseException {

        // 检查用户名是否已经存在
        User existUser = userService.getUserByUserName(user.getUsername());
        if (existUser != null){
            return Result.fail(500,"用户已经存在",null);
        }
        String pwd = user.getPassword();
        // 对密码进行加密处理
        String password = SecurityUtil.hashDataSHA256(pwd);
        user.setPassword(password);
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = inputFormat.parse(date);
        user.setCreateTime(date1);
        user.setUpdateTime(null);
        user.setRole(1);
//        user.setUid( String.valueOf(new Random().nextInt()) );
        user.setUploadSize(50.0);
        user.setAllSize(50.0);
        user.setUserStatus("0");
        userService.save(user);
//          操作日志记录
        UserLog userLog = new UserLog();
        User one = userService.getUserByUserName(user.getUsername());
        String uid = one.getUid();
        Date OpTime = inputFormat.parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        userLog.setUid(uid);
        userLog.setUsername(user.getUsername());
        userLog.setOperation("用户注册");
        userLog.setTime(OpTime);
        userLog.setRole(user.getRole());
        userLogMapper.insert(userLog);
        return Result.success(200,"成功",null);
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user, HttpServletResponse response, HttpServletRequest request){

        // 判断验证编码
        String code = request.getSession().getAttribute("code").toString();
        if(code==null) return Result.fail(500,"验证码已过期！");
        if(user.getCode()==null || !user.getCode().equals(code)) {
            return Result.fail(500, "验证码错误!");
        }

        String userName = user.getUsername();
        User getUser = userService.getUserByUserName(userName);
        String password = getUser.getPassword();
        if (getUser != null){
            // 用户状态校验
            // 判断用户是否激活
            if (getUser.getUserStatus().equals("0")){
                return Result.fail("该账户未激活");
            }
            if (getUser.getUserStatus().equals("2")){
                return Result.fail("该账户已经被禁用");
            }

            String userStatus = getUser.getUserStatus();
            if(userStatus.equals("0")){ // 待激活
                return Result.fail(500,"账户未激活！");
            }else if(userStatus.equals("2")){
                return Result.fail(500,"用户已被禁用!");
            }

            // 进行验证密码
            String pwd = user.getPassword();
            String sha256 = SecurityUtil.hashDataSHA256(pwd);
            if (sha256.equals(password)){
                // 验证成功
                UserLog userLog = new UserLog();
                userLog.setUid(getUser.getUid());
                Date opTime = new Date();
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(opTime);
                userLog.setTime(opTime);
                userLog.setOperation("登录系统");
                userLog.setUsername(userName);
                userLog.setRole(user.getRole());
                userLogMapper.insert(userLog);
                // session认证
                HttpSession session = request.getSession();
                session.setAttribute("username",user.getUsername());
                session.setAttribute("userId",getUser.getUid());
                return Result.success(200, "登录成功", getUser);
            }else {
                return Result.success(500,"密码错误请重新输入",null);
            }
        }else {
            return Result.success(500,"用户不存在",null);
        }
    }

    @GetMapping("/info1")
    public User getUserInfo1(Principal principal){
        if(null == principal){
            return null;
        }
        String username = principal.getName();
        User user = userService.getUserByUserName(username);
        user.setPassword(null);
        return user;

    }
    @PostMapping("/newlogout")
    public Result logout(HttpServletRequest request){

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        session.invalidate();
        return Result.success(200,"退出成功",null);
    }

    /**
     * 管理员中心查看得所有用户信息
     *
     * @return
     */
    @GetMapping("/allUser")
    public Map<String, Object> allUser(@RequestParam(defaultValue = "1") int pageNum,
                                       @RequestParam(defaultValue = "10") int pageSize){

        return userService.getUserPage(pageNum, pageSize);

    }

    //新方法
    @GetMapping("/querUser")
    public List<User> querUser(){

        return userService.querUser();

    }
    /**
     *
     *  管理员修改用户状态
     * @return
     */
    @PostMapping("updateStatus")
    public Result  updateStatus(@RequestBody UpdateStatusVo updateStatusVo){
        // 根据 id  修改用户状态   角色
        boolean b = userService.updateStatusById(updateStatusVo.getUid() ,updateStatusVo.getRole(),updateStatusVo.getAllSize(), updateStatusVo.getStatus());
        if (b) return  Result.success(200 , "修改用户状态成功");
        return  Result.fail("修改失败");
    }
    //新方法
    @PostMapping("delUser")
    public Result delUser(@RequestBody UpdateStatusVo updateStatusVo){

        String uid = updateStatusVo.getUid();
        boolean b = userService.removeUserById(uid);
        if (b) return Result.success(200 , "删除成功");
        return Result.fail(200 , "删除失败");
    }
    // 忘记密码功能
    @GetMapping("/queryQuestions")
    public Result  forgotPwd(@RequestParam String username){
        User user = userService.getUserByUserName(username);
        System.out.println(user);
        String answer1 = user.getAnswer_1().split(":")[0];
        String answer2 = user.getAnswer_2().split(":")[0];
        String answer3 = user.getAnswer_3().split(":")[0];
        List<String> answers = new ArrayList<>();
        answers.add(answer1);
        answers.add(answer2);
        answers.add(answer3);
        return Result.success(200, "查询用户密保问题成功",answers );
    }


    // 验证问题


    @PostMapping("/verify")
    public Result verify(@RequestBody VerifyUserQ verifyUserQ){
        // 用户名   密保问题 和 答案
        QueryWrapper queryWrapper = new QueryWrapper<>()
                .eq("username",verifyUserQ.getUsername())
                .eq("answer_1" , verifyUserQ.getQ1()).eq("answer_2" , verifyUserQ.getQ2()).eq("answer_3" , verifyUserQ.getQ3());
        User user = userService.getOne(queryWrapper);

        if (user == null){
            return Result.fail("验证失败");
        }else {
            return Result.success(200 ," 验证成功，请重置密码");
        }

    }


    @PostMapping("updatePwd")
    public Result  updatePwd(@RequestBody UserPwd user){
        String password = user.getPassword();
        String sha256 = SecurityUtil.hashDataSHA256(password);
        user.setPassword(sha256);
        System.out.println(user);
        userService.updatePwd(user);
        return Result.success(200 , "修改密码成功");
    }

    //新增结束


    //个人中心开始
    @GetMapping("/getmessage/{uid}")
    public Result<List<User>> getall(@PathVariable("uid") String uid){
        User user = userMapper.selectByUid(uid);
        user.setPassword(null);
        System.out.println(user);
        return Result.success("200",user);
    }

    //修改密码，根据用户名匹配密码是否正确
    @PostMapping("/VerifyPas")
    public Result VerifyPas(@RequestBody Map<String, String> request){
        String username = request.get("username");
        String password = request.get("password");
        System.out.println(username);
        String  codedPwd = SecurityUtil.hashDataSHA256(password);
        User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
        String pwd = user.getPassword();
        if(!codedPwd.equals(pwd)){
            return Result.success(200,"密码不匹配",false);
        }
        return Result.success(200,"密码匹配",true);
    }


    //修改密码
    @PostMapping("/updatePas")
    public Result updatePas(@RequestBody Map<String, String> requests) {
        try {
            String username = requests.get("username");
            String password = requests.get("password");
            // 假设 userMapper 是 MyBatis 的一个 Mapper 接口
            String pwd = SecurityUtil.hashDataSHA256(password);
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("username", username).set("password", pwd);

            boolean result = userService.update(updateWrapper);

            //  操作日志记录

            UserLog userLog = new UserLog();

            QueryWrapper queryWrapper1  = new QueryWrapper<>();
            queryWrapper1.eq("username",username);

            User one = userService.getOne(queryWrapper1);
            String uid = one.getUid();
            userLog.setUsername(username);
            userLog.setUid(uid);
            userLog.setTime(new Date());
            userLog.setUsername(username);

            if (result) {
                userLog.setOperation("用户修改密码成功");

                userLogMapper.insert(userLog);
                // 更新成功，返回成功结果
                return Result.success(200, "更新成功");
            } else {
                userLog.setOperation("用户修改密码失败");

                userLogMapper.insert(userLog);
                // 更新失败，没有记录被更新
                return Result.success(404, "更新失败，用户不存在或密码未更改");
            }
        } catch (Exception e) {
            String username = requests.get("username");
            UserLog userLog = new UserLog();
            QueryWrapper queryWrapper1  = new QueryWrapper<>();
            queryWrapper1.eq("username",username);
            User one = userService.getOne(queryWrapper1);
            String uid = one.getUid();
            // userLog.setId(1);
            userLog.setUid(uid);
            userLog.setTime(new Date());
            userLog.setOperation("用户修改密码失败，发生未知错误");
            userLogMapper.insert(userLog);
            // 处理可能出现的任何异常，例如数据库连接失败等
            // 记录异常信息，根据实际情况决定是否需要发送错误日志
            // 这里返回一个通用的错误信息
            return Result.success(500, "更新失败，发生未知错误");
        }
    }

    //修改个人信息
    @PostMapping("/updateUser")
    public Result updateUser(@RequestBody User user) {
        try {
            // 假设 userMapper 是 MyBatis 的一个 Mapper 接口

            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("uid",user.getUid());

            int updatedRows = userMapper.update(user, wrapper);
            //  操作日志记录

            UserLog userLog = new UserLog();
            // userLog.setId(1);
            userLog.setUsername(user.getUsername());
            userLog.setUid(user.getUid());
            userLog.setTime(new Date());

            if (updatedRows > 0) {
                // 更新成功，返回成功结果
                userLog.setOperation("用户修改个人信息成功");
                userLogMapper.insert(userLog);

                return Result.success("200", "更新成功");
            } else {

                userLog.setOperation("用户修改个人信息失败");
                userLogMapper.insert(userLog);
                // 更新失败，没有记录被更新
                return Result.success("404", "更新失败，用户不存在");
            }
        } catch (Exception e) {
            // 处理可能出现的任何异常，例如数据库连接失败等
            // 记录异常信息，根据实际情况决定是否需要发送错误日志
            // 这里返回一个通用的错误信息
            return Result.success("500", "更新失败，发生未知错误");
        }
    }

    /**
     *  检查用户名是否重复
     * @param username
     * @return
     */
    @GetMapping("/checkRepetition/{username}")
    public Result checkRepetition(@PathVariable("username") String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);
        List<User> userList = userMapper.selectList(null);
        if(user != null){
            return Result.success(200, "用户名已存在");
        }else {
            return Result.success(200, "用户名可用");
        }
    }
    //个人中心结束

}
