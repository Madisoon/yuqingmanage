package com.syx.yuqingmanage.module.move.web;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.syx.yuqingmanage.module.move.service.ISchemeService;
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
 * Created by Msater Zg on 2017/2/9.
 */
@RestController
@RequestMapping(value = "/manage")
@Api(value = "方案管理", description = "方案相关的一些方法")
public class SchemeController {
    @Autowired
    private ISchemeService iSchemeService;

    @RequestMapping(value = "/insertScheme", method = RequestMethod.POST)
    @ApiOperation(value = "添加方案", notes = "方案对象，标签数组，地区数组，所选标签的基础标签")
    public String insertScheme(@RequestParam("schemeData") String schemeData,
                               @RequestParam("terraceTagIds") String terraceTagIds,
                               @RequestParam("tagIds") String tagIds,
                               @RequestParam("areaId") String areaId,
                               @RequestParam("baseTag") String baseTag) {
        String result = iSchemeService.insertScheme(schemeData, terraceTagIds, tagIds, areaId, baseTag).toString();
        return result;
    }

    @RequestMapping(value = "/getAllScheme", method = RequestMethod.POST)
    @ApiOperation(value = "得到所有的方案", notes = "地区数组")
    public String getAllScheme(HttpServletRequest request) {
        try {
            String param = IOUtils.toString(request.getInputStream(), "utf-8");
            JSONObject params = JSONObject.parseObject(param);
            String areaId = params.getString("areaId");
            String result = iSchemeService.getAllScheme(areaId).toString();
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ExecResult(false, "获取数据异常。").toString();
        }
    }

    @RequestMapping(value = "/deleteSchemeId", method = RequestMethod.POST)
    @ApiOperation(value = "删除方案", notes = "方案id数组")
    public String deleteSchemeId(@RequestParam("schemeId") String schemeId) {
        String result = iSchemeService.deleteSchemeId(schemeId).toString();
        return result;
    }

    @RequestMapping(value = "/updateScheme", method = RequestMethod.POST)
    @ApiOperation(value = "修改方案", notes = "方案id,标签id数组，方案对象，基础标签")
    public String updateScheme(@RequestParam("schemeId") String schemeId,
                               @RequestParam("tagIds") String tagIds,
                               @RequestParam("terraceTagId") String terraceTagId,
                               @RequestParam("schemeData") String schemeData,
                               @RequestParam("baseTag") String baseTag) {
        String result = iSchemeService.updateScheme(schemeId, tagIds, terraceTagId, schemeData, baseTag).toString();
        return result;
    }

    @RequestMapping(value = "/getAllSchemeById", method = RequestMethod.POST)
    @ApiOperation(value = "根据id得到方案", notes = "方案id")
    public String getAllSchemeById(@RequestParam("schemeId") String schemeId) {
        String result = iSchemeService.getAllScheme(schemeId).toString();
        return result;
    }

    @RequestMapping(value = "/getAllSchemeChoose", method = RequestMethod.POST)
    @ApiOperation(value = "得到方案（搜索）", notes = "搜索的方案对象，地区id数组，标签id数组")
    public String getAllSchemeChoose(HttpServletRequest request) {
        try {
            String param = IOUtils.toString(request.getInputStream(), "utf-8");
            JSONObject params = JSONObject.parseObject(param);
            String areaId = params.getString("areaId");
            String tagId = params.getString("tagId");
            String chooseSchemeData = params.getString("chooseSchemeData");
            String result = "";
            if (tagId.equals("") && (chooseSchemeData == null || "{}".equals(chooseSchemeData))) {
                result = iSchemeService.getAllScheme(areaId).toString();
            } else {
                result = iSchemeService.getAllSchemeChoose(areaId, tagId, chooseSchemeData).toString();
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ExecResult(false, "获取数据异常。").toString();
        }
    }
}
