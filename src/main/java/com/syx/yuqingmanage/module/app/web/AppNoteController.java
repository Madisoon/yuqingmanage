package com.syx.yuqingmanage.module.app.web;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.syx.yuqingmanage.module.app.service.imp.AppNoteServiceImpl;
import io.swagger.annotations.Api;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 描述:
 * App日志的控制层
 *
 * @author Msater Zg
 * @create 2017-11-16 18:30
 */
@RequestMapping(value = "/manage")
@RestController
@Api(value = "AppNoteController", description = "开放出去的接口，获取调用其它服务器的接口")
public class AppNoteController {
    @Autowired
    AppNoteServiceImpl appNoteService;

    @RequestMapping(value = "/getAllAppNote", method = RequestMethod.POST)
    public String appAddUser(HttpServletRequest request) {
        try {
            String param = IOUtils.toString(request.getInputStream(), "utf-8");
            JSONObject params = JSONObject.parseObject(param);
            String pageNumber = params.getString("pageNumber");
            String pageSize = params.getString("pageSize");
            String chooseData = params.getString("chooseData");
            String result = "";
            if ("{}".equals(chooseData)) {
                result = appNoteService.getAllAppNote(pageNumber, pageSize).toString();
            } else {
                result = appNoteService.getAllAppNoteChoose(chooseData, pageNumber, pageSize).toString();
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ExecResult(false, "获取数据异常。").toString();
        }
    }

    @RequestMapping(value = "/exportAppNoteExcel", method = RequestMethod.POST)
    public String exportAppNoteExcel(@RequestParam("chooseData") String chooseData) {
        String result = appNoteService.exportAppNoteExcel(chooseData);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", result);
        return jsonObject.toString();
    }
}
