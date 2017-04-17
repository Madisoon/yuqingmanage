package com.syx.yuqingmanage.module.user.web;

import com.syx.yuqingmanage.module.user.service.ICustomTrashService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Msater Zg on 2016/12/19.
 */
@RestController
@RequestMapping(value="/manage")
@Api(value = "已删用户", description = "管理已删除的用户")
public class CustomTrashController {
    @Autowired
    private ICustomTrashService iCustomTrashService;
    @RequestMapping(value = "/getAllTrashUser", method = RequestMethod.POST)
    @ApiOperation(value = "获取到所有删除的用户", notes = "无")
    public String getAllTrashUser(HttpServletRequest request){
        String result = iCustomTrashService.getAllTrashUser().toString();
        return result;
    }
}
