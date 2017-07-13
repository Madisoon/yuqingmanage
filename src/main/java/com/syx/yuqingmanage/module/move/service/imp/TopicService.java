package com.syx.yuqingmanage.module.move.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.move.service.ITopicService;
import com.syx.yuqingmanage.utils.SqlEasy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Msater Zg on 2017/7/10.
 */
@Service
public class TopicService implements ITopicService {
    @Autowired
    JSONResponse jsonResponse;

    @Override
    public ExecResult insertTopicContext(String topicId, String topicInfo) {
        String sqlInsertTopic = SqlEasy.insertObject(topicInfo, "sys_topic_context");
        ExecResult execResult = jsonResponse.getExecInsertId(sqlInsertTopic, null, "", "");
        String id = execResult.getMessage();
        String sqlInsert = "INSERT INTO topic_context (topic_id, topic_context_id) VALUES('" + topicId + "','" + id + "')";
        jsonResponse.getExecResult(sqlInsert, null);
        return execResult;
    }

    @Override
    public ExecResult insertTopic(String topicInfo) {
        String sqlInsertTopic = SqlEasy.insertObject(topicInfo, "sys_topic");
        ExecResult execResult = jsonResponse.getExecResult(sqlInsertTopic, null);
        return execResult;
    }

    @Override
    public ExecResult updateTopic(String topicId, String topicName) {
        String sqlUpdate = "UPDATE sys_topic SET topic_name = '" + topicName + "' WHERE id = " + topicId;
        ExecResult execResult = jsonResponse.getExecResult(sqlUpdate, null);
        return execResult;
    }

    @Override
    public ExecResult deleteTopic(String topicId) {
        String[] topicIds = topicId.split(",");
        int topicIdsLen = topicIds.length;
        List list = new ArrayList();
        List listContext = new ArrayList();
        list.add("DELETE FROM sys_topic WHERE id = '" + topicIds[0] + "'");
        listContext.add("DELETE FROM topic_context WHERE topic_id = '" + topicIds[0] + "'");
        for (int i = 1; i < topicIdsLen; i++) {
            list.add("OR id = " + topicIds[i]);
            listContext.add("OR topic_id = " + topicIds[i]);
        }
        List sqlList = new ArrayList();
        sqlList.add(StringUtils.join(list, ""));
        sqlList.add(StringUtils.join(listContext, ""));
        ExecResult execResult = jsonResponse.getExecResult(sqlList, "", "");
        return execResult;
    }

    @Override
    public JSONArray getAllTopic() {
        String sqlGetAllTopic = "SELECT * FROM sys_topic";
        ExecResult execResult = jsonResponse.getSelectResult(sqlGetAllTopic, null, "");
        return (JSONArray) execResult.getData();
    }

    @Override
    public JSONObject getTopicContextByTopicId(String topicId, String pageSize, String pageNumber) {
        int pageNumberInt = Integer.parseInt(pageNumber, 10);
        int pageSizeInt = Integer.parseInt(pageSize, 10);
        String[] topicIds = topicId.split(",");
        List list = new ArrayList();
        list.add("SELECT b.*,c.user_name FROM (SELECT * FROM topic_context a  ");
        list.add("WHERE a.topic_id = '" + topicIds[0] + "'  ");
        for (int i = 1, topicIdsLen = topicIds.length; i < topicIdsLen; i++) {
            list.add("OR a.topic_id = '" + topicIds[i] + "' ");
        }
        list.add(" ) a ,sys_topic_context b ,sys_user c ");
        list.add(" WHERE a.topic_context_id = b.id AND b.topic_username = c.user_loginname ORDER BY b.topic_time DESC  LIMIT " + ((pageNumberInt - 1) * pageSizeInt) + "," + pageSizeInt + " ");
        ExecResult execResult = jsonResponse.getSelectResult(StringUtils.join(list, ""), null, "");
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = (JSONArray) execResult.getData();
        if (jsonArray == null) {
            jsonObject.put("total", 0);
        } else {
            jsonObject.put("total", jsonArray.size());
        }
        jsonObject.put("data", jsonArray);
        return jsonObject;
    }

    @Override
    public ExecResult deleteTopicContext(String topicContextId) {
        String[] topicContextIds = topicContextId.split(",");
        List list = new ArrayList();
        for (int i = 0, topicContextIdsLen = topicContextIds.length; i < topicContextIdsLen; i++) {
            list.add("DELETE FROM sys_topic_context WHERE id = " + topicContextIds[i]);
            list.add("DELETE FROM topic_context WHERE topic_context_id = " + topicContextIds[i]);
        }
        ExecResult execResult = jsonResponse.getExecResult(list, "", "");
        return execResult;
    }

    @Override
    public ExecResult updateTopicContext(String topicContextId, String topicContext) {
        String updateSql = SqlEasy.updateObject(topicContext, "sys_topic_context", "id = " + topicContextId);
        ExecResult execResult = jsonResponse.getExecResult(updateSql, null);
        return execResult;
    }

    @Override
    public ExecResult checkTopicContext(String topicContextId) {
        String[] topicContextIds = topicContextId.split(",");
        List list = new ArrayList();
        for (int i = 0, topicContextIdsLen = topicContextIds.length; i < topicContextIdsLen; i++) {
            list.add("UPDATE sys_topic_context a SET a.topic_status = 1 WHERE a.id = '" + topicContextIds[i] + "' ");
        }
        ExecResult execResult = jsonResponse.getExecResult(list, "", "");
        return execResult;
    }
}
