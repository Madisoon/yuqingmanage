package com.syx.yuqingmanage.module.infor.web;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.syx.yuqingmanage.module.infor.service.IInForHistoryService;
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
 * Created by Msater Zg on 2017/4/20.
 */
@RequestMapping(value = "/manage")
@RestController
@Api(value = "信息历史", description = "管理已发送信息的api")
public class IInForHistoryController {
    @Autowired
    private IInForHistoryService iInForHistoryService;

    @RequestMapping(value = "/getAllHistory", method = RequestMethod.POST)
    @ApiOperation(value = "获取已发送的消息", notes = "")
    public String getAllHistory(HttpServletRequest request) {
        try {
            String param = IOUtils.toString(request.getInputStream(), "utf-8");
            JSONObject params = JSONObject.parseObject(param);
            String pageNumber = params.getString("pageNumber");
            String pageSize = params.getString("pageSize");
            String tableChoiceData = params.getString("tableChoiceData");
            String result = "";
            if ("{}".equals(tableChoiceData)) {
                result = iInForHistoryService.getAllHistory(pageNumber, pageSize).toString();
            } else {
                result = iInForHistoryService.getChoiceHistory(pageNumber, pageSize, tableChoiceData).toString();

            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ExecResult(false, "获取数据异常。").toString();
        }
    }
}
