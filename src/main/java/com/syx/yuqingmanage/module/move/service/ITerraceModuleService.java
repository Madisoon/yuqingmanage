package com.syx.yuqingmanage.module.move.service;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;

/**
 * Created by Msater Zg on 2017/3/3.
 */
public interface ITerraceModuleService {
    public ExecResult insertTerraceModule(String terraceData, String tagIds, String areaId, String baseTag);

    public JSONObject getAllTerraceModule(String idS);

    public ExecResult deleteTerraceModuleId(String idS);

    public ExecResult updateTerraceModule(String terraceId, String tagId, String terraceData, String baseTag);

    public JSONObject getAllTerraceModuleChoose(String areaId, String tagId, String chooseTerraceData);
}
