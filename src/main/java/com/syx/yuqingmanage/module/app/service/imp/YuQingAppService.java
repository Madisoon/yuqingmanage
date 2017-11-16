package com.syx.yuqingmanage.module.app.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.alienlab.utils.Md5Azdg;
import com.syx.yuqingmanage.module.app.service.IYuQingService;
import com.syx.yuqingmanage.utils.DateTimeUtils;
import com.syx.yuqingmanage.utils.MessagePost;
import com.syx.yuqingmanage.utils.NumberInfoPost;
import com.syx.yuqingmanage.utils.jdbcfilters.Filter;
import com.syx.yuqingmanage.utils.jdbcfilters.SelectParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Msater Zg on 2017/7/13.
 */
@Service
public class YuQingAppService implements IYuQingService {
    @Autowired
    JSONResponse jsonResponse;

    @Autowired
    AppNoteServiceImpl appNoteService;

    // 测试成功
    @Override
    public JSONObject judgerAppUser(String loginName, String password, long timestamp) {
        String sqlGetUser = "SELECT * FROM app_user WHERE app_user_loginname = '" + loginName + "'";
        ExecResult execResult = jsonResponse.getSelectResult(sqlGetUser, null, "");
        JSONObject returnData = new JSONObject();
        if (execResult.getResult() == 1) {
            JSONObject jsonObject = ((JSONArray) execResult.getData()).getJSONObject(0);
            String userPwd = jsonObject.getString("app_user_pwd");
            String userOverDue = jsonObject.getString("app_user_overdue_time").substring(0, 10);
            if (Md5Azdg.md5s(password).equals(userPwd)) {
                String nowTime = DateTimeUtils.getNowTime("yyyy-MM-dd HH:mm:ss");
                if (userOverDue.compareTo(nowTime) < 0) {
                    returnData.put("success", false);
                    returnData.put("value", 11002);
                } else {
                    // 用户存在
                    returnData.put("success", true);
                    JSONObject jsonObjectUser = new JSONObject();
                    // 生成token
                    String token = Md5Azdg.md5s(loginName + ":" + password + ":" + nowTime);
                    jsonObjectUser.put("token", token);
                    String updateToken = "UPDATE app_user SET app_user_token = '" + token + "' , app_timestamp=" + timestamp + "  WHERE app_user_loginname = '" + loginName + "'";
                    jsonResponse.getExecResult(updateToken, null);
                    returnData.put("value", jsonObjectUser);
                    // 添加登陆日志
                    appNoteService.insertAppNote("", "1", "", loginName);
                }
            } else {
                //不存在这个用户
                returnData.put("success", false);
                returnData.put("value", 11001);
            }
        } else {
            //不存在这个用户
            returnData.put("success", false);
            returnData.put("value", 11001);
        }
        return returnData;
    }

    // 测试成功
    @Override
    public JSONObject checkToken(String token) {
        String getUserByCheckToken = "SELECT * FROM app_user WHERE app_user_token = '" + token + "'";
        ExecResult execResult = jsonResponse.getSelectResult(getUserByCheckToken, null, "");
        JSONObject jsonObject = new JSONObject();
        if (execResult.getResult() == 1) {
            // 有数据
            JSONObject jsonObjectUser = ((JSONArray) execResult.getData()).getJSONObject(0);
            String overDueTime = jsonObjectUser.getString("app_user_overdue_time");
            String nowTime = DateTimeUtils.getNowTime("yyyy-MM-dd HH:mm:ss");
            if (overDueTime.compareTo(nowTime) > 0) {
                jsonObject.put("success", true);
                jsonObject.put("value", "");
            } else {
                jsonObject.put("success", false);
                jsonObject.put("value", 11002);
            }
        } else {
            // 没有数据，说明过期了
            jsonObject.put("success", false);
            jsonObject.put("value", 12001);
        }
        return jsonObject;
    }

    // 测试成功
    @Override
    public JSONObject searchMenus(String loginName) {
        String getSMenus = "SELECT a.id AS tag_id,a.app_program_name AS NAME,b.app_module_type AS TYPE  " +
                "FROM (SELECT a.*,b.app_module_id FROM app_user_program a  " +
                "LEFT JOIN app_user_program_module b ON a.id=b.app_program_id WHERE a.app_user_loginname = '" + loginName + "' ) a  " +
                "LEFT JOIN app_module b ON a.app_module_id = b.id GROUP BY a.id";
        ExecResult execResult = jsonResponse.getSelectResult(getSMenus, null, "");
        JSONObject jsonObjectReturn = new JSONObject();
        if (execResult.getResult() == 1) {
            // 有数据
            JSONArray jsonArray = (JSONArray) execResult.getData();
            JSONObject jsonObjectTag = new JSONObject();
            jsonObjectTag.put("tags", jsonArray);
            jsonObjectReturn.put("success", true);
            jsonObjectReturn.put("value", jsonObjectTag);
        } else {
            // 无数据
            jsonObjectReturn.put("success", false);
            jsonObjectReturn.put("value", 21001);
        }
        return jsonObjectReturn;
    }

    // 测试成功
    @Override
    public JSONObject searchFocus(int tag_id, int limit, String date, String loginName) {
        JSONObject jsonObject = new JSONObject();
        String getFoucs = "";
        ExecResult execResult = new ExecResult();
        // 没有时间点
        appNoteService.insertAppNote("查看了聚焦的信息！", "2", "", loginName);
        if ("".equals(date)) {
            // 根据id获取到焦点数据的
            getFoucs = "SELECT a.*, CASE WHEN (b.app_read_id IS NULL) THEN '0' " +
                    "            ELSE  '1' END as info_read  FROM (SELECT b.id ,b.topic_title AS title,b.topic_abstract AS content,b.topic_context AS context,b.topic_time " +
                    "            AS pub_time,b.topic_url AS source_url " +
                    "            FROM (SELECT a.*,b.topic_context_id FROM (SELECT a.*,b.tag_id FROM " +
                    "                            (SELECT a.*,b.app_module_id FROM  app_user_program a " +
                    "                                    LEFT JOIN app_user_program_module b " +
                    "                                    ON a.id = b.app_program_id WHERE a.id = '" + tag_id + "') a LEFT JOIN app_module_tag_dep b " +
                    "                    ON a.app_module_id = b.app_module_id) a , topic_context  b " +
                    "                    WHERE a.tag_id = b.topic_id) a LEFT JOIN sys_topic_context b " +
                    "            ON a.topic_context_id = b.id WHERE b.topic_status = 1 GROUP BY b.id ORDER BY b.topic_time DESC LIMIT 0," + limit + ") a " +
                    "            LEFT JOIN (SELECT * FROM app_user_read WHERE app_read_type = '1' AND app_user_loginName = '" + loginName + "') b " +
                    "            ON a.id = b.app_read_id";
        } else {
            getFoucs = "SELECT a.*, CASE WHEN (b.app_read_id IS NULL) THEN '0' " +
                    "            ELSE  '1' END as info_read  FROM (SELECT b.id ,b.topic_title AS title,b.topic_abstract AS content,b.topic_context AS context,b.topic_time " +
                    "            AS pub_time,b.topic_url AS source_url " +
                    "            FROM (SELECT a.*,b.topic_context_id FROM (SELECT a.*,b.tag_id FROM " +
                    "                            (SELECT a.*,b.app_module_id FROM  app_user_program a " +
                    "                                    LEFT JOIN app_user_program_module b " +
                    "                                    ON a.id = b.app_program_id WHERE a.id = '" + tag_id + "') a LEFT JOIN app_module_tag_dep b " +
                    "                    ON a.app_module_id = b.app_module_id) a , topic_context  b " +
                    "                    WHERE a.tag_id = b.topic_id) a LEFT JOIN sys_topic_context b " +
                    "            ON a.topic_context_id = b.id WHERE b.topic_status = 1  AND b.topic_time < '" + date + "'  GROUP BY b.id ORDER BY b.topic_time DESC LIMIT 0," + limit + ") a " +
                    "            LEFT JOIN (SELECT * FROM app_user_read WHERE app_read_type = '1' AND app_user_loginName = '" + loginName + "') b " +
                    "            ON a.id = b.app_read_id";
        }
        execResult = jsonResponse.getSelectResult(getFoucs, null, "");
        if (execResult.getResult() == 1) {
            // 有数据
            JSONObject jsonObjectValue = new JSONObject();
            JSONArray jsonArray = (JSONArray) execResult.getData();
            jsonObjectValue = jsonArray.getJSONObject(jsonArray.size() - 1);
            JSONObject jsonObjectData = new JSONObject();
            jsonObjectData.put("time", jsonObjectValue.getString("pub_time"));
            jsonObjectData.put("data", jsonArray);
            jsonObject.put("success", true);
            jsonObject.put("value", jsonObjectData);
        } else {
            // 无数据
            jsonObject.put("success", false);
            jsonObject.put("value", 22001);
        }
        return jsonObject;
    }

    // 测试普通信息的获取
    @Override
    public JSONObject searchTagInfo(String filters, int limit, String date, String loginName) {

        // 普通数据的获取

        SelectParam selectParam = getSelectParam(filters);
        String sqlWhere = selectParam.getWhereClause();
        // 所有的值
        List<String> list = new ArrayList<>();
        String[] sqlWhereValue = selectParam.getParams();
        for (int i = 0, sqlWhereValueLen = sqlWhereValue.length; i < sqlWhereValueLen; i++) {
            sqlWhere = sqlWhere.replaceFirst("\\?", "''{" + i + "}''");
            list.add(sqlWhereValue[i]);
        }
        appNoteService.insertAppNote(StringUtils.join(list, "|"), "2", "", loginName);
        String getSql = "";
        if (!"".equals(date)) {
            getSql = " SELECT a.*,CASE WHEN (b.app_read_id IS NULL) THEN ''0''  " +
                    "ELSE  ''1'' END AS info_read FROM (SELECT * FROM (SELECT *,CASE a.source    " +
                    " WHEN ''新闻'' THEN ''1''    " +
                    " WHEN ''论坛'' THEN ''2''    " +
                    " WHEN ''博客'' THEN ''3''    " +
                    " WHEN ''微博'' THEN ''5''    " +
                    " WHEN ''微信'' THEN ''6''    " +
                    " WHEN ''移动咨询'' THEN ''7''    " +
                    " ELSE ''8'' END AS source_id FROM (SELECT a.id AS program_id, b.id , b.infor_type AS level_id,b.infor_title    " +
                    " AS title,b.infor_context AS content,b.infor_link   AS source_url,b.infor_createtime AS pub_time,b.infor_source    " +
                    " AS source, b.infor_site AS site,a.id AS tag_id  FROM (SELECT a.*,b.infor_id FROM (SELECT a.*,b.tag_id FROM     " +
                    " (SELECT a.*,b.app_module_id FROM  app_user_program a   LEFT JOIN app_user_program_module b   ON a.id = b.app_program_id   " +
                    " WHERE a.app_user_loginname = ''" + loginName + "'' ) a LEFT JOIN app_module_tag_dep b   ON a.app_module_id = b.app_module_id) a   " +
                    " LEFT JOIN infor_tag b ON a.tag_id = b.tag_id) a,sys_infor b   WHERE a.infor_id = b.id  AND  b.infor_createtime < ''" + date + "''  ORDER BY b.infor_createtime DESC) a) a   " +
                    " " + sqlWhere + "  LIMIT 0, " + limit + ") a LEFT JOIN (SELECT * FROM app_user_read WHERE app_read_type = ''0'' AND app_user_loginName = ''" + loginName + "'') b ON a.id = b.app_read_id";
        } else {
            getSql = " SELECT a.*,CASE WHEN (b.app_read_id IS NULL) THEN ''0''  " +
                    "ELSE  ''1'' END AS info_read FROM (SELECT * FROM (SELECT *,CASE a.source    " +
                    " WHEN ''新闻'' THEN ''1''    " +
                    " WHEN ''论坛'' THEN ''2''    " +
                    " WHEN ''博客'' THEN ''3''    " +
                    " WHEN ''微博'' THEN ''5''    " +
                    " WHEN ''微信'' THEN ''6''    " +
                    " WHEN ''移动咨询'' THEN ''7''    " +
                    " ELSE ''8'' END AS source_id FROM (SELECT a.id AS program_id, b.id , b.infor_type AS level_id,b.infor_title    " +
                    " AS title,b.infor_context AS content,b.infor_link   AS source_url,b.infor_createtime AS pub_time,b.infor_source    " +
                    " AS source, b.infor_site AS site,a.id AS tag_id  FROM (SELECT a.*,b.infor_id FROM (SELECT a.*,b.tag_id FROM     " +
                    " (SELECT a.*,b.app_module_id FROM  app_user_program a   LEFT JOIN app_user_program_module b   ON a.id = b.app_program_id   " +
                    " WHERE a.app_user_loginname = ''" + loginName + "'' ) a LEFT JOIN app_module_tag_dep b   ON a.app_module_id = b.app_module_id) a   " +
                    " LEFT JOIN infor_tag b ON a.tag_id = b.tag_id) a,sys_infor b   WHERE a.infor_id = b.id   ORDER BY b.infor_createtime DESC) a) a   " +
                    " " + sqlWhere + "  LIMIT 0, " + limit + ") a LEFT JOIN (SELECT * FROM app_user_read WHERE app_read_type = ''0'' AND app_user_loginName = ''" + loginName + "'') b ON a.id = b.app_read_id";
        }
        ExecResult execResult = jsonResponse.getSelectResult(getSql, sqlWhereValue, "");
        JSONObject jsonObject = new JSONObject();
        if (execResult.getResult() == 1) {
            // 有数据
            JSONArray jsonArrayImpReturn = new JSONArray();
            JSONArray jsonArray = (JSONArray) execResult.getData();
            JSONObject jsonObjectOne = jsonArray.getJSONObject(0);
            String imp = "SELECT * FROM  app_user_program a ,app_user_program_module b ,app_module c " +
                    "WHERE a.id = b.`app_program_id` AND a.`id` = '" + jsonObjectOne.getString("program_id") + "' " +
                    "AND b.`app_module_id` = c.id AND a.`app_user_loginname` = '" + loginName + "'";
            ExecResult execResult1 = jsonResponse.getSelectResult(imp, null, "");
            JSONArray jsonArrayImp = (JSONArray) execResult1.getData();
            boolean flag = false;
            // 需要先判断是否有关键词
            for (int i = 0; i < jsonArrayImp.size(); i++) {
                JSONObject jsonObjectImp = jsonArrayImp.getJSONObject(i);
                String impContent = jsonObjectImp.getString("app_module_imp_word");
                String noImpContent = jsonObjectImp.getString("app_module_noimp_word");
                if (impContent != "" || noImpContent != "") {
                    flag = true;
                    break;
                }
            }
            JSONObject jsonObjectValue = new JSONObject();
            if (flag) {
                // 有关键词，需要重新筛选匹配
                String getSqlImp = "";
                if (!"".equals(date)) {
                    getSqlImp = " SELECT a.*,CASE WHEN (b.app_read_id IS NULL) THEN ''0''  " +
                            "ELSE  ''1'' END AS info_read FROM (SELECT * FROM (SELECT *,CASE a.source    " +
                            " WHEN ''新闻'' THEN ''1''    " +
                            " WHEN ''论坛'' THEN ''2''    " +
                            " WHEN ''博客'' THEN ''3''    " +
                            " WHEN ''微博'' THEN ''5''    " +
                            " WHEN ''微信'' THEN ''6''    " +
                            " WHEN ''移动咨询'' THEN ''7''    " +
                            " ELSE ''8'' END AS source_id FROM (SELECT a.id AS program_id, b.id , b.infor_type AS level_id,b.infor_title    " +
                            " AS title,b.infor_context AS content,b.infor_link   AS source_url,b.infor_createtime AS pub_time,b.infor_source    " +
                            " AS source, b.infor_site AS site,a.id AS tag_id  FROM (SELECT a.*,b.infor_id FROM (SELECT a.*,b.tag_id FROM     " +
                            " (SELECT a.*,b.app_module_id FROM  app_user_program a   LEFT JOIN app_user_program_module b   ON a.id = b.app_program_id   " +
                            " WHERE a.app_user_loginname = ''" + loginName + "'' ) a LEFT JOIN app_module_tag_dep b   ON a.app_module_id = b.app_module_id) a   " +
                            " LEFT JOIN infor_tag b ON a.tag_id = b.tag_id) a,sys_infor b   WHERE a.infor_id = b.id  AND  b.infor_createtime < ''" + date + "''  ORDER BY b.infor_createtime DESC) a) a   " +
                            " " + sqlWhere + " ) a LEFT JOIN (SELECT * FROM app_user_read WHERE app_read_type = ''0'' AND app_user_loginName = ''" + loginName + "'') b ON a.id = b.app_read_id";
                } else {
                    getSqlImp = " SELECT a.*,CASE WHEN (b.app_read_id IS NULL) THEN ''0''  " +
                            "ELSE  ''1'' END AS info_read FROM (SELECT * FROM (SELECT *,CASE a.source    " +
                            " WHEN ''新闻'' THEN ''1''    " +
                            " WHEN ''论坛'' THEN ''2''    " +
                            " WHEN ''博客'' THEN ''3''    " +
                            " WHEN ''微博'' THEN ''5''    " +
                            " WHEN ''微信'' THEN ''6''    " +
                            " WHEN ''移动咨询'' THEN ''7''    " +
                            " ELSE ''8'' END AS source_id FROM (SELECT a.id AS program_id, b.id , b.infor_type AS level_id,b.infor_title    " +
                            " AS title,b.infor_context AS content,b.infor_link   AS source_url,b.infor_createtime AS pub_time,b.infor_source    " +
                            " AS source, b.infor_site AS site,a.id AS tag_id  FROM (SELECT a.*,b.infor_id FROM (SELECT a.*,b.tag_id FROM     " +
                            " (SELECT a.*,b.app_module_id FROM  app_user_program a   LEFT JOIN app_user_program_module b   ON a.id = b.app_program_id   " +
                            " WHERE a.app_user_loginname = ''" + loginName + "'' ) a LEFT JOIN app_module_tag_dep b   ON a.app_module_id = b.app_module_id) a   " +
                            " LEFT JOIN infor_tag b ON a.tag_id = b.tag_id) a,sys_infor b   WHERE a.infor_id = b.id   ORDER BY b.infor_createtime DESC) a) a   " +
                            " " + sqlWhere + " ) a LEFT JOIN (SELECT * FROM app_user_read WHERE app_read_type = ''0'' AND app_user_loginName = ''" + loginName + "'') b ON a.id = b.app_read_id";
                }

                ExecResult execResultImp = jsonResponse.getSelectResult(getSqlImp, sqlWhereValue, "");
                JSONArray jsonArray1 = (JSONArray) execResultImp.getData();
                for (int j = 0, jsonArrayLen = jsonArray1.size(); j < jsonArrayLen; j++) {
                    JSONObject jsonObjectInfo = jsonArray1.getJSONObject(j);
                    String title = jsonObjectInfo.getString("title");
                    String content = jsonObjectInfo.getString("content");
                    for (int i = 0; i < jsonArrayImp.size(); i++) {
                        JSONObject jsonObjectImp = jsonArrayImp.getJSONObject(i);
                        String impContent = jsonObjectImp.getString("app_module_imp_word");
                        String noImpContent = jsonObjectImp.getString("app_module_noimp_word");
                        boolean noWordJudgeFlag = !MessagePost.judgeWord(noImpContent, content, title) || noImpContent.equals("");
                        if (noWordJudgeFlag) {
                            //不包含排除关键词。
                            boolean wordJudgeFlag = (impContent.equals("") || MessagePost.judgeWord(impContent, content, title));
                            if (wordJudgeFlag) {
                                jsonArrayImpReturn.add(jsonObjectInfo);
                            }
                        }
                    }
                }
                if (jsonArrayImpReturn.size() == 0) {
                    jsonObject.put("success", false);
                    jsonObject.put("value", 23001);
                } else {
                    JSONObject jsonObjectReturn = jsonArrayImpReturn.getJSONObject(jsonArrayImpReturn.size() - 1);
                    jsonObjectValue.put("time", jsonObjectReturn.getString("pub_time"));
                    jsonObjectValue.put("data", jsonArrayImpReturn);
                    jsonObject.put("success", true);
                    jsonObject.put("value", jsonObjectValue);
                }
            } else {
                JSONObject jsonObjectReturn = jsonArray.getJSONObject(jsonArray.size() - 1);
                jsonObjectValue.put("time", jsonObjectReturn.getString("pub_time"));
                jsonObjectValue.put("data", jsonArray);
                jsonObject.put("success", true);
                jsonObject.put("value", jsonObjectValue);
            }
        } else {
            // 无数据
            jsonObject.put("success", false);
            jsonObject.put("value", 23001);
        }
        return jsonObject;
    }

    // 测试完成
    @Override
    public JSONObject getInfodetail(String id, String loginName) {
        String sqlGetDeatil = "SELECT b.id, b.infor_type AS level_id,b.infor_title  " +
                "AS title,b.infor_context AS content,b.infor_link   " +
                "AS source_url,b.infor_createtime AS pub_time,b.infor_source   " +
                "AS source_id, b.infor_site AS site FROM  sys_infor b WHERE b.id = '" + id + "' ";
        ExecResult execResult = jsonResponse.getSelectResult(sqlGetDeatil, null, "");
        JSONObject jsonObject = new JSONObject();
        if (execResult.getResult() == 1) {
            JSONObject jsonObjectInfo = ((JSONArray) execResult.getData()).getJSONObject(0);
            String sourceId = jsonObjectInfo.getString("source_id");
            jsonObjectInfo.put("source_id", returnSource(sourceId));
            jsonObject.put("success", true);
            jsonObject.put("value", jsonObjectInfo);
            // 添加得到详情的日志
            appNoteService.insertAppNote(jsonObjectInfo.getString("title"), "3", "", loginName);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("value", 53001);
        }
        return jsonObject;
    }

    // 测试成功
    @Override
    public JSONObject searchFavor(int limit, String date, String loginName) {
        JSONObject jsonObject = new JSONObject();
        String getFavor = "";
        ExecResult execResult = new ExecResult();
        // 没有时间点
        if ("".equals(date)) {
            // 根据id获取到焦点数据的
            getFavor = "  SELECT a.*, CASE WHEN (b.app_favor_id IS NULL) THEN '0' ELSE  '1' END AS favor_read " +
                    " FROM ( SELECT a.id AS favor_id, b.id , b.infor_type AS level_id,b.infor_title  " +
                    " AS title,b.infor_context AS content,b.infor_link  " +
                    " AS source_url,b.infor_createtime AS pub_time,b.infor_source   " +
                    " AS source_id, b.infor_site AS site FROM  app_user_favor a   " +
                    " LEFT JOIN sys_infor b ON a.app_favor_infor_id = b.id  " +
                    " WHERE a.app_user_loginname = '" + loginName + "' ORDER BY b.infor_createtime DESC LIMIT 0," + limit + " ) a LEFT JOIN app_user_favor_read b " +
                    " ON a.favor_id = b.app_favor_id ORDER BY a.pub_time DESC";
        } else {
            getFavor = " SELECT a.*, CASE WHEN (b.app_favor_id IS NULL) THEN '0' ELSE  '1' END AS favor_read " +
                    " FROM ( SELECT a.id AS favor_id, b.id , b.infor_type AS level_id,b.infor_title   " +
                    " AS title,b.infor_context AS content,b.infor_link  " +
                    " AS source_url,b.infor_createtime AS pub_time,b.infor_source    " +
                    " AS source_id, b.infor_site AS site FROM  app_user_favor a    " +
                    " LEFT JOIN sys_infor b ON a.app_favor_infor_id = b.id   " +
                    " WHERE a.app_user_loginname = '" + loginName + "' AND b.infor_createtime < '" + date + "'  " +
                    " ORDER BY b.infor_createtime DESC LIMIT 0," + limit + " ) a LEFT JOIN app_user_favor_read b " +
                    " ON a.favor_id = b.app_favor_id ORDER BY a.pub_time DESC";
        }
        execResult = jsonResponse.getSelectResult(getFavor, null, "");
        if (execResult.getResult() == 1) {
            // 有数据
            JSONArray jsonArray = (JSONArray) execResult.getData();
            JSONArray jsonArrayReturn = new JSONArray();
            for (int i = 0, jsonArrayLen = jsonArray.size(); i < jsonArrayLen; i++) {
                JSONObject jsonObjectInfo = jsonArray.getJSONObject(i);
                String sourceId = jsonObjectInfo.getString("source_id");
                jsonObjectInfo.put("source_id", returnSource(sourceId));
                jsonArrayReturn.add(jsonObjectInfo);
            }
            JSONObject jsonObjectValue = new JSONObject();
            jsonObjectValue = jsonArrayReturn.getJSONObject(jsonArrayReturn.size() - 1);
            JSONObject jsonObjectData = new JSONObject();
            jsonObjectData.put("time", jsonObjectValue.getString("pub_time"));
            jsonObjectData.put("data", jsonArrayReturn);
            jsonObject.put("success", true);
            jsonObject.put("value", jsonObjectData);
        } else {
            // 无数据
            jsonObject.put("success", false);
            jsonObject.put("value", 31001);
        }
        return jsonObject;
    }

    // 测试完成
    @Override
    public JSONObject removeFavor(String id, String loginName) {
        String removeFavorSql = "";
        JSONObject jsonObject = new JSONObject();
        if ("".equals(id)) {
            removeFavorSql = "DELETE FROM app_user_favor WHERE app_user_loginname = '" + loginName + "'";
            ExecResult execResult = jsonResponse.getExecResult(removeFavorSql, null);
            if (execResult.getResult() == 1) {
                jsonObject.put("success", true);
                jsonObject.put("value", true);
            } else {
                jsonObject.put("success", false);
                jsonObject.put("value", 33002);
            }
        } else {
            removeFavorSql = "DELETE FROM app_user_favor WHERE app_favor_infor_id = '" + id + "'";
            ExecResult execResult = jsonResponse.getExecResult(removeFavorSql, null);
            if (execResult.getResult() == 1) {
                jsonObject.put("success", true);
                jsonObject.put("value", true);
            } else {
                jsonObject.put("success", false);
                jsonObject.put("value", 33001);
            }
        }
        return jsonObject;
    }

    // 测试完成
    @Override
    public JSONObject addFavor(String id, String loginName) {
        String insertSql = "INSERT INTO  app_user_favor (app_user_loginname, app_favor_infor_id) VALUES ('" + loginName + "','" + id + "')";
        ExecResult execResult = jsonResponse.getExecResult(insertSql, null);
        JSONObject jsonObject = new JSONObject();
        if (execResult.getResult() == 1) {
            jsonObject.put("success", true);
            jsonObject.put("value", true);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("value", 32001);
        }
        return jsonObject;
    }

    // 测试完成
    @Override
    public JSONObject checkFavor(String id, String loginName) {
        String checkSql = "SELECT * FROM app_user_favor a WHERE a.app_user_loginname = '" + loginName + "' AND a.app_favor_infor_id = '" + id + "'";
        ExecResult execResult = jsonResponse.getSelectResult(checkSql, null, "");
        JSONObject jsonObject = new JSONObject();
        if (execResult.getResult() == 1) {
            jsonObject.put("success", true);
            jsonObject.put("value", true);
        } else {
            jsonObject.put("success", true);
            jsonObject.put("value", false);
        }
        return jsonObject;
    }

    // 测试完成
    @Override
    public JSONObject updatePwd(String oldPwd, String newPwd, String loginName) {
        String sqlPwd = "UPDATE app_user a SET a.app_user_pwd = '" + Md5Azdg.md5s(newPwd) + "' " +
                "WHERE a.app_user_loginname = '" + loginName + "' AND a.app_user_pwd = '" + Md5Azdg.md5s(oldPwd) + "'";
        JSONObject jsonObject = new JSONObject();
        ExecResult execResult = jsonResponse.getExecResult(sqlPwd, null);
        if (execResult.getResult() == 1) {
            jsonObject.put("success", true);
            jsonObject.put("value", true);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("value", 41001);
        }
        return jsonObject;
    }

    @Override
    public JSONObject updateConfig(String tag_id, String push, String loginName) {
        String[] tagIds = tag_id.split(",");
        int tagIdsLen = tagIds.length;
        List list = new ArrayList();
        for (int i = 0; i < tagIdsLen; i++) {
            list.add("UPDATE app_user_config SET tag_push = " + push + " WHERE tag_id = " + tagIds[i] + " ");
        }
        ExecResult execResult = jsonResponse.getExecResult(list, "", "");
        JSONObject jsonObject = new JSONObject();
        if (execResult.getResult() == 1) {
            jsonObject.put("success", true);
            jsonObject.put("value", true);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("value", 51001);
        }
        return jsonObject;
    }

    @Override
    public JSONObject getConfig(String loginName) {
        String getConfigSql = "SELECT a.tag_push AS push,a.app_program_name AS NAME ,a.tag_id,b.app_module_type AS TYPE  " +
                "FROM (SELECT * FROM app_user_config a  " +
                "LEFT JOIN app_user_program_module b  " +
                "ON a.tag_id = b.app_program_id LEFT JOIN app_user_program c ON a.tag_id = c.id " +
                "WHERE a.tag_user = ''{0}'') a LEFT JOIN app_module b  " +
                "ON a.app_module_id = b.id GROUP BY a.tag_id";
        ExecResult execResult = jsonResponse.getSelectResult(getConfigSql, new String[]{loginName}, "");
        JSONObject jsonObject = new JSONObject();
        if (execResult.getResult() == 1) {
            JSONObject jsonObjectItem = new JSONObject();
            jsonObjectItem.put("push", (JSONArray) execResult.getData());
            jsonObject.put("success", true);
            jsonObject.put("value", jsonObjectItem);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("value", 52001);
        }
        return jsonObject;
    }

    public int returnSource(String sourceId) {
        int returnSourceId = 0;
        switch (sourceId) {
            case "新闻":
                returnSourceId = 1;
                break;
            case "论坛":
                returnSourceId = 2;
                break;
            case "博客":
                returnSourceId = 3;
                break;
            case "视频":
                returnSourceId = 4;
                break;
            case "微博":
                returnSourceId = 5;
                break;
            case "微信":
                returnSourceId = 6;
                break;
            case "移动资讯":
                returnSourceId = 7;
                break;
            default:
                returnSourceId = 8;
                break;
        }
        return returnSourceId;
    }

    public SelectParam getSelectParam(String filters) {
        JSONArray jsonArray = JSON.parseArray(filters);
        List<Filter> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Filter filter = JSON.parseObject(jsonObject.toString(), Filter.class);
            list.add(filter);
        }
        SelectParam selectParam = Filter.getFilterParams(list);
        return selectParam;
    }

    @Override
    public JSONObject checkVersion(String appVersion) {
        String selectApp = "SELECT * FROM app_version where app_type = 1 ORDER BY app_time DESC  LIMIT 0,1";
        ExecResult execResult = jsonResponse.getSelectResult(selectApp, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        JSONObject returnJsonObject = new JSONObject();
        String appVs = jsonObject.getString("app_version");
        if (appVersion.equals(appVs)) {
            returnJsonObject.put("success", false);
            returnJsonObject.put("value", "");
        } else {
            returnJsonObject.put("success", true);
            JSONObject jsonObjectApp = new JSONObject();
            jsonObjectApp.put("version", appVs);
            jsonObjectApp.put("url", jsonObject.getString("app_url"));
            jsonObjectApp.put("info", jsonObject.getString("app_info"));
            returnJsonObject.put("value", jsonObjectApp);
        }
        return returnJsonObject;
    }

    @Override
    public JSONObject checkVersionNoCustom(String appVersion) {
        String selectApp = "SELECT * FROM app_version where app_type = 0 ORDER BY app_time DESC  LIMIT 0,1";
        ExecResult execResult = jsonResponse.getSelectResult(selectApp, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        JSONObject returnJsonObject = new JSONObject();
        String appVs = jsonObject.getString("app_version");
        if (appVersion.equals(appVs)) {
            returnJsonObject.put("success", false);
            returnJsonObject.put("value", "");
        } else {
            returnJsonObject.put("success", true);
            JSONObject jsonObjectApp = new JSONObject();
            jsonObjectApp.put("version", appVs);
            jsonObjectApp.put("url", jsonObject.getString("app_url"));
            jsonObjectApp.put("info", jsonObject.getString("app_info"));
            returnJsonObject.put("value", jsonObjectApp);
        }
        return returnJsonObject;
    }

    @Override
    public JSONObject clickInfoData(String userName, String infoId, String infoType) {
        String selectSqlInfo = "SELECT * FROM app_user_read a WHERE a.app_user_loginName = '" + userName + "' " +
                "AND a.app_read_id = '" + infoId + "' AND a.app_read_type = '" + infoType + "'";
        ExecResult selectSqlInfoResult = jsonResponse.getSelectResult(selectSqlInfo, null, "");
        String sql = "SELECT * FROM sys_infor a WHERE a.id = '" + infoId + "'";
        ExecResult execResultData = jsonResponse.getSelectResult(sql, null, "");
        JSONArray jsonArray = (JSONArray) execResultData.getData();
        JSONObject jsonObjectData = jsonArray.getJSONObject(0);
        appNoteService.insertAppNote(jsonObjectData.getString("infor_title"), "3", "", userName);
        JSONObject returnJsonObject = new JSONObject();
        if (selectSqlInfoResult.getResult() != 1) {
            String insertSql = "INSERT INTO app_user_read (app_user_loginName, app_read_id, app_read_type) VALUES (''{0}'',''{1}'',''{2}'')";
            ExecResult execResult = jsonResponse.getExecResult(insertSql, new String[]{userName, infoId, infoType});
            if (execResult.getResult() == 1) {
                returnJsonObject.put("success", true);
                returnJsonObject.put("value", "");
            } else {
                returnJsonObject.put("success", false);
                returnJsonObject.put("value", "");
            }
        } else {
            returnJsonObject.put("success", false);
            returnJsonObject.put("value", "");
        }
        return returnJsonObject;
    }

    @Override
    public JSONObject clickFavor(String favorId) {

        String selectSql = "SELECT * FROM app_user_favor_read a WHERE a.app_favor_id = " + favorId + "";
        ExecResult execResultFavor = jsonResponse.getSelectResult(selectSql, null, "");
        JSONObject returnJsonObject = new JSONObject();
        if (execResultFavor.getResult() != 1) {
            String insertSql = "INSERT INTO app_user_favor_read (app_favor_id) VALUES (" + favorId + ")";
            ExecResult execResult = jsonResponse.getExecResult(insertSql, null);
            if (execResult.getResult() == 1) {
                returnJsonObject.put("success", true);
                returnJsonObject.put("value", "");
            } else {
                returnJsonObject.put("success", false);
                returnJsonObject.put("value", "");
            }
        } else {
            returnJsonObject.put("success", false);
            returnJsonObject.put("value", "");
        }
        return returnJsonObject;
    }
}
