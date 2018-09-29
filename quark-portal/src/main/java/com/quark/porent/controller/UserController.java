package com.quark.porent.controller;

import com.quark.common.base.BaseController;
import com.quark.common.dto.QuarkResult;
import com.quark.common.entity.Posts;
import com.quark.common.entity.User;
import com.quark.porent.interceptor.LoginInterceptor;
import com.quark.porent.interceptor.SubjectHolder;
import com.quark.porent.service.NotificationService;
import com.quark.porent.service.PostsService;
import com.quark.porent.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

/**
 * @Author LHR
 * Create By 2017/8/23
 */
@RequestMapping("/user")
@Controller
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    @Autowired
    private PostsService postsService;

    @Autowired
    private NotificationService notificationService;


    @RequestMapping("/login")
    public String login() {
        return "user/login";
    }

    @RequestMapping("/register")
    public String register() {
        return "user/register";
    }

    @RequestMapping("/home")
    public String home() {
        return "user/home";
    }

    @RequestMapping("/set")
    public String setting() {
        return "user/set";
    }

    @RequestMapping("/seticon")
    public String seticon() {
        return "user/seticon";
    }

    @RequestMapping("/setpsw")
    public String setpsw() {
        return "user/setpsw";
    }

    @RequestMapping("/message")
    public String message() { return "user/message"; }

    private void addCookie(HttpServletResponse httpServletResponse,String token){
        Cookie cookie = new Cookie(LoginInterceptor.TOKEN,token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        httpServletResponse.addCookie(cookie);
    }

    private void deleteCookie(HttpServletResponse httpServletResponse){
        Cookie cookie = new Cookie(LoginInterceptor.TOKEN,"");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        httpServletResponse.addCookie(cookie);
    }


    @ApiOperation("注册接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "用户邮箱",dataType = "String"),
            @ApiImplicitParam(name = "username", value = "用户名称",dataType = "String"),
            @ApiImplicitParam(name = "password", value = "用户密码",dataType = "String")
    })
    @PostMapping("/api/register")
    @ResponseBody
    public QuarkResult checkUserName(String email, String username, String password) {
        QuarkResult result = restProcessor(() -> {
            if (!userService.checkUserName(username))
                return QuarkResult.warn("用户名已存在，请重新输入");

            if (!userService.checkUserEmail(email))
                return QuarkResult.warn("用户邮箱已存在，请重新输入");

            else
                userService.createUser(email,username,password);

            return QuarkResult.ok();

        });
        return result;
    }

    @ApiOperation("登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "用户邮箱",dataType = "String"),
            @ApiImplicitParam(name = "password", value = "用户密码",dataType = "String")
    })
    @PostMapping("/api/login")
    @ResponseBody
    public QuarkResult Login(String email,String password,HttpServletResponse response) {

        QuarkResult result = restProcessor(() -> {
            User loginUser = userService.findByEmail(email);
            if (loginUser == null)
                return QuarkResult.warn("用户邮箱不存在，请重新输入");
            if (!loginUser.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes())))
                return QuarkResult.warn("用户密码错误，请重新输入");
            String token = userService.LoginUser(loginUser);
            addCookie(response,token);
            return QuarkResult.ok(token);
        });
        return result;
    }

    @ApiOperation("根据Token获取用户的信息")
    @ApiImplicitParams({
    })
    @GetMapping("/api/info")
    @ResponseBody
    public QuarkResult getUserByToken() {
        QuarkResult result = restProcessor(() -> {
            User user = SubjectHolder.get();
            if (user == null) return QuarkResult.warn("session过期,请重新登录");
            return QuarkResult.ok(user);
        });

        return result;
    }

    @ApiOperation("根据Token获取用户的信息与通知消息数量")
    @ApiImplicitParams({
    })
    @GetMapping("/api/message/{token}")
    @ResponseBody
    public QuarkResult getUserAndMessageByToken(){
        QuarkResult result = restProcessor(() -> {
            HashMap<String, Object> map = new HashMap<>();
            User user = SubjectHolder.get();
            long count = notificationService.getNotificationCount(user.getId());
            map.put("user",user);
            map.put("messagecount",count);
            return QuarkResult.ok(map);
        });

        return result;
    }

    @ApiOperation("根据Token修改用户的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "要修改的用户名",dataType = "String"),
            @ApiImplicitParam(name = "signature", value = "用户签名",dataType = "String"),
            @ApiImplicitParam(name = "sex", value = "要修改的性别：数值0为男，1为女",dataType = "int"),
    })
    @PutMapping("/api/info")
    @ResponseBody
    public QuarkResult updateUser(String username, String signature, Integer sex, HttpServletRequest servletRequest, HttpServletResponse httpServletResponse){
        QuarkResult result = restProcessor(() -> {
            if (!userService.checkUserName(username)) return QuarkResult.warn("用户名重复！");
            if (sex != 0 && sex != 1) return QuarkResult.warn("性别输入错误！");
            User user = SubjectHolder.get();
            String token = userService.updateUser(user.getId(), username, signature, sex);
            addCookie(httpServletResponse,token);
            return QuarkResult.ok();
        });

        return result;
    }

    @ApiOperation("根据Token修改用户的密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newpsd",value = "旧的密码",dataType = "String"),
            @ApiImplicitParam(name = "oldpsd",value = "新的密码",dataType = "String"),
    })
    @PutMapping("/api/password")
    @ResponseBody
    public QuarkResult updatePassword(String newpsd,String oldpsd){

        QuarkResult result = restProcessor(() -> {
            User user = SubjectHolder.get();
            userService.updateUserPassword(user.getId(),oldpsd,newpsd);
            return QuarkResult.ok();
        });
        return result;
    }

    @ApiOperation("登出用户")
    @ApiImplicitParams({
    })
    @PostMapping("/api/logout")
    @ResponseBody
    public QuarkResult logout(HttpServletResponse httpServletResponse) {
        QuarkResult result = restProcessor(() -> {
            deleteCookie(httpServletResponse);
            return QuarkResult.ok();
        });
        return result;
    }

    @ApiOperation("根据用户ID获取用户详情与用户最近发布的十个帖子[主要用于用户主页展示]")
    @ApiImplicitParams({
    })
    @GetMapping("/api/detail")
    @ResponseBody
    public QuarkResult getUserById(){
        QuarkResult result = restProcessor(() -> {
            User user = SubjectHolder.get();
            if (user == null ) return QuarkResult.warn("用户不存在");
            List<Posts> postss = postsService.getPostsByUser(user);
            HashMap<String, Object> map = new HashMap<>();
            map.put("posts",postss);
            map.put("user",user);
            return QuarkResult.ok(map);
        });
        return result;
    }


}
