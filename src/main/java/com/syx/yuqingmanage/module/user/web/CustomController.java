package com.syx.yuqingmanage.module.user.web;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.syx.yuqingmanage.module.user.service.ICustomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Master  Zg on 2016/11/21.
 */
@RequestMapping(value = "/manage")
@RestController
@Api(value = "用户管理", description = "管理系统用户的功能")
public class CustomController {
    @Autowired
    private ICustomService customService;

    @RequestMapping(value = "/getAllCustom", method = RequestMethod.POST)
    @ApiOperation(value = "得到所有的用户", notes = "")
    public String getAllCustom(HttpServletRequest request) {

        try {
            String param = IOUtils.toString(request.getInputStream(), "utf-8");
            JSONObject params = JSONObject.parseObject(param);
            String pageNumber = params.getString("pageNumber");
            String pageSize = params.getString("pageSize");
            String choiceSelect = params.getString("choiceSelect");
            String result = customService.getAllCustom(pageNumber, pageSize, choiceSelect).toString();
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ExecResult(false, "获取数据异常。").toString();
        }
    }

    @RequestMapping(value = "/getAllCustomGroup", method = RequestMethod.POST)
    @ApiOperation(value = "得到用户分组（客户）", notes = "")
    public String getAllCustomGroup(HttpServletRequest request) {

        try {
            String param = IOUtils.toString(request.getInputStream(), "utf-8");
            JSONObject params = JSONObject.parseObject(param);
            String pageNumber = params.getString("pageNumber");
            String pageSize = params.getString("pageSize");
            String result = customService.getAllCustomGroup(pageNumber, pageSize).toString();
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ExecResult(false, "获取数据异常。").toString();
        }
    }

    @RequestMapping(value = "/deleteCustom", method = RequestMethod.POST)
    @ApiOperation(value = "删除用户", notes = "用户id，多个用逗号隔开")
    public String deleteCustom(@RequestParam("id") String id) {
        String result = customService.deleteCustom(id).toString();
        return result;
    }

    @RequestMapping(value = "/postCustomData", method = RequestMethod.POST)
    @ApiOperation(value = "添加用户", notes = "客户对象，客户id")
    public String postCustomData(@RequestParam("customData") String customData, @RequestParam("customId") String customId, @RequestParam("customInfo") String customInfo) {
        String result = customService.postCustomData(customData, customId, customInfo).toString();
        return result;
    }

    @RequestMapping(value = "/getDeparmentUser", method = RequestMethod.POST)
    @ApiOperation(value = "得到系统用户", notes = "无")
    public String getDeparmentUser(HttpServletRequest request) {
        String result = customService.getDeparmentUser().toString();
        return result;
    }

    @RequestMapping(value = "/getAllCustomerById", method = RequestMethod.POST)
    public String getAllCustomerById(HttpServletRequest request) {
        try {
            String param = IOUtils.toString(request.getInputStream(), "utf-8");
            JSONObject params = JSONObject.parseObject(param);
            String result = customService.getAllCustomerById(params.getString("customerId")).toString();
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ExecResult(false, "获取数据异常。").toString();
        }
    }
}
