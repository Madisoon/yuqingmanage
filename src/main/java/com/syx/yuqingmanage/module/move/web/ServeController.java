package com.syx.yuqingmanage.module.move.web;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.syx.yuqingmanage.module.move.service.IServeService;
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
 * Created by Msater Zg on 2017/2/14.
 */
@RestController
@RequestMapping(value = "/manage")
@Api(value = "服务客户管理", description = "服务客户管理的相关接口方法")
public class ServeController {
    @Autowired
    private IServeService iServeService;

    @RequestMapping(value = "/updateServeCustomer", method = RequestMethod.POST)
    @ApiOperation(value = "修改服务客户", notes = "服务客户对象，客户接收信息数组，服务客户id")
    public String updateServeCustomer(@RequestParam("customerData") String customerData,
                                      @RequestParam("getData") String getData,
                                      @RequestParam("serveCustomerId") String serveCustomerId) {
        String result = iServeService.updateServeCustomer(customerData, getData, serveCustomerId).toString();
        return result;
    }

    @RequestMapping(value = "/insertServeCustomer", method = RequestMethod.POST)
    @ApiOperation(value = "插入服务客户", notes = "服务客户对象，客户接收信息数组，地区id")
    public String insertScheme(@RequestParam("customerData") String customerData,
                               @RequestParam("getData") String getData,
                               @RequestParam("areaId") String areaId) {
        String result = iServeService.insertServeCustomer(customerData, getData, areaId).toString();
        return result;
    }

    @RequestMapping(value = "/deleteServeCustomer", method = RequestMethod.POST)
    @ApiOperation(value = "删除服务客户", notes = "服务客户id")
    public String deleteServeCustomer(@RequestParam("serveCustomerId") String serveCustomerId) {
        String result = iServeService.deleteServeCustomer(serveCustomerId).toString();
        return result;
    }

    @RequestMapping(value = "/getAllServeCustomer", method = RequestMethod.POST)
    @ApiOperation(value = "获取所有的服务客户（搜索也可以）", notes = "地区id，搜索条件对象")
    public String getAllServeCustomer(HttpServletRequest request) {
        try {
            String param = IOUtils.toString(request.getInputStream(), "utf-8");
            JSONObject params = JSONObject.parseObject(param);
            String areaId = params.getString("areaId");
            String serveCustomerSearch = params.getString("serveCustomerSearch");
            String result = "";
            if (serveCustomerSearch == null || "{}".equals(serveCustomerSearch)) {
                result = iServeService.getAllServeCustomer(areaId).toString();
            } else {
                result = iServeService.getAllServeCustomerChoose(areaId, serveCustomerSearch).toString();
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ExecResult(false, "获取数据异常。").toString();
        }
    }

    @RequestMapping(value = "/getAllServeCustomers", method = RequestMethod.POST)
    @ApiOperation(value = "获取所有的服务客户", notes = "地区id")
    public String getAllServeCustomers(@RequestParam("areaId") String areaId) {
        String result = iServeService.getAllServeCustomer(areaId).toString();
        return result;
    }

    @RequestMapping(value = "/exportCustomerData", method = RequestMethod.POST)
    @ApiOperation(value = "导出服务客户的excel表格", notes = "标签，筛选的条件，导出的类型")
    public String exportCustomerData(@RequestParam("areaId") String areaId,
                                     @RequestParam("searchData") String searchData,
                                     @RequestParam("exportType") String exportType) {
        String result = iServeService.exportCustomerData(areaId, searchData, exportType).toString();
        return result;
    }
}
