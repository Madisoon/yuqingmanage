package com.syx.yuqingmanage.module.user.web;

import com.syx.yuqingmanage.module.user.service.IFieldService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Master  Zg on 2016/11/17.
 */
@RestController
@RequestMapping(value = "/manage")
@Api(value = "自定义字段管理", description = "管理字段的API")
public class FieldController {
    @Autowired
    private IFieldService iFieldService;

    @RequestMapping(value = "/getAllField", method = RequestMethod.POST)
    @ApiOperation(value = "获取不同类型的字段", notes = "类型")
    public String getAllField(@RequestParam("dataType") String dataType) {
        String result = iFieldService.getAllField(dataType).toString();
        return result;
    }

    @RequestMapping(value = "/postFieldData", method = RequestMethod.POST)
    @ApiOperation(value = "插入字段", notes = "字段对象，id")
    public String postFieldData(HttpServletRequest request) {
        String configData = request.getParameter("configData");
        String id = request.getParameter("id");
        String result = iFieldService.postFieldData(configData, id).toString();
        return result;
    }

    @RequestMapping(value = "/deleteField", method = RequestMethod.POST)
    @ApiOperation(value = "删除字段", notes = "字段id")
    public String deleteField(@RequestParam("id") String id) {
        String result = iFieldService.deleteField(id).toString();
        return result;
    }

    @RequestMapping(value = "/getFieldName", method = RequestMethod.POST)
    @ApiOperation(value = "得到所有的字段名称", notes = "无")
    public String getFieldName(HttpServletRequest request) {
        String result = iFieldService.getFieldName().toString();
        return result;
    }

    @RequestMapping(value = "/getSingleField", method = RequestMethod.POST)
    @ApiOperation(value = "得到单个字段", notes = "字段id")
    public String getSingleField(@RequestParam("id") String id) {
        String result = iFieldService.getSingleField(id).toString();
        return result;
    }
}
