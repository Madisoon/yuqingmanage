package com.syx.yuqingmanage.module.setting.web;

import com.syx.yuqingmanage.module.setting.service.IRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Master  Zg on 2016/11/16.
 */
@RequestMapping(value = "/manage")
@RestController
@Api(value = "系统角色管理", description = "管理角色的API")
public class RoleController {
    @Autowired
    private IRoleService iRoleService;

    @ApiOperation(value = "获取所有的角色", notes = "无")
    @RequestMapping(value = "/getAllRole", method = RequestMethod.POST)
    public String getAllDep(HttpServletRequest request) {
        String result = iRoleService.getAllRole().toString();
        return result;
    }

    @RequestMapping(value = "/getSingleRole", method = RequestMethod.POST)
    @ApiOperation(value = "获取单个角色信息", notes = "角色id")
    public String getSingleRole(@RequestParam("role_id") String roleId) {
        String result = iRoleService.getSingleRole(roleId).toString();
        return result;
    }

    @RequestMapping(value = "/changeRole", method = RequestMethod.POST)
    @ApiOperation(value = "改变角色信息", notes = "角色id,菜单id，菜单父级id")
    public String changeRole(@RequestParam("role_id") String roleId,
                             @RequestParam("menu_id") String menuId,
                             @RequestParam("menu_pid") String menuPid,
                             @RequestParam("menu_purview") String menuPurview) {
        String result = iRoleService.changeRole(roleId, menuId, menuPid, menuPurview).toString();
        return result;
    }

    @RequestMapping(value = "/deleteRole", method = RequestMethod.POST)
    @ApiOperation(value = "删除角色", notes = "角色id，多个用,隔开")
    public String deleteRole(@RequestParam("id") String roleId) {
        String result = iRoleService.deleteRole(roleId).toString();
        return result;
    }

    @RequestMapping(value = "/insertRole", method = RequestMethod.POST)
    @ApiOperation(value = "插入角色", notes = "角色名称")
    public String insertRole(@RequestParam("role_name") String roleName) {
        String result = iRoleService.insertRole(roleName).toString();
        return result;
    }

    @RequestMapping(value = "/updateRoleName", method = RequestMethod.POST)
    @ApiOperation(value = "修改角色", notes = "角色名称，角色id")
    public String updateRoleName(@RequestParam("role_name") String roleName,
                                 @RequestParam("role_id") String roleId) {
        String result = iRoleService.updateRoleName(roleId, roleName).toString();
        return result;
    }

    @RequestMapping(value = "/getUserRole", method = RequestMethod.POST)
    @ApiOperation(value = "获取个人角色信息", notes = "角色id")
    public String getUserRole(@RequestParam("role_id") String roleId) {
        String result = iRoleService.getUserRole(roleId).toString();
        return result;
    }
}
