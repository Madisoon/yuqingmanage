package com.syx.yuqingmanage.module.infor.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.infor.service.IInForService;
import com.syx.yuqingmanage.utils.*;
import com.syx.yuqingmanage.utils.freemark.XmlToDocx;
import com.syx.yuqingmanage.utils.freemark.XmlToExcel;
import com.syx.yuqingmanage.utils.jpush.JpushBean;
import com.syx.yuqingmanage.utils.jpush.JpushServer;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Master  Zg on 2016/11/9.
 */
@Service
public class InForService implements IInForService {
    @Autowired
    private JSONResponse jsonResponse;
    @Autowired
    private NumberInfoPost numberInfoPost;
    @Autowired
    private QqMessagePost qqMessagePost;
    @Autowired
    private FailData failData;
    @Autowired
    private QqAsyncMessagePost qqAsyncMessagePost;
    private DataExport dataExport = new DataExport();

    @Autowired
    private JpushServer jpushServer;

    @Override
    public ExecResult insertInFor(String infoData, String infoTag) {

        JSONObject jsonObject = JSONObject.parseObject(infoData);
        // 信息的内容
        String infoContext = jsonObject.getString("infor_context");
        // 信息的标题
        String infoTitle = jsonObject.getString("infor_title");
        // 信息的链接
        String infoLink = jsonObject.getString("infor_link");
        // 信息的来源
        String infoSource = jsonObject.getString("infor_source");
        // 信息的等级
        String infoGrade = jsonObject.getString("infor_grade");
        // 信息的正负面
        String infoType = jsonObject.getString("infor_type");
        // 信息的站点
        String infoSite = jsonObject.getString("infor_site");
        String inforCreater = jsonObject.getString("infor_creater");

        String tagId = infoTag;
        String inFoData = String.valueOf(jsonObject);
        String sqlInsert = SqlEasy.insertObject(inFoData, "sys_infor");
        ExecResult execResult = jsonResponse.getExecInsertId(sqlInsert, null, "", "");
        //只有主表插入成功之后才会执行插入从表和发送逻辑
        String infor_id = "";
        if (execResult.getResult() == 1) {
            //所有的标签的id
            infor_id = execResult.getMessage();
            String[] tagIds = tagId.split(",");
            //信息的等级
            int tagIdsLen = tagIds.length;
            List<String> tagSql = new ArrayList<>();
            for (int i = 0; i < tagIdsLen; i++) {
                String tag_id = tagIds[i];
                String sqlTag = "INSERT INTO infor_tag (infor_id,tag_id) VALUES('" + infor_id + "','" + tag_id + "')";
                tagSql.add(sqlTag);
            }
            jsonResponse.getExecResult(tagSql, "", "");

            // 发送给客户的逻辑
            List<String> sqlList = new ArrayList<>();
            for (int i = 0; i < tagIdsLen; i++) {
                if (i == 0) {
                    sqlList.add(" tag_id =  " + tagIds[i]);
                } else {
                    sqlList.add(" OR tag_id =  " + tagIds[i]);
                }
            }
            List<String> selectList = new ArrayList<>();
            selectList.add(" SELECT * FROM (SELECT scheme_id FROM sys_scheme_tag_base ");
            selectList.add(" WHERE " + StringUtils.join(sqlList, "") + " GROUP BY scheme_id) a , sys_scheme b ");
            selectList.add(" WHERE a.scheme_id = b.id AND b.scheme_grade LIKE '%" + infoGrade + "%' ");
            //获取到符合到条件的方案（去除重复+等级匹配）
            String sqlScheme = StringUtils.join(selectList, "");
            ExecResult allSqlScheme = jsonResponse.getSelectResult(sqlScheme, null, "");
            JSONArray jsonArrayScheme = (JSONArray) allSqlScheme.getData();
            List<String> schemeIdList = new ArrayList<>();
            List<String> schemeIdListLate = new ArrayList<>();
            if (jsonArrayScheme != null) {
                int jsonArraySchemeLen = jsonArrayScheme.size();
                System.out.println("符合条件的方案" + jsonArrayScheme.toString());
                for (int i = 0; i < jsonArraySchemeLen; i++) {
                    JSONObject jsonObjectScheme = jsonArrayScheme.getJSONObject(i);
                    String schemeId = jsonObjectScheme.getString("id");
                    String schemeStatus = jsonObjectScheme.getString("scheme_status");
                    int flag = 1;
                    if ("0".equals(schemeStatus)) {
                        String intervalTime = getPostTime(schemeId, new Date());
                        flag = DifTimeGet.judgeTimeInterval(intervalTime, new Date());
                    }
                    String impWord = jsonObjectScheme.getString("scheme_imp");
                    String noImpWord = jsonObjectScheme.getString("scheme_no_imp");
                    String impLink = jsonObjectScheme.getString("scheme_link");
                    String noImpLink = jsonObjectScheme.getString("scheme_no_link");
                    boolean noWordJudgeFlag = !MessagePost.judgeWord(noImpWord, infoContext, infoTitle) || noImpWord.equals("")
                            && (!MessagePost.judgeLink(noImpLink, infoLink) || noImpLink.equals(""));
                    if (noWordJudgeFlag) {
                        //不包含排除关键词，如果包含
                        boolean wordJudgeFlag = (impWord.equals("") || MessagePost.judgeWord(impWord, infoContext, infoTitle)) &&
                                ("".equals(impLink) || MessagePost.judgeLink(impLink, infoLink));
                        if (wordJudgeFlag) {
                            //执行发标签 执行的事情是一样的
                            if (flag == 1) {
                                schemeIdList.add(schemeId);
                            } else {
                                schemeIdListLate.add(schemeId);
                            }
                        }
                    } else {
                        System.out.println("包含了排除关键词，或者包含了匹配链接");
                    }
                }
                //所有需要发送信息的发送和信息
                if (schemeIdList.size() > 0) {
                    //立刻发送
                    JSONArray allCustomer = getAllCustomerByScheme(schemeIdList);
                    if (allCustomer == null) {
                    } else {
                        qqAsyncMessagePost.postCustomerMessage(allCustomer, infoContext, infoTitle, infoLink, infoSource, inforCreater, infoSite);
                    }

                }
                if (schemeIdListLate.size() > 0) {
                    //延迟发送
                    JSONArray allCustomerLate = getAllCustomerByScheme(schemeIdListLate);
                    if (allCustomerLate == null) {
                        //没有需要延迟发送的人
                        System.out.println("客户为空");
                    } else {
                        qqAsyncMessagePost.postCustomerLate(allCustomerLate, infoContext, infoTitle, infoLink, infoSource, inforCreater, infoSite);
                    }
                }
            }
            // 发送给平台的逻辑
            postTerraceCustomer(infoTag, infoGrade, infoContext, infoTitle, infoLink, infoSource, infoType, infoSite);
            // 推送模块 APP starter
            String infoProgram = "SELECT d.id FROM  app_module_tag_dep a ,app_module b , " +
                    "app_user_program_module c,app_user_program d " +
                    "WHERE a.app_module_id = b.id AND b.id = c.app_module_id  " +
                    "AND c.app_program_id = d.id AND ( " + StringUtils.join(sqlList, "") + " ) " +
                    "AND b.app_module_type = 0 GROUP BY d.id  ";
            ExecResult execResultProgram = jsonResponse.getSelectResult(infoProgram, null, "");
            if (execResultProgram.getResult() == 1) {
                JSONArray jsonArray = (JSONArray) execResultProgram.getData();
                int jsonArrayLen = jsonArray.size();
                List<JpushBean> list = new ArrayList<>();
                for (int i = 0; i < jsonArrayLen; i++) {
                    JSONObject jsonObjectTag = jsonArray.getJSONObject(i);
                    JpushBean jpushBean = new JpushBean();
                    jpushBean.setId(infor_id);
                    jpushBean.setTagId(jsonObjectTag.getString("id"));
                    jpushBean.setTitle(infoTitle);
                    jpushBean.setContent("");
                    jpushBean.setType("0");
                    jpushBean.setUrl("");
                    list.add(jpushBean);
                }
                /*jpushServer.pushNotification(list);*/
            }
            // 推送模块 end
        }
        return execResult;
    }

    @Override
    public JSONObject getAllInfor(String pageNumber, String pageSize) {
        JSONObject returnData = new JSONObject();
        int pageNumberInt = Integer.parseInt(pageNumber, 10);
        int pageSizeInt = Integer.parseInt(pageSize, 10);
        String sqlLen = "SELECT COUNT(id) AS total FROM sys_infor";
        ExecResult execResult = jsonResponse.getSelectResult(sqlLen, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        JSONObject jsonObjectLen = jsonArray.getJSONObject(0);

        String sqlInFor = "SELECT a.*,GROUP_CONCAT(a.tag_id) AS tag_ids,GROUP_CONCAT(a.name) AS tag_names  " +
                "FROM (SELECT a.*,b.name FROM (SELECT a.*,b.tag_id,c.user_name FROM (SELECT * FROM  sys_infor a  " +
                "ORDER BY a.infor_createtime DESC  LIMIT " + ((pageNumberInt - 1) * pageSizeInt) + "," + pageSizeInt + " )  a LEFT JOIN infor_tag b " +
                "ON a.id = b.infor_id LEFT JOIN sys_user c ON a.infor_creater = c.user_loginname) a  " +
                "LEFT JOIN sys_tag b ON a.tag_id = b.id) a GROUP BY a.id";
        execResult = jsonResponse.getSelectResult(sqlInFor, null, "");
        JSONArray allData = (JSONArray) execResult.getData();
        returnData.put("data", allData);
        returnData.put("total", jsonObjectLen.getInteger("total"));
        return returnData;
    }

    @Override
    public ExecResult updateInfoData(String infoData, String infoTagId, String infoId) {
        List<String> list = new ArrayList<>();
        String sql = SqlEasy.updateObject(infoData, "sys_infor", " id = " + infoId);
        list.add(sql);
        list.add("DELETE FROM infor_tag WHERE infor_id = " + infoId);
        String[] infoTagIds = infoTagId.split(",");
        int infoTagIdsLen = infoTagIds.length;
        for (int i = 0; i < infoTagIdsLen; i++) {
            list.add("INSERT INTO infor_tag VALUES(" + infoId + "," + infoTagIds[i] + ")");
        }
        ExecResult execResult = jsonResponse.getExecResult(list, "", "");
        return execResult;
    }

    public JSONArray getAllCustomerByScheme(List<String> list) {
        //获取该标签关联的客户，包括他们的联系方式
        int listLen = list.size();
        List<String> listWhere = new ArrayList<>();
        for (int i = 0; i < listLen; i++) {
            if (i == 0) {
                listWhere.add(" a.customer_scheme = " + list.get(i));
            } else {
                listWhere.add(" OR a.customer_scheme = " + list.get(i));
            }
        }

        // 启用
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat = simpleDateFormat.format(date);
        List<String> sqlList = new ArrayList<>();
        sqlList.add(" SELECT a.*,b.qq_number FROM (SELECT  a.*,b.get_number,b.get_remark,b.get_type,c.scheme_plan_id  ");
        sqlList.add(" FROM sys_post_customer a, sys_customer_get b ,sys_scheme c ");
        sqlList.add(" WHERE a.customer_status = 1 AND  a.customer_start_time <='" + dateFormat + "' AND  a.customer_end_time >='" + dateFormat + "'  AND (" + StringUtils.join(listWhere, "") + ")  ");
        sqlList.add(" AND a.id = b.post_customer_id AND a.customer_scheme = c.id ) a LEFT JOIN sys_qq b  ON a.customer_post_qq = b.id ");
        ExecResult execResult = jsonResponse.getSelectResult(StringUtils.join(sqlList, ""), null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        return jsonArray;
    }

    // 得到所有需要发送信息的平台
    public JSONArray getAllCustomerByModule(List<String> list) {
        int listLen = list.size();
        List<String> listWhere = new ArrayList<>();
        for (int i = 0; i < listLen; i++) {
            if (i == 0) {
                listWhere.add(" a.module_id = " + list.get(i));
            } else {
                listWhere.add(" OR a.module_id = " + list.get(i));
            }
        }
        List<String> sqlList = new ArrayList<>();
        sqlList.add(" SELECT a.*,b.*,c.terrace_module_name FROM (SELECT * FROM  terrace_module a  WHERE ( " + StringUtils.join(listWhere, "") + " ) ) a , ");
        sqlList.add(" sys_terrace b,sys_terrace_module c WHERE a.terrace_id = b.id AND a.module_id = c.id ");
        ExecResult execResult = jsonResponse.getSelectResult(StringUtils.join(sqlList, ""), null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        return jsonArray;
    }

    public String getPostTime(String schemeId, Date nowTime) {
        String weekDate = DifTimeGet.getWeekTime(nowTime);
        String sql = "SELECT * FROM  sys_scheme a , sys_plan b WHERE a.scheme_plan_id = b.id AND a.id = " + schemeId;
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        return jsonObject.getString(weekDate);
    }

    @Override
    public JSONObject getAllInfoChoose(String pageNumber, String pageSize,
                                       String searchTagId, String searchInfoData,
                                       String customerName) {
/*        JSONObject jsonObject = new JSONObject();
        int pageNumberInt = Integer.parseInt(pageNumber, 10);
        int pageSizeInt = Integer.parseInt(pageSize, 10);
        JSONObject jsonObjectData = JSON.parseObject(searchInfoData);
        String chooseTime = jsonObjectData.getString("infor_createtime");
        jsonObjectData.remove("infor_createtime");


        String[] searchTagIds = searchTagId.split(",");
        int searchTagIdsLen = searchTagIds.length;
        String[] customerNames = customerName.split("\\|");
        int customerNamesLen = customerNames.length;

        List<String> list = new ArrayList<>();


        list.add(" SELECT b.* FROM (SELECT a.id FROM ");
        list.add(" (SELECT  a.*, b.customer_name FROM (SELECT  a.*,c.scheme_id,c.tag_id, d.user_name  ");
        list.add(" FROM sys_infor a,infor_tag b, sys_scheme_tag_base c ,sys_user d  ");
        list.add(" WHERE a.id = b.infor_id AND b.tag_id = c.tag_id AND a.infor_creater = d.user_loginname ) a ");
        list.add(" LEFT JOIN sys_post_customer b  ON a.scheme_id = b.customer_scheme ");
        if (jsonObjectData.isEmpty() &&
                "".equals(searchTagId) &&
                "".equals(customerName)) {

        } else {
            list.add("  WHERE ");
        }
        if (!"".equals(searchTagId)) {
            for (int i = 0; i < searchTagIdsLen; i++) {
                if (searchTagIdsLen == 1) {
                    list.add("  a. tag_id =  " + searchTagIds[i]);
                } else {
                    if (i == 0) {
                        list.add("( a. tag_id = " + searchTagIds[i]);
                    } else if (i == (searchTagIdsLen - 1)) {
                        list.add(" OR a. tag_id = " + searchTagIds[i] + ") ");
                    } else {
                        list.add(" OR a. tag_id = " + searchTagIds[i]);
                    }
                }
            }
        }
        Set<String> set = jsonObjectData.keySet();
        Iterator<String> iterator = set.iterator();
        int m = 0;
        while (iterator.hasNext()) {
            String jsonObjectValue = iterator.next();
            if ("".equals(searchTagId)) {
                //如果查询条件没有标签时。
                if (m == 0) {
                    list.add("  a." + jsonObjectValue + " LIKE '%" + jsonObjectData.getString(jsonObjectValue) + "%' ");
                } else {
                    list.add(" AND a." + jsonObjectValue + " LIKE '%" + jsonObjectData.getString(jsonObjectValue) + "%' ");
                }
            } else {
                list.add(" AND a." + jsonObjectValue + " LIKE '%" + jsonObjectData.getString(jsonObjectValue) + "%' ");
            }
            m++;
        }
        if (!"".equals(customerName)) {
            for (int i = 0; i < customerNamesLen; i++) {
                if (customerNamesLen == 1) {
                    list.add(" AND  b.customer_name LIKE '%" + customerNames[i] + "%' ");
                } else {
                    if (i == 0) {
                        list.add(" AND ( b.customer_name LIKE '%" + customerNames[i] + "%' ");
                    } else if (i == (customerNamesLen - 1)) {
                        list.add(" OR b.customer_name LIKE '%" + customerNames[i] + "%' ) ");
                    } else {
                        list.add(" OR b.customer_name LIKE '%" + customerNames[i] + "%'");
                    }
                }
            }
        }
        list.add("  ) a ");
        list.add(" GROUP BY a.id ) a ,(SELECT * ,GROUP_CONCAT(a.name) AS tag_names,GROUP_CONCAT(a.tag_id) AS tag_ids ");
        list.add(" FROM (SELECT  a.*,c.name,b.tag_id,d.user_name,d.user_loginname ");
        list.add(" FROM sys_infor a,infor_tag b,sys_tag c,sys_user d");
        list.add(" WHERE a.id = b.infor_id  AND b.tag_id = c.id  AND a.infor_creater = d.user_loginname) a ");
        list.add(" GROUP BY a.id) b ");
        if ("".equals(chooseTime) || chooseTime == null) {
            list.add(" WHERE a.id = b.id  ORDER BY b.infor_createtime DESC ");
        } else {
            String[] chooseTimes = chooseTime.split("&");
            list.add(" WHERE a.id = b.id AND  b.infor_createtime > '" + chooseTimes[0] + "' AND  b.infor_createtime < '" + chooseTimes[1] + "' ORDER BY b.infor_createtime DESC  ");
        }

        String sql = StringUtils.join(list, "");
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        if (jsonArray == null) {
            jsonObject.put("total", 0);
        } else {
            jsonObject.put("total", jsonArray.size());
        }
        list.add("LIMIT " + ((pageNumberInt - 1) * pageSizeInt) + "," + pageSizeInt + "");
        sql = StringUtils.join(list, "");
        System.out.println(sql);
        ExecResult execResult2 = jsonResponse.getSelectResult(sql, null, "");
        JSONArray jsonArray2 = (JSONArray) execResult2.getData();
        jsonObject.put("data", jsonArray2);
        return jsonObject;*/
        JSONObject jsonObject = new JSONObject();
        int pageNumberInt = Integer.parseInt(pageNumber, 10);
        int pageSizeInt = Integer.parseInt(pageSize, 10);
        // 搜索条件的普通条件
        JSONObject jsonObjectData = JSON.parseObject(searchInfoData);
        String chooseTime = jsonObjectData.getString("infor_createtime");
        jsonObjectData.remove("infor_createtime");
        String userName = jsonObjectData.getString("user_name");
        jsonObjectData.remove("user_name");
        // 搜索条件中的标签
        String[] searchTagIds = searchTagId.split(",");
        int searchTagIdsLen = searchTagIds.length;
        // 搜索条件中的用户名称
        String[] customerNames = customerName.split("\\|");
        int customerNamesLen = customerNames.length;
        List<String> list = new ArrayList<>();
        list.add("SELECT a.*,GROUP_CONCAT(a.tag_id) AS tag_ids,GROUP_CONCAT(a.name) AS tag_names ");
        list.add("FROM (SELECT a.*,b.customer_name FROM (SELECT a.*,c.scheme_id ");
        list.add("FROM (SELECT a.*,b.tag_id,c.user_name,d.name FROM sys_infor a ,infor_tag b ,sys_user c,sys_tag d ");
        list.add("WHERE a.id = b.infor_id AND a.infor_creater = c.user_loginname ");

        list.add("AND b.tag_id=d.id  ");
        /*list.add("SELECT a.*,GROUP_CONCAT(a.tag_id) AS tag_ids,GROUP_CONCAT(a.name) AS tag_names FROM ");
        list.add("(SELECT a.*,b.customer_name FROM (SELECT a.*,b.name,c.scheme_id FROM ");
        list.add("(SELECT * FROM(SELECT a.*,b.tag_id,c.user_name FROM sys_infor a ");
        list.add("LEFT JOIN infor_tag b ON a.id = b.infor_id ");
        list.add("LEFT JOIN sys_user c ON a.infor_creater = c.user_loginname ");*/
        if (!"".equals(searchTagId)) {
            List tagList = new ArrayList();
            tagList.add("(");
            for (int i = 0; i < searchTagIdsLen; i++) {
                if (i == 0) {
                    tagList.add(" b.tag_id ='" + searchTagIds[i] + "'");
                } else {
                    tagList.add("OR b.tag_id ='" + searchTagIds[i] + "'");
                }
            }
            tagList.add(")");
            list.add(" AND " + StringUtils.join(tagList, ""));
        }
        Set<String> set = jsonObjectData.keySet();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String jsonObjectValue = iterator.next();
            list.add(" AND  a." + jsonObjectValue + " LIKE '%" + jsonObjectData.getString(jsonObjectValue) + "%' ");
        }

        if ("".equals(chooseTime) || chooseTime == null) {
        } else {
            String[] chooseTimes = chooseTime.split("&");
            list.add(" AND a.infor_createtime > '" + chooseTimes[0] + "' AND  a.infor_createtime < '" + chooseTimes[1] + "'");
        }
        if ("".equals(userName) || userName == null) {
        } else {
            list.add("  AND c.user_name LIKE '%" + userName + "%' ");
        }
        /*// 没有基本的搜索条件
        if (jsonObjectData.isEmpty()) {
            if ("".equals(chooseTime) || chooseTime == null) {
            } else {
                String[] chooseTimes = chooseTime.split("&");
                list.add(" WHERE a.infor_createtime > '" + chooseTimes[0] + "' AND  a.infor_createtime < '" + chooseTimes[1] + "'");
            }
        } else {
            if ("".equals(chooseTime) || chooseTime == null) {
            } else {
                String[] chooseTimes = chooseTime.split("&");
                list.add(" AND a.infor_createtime > '" + chooseTimes[0] + "' AND  a.infor_createtime < '" + chooseTimes[1] + "'");
            }
        }*/
        /*list.add("WHERE a.infor_title = '2312' ");*/
        /*list.add(") a ");
        list.add("LEFT JOIN sys_tag b ON a.tag_id = b.id ");
        list.add("LEFT JOIN sys_scheme_tag_base c ON a.tag_id = c.tag_id ) a LEFT JOIN sys_post_customer b ");*/
        list.add(" ) a ");
        list.add("LEFT JOIN sys_scheme_tag_base c ");
        list.add("ON a.tag_id = c.tag_id ) a ");
        list.add("LEFT JOIN sys_post_customer b ON a.scheme_id = b.customer_scheme  ");
        if (!"".equals(customerName)) {
            for (int i = 0; i < customerNamesLen; i++) {
                if (i == 0) {
                    list.add(" WHERE  b.customer_name LIKE '%" + customerNames[i] + "%' ");
                } else {
                    list.add(" AND  b.customer_name LIKE '%" + customerNames[i] + "%' ");
                }
            }
        }
        list.add(" ) a ");
        list.add("GROUP BY a.id ORDER BY a.infor_createtime DESC  ");
        String sql = StringUtils.join(list, "");
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        if (jsonArray == null) {
            jsonObject.put("total", 0);
        } else {
            jsonObject.put("total", jsonArray.size());
        }
        list.add("LIMIT " + ((pageNumberInt - 1) * pageSizeInt) + "," + pageSizeInt + "");
        sql = StringUtils.join(list, "");
        ExecResult execResult2 = jsonResponse.getSelectResult(sql, null, "");
        JSONArray jsonArray2 = (JSONArray) execResult2.getData();
        JSONArray jsonArrayData = new JSONArray();

        if (!"".equals(customerName)) {
            List listCustomerName = new ArrayList();
            /*SELECT b.scheme_imp,b.scheme_no_imp,b.scheme_link,b.scheme_no_link
            FROM sys_post_customer a , sys_scheme b
            WHERE (a.customer_name LIKE'%昆山宣传部%' OR  a.customer_name LIKE '%菏泽单县公安%')
            AND  a.customer_scheme = b.id*/
            listCustomerName.add("SELECT b.scheme_imp,b.scheme_no_imp,b.scheme_link,b.scheme_no_link " +
                    "FROM sys_post_customer a , sys_scheme b  " +
                    "WHERE (  ");
            for (int i = 0; i < customerNamesLen; i++) {
                if (i == 0) {
                    listCustomerName.add(" a.customer_name LIKE'%" + customerNames[0] + "%' ");
                } else {
                    listCustomerName.add(" OR a.customer_name LIKE'%" + customerNames[0] + "%' ");
                }
            }
            listCustomerName.add(" ) AND  a.customer_scheme = b.id ");

            ExecResult execResultImp = jsonResponse.getSelectResult(StringUtils.join(listCustomerName, ""), null, "");
            JSONArray jsonArrayImp = (JSONArray) execResultImp.getData();

            for (int n = 0; n < jsonArray2.size(); n++) {
                JSONObject jsonObjectImpData = jsonArray2.getJSONObject(n);
                String title = jsonObjectImpData.getString("infor_title");
                String content = jsonObjectImpData.getString("infor_context");
                for (int m = 0; m < jsonArrayImp.size(); m++) {
                    JSONObject jsonObjectImp = jsonArrayImp.getJSONObject(m);
                    String dataImp = jsonObjectImp.getString("scheme_imp");
                    System.out.println(dataImp);
                    if (!"".equals(dataImp)) {
                        System.out.println("执行那一步");
                        String[] impWords = dataImp.split("\\|");
                        int impWordsLen = impWords.length;
                        int i;
                        for (i = 0; i < impWordsLen; i++) {
                            if (title.indexOf(impWords[i]) != -1 || content.indexOf(impWords[i]) != -1) {
                                jsonArrayData.add(jsonObjectImpData);
                                System.out.println("满足条件");
                                break;
                            }
                        }
                    } else {
                        System.out.println("执行这一步");
                        jsonArrayData.add(jsonObjectImpData);
                    }
                }
            }
        } else {
            jsonArrayData = jsonArray2;
        }
        jsonObject.put("data", jsonArrayData);
        return jsonObject;
    }

    @Override
    public ExecResult deleteInfoData(String infoId) {
        String[] infoIdS = infoId.split(",");
        int infoIdSLen = infoIdS.length;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < infoIdSLen; i++) {
            list.add("DELETE FROM sys_infor WHERE  id= " + infoIdS[i]);
            list.add("DELETE FROM infor_tag WHERE  infor_id= " + infoIdS[i]);
        }
        ExecResult execResult = jsonResponse.getExecResult(list, "", "");
        return execResult;
    }

    //给平台客户发送信息
    public void postTerraceCustomer(String tagId, String inforGrade, String infoContext, String infoTitle, String infoLink, String infoSource, String infoType, String infoSite) {
        String[] tagIdS = tagId.split(",");
        int tagIdLen = tagIdS.length;
        List<String> tagIdWhere = new ArrayList<>();
        for (int i = 0; i < tagIdLen; i++) {
            if (i == 0) {
                tagIdWhere.add(" tag_id =  " + tagIdS[i]);
            } else {
                tagIdWhere.add(" OR tag_id =  " + tagIdS[i]);
            }
        }
        List<String> sqlListModule = new ArrayList<>();
        sqlListModule.add(" SELECT b.* FROM (SELECT terrace_module_id FROM sys_terrace_module_tag_base ");
        sqlListModule.add(" WHERE ( " + StringUtils.join(tagIdWhere, "") + " )) a,sys_terrace_module b ");
        sqlListModule.add(" WHERE a.terrace_module_id = b.id AND b.terrace_module_grade LIKE '%" + inforGrade + "%' ");
        // 1：获取到符合标签和等级的模块
        ExecResult execResult = jsonResponse.getSelectResult(StringUtils.join(sqlListModule, ""), null, "");
        // 2：匹配排除关键词和匹配关键词是否合适
        if (execResult.getResult() > 0) {
            List<String> listModule = new ArrayList<>();
            JSONArray jsonArrayModule = (JSONArray) execResult.getData();
            int jsonLen = jsonArrayModule.size();
            for (int i = 0; i < jsonLen; i++) {
                JSONObject jsonObjectModule = jsonArrayModule.getJSONObject(i);
                String moduleId = jsonObjectModule.getString("id");
                String impWord = jsonObjectModule.getString("terrace_module_imp");
                String noImpWord = jsonObjectModule.getString("terrace_module_no_imp");
                if (!MessagePost.judgeWord(noImpWord, infoContext, infoTitle) || noImpWord.equals("")) {
                    System.out.println("平台:不包含排除关键词");
                    //不包含排除关键词，如果包含
                    if (impWord.equals("")) {
                        //执行发标签 执行的事情是一样的
                        System.out.println("平台:匹配关键词为空");
                        listModule.add(moduleId);
                    } else {
                        System.out.println("平台:匹配关键词不为空");
                        if (MessagePost.judgeWord(impWord, infoContext, infoTitle)) {
                            //执行发标签 执行的事情是一样的
                            listModule.add(moduleId);
                        } else {
                            System.out.println("平台:不包含匹配关键词，不发送");
                        }
                    }
                } else {
                    System.out.println("平台:包含了排除关键词");
                }

            }
            //所有需要发送信息的方案
            if (listModule.size() > 0) {
                //立刻发送
                JSONArray allTerraceCustomer = getAllCustomerByModule(listModule);
                if (allTerraceCustomer == null) {
                } else {
                    qqAsyncMessagePost.postMessAgeTerrace(allTerraceCustomer, infoContext, infoTitle, infoLink, infoSource, infoType, infoSite);
                }
            }
        }
    }

    @Override
    public ExecResult manualPost(String infoId, String customerId) {
        // 手共发送
        List<String> customerInfo = new ArrayList<>();
        customerInfo.add(" SELECT b.*,c.qq_number FROM sys_post_customer a ,sys_customer_get b,sys_qq c ");
        customerInfo.add(" WHERE a.id = b.post_customer_id AND a.customer_post_qq = c.id AND a.id = " + customerId);
        String customerSql = StringUtils.join(customerInfo, "");
        ExecResult execResult = jsonResponse.getSelectResult(customerSql, null, "");

        JSONArray jsonArray = (JSONArray) execResult.getData();

        int customerLen = jsonArray.size();
        String[] infoIdS = infoId.split(",");
        int infoIdSLen = infoIdS.length;
        for (int i = 0; i < infoIdSLen; i++) {
            String infoData = " SELECT a.infor_title,a.infor_context,a.infor_link,a.infor_source FROM sys_infor a WHERE a.id =  " + infoIdS[i];
            execResult = jsonResponse.getSelectResult(infoData, null, "");
            JSONArray jsonArrayData = (JSONArray) execResult.getData();
            // 一条信息
            JSONObject jsonObject = jsonArrayData.getJSONObject(0);
            String infoTitle = jsonObject.getString("infor_title");
            String infoContext = jsonObject.getString("infor_context");
            String infoLink = jsonObject.getString("infor_link");
            String infoSource = jsonObject.getString("infor_source");
            for (int j = 0; j < customerLen; j++) {
                JSONObject jsonObjectData = jsonArray.getJSONObject(j);
                String getType = jsonObjectData.getString("get_type");
                String getNumber = jsonObjectData.getString("get_number");
                String qqNumber = jsonObjectData.getString("qq_number");
                if ("number".equals(getType)) {
                    System.out.println("发送手机");
                    numberInfoPost.sendMsgByYunPian(infoLink, getNumber);
                } else {

                    // 定时发qq消息
                    int timeNumber = 5 + (int) Math.random() * 4;
                    try {
                        TimeUnit.SECONDS.sleep(timeNumber);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    boolean flag = qqMessagePost.postMessage(getNumber, qqNumber, getType, infoTitle, infoContext, infoLink, infoSource);
                    if (!flag) {
                        numberInfoPost.sendMsgByYunPian(qqNumber + "消息发送失败了!", "18752002129");
                        failData.qqResend(getNumber, qqNumber, getType, infoTitle, infoContext, infoLink, infoSource);
                    }
                }
            }
        }
        return execResult;
    }

    @Override
    public String exportData(String searchTagId, String searchInfoData, String customerName, String exportType) {
        JSONObject jsonObjectData = JSON.parseObject(searchInfoData);
        String chooseTime = jsonObjectData.getString("infor_createtime");
        jsonObjectData.remove("infor_createtime");
        String userName = jsonObjectData.getString("user_name");
        jsonObjectData.remove("user_name");
        String[] searchTagIds = searchTagId.split(",");
        int searchTagIdsLen = searchTagIds.length;
        // 搜索条件中的用户名称
        String[] customerNames = customerName.split("\\|");
        int customerNamesLen = customerNames.length;
        List sqlList = new ArrayList();
        sqlList.add("SELECT a.*,b.user_loginname,c.tag_id ,f.customer_name " +
                "FROM  sys_infor a, sys_user b,infor_tag c,sys_tag d,sys_scheme_tag_base e,sys_post_customer f " +
                "WHERE a.infor_creater = b.user_loginname AND  a.id = c.infor_id AND c.tag_id = d.id  " +
                "AND c.tag_id = e.tag_id AND e.scheme_id = f.customer_scheme ");
        String sqlExport = "";
        if (!"".equals(customerName)) {
            List customerNameList = new ArrayList();
            customerNameList.add("(");
            for (int i = 0; i < customerNamesLen; i++) {
                if (i == 0) {
                    customerNameList.add(" f.customer_name = '" + customerNames[i] + "' ");
                } else {
                    customerNameList.add(" OR f.customer_name = '" + customerNames[i] + "'");
                }
            }
            customerNameList.add(")");
            sqlList.add(" AND " + StringUtils.join(customerNameList, "") + "");
        }
        if (!"".equals(searchTagId)) {
            List areaList = new ArrayList();
            areaList.add(" (");
            for (int i = 0; i < searchTagIdsLen; i++) {
                if (i == 0) {
                    areaList.add(" c.tag_id = " + searchTagIds[i]);
                } else {
                    areaList.add(" OR c.tag_id = " + searchTagIds[i]);
                }
            }
            areaList.add(" )");
            sqlList.add(" AND " + StringUtils.join(areaList, "") + "");
        }
        if ("".equals(chooseTime) || chooseTime == null) {
        } else {
            String[] chooseTimes = chooseTime.split("&");
            sqlList.add(" AND a.infor_createtime > '" + chooseTimes[0] + "' AND  a.infor_createtime < '" + chooseTimes[1] + "'");
        }
        if ("".equals(userName) || userName == null) {
        } else {
            sqlList.add(" AND b.user_name LIKE'%" + userName + "%' ");
        }

        Set<String> set = jsonObjectData.keySet();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String jsonObjectValue = iterator.next();
            sqlList.add(" AND a." + jsonObjectValue + " LIKE '%" + jsonObjectData.getString(jsonObjectValue) + "%' ");
        }
        sqlList.add(" GROUP BY a.id ");
        sqlExport = StringUtils.join(sqlList, "");
        System.out.println(sqlExport);
        ExecResult execResult = jsonResponse.getSelectResult(sqlExport, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        JSONArray jsonArrayData = new JSONArray();

        if (jsonArray != null) {
            if (!"".equals(customerName)) {
                List list = new ArrayList();
            /*SELECT b.scheme_imp,b.scheme_no_imp,b.scheme_link,b.scheme_no_link
            FROM sys_post_customer a , sys_scheme b
            WHERE (a.customer_name LIKE'%昆山宣传部%' OR  a.customer_name LIKE '%菏泽单县公安%')
            AND  a.customer_scheme = b.id*/
                list.add("SELECT b.scheme_imp,b.scheme_no_imp,b.scheme_link,b.scheme_no_link " +
                        "FROM sys_post_customer a , sys_scheme b  " +
                        "WHERE (  ");
                for (int i = 0; i < customerNamesLen; i++) {
                    if (i == 0) {
                        list.add(" a.customer_name LIKE'%" + customerNames[0] + "%' ");
                    } else {
                        list.add(" OR a.customer_name LIKE'%" + customerNames[0] + "%' ");
                    }
                }
                list.add(" ) AND  a.customer_scheme = b.id ");

                ExecResult execResultImp = jsonResponse.getSelectResult(StringUtils.join(list, ""), null, "");
                JSONArray jsonArrayImp = (JSONArray) execResultImp.getData();
                for (int n = 0; n < jsonArray.size(); n++) {
                    JSONObject jsonObjectImpData = jsonArray.getJSONObject(n);
                    String title = jsonObjectImpData.getString("infor_title");
                    String content = jsonObjectImpData.getString("infor_context");
                    for (int m = 0; m < jsonArrayImp.size(); m++) {
                        JSONObject jsonObjectImp = jsonArrayImp.getJSONObject(m);
                        String dataImp = jsonObjectImp.getString("scheme_imp");
                        if (!"".equals(dataImp)) {
                            System.out.println("执行那一步");
                            String[] impWords = dataImp.split("\\|");
                            int impWordsLen = impWords.length;
                            int i;
                            for (i = 0; i < impWordsLen; i++) {
                                if (title.indexOf(impWords[i]) != -1 || content.indexOf(impWords[i]) != -1) {
                                    jsonArrayData.add(jsonObjectImpData);
                                    System.out.println("满足条件");
                                    break;
                                }
                            }
                        } else {
                            System.out.println("执行这一步");
                            jsonArrayData.add(jsonObjectImpData);
                        }
                    }
                }
            } else {
                jsonArrayData = jsonArray;
            }
            String returnResult = "";
            if (jsonArrayData == null || jsonArray == null) {
                return "";
            } else {
                if (jsonArrayData.size() == 0) {
                    return "";
                } else {
                    returnResult = dataExport.exportInforData(jsonArrayData, exportType);
                    return returnResult;
                }
            }
        } else {
            return "";
        }
    }

/*    // 快速排序的一趟分划
    int completeJ(int leftNumber, int rightNumber) {
        int[] a = {3, 2, 1, 4};
        int i = leftNumber;
        int j = rightNumber + 1;
        do {
            do {
                i++;
            } while (a[i] < a[leftNumber]);
            do {
                j--;
            } while (a[j] > a[leftNumber]);
            if (i < j) {
                int t;
                t = a[i];
                a[i] = a[j];
                a[j] = t;
            }
        } while (i < j);
        int t;
        t = a[leftNumber];
        a[leftNumber] = a[j];
        a[j] = t;
        for (int m = 0; m < a.length; m++) {
            System.out.println(a[m]);
        }
        return 0;
    }

    public static void main(String[] args) {
        InForService inForService = new InForService();
        inForService.completeJ(0, 3);
    }*/
}
