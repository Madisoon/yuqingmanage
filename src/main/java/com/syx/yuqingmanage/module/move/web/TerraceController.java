package com.syx.yuqingmanage.module.move.web;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.syx.yuqingmanage.module.move.service.ITerraceService;
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
 * Created by Msater Zg on 2017/3/6.
 */
@RestController
@RequestMapping(value = "/manage")
@Api(value = "平台客户", description = "管理平台客户相关的方法")
public class TerraceController {
    @Autowired
    private ITerraceService iTerraceService;

    @RequestMapping(value = "/insertTerraceCustomer", method = RequestMethod.POST)
    @ApiOperation(value = "插入平台客户", notes = "平台客户对象，模块id数组，地区id")
    public String insertTerraceCustomer(@RequestParam("terraceCustomerData") String terraceCustomerData,
                                        @RequestParam("moduleId") String moduleId,
                                        @RequestParam("areaId") String areaId) {
        String result = iTerraceService.insertTerrace(terraceCustomerData, moduleId, areaId).toString();
        return result;
    }

    @RequestMapping(value = "/getAllTerraceCustomer", method = RequestMethod.POST)
    @ApiOperation(value = "得到平台用户", notes = "地区id数组，平台客户搜索（对象）")
    public String getAllTerraceCustomer(HttpServletRequest request) {
        try {
            String param = IOUtils.toString(request.getInputStream(), "utf-8");
            JSONObject params = JSONObject.parseObject(param);
            String areaId = params.getString("areaId");
            String serveTerraceSearch = params.getString("serveTerraceSearch");
            String result = "";
            if (serveTerraceSearch == null || "{}".equals(serveTerraceSearch)) {
                result = iTerraceService.getAllTerrace(areaId).toString();
            } else {
                result = iTerraceService.getAllTerraceChoose(areaId, serveTerraceSearch).toString();
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ExecResult(false, "获取数据异常。").toString();
        }
    }

    @RequestMapping(value = "/deleteTerraceCustomerId", method = RequestMethod.POST)
    @ApiOperation(value = "删除平台客户", notes = "平台客户id")
    public String deleteTerraceCustomerId(@RequestParam("terraceId") String terraceId) {
        String result = iTerraceService.deleteTerrace(terraceId).toString();
        return result;
    }

    @RequestMapping(value = "/updateTerraceCustomer", method = RequestMethod.POST)
    @ApiOperation(value = "修改平台客户", notes = "平台客户对象，模块id数组，平台客户id")
    public String updateTerraceCustomer(@RequestParam("terraceCustomerData") String terraceCustomerData,
                                        @RequestParam("moduleId") String moduleId,
                                        @RequestParam("terraceId") String terraceId) {
        String result = iTerraceService.updateTerrace(terraceCustomerData, moduleId, terraceId).toString();
        return result;
    }
}
