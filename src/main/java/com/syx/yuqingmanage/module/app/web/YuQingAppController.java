package com.syx.yuqingmanage.module.app.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

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
            @ApiImplicitParam(name = "loginName", value = "用户名", required = true, dataType = "STRING", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "LONG", paramType = "query"),
            @ApiImplicitParam(name = "timestamp", value = "调用该接口时的时间戳，用于和用户名共同组成极光推送的别名alias", required = true, dataType = "STRING", paramType = "query")
    })
    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    public String judgerAppUser(
            @RequestParam("loginName") String loginName,
            @RequestParam("password") String password,
            @RequestParam("timestamp") long timestamp) {
        JSONObject jsonObject = iYuQingService.judgerAppUser(loginName, password, timestamp);
        return jsonObject.toString();
    }

    @ApiOperation(value = "checkToken", notes = "查看登录状态码是否过期")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/user/checkToken", method = RequestMethod.POST)
    public String checkToken(HttpServletRequest httpServletRequest) {
        String result = iYuQingService.checkToken(sysCookie.getToken(httpServletRequest)).toString();
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
            result = iYuQingService.searchMenus(loginName).toString();
        }
        return result;
    }

    @ApiOperation(value = "searchFocus", notes = "获取聚焦频道的数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag_id", value = "聚焦频道的tag_id的值", required = true, dataType = "INT", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "分页查询每页条数，固定值：20", required = true, dataType = "INT", paramType = "query"),
            @ApiImplicitParam(name = "date", value = "日期字符串，固定值：“”（空字符串）", required = false, dataType = "STRING", paramType = "query")
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
            result = iYuQingService.searchFocus(tagId, limit, date, loginName).toString();
        }
        return result;
    }

    @ApiOperation(value = "searchTagInfo", notes = "获取某个信息频道（非聚焦频道）的数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filters", value = "Json字符串，[{\"columnName\":\"tag_id\",\"op\":2,\"value\": xxxxx}]。其中的 xxxxx，是该频道的tag_id。", required = true, dataType = "STRING", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "分页查询每页条数，固定值：20", required = true, dataType = "INT", paramType = "query"),
            @ApiImplicitParam(name = "date", value = "日期字符串，固定值：“”（空字符串）", required = true, dataType = "STRING", paramType = "query")
    })
    @RequestMapping(value = "/data/searchTagInfo", method = RequestMethod.POST)
    public String searchTagInfo(@RequestParam("filters") String filters,
                                @RequestParam("limit") int limit,
                                @RequestParam("date") String date,
                                HttpServletRequest httpServletRequest) {
        String loginName = judgeCookie(httpServletRequest);
        String result = "";
        if ("".equals(loginName)) {
            result = returnStaticJsonObject();
        } else {
            result = iYuQingService.searchTagInfo(filters, limit, date, loginName).toString();
        }
        return result;
    }

    @ApiOperation(value = "searchFavor", notes = "获取收藏信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "分页查询每页条数，固定值：20", required = true, dataType = "INT", paramType = "query"),
            @ApiImplicitParam(name = "date", value = "日期字符串，固定值：“”（空字符串）", required = true, dataType = "STRING", paramType = "query")
    })
    @RequestMapping(value = "/favor/search", method = RequestMethod.POST)
    public String searchFavor(@RequestParam("limit") int limit,
                              @RequestParam("date") String date,
                              HttpServletRequest httpServletRequest) {
        System.out.println("执行");
        System.out.println(limit);
        System.out.println(date);
        String loginName = judgeCookie(httpServletRequest);
        String result = "";
        if ("".equals(loginName)) {
            result = returnStaticJsonObject();
        } else {
            result = iYuQingService.searchFavor(limit, date, loginName).toString();
        }
        return result;
    }

    @ApiOperation(value = "removeFavor", notes = "删除收藏数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "被删除的消息的id", required = true, dataType = "STRING", paramType = "query"),
    })
    @RequestMapping(value = "/favor/remove", method = RequestMethod.POST)
    public String removeFavor(@RequestParam("id") String id,
                              HttpServletRequest httpServletRequest) {
        String loginName = judgeCookie(httpServletRequest);
        String result = "";
        if ("".equals(loginName)) {
            result = returnStaticJsonObject();
        } else {
            result = iYuQingService.removeFavor(id, loginName).toString();
        }
        return result;
    }

    @ApiOperation(value = "addFavor", notes = "添加收藏信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "需要添加的数据的id", required = true, dataType = "STRING", paramType = "query"),
    })
    @RequestMapping(value = "/favor/add", method = RequestMethod.POST)
    public String addFavor(@RequestParam("id") String id,
                           HttpServletRequest httpServletRequest) {
        String loginName = judgeCookie(httpServletRequest);
        String result = "";
        if ("".equals(loginName)) {
            result = returnStaticJsonObject();
        } else {
            result = iYuQingService.addFavor(id, loginName).toString();
        }
        return result;
    }

    @ApiOperation(value = "checkFavor", notes = "检查收藏关系")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "需要查看的数据的id", required = true, dataType = "STRING", paramType = "query"),
    })
    @RequestMapping(value = "/favor/check", method = RequestMethod.POST)
    public String checkFavor(@RequestParam("id") String id,
                             HttpServletRequest httpServletRequest) {
        String loginName = judgeCookie(httpServletRequest);
        String result = "";
        if ("".equals(loginName)) {
            result = returnStaticJsonObject();
        } else {
            result = iYuQingService.checkFavor(id, loginName).toString();
        }
        return result;
    }

    @ApiOperation(value = "updatePwd", notes = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "old", value = "原密码", required = true, dataType = "STRING", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "新密码", required = true, dataType = "STRING", paramType = "query")
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
            result = iYuQingService.updatePwd(old, password, loginName).toString();
        }
        return result;
    }

    @ApiOperation(value = "updateConfig", notes = "修改推送设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag_id", value = "频道的tag_id的值；", required = true, dataType = "STRING", paramType = "query"),
            @ApiImplicitParam(name = "push", value = "是否推送。 0：否；1：是", required = true, dataType = "STRING", paramType = "query")
    })
    @RequestMapping(value = "/user/push/updateConfig", method = RequestMethod.POST)
    public String updateConfig(@RequestParam("tag_id") String tagId,
                               @RequestParam("push") String push,
                               HttpServletRequest httpServletRequest) {
        String loginName = judgeCookie(httpServletRequest);
        String result = "";
        if ("".equals(loginName)) {
            result = returnStaticJsonObject();
        } else {
            result = iYuQingService.updateConfig(tagId, push, loginName).toString();
        }
        return result;
    }

    @ApiOperation(value = "getConfig", notes = "获取推送设置")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/user/push/getConfig", method = RequestMethod.POST)
    public String getConfig(HttpServletRequest httpServletRequest) {
        String loginName = judgeCookie(httpServletRequest);
        String result = "";
        if ("".equals(loginName)) {
            result = returnStaticJsonObject();
        } else {
            result = iYuQingService.getConfig(loginName).toString();
        }
        return result;
    }

    @ApiOperation(value = "detailInfo", notes = "根据id来获取详情的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "想要查询的信息的id", required = true, dataType = "STRING", paramType = "query")
    })
    @RequestMapping(value = "/data/detail", method = RequestMethod.POST)
    public String detailInfo(@RequestParam("id") String id,
                             HttpServletRequest httpServletRequest) {
        String loginName = judgeCookie(httpServletRequest);
        String result = "";
        if ("".equals(loginName)) {
            result = returnStaticJsonObject();
        } else {
            result = iYuQingService.getInfodetail(id, loginName).toString();
        }
        return result;
    }

    @ApiOperation(value = "checkVersion", notes = "更新版本")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "version", value = "版本号", required = true, dataType = "STRING", paramType = "query")
    })
    @RequestMapping(value = "/release/check", method = RequestMethod.POST)
    public String checkVersion(@RequestParam("version") String version,
                               HttpServletRequest httpServletRequest) {
        String result = iYuQingService.checkVersion(version, "1").toString();
        return result;
    }

    @ApiOperation(value = "checkNoCus", notes = "更新版本")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "version", value = "版本号", required = true, dataType = "STRING", paramType = "query")
    })
    @RequestMapping(value = "/release/checkNoCus", method = RequestMethod.POST)
    public String checkNoCus(@RequestParam("version") String version,
                             HttpServletRequest httpServletRequest) {
        String result = iYuQingService.checkVersion(version, "0").toString();
        return result;
    }

    @ApiOperation(value = "clickInfoData", notes = "点击信息，已读状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "infoId", value = "信息的id", required = true, dataType = "STRING", paramType = "query"),
            @ApiImplicitParam(name = "infoType", value = "信息的类型", required = true, dataType = "STRING", paramType = "query")
    })
    @RequestMapping(value = "/data/click", method = RequestMethod.POST)
    public String clickInfoData(@RequestParam("infoId") String infoId,
                                @RequestParam("infoType") String infoType,
                                HttpServletRequest httpServletRequest) {
        String loginName = judgeCookie(httpServletRequest);
        String result = "";
        if ("".equals(loginName)) {
            result = returnStaticJsonObject();
        } else {
            result = iYuQingService.clickInfoData(loginName, infoId, infoType).toString();
        }
        return result;
    }

    @ApiOperation(value = "clickFavor", notes = "点击信息，已读状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "infoId", value = "信息的id", required = true, dataType = "STRING", paramType = "query"),
            @ApiImplicitParam(name = "infoType", value = "信息的类型", required = true, dataType = "STRING", paramType = "query")
    })
    @RequestMapping(value = "/data/clickFavor", method = RequestMethod.POST)
    public String clickFavor(@RequestParam("favorId") String favorId,
                             HttpServletRequest httpServletRequest) {
        String loginName = judgeCookie(httpServletRequest);
        String result = "";
        if ("".equals(loginName)) {
            result = returnStaticJsonObject();
        } else {
            result = iYuQingService.clickFavor(favorId).toString();
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

    @RequestMapping(value = "/guidance/uploadOrderFile", method = RequestMethod.POST)
    public String uploadHead(HttpServletRequest request, HttpServletResponse httpServletResponse, @RequestParam("myImage") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String str = sdf.format(date);
            String filePath = "C:/dummyPath/" + str + ""
                    + file.getOriginalFilename();//获取服务器的绝对路径+项目相对路径head/图片原名
            //讲客户端文件传输到服务器端
            file.transferTo(new File(filePath));
            httpServletResponse.setContentType("text/text;charset=utf-8");
            PrintWriter out = httpServletResponse.getWriter();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("errno", "0");
            JSONArray jsonArray = new JSONArray();
            jsonArray.add("http://118.178.237.219:8080/dummyPath/" + str + file.getOriginalFilename() + "");
            jsonObject.put("data", jsonArray);
            out.print(jsonObject.toString());
            out.flush();
            out.close();
            return jsonObject.toString();
        }
        return "";
    }

}
