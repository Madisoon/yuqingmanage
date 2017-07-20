package com.syx.yuqingmanage.module.appmodule.web;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.syx.yuqingmanage.module.appmodule.service.ISysAppUserService;
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
 * Created by Master  Zg on 2016/11/9.
 */
@RequestMapping(value = "/manage")
@RestController
@Api(value = "SysAppUserController", description = "app用户管理")
public class SysAppUserController {
    @Autowired
    ISysAppUserService iSysAppUserService;

    @RequestMapping(value = "/insertAppUser", method = RequestMethod.POST)
    @ApiOperation(value = "insertAppUser", notes = "插入App模块")
    public String insertAppModule(@RequestParam("appUserInfo") String appUserInfo,
                                  @RequestParam("appUserProgram") String appUserProgram,
                                  @RequestParam("areaId") String areaId) {
        String result = iSysAppUserService.insertAppUser(appUserInfo, appUserProgram, areaId).toString();
        return result;
    }

    @RequestMapping(value = "/deleteAppUser", method = RequestMethod.POST)
    @ApiOperation(value = "deleteAppUser", notes = "插入App模块")
    public String deleteAppUser(@RequestParam("appUserLoginName") String appUserLoginName) {
        String result = iSysAppUserService.deleteAppUser(appUserLoginName).toString();
        return result;
    }

    @RequestMapping(value = "/updateAppUser", method = RequestMethod.POST)
    @ApiOperation(value = "updateAppUser", notes = "插入App模块")
    public String updateAppUser(@RequestParam("appUserLoginName") String appUserLoginName,
                                @RequestParam("appUserInfo") String appUserInfo,
                                @RequestParam("appUserProgram") String appUserProgram) {
        String result = iSysAppUserService.updateAppUser(appUserLoginName, appUserInfo, appUserProgram).toString();
        return result;
    }

    @RequestMapping(value = "/getAllAppUserModule", method = RequestMethod.POST)
    @ApiOperation(value = "getAllAppUserModule", notes = "得到所有的APP用户信息")
    public String getAllAppUser(HttpServletRequest request) {
        try {
            String param = IOUtils.toString(request.getInputStream(), "utf-8");
            JSONObject params = JSONObject.parseObject(param);
            String areaId = params.getString("areaId");
            String result = iSysAppUserService.getAllAppUserModule(areaId).toString();
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ExecResult(false, "获取数据异常。").toString();
        }
    }
}
