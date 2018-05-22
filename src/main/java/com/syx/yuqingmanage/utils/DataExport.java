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
            map.put("title", jsonObject.getString("infor_title").trim());
            map.put("source", jsonObject.getString("infor_site"));
            map.put("time", jsonObject.getString("infor_createtime"));
            map.put("link", jsonObject.getString("infor_link").replaceAll("&", "&amp;"));
            String regEx = "[`~@#$%^&*()+=|{}'',\\[\\].<>/?~@#￥%……&*（）——+|{}【】'”“’]";
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
            // 导出的路径
            String toFilePath = "C:/dummyPath/" + fileName;
            //填充完数据的临时xml
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

    public String exeportHistoryInfor(JSONArray jsonArray, String customerName, String type) {
        List list = new ArrayList();
        DocumentHandler documentHandler = new DocumentHandler();
        int jsonArrayLen = jsonArray.size();
        for (int i = 0; i < jsonArrayLen; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Map map = new HashedMap();
            String inforContext = jsonObject.getString("infor_context").replaceAll("<系统提示：因该条信息内容过长，为节省您的存储空间，本软件只存储并显示其部分内容，详细内容请到原网站浏览>", " ");
            map.put("index", i + 1);
            String inforTitle = jsonObject.getString("infor_title");
            if (inforTitle.length() > 30) {
                map.put("title", inforTitle.substring(0, 30).trim() + "......");
            } else {
                map.put("title", inforTitle.trim());
            }
            map.put("site", jsonObject.getString("infor_site"));
            map.put("source", jsonObject.getString("infor_source"));
            map.put("author", jsonObject.getString("infor_author"));
            map.put("time", jsonObject.getString("gmt_create").substring(0, 16));
            map.put("link", jsonObject.getString("infor_link").replaceAll("&", "&amp;"));
            String regEx = "[`~@#$%^&*()+=|{}'',\\[\\].<>/?~@#￥%……&*（）——+|{}【】'”“’]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(inforContext);
            String tableContext = m.replaceAll("").trim();
            if (tableContext.length() > 200) {
                map.put("context", tableContext.substring(0, 200).trim() + "......");
            } else {
                map.put("context", tableContext.trim());
            }
            list.add(map);
        }
        Map maps = new HashMap();
        maps.put("inforList", list);
        maps.put("reportName", customerName);
        maps.put("reportTime", DateTimeUtils.getNowTime("yyyy 年 MM 月 dd 日"));
        String longTime = String.valueOf(System.currentTimeMillis());
        String fileName = "";
        String fileFtl = "";
        if ("word".equals(type)) {
            fileFtl = "newWord.ftl";
            /*String xmlTemp = "/Users/zg/htmlproject/freemarkTest.xml";*/
            String xmlTemp = "C:/dummyPath/freemarkTest.xml";
            try {
                documentHandler.createDoc(maps, xmlTemp, fileFtl);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            fileName = longTime + ".docx";
            //设置docx的模板路径 和文件名
            String docxTemplate = "template/newword.docx";
            /*String toFilePath = "/Users/zg/htmlproject/" + fileName;*/
            String toFilePath = "C:/dummyPath/" + fileName;
            //填充完数据的临时xml
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

    public String exportAppNoteExcel(JSONArray jsonArray) {
        List list = new ArrayList();
        DocumentHandler documentHandler = new DocumentHandler();
        int jsonArrayLen = jsonArray.size();
        for (int i = 0; i < jsonArrayLen; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Map map = new HashedMap();
            map.put("user", jsonObject.getString("note_create"));
            switch (jsonObject.getString("note_type")) {
                case "1":
                    map.put("name", "登陆");
                    break;
                case "2":
                    map.put("name", "查询");
                    break;
                case "3":
                    map.put("name", "详情");
                    break;
                default:
                    break;
            }

            String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】'；：”“’。，、？]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(jsonObject.getString("note_title"));
            map.put("title", m.replaceAll("").trim());
            map.put("time", jsonObject.getString("note_date"));
            list.add(map);
        }
        Map maps = new HashMap(16);
        maps.put("appList", list);
        String longTime = String.valueOf(System.currentTimeMillis());
        String fileName = "";
        String fileFtl = "";

        fileName = longTime + ".xls";
        fileFtl = "appnoteexcel.ftl";
        try {
            documentHandler.createDoc(maps, "C:/dummyPath/" + fileName, fileFtl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fileName;
    }
}
