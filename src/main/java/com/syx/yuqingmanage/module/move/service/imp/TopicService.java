package com.syx.yuqingmanage.module.move.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.move.service.ITopicService;
import com.syx.yuqingmanage.utils.DataExport;
import com.syx.yuqingmanage.utils.SqlEasy;
import com.syx.yuqingmanage.utils.jpush.JpushBean;
import com.syx.yuqingmanage.utils.jpush.JpushServer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Msater Zg on 2017/7/10.
 */
@Service
public class TopicService implements ITopicService {
    @Autowired
    JSONResponse jsonResponse;
    @Autowired
    JpushServer jpushServer;

    @Override
    public ExecResult insertTopicContext(String topicId, String topicInfo) {
        DataExport dataExport = new DataExport();
        JSONObject jsonObject = JSON.parseObject(topicInfo);
        String topicTitle = jsonObject.getString("topic_title");
        String topicContext = jsonObject.getString("topic_abstract");
        String topicContent = jsonObject.getString("topic_context");
        String urlHtml = dataExport.exportHtmlUrl(topicContent);
        jsonObject.put("topic_url", urlHtml);
        String sqlInsertTopic = SqlEasy.insertObject(jsonObject.toJSONString(), "sys_topic_context");
        ExecResult execResult = jsonResponse.getExecInsertId(sqlInsertTopic, null, "", "");
        List<JpushBean> list = new ArrayList<>();
        // 推送模块 starter
        String getProgram = "SELECT d.id FROM  app_module_tag_dep a ,app_module b , " +
                "app_user_program_module c,app_user_program d " +
                "WHERE a.app_module_id = b.id AND b.id = c.app_module_id  " +
                "AND c.app_program_id = d.id AND a.tag_id = '" + topicId + "'  " +
                "AND b.app_module_type = 1 GROUP BY d.id  ";
        ExecResult execResultProgram = jsonResponse.getSelectResult(getProgram, null, "");
        if (execResultProgram.getResult() == 1) {
            JSONArray jsonArray = (JSONArray) execResultProgram.getData();
            int jsonArrayLen = jsonArray.size();
            for (int i = 0; i < jsonArrayLen; i++) {
                JSONObject jsonObjectTag = jsonArray.getJSONObject(i);
                JpushBean jpushBean = new JpushBean();
                jpushBean.setId("");
                jpushBean.setTagId(jsonObjectTag.getString("id"));
                jpushBean.setTitle(topicTitle);
                jpushBean.setType("1");
                jpushBean.setContent(topicContext.substring(0, 40));
                jpushBean.setUrl("https://www.baidu.com/");
                list.add(jpushBean);
            }
            jpushServer.pushNotification(list);
        }
        // end
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
        String sql = "SELECT * FROM  sys_topic_context WHERE id = '" + topicContextId + "'";
        ExecResult execResultUrl = jsonResponse.getSelectResult(sql, null, "");
        if (execResultUrl.getResult() > 0) {
            JSONObject jsonObject = ((JSONArray) execResultUrl.getData()).getJSONObject(0);
            String getUrl = jsonObject.getString("topic_url");
            deleteFile("C:/dummyPath/" + getUrl + "");
        }
        JSONObject jsonObject = JSON.parseObject(topicContext);
        String topicContent = jsonObject.getString("topic_context");
        DataExport dataExport = new DataExport();
        String urlHtml = dataExport.exportHtmlUrl(topicContent);
        jsonObject.put("topic_url", urlHtml);
        String updateSql = SqlEasy.updateObject(jsonObject.toJSONString(), "sys_topic_context", "id = " + topicContextId);
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

    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }
}
