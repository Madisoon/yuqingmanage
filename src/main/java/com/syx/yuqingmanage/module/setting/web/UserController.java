package com.syx.yuqingmanage.module.setting.web;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.syx.yuqingmanage.module.setting.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * Created by Master  Zg on 2016/11/24.
 */
@RestController
@RequestMapping(value = "/manage")
@Api(value = "系统用户管理", description = "管理用户的API")
public class UserController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "/getUserAllInfo", method = RequestMethod.POST)
    @ApiOperation(value = "获取单个用户所有信息", notes = "账号，密码")
    public String getUserAllInfo(@RequestParam("user_loginname") String user_loginname, @RequestParam("user_password") String user_password, HttpServletRequest request) {

        ExecResult execResult = iUserService.getUserAllInfo(user_loginname, user_password);
        JSONObject jsonObject = (JSONObject) execResult.getData();
        System.out.println(jsonObject.getString("funciton"));
        System.out.println(request.getHeader("apiToken"));
        request.getSession().setAttribute("secret", "1");
        request.getSession().setAttribute("user", jsonObject.getString("user"));
        request.getSession().setAttribute("module", jsonObject.getString("module"));
        request.getSession().setAttribute("moduleFunction", jsonObject.getString("function"));
        String result = execResult.toString();
        return result;
    }

    @RequestMapping(value = "/getAllUser", method = RequestMethod.POST)
    @ApiOperation(value = "获取所有用户", notes = "无")
    public String getAllUser(HttpServletRequest request) {
        String result = iUserService.getAllUser().toString();
        return result;
    }

    @RequestMapping(value = "/getAllDepRole", method = RequestMethod.POST)
    @ApiOperation(value = "获取部门和角色", notes = "无")
    public String getAllDepRole(HttpServletRequest request) {
        String result = iUserService.getAllDepRole().toString();
        return result;
    }

    @RequestMapping(value = "/insertSysUser", method = RequestMethod.POST)
    @ApiOperation(value = "插入系统用户", notes = "用户对象")
    public String insertSysUser(@RequestParam("userAllData") String userAllData) {
        String result = iUserService.insertSysUser(userAllData).toString();
        return result;
    }

    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    @ApiOperation(value = "修改用户信息", notes = "用户对象")
    public String updateUserInfo(@RequestParam("userAllData") String userAllData) {
        String result = iUserService.updateUserInfo(userAllData).toString();
        return result;
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    @ApiOperation(value = "删除用户", notes = "用户id多个用 , 隔开")
    public String deleteUser(@RequestParam("id") String id) {
        String result = iUserService.deleteUser(id).toString();
        return result;
    }

}
