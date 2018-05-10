package com.syx.yuqingmanage.module.email.web;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.syx.yuqingmanage.module.email.service.imp.EmailService;
import com.syx.yuqingmanage.utils.email.JavaMailWithAttachment;
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
 * Created by Master  Zg on 2016/12/12.
 */
@RequestMapping(value = "/manage")
@RestController
@Api(value = "EmailController", description = "邮件发送相关的东西")
public class EmailController {
    @Autowired
    EmailService emailService;

    @RequestMapping(value = "/insertEmailData", method = RequestMethod.POST)
    @ApiOperation(value = "插入邮箱信息", notes = "模版id，模版url，标签id")
    public String insertInFor(@RequestParam("id") String id,
                              @RequestParam("url") String url,
                              @RequestParam("tagIdS") String tagIdS) {
        String result = emailService.insertEmailData(id, url, tagIdS).toString();
        return result;
    }

    @RequestMapping(value = "/deleteEmailData", method = RequestMethod.POST)
    @ApiOperation(value = "删除模版信息", notes = "id")
    public String deleteEmailData(@RequestParam("id") String id) {
        String result = emailService.deleteEmailData(id).toString();
        return result;
    }

    @RequestMapping(value = "/getAllPostEmail", method = RequestMethod.POST)
    @ApiOperation(value = "获取信息", notes = "无")
    public String getAllPostEmail(HttpServletRequest request) {
        try {
            String param = IOUtils.toString(request.getInputStream(), "utf-8");
            JSONObject params = JSONObject.parseObject(param);
            String pageNumber = params.getString("pageNumber");
            String pageSize = params.getString("pageSize");
            String result = emailService.getAllPostEmail(pageNumber, pageSize).toString();
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ExecResult(false, "获取数据异常。").toString();
        }
    }

    @RequestMapping(value = "/insertTemplateData", method = RequestMethod.POST)
    @ApiOperation(value = "插入模版信息", notes = "模版数据")
    public String insertTemplateData(@RequestParam("templateData") String templateData) {
        String result = emailService.insertTemplateData(templateData).toString();
        return result;
    }

    @RequestMapping(value = "/updateTemplateData", method = RequestMethod.POST)
    @ApiOperation(value = "修改模版信息", notes = "模版id，模版url，标签id")
    public String updateTemplateData(@RequestParam("templateData") String templateData,
                                     @RequestParam("id") String id) {
        String result = emailService.updateTemplateData(templateData, id).toString();
        return result;
    }

    @RequestMapping(value = "/deleteTemplateData", method = RequestMethod.POST)
    @ApiOperation(value = "删除模版信息", notes = "id")
    public String deleteTemplateData(@RequestParam("id") String id) {
        String result = emailService.deleteTemplateData(id).toString();
        return result;
    }

    @RequestMapping(value = "/getAllTemplate", method = RequestMethod.POST)
    @ApiOperation(value = "得到所有的模版信息", notes = "模版id，模版url，标签id")
    public String insertInFor() {
        String result = emailService.getAllTemplate().toString();
        return result;
    }

    @RequestMapping(value = "/deleteEmailMonitor", method = RequestMethod.POST)
    @ApiOperation(value = "删除手动发送的邮箱的信息", notes = "id")
    public String deleteEmailMonitor(@RequestParam("id") String id) {
        String result = emailService.deleteEmailMonitor(id).toString();
        return result;
    }

    @RequestMapping(value = "/updateEmailMonitor", method = RequestMethod.POST)
    @ApiOperation(value = "修改邮箱的状态", notes = "id")
    public String updateEmailMonitor(@RequestParam("id") String id) {
        String result = emailService.updateEmailMonitor(id).toString();
        return result;
    }

    @RequestMapping(value = "/getAllPostEmailMonitor", method = RequestMethod.POST)
    @ApiOperation(value = "获取邮件的状态", notes = "无")
    public String getAllPostEmailMonitor(HttpServletRequest request) {
        try {
            String param = IOUtils.toString(request.getInputStream(), "utf-8");
            JSONObject params = JSONObject.parseObject(param);
            String pageNumber = params.getString("pageNumber");
            String pageSize = params.getString("pageSize");
            String isStatus = params.getString("isStatus");
            String result = emailService.getAllPostEmailMonitor(pageNumber, pageSize, isStatus).toString();
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ExecResult(false, "获取数据异常。").toString();
        }
    }
}
