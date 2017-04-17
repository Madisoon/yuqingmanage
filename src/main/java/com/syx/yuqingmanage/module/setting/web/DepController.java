package com.syx.yuqingmanage.module.setting.web;

import com.alienlab.db.ExecResult;
import com.syx.yuqingmanage.module.setting.service.IDepService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Master  Zg on 2016/11/8.
 */
@RequestMapping(value = "/manage")
@RestController
@Api(value = "系统部门", description = "管理系统部门的API")
public class DepController {

    @Autowired
    private IDepService iDepService;

    @RequestMapping(value = "/getAllDep", method = RequestMethod.POST)
    @ApiOperation(value = "获取所有部门", notes = "无")
    public String getAllDep(HttpServletRequest request) {
        String result = iDepService.getAllDep().toString();
        return result;
    }

    /**
     * 新增部门
     */
    @RequestMapping(value = "/postDepData", method = RequestMethod.POST)
    @ApiOperation(value = "插入部门", notes = "部门编号，部门名称")
    public String postDepData(@RequestParam("dep_name") String depName,
                              @RequestParam("dep_no") String depNo) {
        String result = iDepService.postDepData(depName, depNo).toString();
        return result;
    }

    @RequestMapping(value = "/deleteById", method = RequestMethod.POST)
    @ApiOperation(value = "删除部门", notes = "部门id，多个 , 隔开")
    public String deleteById(@RequestParam("id") String depId) {
        String result = iDepService.deleteById(depId).toString();
        return result;
    }

    @RequestMapping(value = "/getUserByDepNo", method = RequestMethod.POST)
    @ApiOperation(value = "得到本部门相关的人物", notes = "部门编号")
    public String getUserByDepNo(@RequestParam("dep_no") String depNo) {
        String result = iDepService.getUserByDepNo(depNo).toString();
        return result;
    }

    @RequestMapping(value = "/updateDep", method = RequestMethod.POST)
    @ApiOperation(value = "修改部门信息", notes = "部门名称，部门编号，部门id")
    public String updateDep(@RequestParam("dep_name") String depName,
                            @RequestParam("dep_no") String depNo,
                            @RequestParam("id") String depId) {
        String result = iDepService.updateDep(depName, depNo, depId).toString();
        return result;
    }


}
