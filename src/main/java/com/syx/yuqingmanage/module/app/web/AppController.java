package com.syx.yuqingmanage.module.app.web;

import com.syx.yuqingmanage.module.app.service.IAppService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Master  Zg on 2016/12/12.
 */
@RequestMapping(value = "/manage")
@RestController
@Api(value = "开放接口", description = "开放出去的接口，获取调用其它服务器的接口")
public class AppController {
    @Autowired
    private IAppService iAppService;

    @ApiOperation(value = "插入人工发送的信息", notes = "无")
    @RequestMapping(value = "/insertInformation", method = RequestMethod.POST)
    public String insertInformation(@RequestParam("data") String data) {
        return iAppService.insertInformation(data).toString();
    }

    @ApiOperation(value = "得到平台的客户标签数据", notes = "无")
    @RequestMapping(value = "/getTerraceCustomerTag", method = RequestMethod.POST)
    public String getTerraceCustomerTag() {
        return iAppService.getTerraceCustomerTag().toString();
    }

    @ApiOperation(value = "插入标签", notes = "无")
    @RequestMapping(value = "/insertSortingTag", method = RequestMethod.POST)
    public String insertSortingTag(@RequestParam("id") String id, @RequestParam("name") String name) {
        return iAppService.insertSortingTag(name, id).toString();
    }

    @ApiOperation(value = "删除标签", notes = "无")
    @RequestMapping(value = "/deleteSortingTag", method = RequestMethod.POST)
    public String deleteSortingTag(@RequestParam("id") String id) {
        return iAppService.deleteSortingTag(id).toString();
    }

    @RequestMapping(value = "/uploadImageFile", method = RequestMethod.POST)
    public String uploadImageFile(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String str = sdf.format(date);
            String filePath = "C:/dummyPath/" + str + ""
                    + file.getOriginalFilename();
            file.transferTo(new File(filePath));
            return str + file.getOriginalFilename();
        }
        return "";
    }
}
