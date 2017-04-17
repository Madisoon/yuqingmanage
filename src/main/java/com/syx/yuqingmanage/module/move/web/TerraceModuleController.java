package com.syx.yuqingmanage.module.move.web;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.syx.yuqingmanage.module.move.service.ISchemeService;
import com.syx.yuqingmanage.module.move.service.ITerraceModuleService;
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
 * Created by Msater Zg on 2017/3/3.
 */
@RestController
@RequestMapping(value = "/manage")
@Api(value = "平台模块", description = "管理平台模块的相关方法")
public class TerraceModuleController {
    @Autowired
    private ITerraceModuleService iTerraceModuleService;

    @RequestMapping(value = "/insertTerraceModule", method = RequestMethod.POST)
    @ApiOperation(value = "插入平台模块", notes = "平台模块对象，标签id数组，地区id，基础id数组")
    public String insertTerraceModule(@RequestParam("terraceData") String terraceData,
                                      @RequestParam("tagIds") String tagIds,
                                      @RequestParam("areaId") String areaId,
                                      @RequestParam("baseTag") String baseTag) {
        String result = iTerraceModuleService.insertTerraceModule(terraceData, tagIds, areaId, baseTag).toString();
        return result;
    }

    @RequestMapping(value = "/getAllTerraceModule", method = RequestMethod.POST)
    @ApiOperation(value = "得到平台模块", notes = "地区id数组")
    public String getAllScheme(@RequestParam("areaId") String areaId) {
        String result = iTerraceModuleService.getAllTerraceModule(areaId).toString();
        return result;
    }

    @RequestMapping(value = "/deleteTerraceModuleId", method = RequestMethod.POST)
    @ApiOperation(value = "删除平台模块", notes = "平台模块id")
    public String deleteTerraceModuleId(@RequestParam("terraceId") String terraceId) {
        String result = iTerraceModuleService.deleteTerraceModuleId(terraceId).toString();
        return result;
    }

    @RequestMapping(value = "/updateTerraceModule", method = RequestMethod.POST)
    @ApiOperation(value = "修改平台模块", notes = "平台模块id，标签id数组，平台模块对象，基础标签数组")
    public String updateScheme(@RequestParam("terraceId") String terraceId,
                               @RequestParam("tagIds") String tagIds,
                               @RequestParam("terraceData") String terraceData,
                               @RequestParam("baseTag") String baseTag) {
        String result = iTerraceModuleService.updateTerraceModule(terraceId, tagIds, terraceData, baseTag).toString();
        return result;
    }

    /*@RequestMapping(value = "/getAllSchemeById", method = RequestMethod.POST)
    public String getAllSchemeById(@RequestParam("schemeId") String schemeId) {
        String result = iTerraceModuleService.getAllScheme(schemeId).toString();
        return result;
    }*/

    @RequestMapping(value = "/getAllTerraceModuleChoose", method = RequestMethod.POST)
    @ApiOperation(value = "得到平台模块（搜索）", notes = "地区id数组，平台模块搜索对象")
    public String getAllTerraceModuleChoose(HttpServletRequest request) {
        try {
            String param = IOUtils.toString(request.getInputStream(), "utf-8");
            JSONObject params = JSONObject.parseObject(param);
            String areaId = params.getString("areaId");
            String tagId = params.getString("tagId");
            String chooseTerraceData = params.getString("chooseTerraceData");
            String result = "";
            if (tagId.equals("") && (chooseTerraceData == null || "{}".equals(chooseTerraceData))) {
                result = iTerraceModuleService.getAllTerraceModule(areaId).toString();
            } else {
                result = iTerraceModuleService.getAllTerraceModuleChoose(areaId, tagId, chooseTerraceData).toString();
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ExecResult(false, "获取数据异常。").toString();
        }
    }
}
