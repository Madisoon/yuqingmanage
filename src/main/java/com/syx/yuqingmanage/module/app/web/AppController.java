package com.syx.yuqingmanage.module.app.web;

import com.syx.yuqingmanage.module.app.service.IAppService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Master  Zg on 2016/12/12.
 */
@RequestMapping(value = "/manage")
@RestController
@Api(value = "开放接口", description = "开放出去的接口，获取调用其它服务器的接口")
public class AppController {
    @Autowired
    private IAppService iAppService;

    @RequestMapping(value = "/appAddUser", method = RequestMethod.POST)
    public String appAddUser(@RequestParam("userInfo") String userInfo) {
        String result = iAppService.addUser(userInfo).toString();
        return result;
    }

    @RequestMapping(value = "/appDeleteUser", method = RequestMethod.POST)
    public String appDeleteUser(@RequestParam("userId") String userId) {
        String result = iAppService.deleteUser(userId).toString();
        return result;
    }

    @RequestMapping(value = "/appUpdateUser", method = RequestMethod.POST)
    public String appUpdateUser(@RequestParam("userInfo") String userInfo) {
        String result = iAppService.updateUser(userInfo).toString();
        return result;
    }

    @RequestMapping(value = "/appGetAllCustomer", method = RequestMethod.POST)
    public String appGetAllCustomer(HttpServletRequest request) {
        String result = iAppService.getAllCustomer().toString();
        return result;
    }

    @ApiOperation(value = "插入自定义标签的id", notes = "客户信息的id")
    @RequestMapping(value = "/appGetCustomerById", method = RequestMethod.POST)
    public String appGetCustomerById(@RequestParam("id") String id) {
        String result = iAppService.getCustomerById(id).toString();
        return result;
    }

    @ApiOperation(value = "删除自定义标签的id", notes = "客户信息id")
    @RequestMapping(value = "/deleteCustomerInfo", method = RequestMethod.POST)
    public String deleteCustomerInfo(@RequestParam("id") String id) {
        String result = iAppService.deleteCustomerInfo(id).toString();
        return result;
    }

    @ApiOperation(value = "更新服务平台基础数据", notes = "无")
    @RequestMapping(value = "/refreshData", method = RequestMethod.POST)
    public void refreshData() {
        iAppService.refreshData();
    }
}
