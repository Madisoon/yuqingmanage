package com.syx.yuqingmanage.module.move.service;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;

/**
 * Created by Msater Zg on 2017/2/9.
 */
public interface ISchemeService {
    public ExecResult insertScheme(String schemeData, String terraceTagIds, String tagIds, String areaId, String baseTag);

    public JSONObject getAllScheme(String idS);

    public ExecResult deleteSchemeId(String idS);

    public ExecResult updateScheme(String schemeId, String tagId, String terraceTagId, String schemeData, String baseTag);

    public JSONObject getAllSchemeChoose(String areaId, String tagId, String chooseSchemeData);
}
