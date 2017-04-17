package com.syx.yuqingmanage.module.infor.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.infor.service.IInForService;
import com.syx.yuqingmanage.utils.DocumentHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Master  Zg on 2016/11/9.
 */
@RequestMapping(value = "/manage")
@Controller
@Api(value = "信息", description = "管理信息的API")
public class inForController {
    @Autowired
    private IInForService iInForService;

    @RequestMapping(value = "/insertInFor", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "插入信息", notes = "信息的对象，信息所属标签")
    public String insertInFor(@RequestParam("infoData") String infoData,
                              @RequestParam("infoTag") String infoTag) {
        String result = iInForService.insertInFor(infoData, infoTag).toString();
        return result;
    }

    @RequestMapping(value = "/getAllInfor", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "获取信息", notes = "无")
    public String getAllInfor(HttpServletRequest request) {
        try {
            String param = IOUtils.toString(request.getInputStream(), "utf-8");
            JSONObject params = JSONObject.parseObject(param);
            String pageNumber = params.getString("pageNumber");
            String pageSize = params.getString("pageSize");
            String searchTagId = params.getString("searchTagId");
            String searchInfoData = params.getString("searchInfoData");
            String customerName = params.getString("customerName");
            String result = "";
            if ("".equals(searchTagId) && "".equals(customerName) && (searchInfoData == null || "{}".equals(searchInfoData))) {
                result = iInForService.getAllInfor(pageNumber, pageSize).toString();
            } else {
                result = iInForService.getAllInfoChoose(pageNumber, pageSize, searchTagId, searchInfoData, customerName).toString();
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ExecResult(false, "获取数据异常。").toString();
        }
    }

    @RequestMapping(value = "/updateInfoData", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "修改信息", notes = "信息对象，信息标签，信息id")
    public String updateInfoData(@RequestParam("infoData") String infoData,
                                 @RequestParam("infoTagId") String infoTagId,
                                 @RequestParam("infoId") String infoId) {
        String result = iInForService.updateInfoData(infoData, infoTagId, infoId).toString();
        return result;
    }

    @RequestMapping(value = "/deleteInfoData", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "修改信息", notes = "信息id(多个用 , 隔开)")
    public String deleteInfoData(@RequestParam("infoId") String infoId) {
        String result = iInForService.deleteInfoData(infoId).toString();
        return result;
    }

    @RequestMapping(value = "/manualPost", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "手工发送", notes = "信息的id，客户id")
    public String manualPost(@RequestParam("infoId") String infoId,
                             @RequestParam("customerId") String customerId) {
        String result = iInForService.manualPost(infoId, customerId).toString();
        return result;
    }

    @RequestMapping(value = "/exportData", method = RequestMethod.GET)
    public void exportData(@RequestParam("searchTagId") String searchTagId,
                           @RequestParam("searchInfoData") String searchInfoData,
                           @RequestParam("customerName") String customerName,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        File file = null;
        InputStream inputStream = null;
        ServletOutputStream out = null;
        try {
            DocumentHandler documentHandler = new DocumentHandler();
            JSONArray jsonArray = iInForService.exportData(searchTagId, searchInfoData, customerName);
            List list = documentHandler.getInfoListData(jsonArray);
            Map maps = new HashMap();
            maps.put("inforList", list);
            maps.put("inforNumber", String.valueOf(list.size() + 5));
            try {

                file = documentHandler.createDoc(maps, "C:/dummyPath/" + System.currentTimeMillis() + ".doc", "inforword.ftl");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            request.setCharacterEncoding("UTF-8");
            inputStream = new FileInputStream(file);
            response.setCharacterEncoding("utf-8");

            // 设置附加文件名
            response.setContentType("application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("ceswenjian" + ".xls", "UTF-8"));
            out = response.getOutputStream();
            byte[] buffer = new byte[512]; // 缓冲区
            int bytesToRead = -1;
            // 通过循环将读入的Excel文件的内容输出到浏览器中
            while ((bytesToRead = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesToRead);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            /*if (file != null)
                file.delete(); // 删除临时文件*/
        }
    }
}
