package com.syx.yuqingmanage.module.move.web;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.syx.yuqingmanage.module.move.service.ITopicService;
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
 * Created by Msater Zg on 2017/7/10.
 */
@RestController
@RequestMapping(value = "/manage")
@Api(value = "TopicController", description = "专题接口管理")
public class TopicController {
    @Autowired
    ITopicService iTopicService;

    @RequestMapping(value = "/insertTopicContext", method = RequestMethod.POST)
    @ApiOperation(value = "insertTopicContext", notes = "插入专题内容")
    public String insertTopicContext(@RequestParam("topicInfo") String topicInfo, @RequestParam("topicId") String topicId) {
        String result = iTopicService.insertTopicContext(topicId, topicInfo).toString();
        return result;
    }

    @RequestMapping(value = "/deleteTopicContext", method = RequestMethod.POST)
    @ApiOperation(value = "deleteTopicContext", notes = "删除专题内容")
    public String deleteTopicContext(@RequestParam("topicId") String topicId) {
        String result = iTopicService.deleteTopicContext(topicId).toString();
        return result;
    }

    @RequestMapping(value = "/checkTopicContext", method = RequestMethod.POST)
    @ApiOperation(value = "checkTopicContext", notes = "审核专题内容")
    public String checkTopicContext(@RequestParam("topicId") String topicId) {
        String result = iTopicService.checkTopicContext(topicId).toString();
        return result;
    }

    @RequestMapping(value = "/updateTopicContext", method = RequestMethod.POST)
    @ApiOperation(value = "updateTopicContext", notes = "修改专题内容")
    public String updateTopicContext(@RequestParam("topicInfo") String topicInfo, @RequestParam("topicId") String topicId) {
        String result = iTopicService.updateTopicContext(topicId, topicInfo).toString();
        return result;
    }

    @RequestMapping(value = "/getTopicContextByTopicId", method = RequestMethod.POST)
    @ApiOperation(value = "getTopicContextByTopicId", notes = "根据专题id获取专题内容")
    public String getTopicContextByTopicId(HttpServletRequest request) {
        try {
            String param = IOUtils.toString(request.getInputStream(), "utf-8");
            JSONObject params = JSONObject.parseObject(param);
            String pageNumber = params.getString("pageNumber");
            String pageSize = params.getString("pageSize");
            String topicId = params.getString("topicId");
            String result = iTopicService.getTopicContextByTopicId(topicId, pageSize, pageNumber).toString();
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ExecResult(false, "获取数据异常。").toString();
        }
    }

    @RequestMapping(value = "/insertTopic", method = RequestMethod.POST)
    @ApiOperation(value = "insertTopic", notes = "插入专题")
    public String insertTopic(@RequestParam("topicInfo") String topicInfo) {
        String result = iTopicService.insertTopic(topicInfo).toString();
        return result;
    }

    @RequestMapping(value = "/updateTopic", method = RequestMethod.POST)
    @ApiOperation(value = "updateTopic", notes = "修改专题")
    public String updateTopic(@RequestParam("topicId") String topicId, @RequestParam("topicName") String topicName) {
        String result = iTopicService.updateTopic(topicId, topicName).toString();
        return result;
    }

    @RequestMapping(value = "/deleteTopic", method = RequestMethod.POST)
    @ApiOperation(value = "deleteTopic", notes = "删除专题")
    public String deleteTopic(@RequestParam("topicId") String topicId) {
        String result = iTopicService.deleteTopic(topicId).toString();
        return result;
    }

    @RequestMapping(value = "/getAllTopic", method = RequestMethod.POST)
    @ApiOperation(value = "getAllTopic", notes = "得到所有专题")
    public String getAllTopic() {
        String result = iTopicService.getAllTopic().toString();
        return result;
    }
}
