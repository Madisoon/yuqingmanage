package com.syx.yuqingmanage.module.move.service;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;

/**
 * Created by Msater Zg on 2017/3/6.
 */
public interface ITerraceService {
    public ExecResult insertTerrace(String terraceData, String moduleData, String areaId);

    public JSONObject getAllTerrace(String areaId);

    public ExecResult updateTerrace(String terraceData, String moduleData, String terraceId);

    public ExecResult deleteTerrace(String terraceId);

    public JSONObject getAllTerraceChoose(String areaId, String terraceChooseData);
}