package com.syx.yuqingmanage.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Msater Zg on 2017/4/12.
 */
public class DataExport {
    public String exportInforData(JSONArray jsonArray, String exportType) {
        List list = new ArrayList();
        DocumentHandler documentHandler = new DocumentHandler();
        int jsonArrayLen = jsonArray.size();
        for (int i = 0; i < jsonArrayLen; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            System.out.println(jsonObject);
            Map map = new HashMap();
            map.put("index", i + 1);
            map.put("title", jsonObject.getString("infor_title"));
            map.put("source", jsonObject.getString("infor_source"));
            map.put("time", jsonObject.getString("infor_createtime"));
            map.put("context", jsonObject.getString("infor_context"));
            map.put("link", jsonObject.getString("infor_link").replaceAll("&", "&amp;"));
            list.add(map);
        }
        Map maps = new HashMap();
        maps.put("inforList", list);
        String longTime = String.valueOf(System.currentTimeMillis());
        String fileName = "";
        String fileFtl = "";
        if ("word".equals(exportType)) {
            fileName = longTime + ".doc";
            fileFtl = "inforword.ftl";
        } else {
            fileName = longTime + ".xls";
            fileFtl = "inforexcel.ftl";
        }
        try {
            documentHandler.createDoc(maps, "C:/dummyPath/" + fileName, fileFtl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fileName;
    }
}
