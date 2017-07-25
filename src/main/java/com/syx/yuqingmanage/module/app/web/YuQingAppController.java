package com.syx.yuqingmanage.module.app.web;

import com.alibaba.fastjson.JSONObject;
import com.syx.yuqingmanage.module.app.service.IYuQingService;
import com.syx.yuqingmanage.utils.SysCookie;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Msater Zg on 2017/7/13.
 */
@RestController
@Api(value = "YuQingAppController", description = "定制版手机APP接口文档")
public class YuQingAppController {
    @Autowired
    IYuQingService iYuQingService;

    @Autowired
    SysCookie sysCookie;


    @ApiOperation(value = "judgerAppUser", notes = "app用户登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginName", value = "用户名", required = true, dataType = "STRING"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "LONG"),
            @ApiImplicitParam(name = "timestamp", value = "调用该接口时的时间戳，用于和用户名共同组成极光推送的别名alias", required = true, dataType = "STRING")
    })
    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    public String judgerAppUser(
            @RequestParam("loginName") String loginName,
            @RequestParam("password") String password,
            @RequestParam("timestamp") long timestamp,
            HttpServletResponse httpServletResponse) {
        JSONObject jsonObject = iYuQingService.judgerAppUser(loginName, password, timestamp);
        boolean flag = jsonObject.getBoolean("success");
        String token = "";
        if (flag) {
            token = jsonObject.getString("value");
            Cookie cookie = new Cookie("token", token);
            cookie.setMaxAge(3600 * 24);
            httpServletResponse.addCookie(cookie);
        }
        return jsonObject.toString();
    }

    @ApiOperation(value = "checkToken", notes = "查看登录状态码是否过期")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/user/checkToken", method = RequestMethod.POST)
    public String checkToken(HttpServletRequest httpServletRequest) {
        String result = "";
        return result;
    }

    @ApiOperation(value = "searchMenus", notes = "获取频道")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/user/searchMenus", method = RequestMethod.POST)
    public String searchMenus(HttpServletRequest httpServletRequest) {
        String loginName = judgeCookie(httpServletRequest);
        String result = "";
        if ("".equals(loginName)) {
            result = returnStaticJsonObject();
        } else {

        }
        return result;
    }

    @ApiOperation(value = "searchFocus", notes = "获取聚焦频道的数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag_id", value = "聚焦频道的tag_id的值", required = true, dataType = "INT"),
            @ApiImplicitParam(name = "limit", value = "分页查询每页条数，固定值：20", required = true, dataType = "INT"),
            @ApiImplicitParam(name = "date", value = "日期字符串，固定值：“”（空字符串）", required = true, dataType = "STRING")
    })
    @RequestMapping(value = "/data/searchFocus", method = RequestMethod.POST)
    public String searchFocus(@RequestParam("tag_id") int tagId,
                              @RequestParam("limit") int limit,
                              @RequestParam("date") String date,
                              HttpServletRequest httpServletRequest) {
        String loginName = judgeCookie(httpServletRequest);
        String result = "";
        if ("".equals(loginName)) {
            result = returnStaticJsonObject();
        } else {

        }
        return result;
    }

    @ApiOperation(value = "searchTagInfo", notes = "获取某个信息频道（非聚焦频道）的数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filters", value = "Json字符串，[{\"columnName\":\"tag_id\",\"op\":2,\"value\": xxxxx}]。其中的 xxxxx，是该频道的tag_id。", required = true, dataType = "STRING"),
            @ApiImplicitParam(name = "limit", value = "分页查询每页条数，固定值：20", required = true, dataType = "INT"),
            @ApiImplicitParam(name = "date", value = "日期字符串，固定值：“”（空字符串）", required = true, dataType = "STRING")
    })
    @RequestMapping(value = "/data/searchTagInfo", method = RequestMethod.POST)
    public String searchTagInfo(@RequestParam("filters") int tagId,
                                @RequestParam("limit") int limit,
                                @RequestParam("date") String date,
                                HttpServletRequest httpServletRequest) {
        String loginName = judgeCookie(httpServletRequest);
        String result = "";
        if ("".equals(loginName)) {
            result = returnStaticJsonObject();
        } else {

        }
        return result;
    }

    @ApiOperation(value = "searchFavor", notes = "获取收藏信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "分页查询每页条数，固定值：20", required = true, dataType = "INT"),
            @ApiImplicitParam(name = "date", value = "日期字符串，固定值：“”（空字符串）", required = true, dataType = "STRING")
    })
    @RequestMapping(value = "/favor/search", method = RequestMethod.POST)
    public String searchFavor(@RequestParam("limit") int limit,
                              @RequestParam("date") String date,
                              HttpServletRequest httpServletRequest) {
        String loginName = judgeCookie(httpServletRequest);
        String result = "";
        if ("".equals(loginName)) {
            result = returnStaticJsonObject();
        } else {

        }
        return result;
    }

    @ApiOperation(value = "removeFavor", notes = "删除收藏数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "被删除的消息的id", required = true, dataType = "STRING"),
    })
    @RequestMapping(value = "/favor/remove", method = RequestMethod.POST)
    public String removeFavor(@RequestParam("id") String limit,
                              HttpServletRequest httpServletRequest) {
        String loginName = judgeCookie(httpServletRequest);
        String result = "";
        if ("".equals(loginName)) {
            result = returnStaticJsonObject();
        } else {

        }
        return result;
    }

    @ApiOperation(value = "addFavor", notes = "添加收藏信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "需要添加的数据的id", required = true, dataType = "STRING"),
    })
    @RequestMapping(value = "/favor/add", method = RequestMethod.POST)
    public String addFavor(@RequestParam("limit") int limit,
                           @RequestParam("date") String date,
                           HttpServletRequest httpServletRequest) {
        String loginName = judgeCookie(httpServletRequest);
        String result = "";
        if ("".equals(loginName)) {
            result = returnStaticJsonObject();
        } else {

        }
        return result;
    }

    @ApiOperation(value = "checkFavor", notes = "检查收藏关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "需要查看的数据的id", required = true, dataType = "STRING"),
    })
    @RequestMapping(value = "/favor/check", method = RequestMethod.POST)
    public String checkFavor(@RequestParam("id") String id,
                             HttpServletRequest httpServletRequest) {
        String loginName = judgeCookie(httpServletRequest);
        String result = "";
        if ("".equals(loginName)) {
            result = returnStaticJsonObject();
        } else {

        }
        return result;
    }

    @ApiOperation(value = "updatePwd", notes = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "old", value = "原密码", required = true, dataType = "STRING"),
            @ApiImplicitParam(name = "password", value = "新密码", required = true, dataType = "STRING")
    })
    @RequestMapping(value = "/user/update", method = RequestMethod.POST)
    public String updatePwd(@RequestParam("old") String old,
                            @RequestParam("password") String password,
                            HttpServletRequest httpServletRequest) {
        String loginName = judgeCookie(httpServletRequest);
        String result = "";
        if ("".equals(loginName)) {
            result = returnStaticJsonObject();
        } else {

        }
        return result;
    }

    @ApiOperation(value = "updateConfig", notes = "修改推送设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "频道的类型。1: 信息频道；2: 聚焦频道。即接口2.1的返回值中对应的type的值。", required = true, dataType = "STRING"),
            @ApiImplicitParam(name = "tag_id", value = "频道的tag_id的值；", required = true, dataType = "STRING"),
            @ApiImplicitParam(name = "name", value = "频道的名称", required = true, dataType = "STRING"),
            @ApiImplicitParam(name = "push", value = "是否推送。 0：否；1：是", required = true, dataType = "STRING")
    })
    @RequestMapping(value = "/user/push/updateConfig", method = RequestMethod.POST)
    public String updateConfig(@RequestParam("type") String type,
                               @RequestParam("tag_id") String tag_id,
                               @RequestParam("name") String name,
                               @RequestParam("push") String push,
                               HttpServletRequest httpServletRequest) {
        String loginName = judgeCookie(httpServletRequest);
        String result = "";
        if ("".equals(loginName)) {
            result = returnStaticJsonObject();
        } else {

        }
        return result;
    }

    @ApiOperation(value = "getConfig", notes = "获取推送设置")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/user/push/getConfig", method = RequestMethod.POST)
    public String getConfig(HttpServletRequest httpServletRequest) {
        String result = "";
        return result;
    }

    @ApiOperation(value = "detailInfo", notes = "根据id来获取详情的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "想要查询的信息的id", required = true, dataType = "STRING")
    })
    @RequestMapping(value = "/data/detail", method = RequestMethod.POST)
    public String detailInfo(HttpServletRequest httpServletRequest) {
        String loginName = judgeCookie(httpServletRequest);
        String result = "";
        if ("".equals(loginName)) {
            result = returnStaticJsonObject();
        } else {

        }
        return result;
    }


    public String judgeCookie(HttpServletRequest httpServletRequest) {
        String loginName = sysCookie.getUser(httpServletRequest);
        return loginName;
    }

    public String returnStaticJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", false);
        jsonObject.put("value", 12001);
        return jsonObject.toString();
    }

}
