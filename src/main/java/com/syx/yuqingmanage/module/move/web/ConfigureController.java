package com.syx.yuqingmanage.module.move.web;

import com.syx.yuqingmanage.module.move.service.IConfigureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Master  Zg on 2016/11/9.
 */
@RestController
@RequestMapping(value = "/manage")
@Api(value = "服务配置", description = "服务配置相关接口")
public class ConfigureController {
    @Autowired
    private IConfigureService iConfigureService;

    @RequestMapping(value = "/addQq", method = RequestMethod.POST)
    @ApiOperation(value = "addQq", notes = "添加配置qq")
    public String addQq(@RequestParam("qqDate") String qqDate) {
        String result = iConfigureService.addQq(qqDate).toString();
        return result;
    }

    @RequestMapping(value = "/getAllQq", method = RequestMethod.POST)
    @ApiOperation(value = "getAllQq", notes = "获取所有的配置qq")
    public String getAllQq() {
        String result = iConfigureService.getAllQq().toString();
        return result;
    }

    @RequestMapping(value = "/deleteQq", method = RequestMethod.POST)
    @ApiOperation(value = "deleteQq", notes = "根据id删除QQ")
    public String deleteQq(@RequestParam("idData") String idData) {
        String result = iConfigureService.deleteQq(idData).toString();
        return result;
    }

    @RequestMapping(value = "/updateQq", method = RequestMethod.POST)
    @ApiOperation(value = "updateQq", notes = "根据id修改配置qq信息")
    public String updateQq(@RequestParam("qqData") String qqData, @RequestParam("id") String id) {
        String result = iConfigureService.updateQq(qqData, id).toString();
        return result;
    }

    @RequestMapping(value = "/addPlan", method = RequestMethod.POST)
    @ApiOperation(value = "addPlan", notes = "增加计划任务")
    public String addPlan(@RequestParam("planData") String planData) {
        String result = iConfigureService.addPlan(planData).toString();
        return result;
    }

    @RequestMapping(value = "/getAllPlan", method = RequestMethod.POST)
    @ApiOperation(value = "addPlan", notes = "得到所有的计划任务")
    public String getAllPlan() {
        String result = iConfigureService.getAllPlan().toString();
        return result;
    }

    @RequestMapping(value = "/deletePlan", method = RequestMethod.POST)
    @ApiOperation(value = "deletePlan", notes = "根据id删除计划任务")
    public String deletePlan(@RequestParam("idData") String idData) {
        String result = iConfigureService.deletePlan(idData).toString();
        return result;
    }

    @RequestMapping(value = "/updatePlan", method = RequestMethod.POST)
    @ApiOperation(value = "updatePlan", notes = "根据id修改计划任务")
    public String updatePlan(@RequestParam("planData") String planData, @RequestParam("id") String id) {
        String result = iConfigureService.updatePlan(planData, id).toString();
        return result;
    }
}
