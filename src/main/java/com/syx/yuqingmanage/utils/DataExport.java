package com.syx.yuqingmanage.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.utils.freemark.XmlToDocx;
import com.syx.yuqingmanage.utils.freemark.XmlToExcel;
import freemarker.template.TemplateException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            Map map = new HashedMap();
            String inforContext = jsonObject.getString("infor_context").replaceAll("<系统提示：因该条信息内容过长，为节省您的存储空间，本软件只存储并显示其部分内容，详细内容请到原网站浏览>", " ");
            map.put("index", i + 1);
            map.put("title", jsonObject.getString("infor_title"));
            map.put("source", jsonObject.getString("infor_site"));
            map.put("time", jsonObject.getString("infor_createtime"));
            map.put("link", jsonObject.getString("infor_link").replaceAll("&", "&amp;"));
            String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】'；：”“’。，、？]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(inforContext);
            map.put("context", m.replaceAll("").trim());
            list.add(map);
        }
        Map maps = new HashMap();
        maps.put("inforList", list);
        String longTime = String.valueOf(System.currentTimeMillis());
        String fileName = "";
        String fileFtl = "";
        if ("word".equals(exportType)) {
            fileFtl = "word.ftl";
            String xmlTemp = "C:/dummyPath/freemarkTest.xml";
            try {
                documentHandler.createDoc(maps, xmlTemp, fileFtl);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            fileName = longTime + ".docx";
            /*String xmlTemplate = "word.xml";*/
            //设置docx的模板路径 和文件名
            String docxTemplate = "template/word.docx";
            String toFilePath = "C:/dummyPath/" + fileName; // 导出的路径
            //填充完数据的临时xml
/*            Writer w = new FileWriter(new File(xmlTemp));
            XmlToExcel.process(xmlTemplate, maps, w);
            //3.把填充完成的xml写入到docx中*/
            XmlToDocx xtd = new XmlToDocx();
            try {
                xtd.outDocx(new File(xmlTemp), docxTemplate, toFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            fileName = longTime + ".xls";
            fileFtl = "inforexcel.ftl";
            try {
                documentHandler.createDoc(maps, "C:/dummyPath/" + fileName, fileFtl);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }

    public String exeportHistoryInfor(JSONArray jsonArray, String exportType) {
        List list = new ArrayList();
        DocumentHandler documentHandler = new DocumentHandler();
        int jsonArrayLen = jsonArray.size();
        for (int i = 0; i < jsonArrayLen; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Map map = new HashMap();
            list.add(map);
        }
        Map maps = new HashMap();
        maps.put("inforList", list);
        String longTime = String.valueOf(System.currentTimeMillis());
        String fileName = "";
        String fileFtl = "";
        if ("word".equals(exportType)) {
            fileName = longTime + ".doc";
            /*fileFtl = "inforword.ftl";*/
        } else {
            fileName = longTime + ".xls";
            /*fileFtl = "inforexcel.ftl";*/
        }
        try {
            documentHandler.createDoc(maps, "C:/dummyPath/" + fileName, fileFtl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public String exportCustomerData(JSONArray jsonArray, String exportType) {
        List list = new ArrayList();
        DocumentHandler documentHandler = new DocumentHandler();
        int jsonArrayLen = jsonArray.size();
        for (int i = 0; i < jsonArrayLen; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String[] getNumbers = jsonObject.getString("get_numbers").split(",");
            String[] getTypes = jsonObject.getString("get_types").split(",");
            int getTypesLen = getTypes.length;
            List listQqNumber = new ArrayList();
            List listWeiNumber = new ArrayList();
            List listPhone = new ArrayList();
            for (int n = 0; n < getTypesLen; n++) {
                switch (getTypes[n]) {
                    case "weixinGroup":
                        listWeiNumber.add(getNumbers[n]);
                        break;
                    case "weixin":
                        listWeiNumber.add(getNumbers[n]);
                        break;
                    case "qq":
                        listQqNumber.add(getNumbers[n]);
                        break;
                    case "qqGroup":
                        listQqNumber.add(getNumbers[n]);
                        break;
                    case "number":
                        listPhone.add(getNumbers[n]);
                        break;
                    default:
                        break;
                }
            }
            Map map = new HashMap();
            map.put("index", i + 1);
            map.put("customerName", jsonObject.getString("customer_name"));
            map.put("postScheme", jsonObject.getString("scheme_name"));
            map.put("qqNumber", StringUtils.join(listQqNumber, "&"));
            map.put("phoneNumber", StringUtils.join(listPhone, "&"));
            map.put("weixinNumber", StringUtils.join(listWeiNumber, "&"));
            map.put("createPeople", jsonObject.getString("user_name"));
            map.put("expirationTime", jsonObject.getString("customer_end_time"));
            map.put("cutomerStatus", jsonObject.getString("customer_status").equals("1") ? "启用" : "停用");
            list.add(map);
        }
        Map maps = new HashMap();
        maps.put("customerList", list);
        String longTime = String.valueOf(System.currentTimeMillis());
        String fileName = "";
        String fileFtl = "";
        if ("word".equals(exportType)) {
            fileName = longTime + ".doc";
            fileFtl = "inforword.ftl";
        } else {
            fileName = longTime + ".xls";
            fileFtl = "exportcustomer.ftl";
        }
        try {
            documentHandler.createDoc(maps, "C:/dummyPath/" + fileName, fileFtl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public String exportHtmlUrl(String htmlContent, String fileNameUrl) {
        DocumentHandler documentHandler = new DocumentHandler();
        Map maps = new HashMap();
        maps.put("content", htmlContent);
        String fileName = "";
        String fileFtl = "";
        fileName = fileNameUrl + ".html";
        fileFtl = "newstemplate.ftl";
        try {
            documentHandler.createDoc(maps, "C:/dummyPath/" + fileName, fileFtl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fileName;
    }
}
