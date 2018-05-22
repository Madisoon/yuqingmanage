package com.syx.yuqingmanage.module.email.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.email.service.IEmailService;
import com.syx.yuqingmanage.utils.DateTimeUtils;
import com.syx.yuqingmanage.utils.NumberInfoPost;
import com.syx.yuqingmanage.utils.SqlEasy;
import com.syx.yuqingmanage.utils.email.JavaMailWithAttachment;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Master  Zg on 2016/12/12.
 */
@Service
public class EmailService implements IEmailService {
    @Autowired
    private JSONResponse jsonResponse;

    @Autowired
    private NumberInfoPost numberInfoPost;

    JavaMailWithAttachment javaMailWithAttachment = new JavaMailWithAttachment();

    @Override
    public ExecResult insertEmailData(String id, String url, String tagIdS) {
        String[] tagId = tagIdS.split(",");
        int tagIdsLen = tagId.length;
        String selectId = "SELECT template_title, template_content FROM sys_email_template WHERE id = '" + id + "'";
        ExecResult execResult = jsonResponse.getSelectResult(selectId, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        String templateTitle = jsonObject.getString("template_title");
        String templateContent = jsonObject.getString("template_content");
        String insertSql = "INSERT INTO sys_email (template_id, email_file_url,gmt_create, gmt_modified) " +
                "VALUES ('" + id + "','" + url + "',now(),now())";
        execResult = jsonResponse.getExecInsertId(insertSql, null, "", "");
        String emailId = execResult.getMessage();
        List list = new ArrayList();
        for (int i = 0; i < tagIdsLen; i++) {
            list.add("INSERT INTO sys_email_tag (email_id, tag_id) VALUES (" + emailId + "," + tagId[i] + ")");
        }
        jsonResponse.getExecResult(list, "", "");
        // 推送逻辑
        List<String> sqlList = new ArrayList<>();
        for (int i = 0; i < tagIdsLen; i++) {
            if (i == 0) {
                sqlList.add(" tag_id =  " + tagId[i]);
            } else {
                sqlList.add(" OR tag_id =  " + tagId[i]);
            }
        }
        // 发送给客户的逻辑
        List<String> seletSchemeList = new ArrayList<>();
        seletSchemeList.add(" SELECT * FROM (SELECT scheme_id FROM sys_scheme_tag_base ");
        seletSchemeList.add(" WHERE " + StringUtils.join(sqlList, "") + " GROUP BY scheme_id) a , sys_post_customer b ");
        seletSchemeList.add(" WHERE a.scheme_id = b.customer_scheme  AND b.email_status = 1  " +
                "AND b.customer_end_time >='" + DateTimeUtils.getNowTime("yyyy-MM-dd") + "' " +
                "AND b.customer_start_time <= '" + DateTimeUtils.getNowTime("yyyy-MM-dd") + "' ");
        String sqlScheme = StringUtils.join(seletSchemeList, "");
        execResult = jsonResponse.getSelectResult(sqlScheme, null, "");
        JSONArray jsonArrayCustomer = (JSONArray) execResult.getData();
        for (int i = 0, len = jsonArrayCustomer.size(); i < len; i++) {
            JSONObject jsonObjectCustomer = jsonArrayCustomer.getJSONObject(i);
            String customerName = jsonObjectCustomer.getString("customer_name");
            String emailStatus = jsonObjectCustomer.getString("email_status");
            String emailNumber = jsonObjectCustomer.getString("email_number");
            if (!"".equals(emailNumber)) {
                if ("1".equals(emailStatus)) {
                    // 通过邮箱发送
                    try {
                        javaMailWithAttachment.postEmail(emailNumber, templateTitle, templateContent, "C:/dummyPath/" + url);
                    } catch (Exception e) {
                        // 发送失败的异常
                        numberInfoPost.sendMsgByYunPian("邮箱发送失败！" + emailNumber, "18914704429");
                        numberInfoPost.sendMsgByYunPian("邮箱发送失败！" + emailNumber, "15951924102");
                    }
                } else {
                    String manualPost = "INSERT INTO sys_email_manual " +
                            "(email_url, customer_name, customer_number, gmt_create) " +
                            "VALUES ('" + url + "','" + customerName + "','" + emailNumber + "',now()) ";
                    execResult = jsonResponse.getExecResult(manualPost, null);
                }
            } else {
                numberInfoPost.sendMsgByYunPian("此客户没有配置邮箱" + customerName, "18914704429");
                numberInfoPost.sendMsgByYunPian("此客户没有配置邮箱" + customerName, "15951924102");
            }
        }
        return execResult;
    }

    @Override
    public ExecResult deleteEmailData(String id) {
        String[] idS = id.split(",");
        List list = new ArrayList();
        for (int i = 0, len = idS.length; i < len; i++) {
            list.add("DELETE FROM sys_email WHERE id = " + idS[i]);
            list.add("DELETE FROM sys_email_tag WHERE email_id =" + idS[i]);
        }
        ExecResult execResult = jsonResponse.getExecResult(list, "", "");
        return execResult;
    }

    @Override
    public JSONObject getAllPostEmail(String pageNumber, String pageSize) {
        String sqlTotal = "SELECT a.*,b.template_name,b.template_title,b.template_content,group_concat(d.name) as tag_name " +
                "FROM sys_email a ,sys_email_template b, sys_email_tag c ,sys_tag d " +
                "WHERE  a.template_id = b.id AND a.id = c.email_id AND c.tag_id = d.id group by a.id " +
                "ORDER BY a.gmt_create DESC ";
        String sql = "SELECT a.*,b.template_name,b.template_title,b.template_content,group_concat(d.name) as tag_name " +
                "FROM sys_email a ,sys_email_template b, sys_email_tag c ,sys_tag d " +
                "WHERE  a.template_id = b.id AND a.id = c.email_id AND c.tag_id = d.id group by a.id " +
                "ORDER BY a.gmt_create DESC  " + SqlEasy.limitPage(pageSize, pageNumber) + "";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        ExecResult execResultTotal = jsonResponse.getSelectResult(sqlTotal, null, "");
        JSONArray jsonArrayTotal = (JSONArray) execResultTotal.getData();
        JSONObject jsonObject = new JSONObject();
        if (jsonArrayTotal == null) {
            jsonObject.put("total", 0);
        } else {
            jsonObject.put("total", jsonArrayTotal.size());
        }
        jsonObject.put("data", jsonArray);
        return jsonObject;
    }

    @Override
    public ExecResult insertTemplateData(String data) {
        String sql = SqlEasy.insertObject(data, "sys_email_template");
        ExecResult execResult = jsonResponse.getExecResult(sql, null);
        return execResult;
    }

    @Override
    public ExecResult updateTemplateData(String data, String id) {
        String sql = SqlEasy.updateObject(data, "sys_email_template", "id = " + id);
        ExecResult execResult = jsonResponse.getExecResult(sql, null);
        return execResult;
    }

    @Override
    public ExecResult deleteTemplateData(String id) {
        String[] idS = id.split(",");
        List list = new ArrayList();
        for (int i = 0, len = idS.length; i < len; i++) {
            list.add("DELETE FROM sys_email_template WHERE id = " + idS[i]);
        }
        ExecResult execResult = jsonResponse.getExecResult(list, "", "");
        return execResult;
    }

    @Override
    public JSONArray getAllTemplate() {
        String sql = "SELECT * FROM sys_email_template";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        return jsonArray;
    }

    @Override
    public JSONObject getAllPostEmailMonitor(String pageNumber, String pageSize, String isStatus) {
        String sqlTotal = "SELECT * FROM sys_email_manual WHERE email_status = " + isStatus + " ORDER BY gmt_create DESC ";
        String sql = "SELECT * FROM sys_email_manual WHERE email_status = " + isStatus + " " +
                "ORDER BY gmt_create DESC " + SqlEasy.limitPage(pageSize, pageNumber) + " ";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        ExecResult execResultTotal = jsonResponse.getSelectResult(sqlTotal, null, "");
        JSONArray jsonArrayTotal = (JSONArray) execResultTotal.getData();
        JSONObject jsonObject = new JSONObject();
        if (jsonArrayTotal == null) {
            jsonObject.put("total", 0);
        } else {
            jsonObject.put("total", jsonArrayTotal.size());
        }
        jsonObject.put("data", jsonArray);
        return jsonObject;
    }

    @Override
    public ExecResult updateEmailMonitor(String id) {
        String sql = "UPDATE sys_email_manual SET email_status = 1 WHERE id = " + id;
        ExecResult execResult = jsonResponse.getExecResult(sql, null);
        return execResult;
    }

    @Override
    public ExecResult deleteEmailMonitor(String id) {
        String[] idS = id.split(",");
        List list = new ArrayList();
        for (int i = 0, len = idS.length; i < len; i++) {
            list.add("DELETE FROM sys_email_manual WHERE id = " + idS[i]);
        }
        ExecResult execResult = jsonResponse.getExecResult(list, "", "");
        return execResult;
    }
}
