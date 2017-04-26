package com.syx.yuqingmanage.module.infor.web;

import com.syx.yuqingmanage.module.infor.service.IInForPostService;
import com.syx.yuqingmanage.module.infor.service.imp.InForPostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Msater Zg on 2017/4/20.
 */
@RequestMapping(value = "/manage")
@RestController
@Api(value = "信息发送", description = "管理人工发送消息的api")
public class InForPostController {
    @Autowired
    private IInForPostService iInForPostService;

    @RequestMapping(value = "/getInforPost", method = RequestMethod.POST)
    @ApiOperation(value = "得到需要发送的信息", notes = "无")
    public String getInforPost() {
        String result = iInForPostService.getInforPost().toString();
        return result;
    }

    @RequestMapping(value = "/updateInforPost", method = RequestMethod.POST)
    @ApiOperation(value = "修改信息的状态", notes = "信息的id，和操作人")
    public String updateInforPost(@RequestParam("inforId") String inforId,
                                  @RequestParam("userLoginName") String userLoginName) {
        String result = iInForPostService.updateInforPost(inforId, userLoginName).toString();
        return result;
    }
}
