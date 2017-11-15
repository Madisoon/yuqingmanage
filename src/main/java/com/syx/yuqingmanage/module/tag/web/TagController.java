package com.syx.yuqingmanage.module.tag.web;

import com.syx.yuqingmanage.module.tag.service.ITagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by Master  Zg on 2016/11/9.
 */
@RequestMapping(value = "/manage")
@RestController
@Api(value = "标签管理", description = "管理平台标签的相关API")
public class TagController {
    @Autowired
    private ITagService iTagService;

    @RequestMapping(value = "/insertTag", method = RequestMethod.POST)
    @ApiOperation(value = "插入标签", notes = "标签对象")
    public String insertTag(@ApiParam(required = true, name = "tagData", value = "标签的数据") @RequestParam("tagData") String tagData,
                            @ApiParam(required = true, name = "allParent", value = "标签的数据") @RequestParam("allParent") String allParent) {
        String result = iTagService.insertTag(tagData, allParent).toString();
        return result;
    }

    @RequestMapping(value = "/getIdMax", method = RequestMethod.POST)
    @ApiOperation(value = "得到最大id", notes = "无")
    public String getIdMax() {
        String result = iTagService.getIdMax().toString();
        return result;
    }

    @RequestMapping(value = "/getAllTag", method = RequestMethod.POST)
    @ApiOperation(value = "获取所有id", notes = "无")
    public String getAllTag() {
        String result = iTagService.getAllTag().toString();
        return result;
    }

    @RequestMapping(value = "/updateTag", method = RequestMethod.POST)
    @ApiOperation(value = "修改标签id", notes = "标签id，标签名称")
    public String updateTag(@RequestParam("id") String id, @RequestParam("tagName") String tagName) {
        String result = iTagService.updateTag(id, tagName).toString();
        return result;
    }

    @RequestMapping(value = "/deleteTag", method = RequestMethod.POST)
    @ApiOperation(value = "删除标签", notes = "标签id，多个用,隔开")
    public String deleteTag(@RequestParam("id") String id) {
        String result = iTagService.deleteTag(id).toString();
        return result;
    }

    @RequestMapping(value = "/getMyTag", method = RequestMethod.POST)
    @ApiOperation(value = "获取个人标签", notes = "用户账号")
    public String getMyTag(@RequestParam("userLoginName") String userLoginName) {
        String result = iTagService.getMyTag(userLoginName).toString();
        return result;
    }

    @RequestMapping(value = "/insertMyTag", method = RequestMethod.POST)
    @ApiOperation(value = "插入我的标签", notes = "用户账号,标签id")
    public String insertMyTag(@RequestParam("userLoginName") String userLoginName,
                              @RequestParam("id") String id) {
        String result = iTagService.insertMyTag(userLoginName, id).toString();
        return result;
    }

    @RequestMapping(value = "/deleteMyTag", method = RequestMethod.POST)
    @ApiOperation(value = "删除我的标签", notes = "用户账号,标签id")
    public String deleteMyTag(@RequestParam("userLoginName") String userLoginName,
                              @RequestParam("id") String id) {
        String result = iTagService.deleteMyTag(userLoginName, id).toString();
        return result;
    }

    @RequestMapping(value = "/getTypeTag", method = RequestMethod.POST)
    @ApiOperation(value = "得到所有的子标签", notes = "无")
    public String getTypeTag() {
        String result = iTagService.getTypeTag().toString();
        return result;
    }

    @RequestMapping(value = "/getChildTag", method = RequestMethod.POST)
    @ApiOperation(value = "得到所有的子标签", notes = "无")
    public String getChildTag() {
        String result = iTagService.getChildTag().toString();
        return result;
    }
}
