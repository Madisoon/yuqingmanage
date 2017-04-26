package com.syx.yuqingmanage.module.setting.service;

import com.alienlab.db.ExecResult;

/**
 * Created by Master  Zg on 2016/11/29.
 */
public interface IAreaService {
    /**
     * 获取不同的地址
     *
     * @param areaId
     * @return
     */
    public ExecResult getTypeArea(String areaId);

    /**
     * 批量插入地区信息
     *
     * @param areaId
     * @param areaValue
     * @param areaGade
     * @return
     */
    public ExecResult postAreaData(String areaId, String areaValue, String areaGade);

    public ExecResult getAllArea();

    public ExecResult updateArea(String areaId, String areaName);

    public ExecResult deleteArea(String areaId);

    public ExecResult getAreaMaxId();

    public ExecResult insertArea(String areaData);
}
