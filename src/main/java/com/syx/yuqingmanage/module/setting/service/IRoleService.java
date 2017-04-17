package com.syx.yuqingmanage.module.setting.service;

import com.alienlab.db.ExecResult;

/**
 * Created by Master  Zg on 2016/11/16.
 */
public interface IRoleService {
    /**
     * 得到所有的角色
     *
     * @return
     */
    public ExecResult getAllRole();

    /**
     * 根据id删除角色（附带的权限）
     *
     * @param id
     * @return
     */
    public ExecResult deleteRole(String id);

    /**
     * 得到单个角色的权限
     *
     * @param id
     * @return
     */
    public ExecResult getSingleRole(String id);

    /**
     * 添加或者删除
     *
     * @param role_id
     * @param menu_id
     * @param menu_pid
     * @param menu_purview
     * @return
     */

    public ExecResult changeRole(String role_id, String menu_id, String menu_pid, String menu_purview);

    /**
     * 插入角色，多种一起插入
     *
     * @param role_name
     * @return
     */
    public ExecResult insertRole(String role_name);

    /**
     * 根据id修改部门的名称信息
     *
     * @param roleId
     * @param roleName
     * @return
     */
    public ExecResult updateRoleName(String roleId, String roleName);

    /**
     * 获取本角色的所有的人
     *
     * @param roleId
     * @return
     */
    public ExecResult getUserRole(String roleId);
}
