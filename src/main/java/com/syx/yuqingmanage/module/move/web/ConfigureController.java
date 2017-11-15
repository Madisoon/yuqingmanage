package com.syx.yuqingmanage.module.move.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.syx.yuqingmanage.module.move.service.IConfigureService;
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

    @RequestMapping(value = "/addWx", method = RequestMethod.POST)
    @ApiOperation(value = "addWx", notes = "添加配置qq")
    public String addWx(@RequestParam("wxDate") String wxDate) {
        String result = iConfigureService.addWx(wxDate).toString();
        return result;
    }

    @RequestMapping(value = "/getAllWx", method = RequestMethod.POST)
    @ApiOperation(value = "getAllWx", notes = "获取所有的配置qq")
    public String getAllWx() {
        String result = iConfigureService.getAllWx().toString();
        return result;
    }

    @RequestMapping(value = "/deleteWx", method = RequestMethod.POST)
    @ApiOperation(value = "deleteWx", notes = "根据id删除QQ")
    public String deleteWx(@RequestParam("idData") String idData) {
        String result = iConfigureService.deleteWx(idData).toString();
        return result;
    }

    @RequestMapping(value = "/updateWx", method = RequestMethod.POST)
    @ApiOperation(value = "updateWx", notes = "根据id修改配置qq信息")
    public String updateWx(@RequestParam("wxData") String wxData, @RequestParam("id") String id) {
        String result = iConfigureService.updateWx(wxData, id).toString();
        return result;
    }

    @RequestMapping(value = "/postAnnouncement", method = RequestMethod.POST)
    @ApiOperation(value = "postAnnouncement", notes = "新增公告")
    public String postAnnouncement(@RequestParam("title") String title, @RequestParam("content") String content) {
        String result = iConfigureService.postAnnouncement(title, content).toString();
        return result;
    }

    @RequestMapping(value = "/getAnnouncement", method = RequestMethod.POST)
    @ApiOperation(value = "getAnnouncement", notes = "根据id删除QQ")
    public String getAnnouncement(HttpServletRequest request) {
        try {
            String param = IOUtils.toString(request.getInputStream(), "utf-8");
            JSONObject params = JSONObject.parseObject(param);
            JSONArray jsonArray = new JSONArray();
            String title = params.getString("title");
            if (!"".equals(title)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("columnName", "title");
                jsonObject.put("op", "4");
                jsonObject.put("value", title);
                jsonArray.add(jsonObject);
            }
            String content = params.getString("content");
            if (!"".equals(content)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("columnName", "content");
                jsonObject.put("op", "4");
                jsonObject.put("value", content);
                jsonArray.add(jsonObject);
            }
            String startTime = params.getString("startTime");
            if (!"".equals(startTime)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("columnName", "add_time");
                jsonObject.put("op", "1");
                jsonObject.put("value", startTime);
                jsonArray.add(jsonObject);
            }
            String endTime = params.getString("endTime");
            if (!"".equals(endTime)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("columnName", "add_time");
                jsonObject.put("op", "3");
                jsonObject.put("value", endTime);
                jsonArray.add(jsonObject);
            }
            System.out.println(jsonArray.toJSONString());
            String result = iConfigureService.getAnnouncement(jsonArray.toJSONString(), "0", "1000").toString();
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ExecResult(false, "获取数据异常。").toString();
        }
    }

    @RequestMapping(value = "/resetAnnouncement", method = RequestMethod.POST)
    @ApiOperation(value = "resetAnnouncement", notes = "重置公告")
    public String resetAnnouncement() {
        String result = iConfigureService.resetAnnouncement().toString();
        return result;
    }


}
