package com.syx.yuqingmanage.module.setting.service;

import com.alienlab.db.ExecResult;

/**
 * Created by Master  Zg on 2016/11/15.
 */
public interface IModuleService {
    /**
     *
     * @param moduleValue
     * @param moduleUrl
     * @param moduleId
     * @return
     */
    public ExecResult insertModule(String moduleValue, String moduleUrl, String moduleId);

    /**
     * 得到所有的模块
     *
     * @return
     */
    public ExecResult getAllModule();

    /**
     * 删除模块（包括一级和二级）
     *
     * @param moduleId
     * @return
     */
    public ExecResult deleteModule(String moduleId);

    /**
     * 得到所有的二级菜单
     *
     * @param moduleId
     * @return
     */
    public ExecResult getAllSecondModule(String moduleId);

    /**
     * 修改菜单信息
     * @param menuId
     * @param menuName
     * @param menuContent
     * @return
     */
    public ExecResult updateModuleInfo(String menuId, String menuName, String menuContent);
}
