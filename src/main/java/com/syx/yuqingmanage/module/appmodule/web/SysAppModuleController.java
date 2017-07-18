package com.syx.yuqingmanage.module.appmodule.web;

import com.syx.yuqingmanage.module.appmodule.service.ISysAppModuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
                                  @RequestParam("appArea") String appArea,
                                  @RequestParam("appModuleInfoTag") String appModuleInfoTag) {
        String result = iSysAppModuleService.insertAppModule(appModuleInfo, appModuleInfoTag, appArea).toString();
        return result;
    }

    @RequestMapping(value = "/deleteAppModule", method = RequestMethod.POST)
    @ApiOperation(value = "deleteAppModule", notes = "删除App模块")
    public String deleteAppModule(@RequestParam("appModuleId") String appModuleId) {
        String result = iSysAppModuleService.deleteAppModule(appModuleId).toString();
        return result;
    }
}
