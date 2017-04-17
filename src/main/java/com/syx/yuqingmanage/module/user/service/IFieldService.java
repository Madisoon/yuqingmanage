package com.syx.yuqingmanage.module.user.service;

import com.alienlab.db.ExecResult;

/**
 * Created by Master  Zg on 2016/11/17.
 */
public interface IFieldService {
    /**
     * 获取客户所有的字段
     * @return
     */
    public ExecResult getAllField(String dataType);

    /**
     * 增加新的字段和修改
     * @param data
     * @return
     */
    public ExecResult postFieldData(String data, String id);

    /**
     * 根据id删除字段
     * @param filedId
     * @return
     */
    public ExecResult deleteField(String filedId);

    /**
     * 取所有的一级的属性值
     * @return
     */
    public ExecResult getFieldName();

    /**
     * 根据id取得单个属性字段的值
     * @param id
     * @return
     */
    public ExecResult getSingleField(String id);
}
