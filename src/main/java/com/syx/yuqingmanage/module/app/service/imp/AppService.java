package com.syx.yuqingmanage.module.app.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.app.service.IAppService;
import com.syx.yuqingmanage.module.infor.service.imp.InForService;
import com.syx.yuqingmanage.utils.HttpClientUtil;
import com.syx.yuqingmanage.utils.QqAsyncMessagePost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Master  Zg on 2016/12/12.
 */
@Service
public class AppService implements IAppService {
    @Autowired
    private JSONResponse jsonResponse;
    @Autowired
    QqAsyncMessagePost qqAsyncMessagePost;
    @Autowired
    InForService inForService;

    final static String TERRACE_URL = "http://sync.yuwoyg.com:58080/yuqing-allot-main/config/yuqingmanage/dep/get";

    @Override
    public JSONObject insertInformation(String data) {
        JSONObject jsonObject = JSON.parseObject(data);
        String title = StringFilter(jsonObject.getString("title"));
        String content = StringFilter(jsonObject.getString("content"));
        String grade = jsonObject.getString("alarm");
        String link = jsonObject.getString("source_url");
        String people = "fenjianpingtai";
        String source = jsonObject.getString("source");
        String site = jsonObject.getString("site");
        String customerId = jsonObject.getString("dep_ids");
        JSONObject jsonObjectReturn = new JSONObject();
        String insertSql = "INSERT INTO sys_infor (infor_title, infor_context, infor_grade, " +
                "infor_link, infor_creater, " +
                "infor_source, infor_site) VALUES " +
                "('" + title + "','" + content + "','" + grade + "','" + link + "','" + people + "','" + source + "','" + site + "')";
        ExecResult execResult = jsonResponse.getExecInsertId(insertSql, null, "", "");
        if (execResult.getResult() == 1) {
            jsonObjectReturn.put("flag", true);
            String infoId = execResult.getMessage();
            String[] tagIds = customerId.split(",");
            int tagIdsLen = tagIds.length;
            for (int i = 0; i < tagIdsLen; i++) {
                String tag_id = tagIds[i];
                String sqlTag = "INSERT INTO infor_tag (infor_id,tag_id) VALUES('" + infoId + "','" + tag_id + "')";
                jsonResponse.getExecResult(sqlTag, null);
            }
        } else {
            jsonObjectReturn.put("flag", false);
        }
        return jsonObjectReturn;
    }

    @Override
    public JSONObject getTerraceCustomerTag() {
        Map<String, String> map = new HashMap<>(16);
        JSONObject jsonObject = HttpClientUtil.postJsonData(TERRACE_URL, map);
        return jsonObject;
    }


    @Override
    public JSONObject insertSortingTag(String tagName, String tagId) {
        String sql = "INSERT INTO sys_tag (id,NAME,tag_parent) VALUES('" + tagId + "','" + tagName + "','495')";
        ExecResult execResult = jsonResponse.getExecResult(sql, null);
        JSONObject jsonObject = new JSONObject();
        if (execResult.getResult() == 1) {
            jsonObject.put("flag", true);
        } else {
            jsonObject.put("flag", false);
        }
        return jsonObject;
    }

    @Override
    public JSONObject deleteSortingTag(String tagId) {
        String sql = "DELETE FROM sys_tag WHERE id = " + tagId + "";
        ExecResult execResult = jsonResponse.getExecResult(sql, null);
        JSONObject jsonObject = new JSONObject();
        if (execResult.getResult() == 1) {
            jsonObject.put("flag", true);
        } else {
            jsonObject.put("flag", false);
        }
        return jsonObject;
    }


    public String StringFilter(String str) throws PatternSyntaxException {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’、\"]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
