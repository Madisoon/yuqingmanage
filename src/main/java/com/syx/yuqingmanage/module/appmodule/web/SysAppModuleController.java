package com.syx.yuqingmanage.module.appmodule.web;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.syx.yuqingmanage.module.appmodule.service.ISysAppModuleService;
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
 * Created by Msater Zg on 2017/7/13.
 */
@RequestMapping(value = "/manage")
@RestController
@Api(value = "SysAppModuleController", description = "app模块管理")
public class SysAppModuleController {
    @Autowired
    ISysAppModuleService iSysAppModuleService;

    @RequestMapping(value = "/insertAppModule", method = RequestMethod.POST)
    @ApiOperation(value = "insertAppModule", notes = "插入App模块")
    public String insertAppModule(@RequestParam("appModuleInfo") String appModuleInfo,
                                  @RequestParam("appModuleBaseTag") String appModuleBaseTag,
                                  @RequestParam("appArea") String appArea,
                                  @RequestParam("appModuleTag") String appModuleTag) {
        String result = iSysAppModuleService.insertAppModule(appModuleInfo, appModuleTag, appModuleBaseTag, appArea).toString();
        return result;
    }

    @RequestMapping(value = "/updateAppModule", method = RequestMethod.POST)
    @ApiOperation(value = "updateAppModule", notes = "修改App模块")
    public String updateAppModule(@RequestParam("appModuleId") String appModuleId,
                                  @RequestParam("appModuleInfo") String appModuleInfo,
                                  @RequestParam("appModuleBaseTag") String appModuleBaseTag,
                                  @RequestParam("appModuleTag") String appModuleTag) {
        String result = iSysAppModuleService.updateAppModule(appModuleId, appModuleInfo, appModuleTag, appModuleBaseTag).toString();
        return result;
    }

    @RequestMapping(value = "/deleteAppModule", method = RequestMethod.POST)
    @ApiOperation(value = "deleteAppModule", notes = "删除App模块")
    public String deleteAppModule(@RequestParam("appModuleId") String appModuleId) {
        String result = iSysAppModuleService.deleteAppModule(appModuleId).toString();
        return result;
    }

    @RequestMapping(value = "/getAllAppModule", method = RequestMethod.POST)
    @ApiOperation(value = "getAllAppModule", notes = "获取所有的app模块")
    public String getAllAppModule(HttpServletRequest request) {
        try {
            String param = IOUtils.toString(request.getInputStream(), "utf-8");
            JSONObject params = JSONObject.parseObject(param);
            String areaId = params.getString("areaId");
            String result = iSysAppModuleService.getAllAppModule(areaId).toString();
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ExecResult(false, "获取数据异常。").toString();
        }
    }
}
