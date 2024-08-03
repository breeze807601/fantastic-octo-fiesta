package com.lwl.social_media_platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.domain.pojo.User;
import com.lwl.social_media_platform.domain.vo.UserLoginVo;
import com.lwl.social_media_platform.domain.vo.UserVo;
import com.lwl.social_media_platform.service.UserService;
import com.lwl.social_media_platform.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/login")
    public Result<UserLoginVo> login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userService.getOne(wrapper);
        if (user == null) {
            return Result.error("用户不存在!");
        }
        if (!user.getPassword().equals(password)) {
            return Result.error("密码错误!");
        }
        user.setPassword("********");
        UserLoginVo userLoginVo = new UserLoginVo();
        userLoginVo.setUser(user);
        // 生成token
        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(user.getId()));
        map.put("username", user.getUsername());
        userLoginVo.setToken(JWTUtil.createJWT(map));

        return Result.success(userLoginVo);
    }
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        // 默认头像
        user.setPic("https://homework1015.oss-cn-beijing.aliyuncs.com/pic.png");
        userService.save(user);
        return Result.success(user);
    }
    @GetMapping("/getUser")
    public Result<List<User>> getUser(){
        List<User> list = userService.list();
        return Result.success(list);
    }

    @GetMapping("/getUserById")
    public Result<UserVo> getUserById(@RequestParam("id") Long id){
        return Result.success(userService.getUserById(id));
    }
}
