package com.syx.yuqingmanage.module.user.web;

import com.syx.yuqingmanage.module.user.service.ICustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Msater Zg on 2016/12/23.
 */
@RestController
@RequestMapping(value = "/manage")
@Api(value = "客户管理", description = "管理所有的客户")
public class CustomerController {
    @Autowired
    private ICustomerService iCustomerService;

    @RequestMapping(value = "/getAllCustomer", method = RequestMethod.POST)
    @ApiOperation(value = "得到所有的客户", notes = "无")
    public String getAllCustomer(HttpServletRequest request){
        String result = iCustomerService.getAllCustomer().toString();
        return result;
    }

    @RequestMapping(value = "/deleteCustomer", method = RequestMethod.POST)
    @ApiOperation(value = "删除客户", notes = "客户id多个用,号隔开")
    public String deleteCustomer(@RequestParam("id") String id){
        String result = iCustomerService.deleteCustomer(id).toString();
        return result;
    }

    @RequestMapping(value = "/insertCustomer", method = RequestMethod.POST)
    @ApiOperation(value = "插入客户", notes = "客户对象")
    public String insertCustomer(@RequestParam("customerInfo") String customerInfo){
        String result = iCustomerService.insertCustomer(customerInfo).toString();
        return result;
    }

    @RequestMapping(value = "/changeCustomer", method = RequestMethod.POST)
    @ApiOperation(value = "修改客户", notes = "客户对象，客户id")
    public String changeCustomer(@RequestParam("customerInfo") String customerInfo,@RequestParam("customerId") String customerId){
        String result = iCustomerService.changeCustomer(customerInfo,customerId).toString();
        return result;
    }
}
