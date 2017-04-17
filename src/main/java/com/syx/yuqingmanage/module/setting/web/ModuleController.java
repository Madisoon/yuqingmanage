package com.syx.yuqingmanage.module.setting.web;

import com.syx.yuqingmanage.module.setting.service.IModuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Master  Zg on 2016/11/15.
 */
@RestController
@RequestMapping(value = "/manage")
@Api(value = "系统模块", description = "管理系统模块的API")
public class ModuleController {
    @Autowired
    private IModuleService iModuleService;

    @RequestMapping(value = "/insertModule", method = RequestMethod.POST)
    @ApiOperation(value = "插入系统模块", notes = "名称，url,id")
    public String insertModule(@RequestParam("module_value") String moduleValue,
                               @RequestParam("module_url") String moduleUrl,
                               @RequestParam("module_id") String moduleId) {
        String result = iModuleService.insertModule(moduleValue, moduleUrl, moduleId).toString();
        return result;
    }

    @RequestMapping(value = "/getAllModule", method = RequestMethod.POST)
    @ApiOperation(value = "得到所有的模块", notes = "无")
    public String getAllModule(HttpServletRequest request) {
        String result = iModuleService.getAllModule().toString();
        return result;
    }

    @RequestMapping(value = "/deleteModule", method = RequestMethod.POST)
    @ApiOperation(value = "删除模块", notes = "模块id")
    public String deleteModule(@RequestParam("module_id") String moduleId) {
        String result = iModuleService.deleteModule(moduleId).toString();
        return result;
    }

    @RequestMapping(value = "/getAllSecondModule", method = RequestMethod.POST)
    @ApiOperation(value = "获取二级模块", notes = "一级模块id")
    public String getAllSecondModule(@RequestParam("module_id") String moduleId) {
        String result = iModuleService.getAllSecondModule(moduleId).toString();
        return result;
    }

    @RequestMapping(value = "/updateModuleInfo", method = RequestMethod.POST)
    @ApiOperation(value = "修改模块信息", notes = "模块id,模块名称,模块url")
    public String updateModuleInfo(@RequestParam("module_id") String moduleId,
                                   @RequestParam("module_name") String moduleName,
                                   @RequestParam("module_content") String moduleContent) {
        String result = iModuleService.updateModuleInfo(moduleId, moduleName, moduleContent).toString();
        return result;
    }
}
