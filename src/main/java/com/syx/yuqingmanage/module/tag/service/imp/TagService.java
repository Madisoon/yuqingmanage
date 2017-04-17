package com.syx.yuqingmanage.module.tag.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.tag.service.ITagService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.attribute.standard.JobSheets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Master  Zg on 2016/11/9.
 */
@Service
public class TagService implements ITagService {
    @Autowired
    private JSONResponse jsonResponse;

    @Override
    public ExecResult insertTag(String tagData) {
        JSONObject jsonObject = JSON.parseObject(tagData);
        String tagName = jsonObject.getString("name");
        String tagId = jsonObject.getString("id");
        String tagParentId = jsonObject.getString("tag_parent");
        String sqlTagInsert = "INSERT INTO sys_tag (id,name,tag_parent) VALUES('" + tagId + "','" + tagName + "','" + tagParentId + "')";
        ExecResult execResult = jsonResponse.getExecResult(sqlTagInsert, null);
        return execResult;
    }

    @Override
    public ExecResult updateTag(String id, String name) {
        String sqlTagInsert = "UPDATE sys_tag SET name = '" + name + "' WHERE id = '" + id + "'";
        ExecResult execResult = jsonResponse.getExecResult(sqlTagInsert, null);
        return execResult;
    }

    @Override
    public ExecResult deleteTag(String id) {
        String[] idS = id.split(",");
        int idSLen = idS.length;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < idSLen; i++) {
            list.add("DELETE FROM sys_tag WHERE id = '" + idS[i] + "' OR tag_parent = '" + idS[i] + "' ");
        }
        ExecResult execResult = jsonResponse.getExecResult(list, "", "");
        return execResult;
    }

    @Override
    public ExecResult getIdMax() {
        String sqlMax = "SELECT id FROM sys_tag ORDER BY id DESC LIMIT 0,1";
        ExecResult execResult = jsonResponse.getSelectResult(sqlMax, null, "");
        return execResult;
    }

    @Override
    public ExecResult getAllTag() {
        String sqlTag = "SELECT id,name,tag_parent  FROM  sys_tag";
        ExecResult execResult = jsonResponse.getSelectResult(sqlTag, null, "");
        return execResult;
    }

    @Override
    public ExecResult getMyTag(String userLoginName) {
        String sql = "SELECT * FROM  sys_user_tag a, sys_tag b WHERE a.user_id='" + userLoginName + "' AND a.tag_id = b.id";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");

        return execResult;
    }

    @Override
    public ExecResult insertMyTag(String userLoginName, String id) {
        String sql = "INSERT INTO sys_user_tag (user_id,tag_id) VALUES ('" + userLoginName + "','" + id + "')";
        ExecResult execResult = jsonResponse.getExecResult(sql, null);
        return execResult;
    }

    @Override
    public ExecResult deleteMyTag(String userLoginName, String id) {
        String sql = "DELETE FROM sys_user_tag WHERE user_id = '" + userLoginName + "' AND tag_id =" + id + " ";
        ExecResult execResult = jsonResponse.getExecResult(sql, null);
        return execResult;
    }

    @Override
    public ExecResult getTypeTag() {
        String sqlTag = "SELECT id,name,tag_parent  FROM  sys_tag";
        ExecResult execResult = jsonResponse.getSelectResult(sqlTag, null, "");
        JSONArray jsonArrayAll = new JSONArray();
        JSONArray jsonArray = (JSONArray) execResult.getData();
        int jsonArrayLen = jsonArray.size();
        for (int i = 0; i < jsonArrayLen; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String tagId = jsonObject.getString("id");
            String selectSql = "SELECT * FROM sys_tag WHERE id = " + tagId + " OR tag_parent = " + tagId + " ";
            ExecResult result = jsonResponse.getSelectResult(selectSql, null, "");
            JSONArray jsonLen = (JSONArray) result.getData();
            if (jsonLen.size() > 1) {
                jsonObject.put("nocheck", true);
            }
            jsonArrayAll.add(jsonObject);
        }
        execResult.setData(jsonArrayAll);
        return execResult;
    }
}