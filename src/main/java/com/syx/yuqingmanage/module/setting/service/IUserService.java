package com.syx.yuqingmanage.module.setting.service;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import org.springframework.cache.annotation.Cacheable;

/**
 * Created by Master  Zg on 2016/11/24.
 */
public interface IUserService {
    /**
     * 根据账号得到用户的一些简单的信息
     *
     * @param userLoginName
     * @return
     */
    public ExecResult getUserInfo(String userLoginName);

    /**
     * 根据账号密码得到所有的信息，菜单等
     *
     * @param userLoginName
     * @param userPwd
     * @return
     */
    public ExecResult getUserAllInfo(String userLoginName, String userPwd);

    /*public ExecResult getAllUser(String pageNumber,String pageSize,String choiceSelect);*/

    /**
     * 获取所有的用户修改信息
     *
     * @return
     */
    public JSONObject getAllUser();

    /**
     * 获取所需要的部门和角色
     *
     * @return
     */
    public ExecResult getAllDepRole();

    /**
     * 插入用户
     *
     * @param userData
     * @return
     */
    public ExecResult insertSysUser(String userData);

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    public ExecResult deleteUser(String id);

    /**
     * 修改用户信息
     *
     * @param userData
     * @return
     */
    public ExecResult updateUserInfo(String userData);

}
