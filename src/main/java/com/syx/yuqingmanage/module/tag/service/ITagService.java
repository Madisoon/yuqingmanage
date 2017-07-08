package com.syx.yuqingmanage.module.tag.service;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;

/**
 * Created by Master  Zg on 2016/11/9.
 */
public interface ITagService {
    /**
     * 插入一个标签
     *
     * @param tagData
     * @return
     */
    public ExecResult insertTag(String tagData, String allParent);

    /**
     * 修改标签的名称
     *
     * @param id
     * @param name
     * @return
     */
    public ExecResult updateTag(String id, String name);

    /**
     * 删除id
     *
     * @param id
     * @return
     */
    public ExecResult deleteTag(String id);

    /**
     * 得到最大的id
     *
     * @return
     */
    public ExecResult getIdMax();

    /**
     * 获得所有的标签
     *
     * @return
     */
    public ExecResult getAllTag();

    /**
     * 获取我自己的所有的标签
     *
     * @param userLoginName
     * @return
     */
    public ExecResult getMyTag(String userLoginName);

    /**
     * 插件我的标签
     *
     * @param userLoginName
     * @param ids
     * @return
     */
    public ExecResult insertMyTag(String userLoginName, String ids);

    /**
     * 删除我的标签
     *
     * @param userLoginName
     * @param ids
     * @return
     */
    public ExecResult deleteMyTag(String userLoginName, String ids);

    public ExecResult getTypeTag();
}
