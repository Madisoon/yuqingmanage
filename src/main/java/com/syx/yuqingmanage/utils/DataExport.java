package com.syx.yuqingmanage.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import org.apache.commons.lang3.StringUtils;

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
            String inforContext = jsonObject.getString("infor_context").replaceAll("<系统提示：因该条信息内容过长，为节省您的存储空间，本软件只存储并显示其部分内容，详细内容请到原网站浏览>", " ");
            map.put("index", i + 1);
            map.put("title", jsonObject.getString("infor_title"));
            map.put("source", jsonObject.getString("infor_source"));
            map.put("time", jsonObject.getString("infor_createtime"));
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

    public String exportHtmlUrl(String htmlContent) {
        DocumentHandler documentHandler = new DocumentHandler();
        Map maps = new HashMap();
        maps.put("content", htmlContent);
        String longTime = String.valueOf(System.currentTimeMillis());
        String fileName = "";
        String fileFtl = "";
        fileName = longTime + ".html";
        fileFtl = "newstemplate.ftl";
        try {
            documentHandler.createDoc(maps, "C:/dummyPath/" + fileName, fileFtl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public static void main(String[] args) {
        DataExport dataExport = new DataExport();
        String url = dataExport.exportHtmlUrl("<p style=\"margin-top: 0px; margin-bottom: 0px; padding: 0px; line-height: 24px; color: rgb(51, 51, 51); text-align: justify; font-family: arial; white-space: normal;\">共享经济如火如荼，在摩拜、ofo单车第一轮先后融资6亿、7亿美元后，再次推高了共享经济的浪潮。</p><p style=\"margin-top: 22px; margin-bottom: 0px; padding: 0px; line-height: 24px; color: rgb(51, 51, 51); text-align: justify; font-family: arial; white-space: normal;\">现今，在一片争议声中，涌现了若干共享充电宝公司，共享雨伞也被吹的天花乱坠，共享包包、共享衣服更是层出不穷。有人戏称说：“干脆共享男友，共享女友好了”。</p><p style=\"margin-top: 22px; margin-bottom: 0px; padding: 0px; line-height: 24px; color: rgb(51, 51, 51); text-align: justify; font-family: arial; white-space: normal;\">导致共享经济遍地开花的原因，实际上是创业者的跟风，资本的盲目。这更加反映出当下互联网经济模式所驱动的“膨胀”。</p><p><img class=\"large\" src=\"/ueditor/jsp/upload/image/20170728/1501222168619020954.jpg\"/></p><p style=\"margin-top: 26px; margin-bottom: 0px; padding: 0px; line-height: 24px; color: rgb(51, 51, 51); text-align: justify; font-family: arial; white-space: normal;\">1</p><p style=\"margin-top: 22px; margin-bottom: 0px; padding: 0px; line-height: 24px; color: rgb(51, 51, 51); text-align: justify; font-family: arial; white-space: normal;\">“伪需求”：人们真的有那么多东西可以共享吗？</p><p style=\"margin-top: 22px; margin-bottom: 0px; padding: 0px; line-height: 24px; color: rgb(51, 51, 51); text-align: justify; font-family: arial; white-space: normal;\">互联网作为一个重要的载体，确实改变了人们生活的方方面面。共享经济的起源是来自于各个领域的人们所拥有的资源配置不平均。正因为有越来越多的人拥有闲置的资源，所以占有闲置资源的人愿意把他们的资源共享出来，并收取一定的费用获利。</p><p><img class=\"large\" src=\"/ueditor/jsp/upload/image/20170728/1501222168743041709.jpg\"/></p><p style=\"margin-top: 26px; margin-bottom: 0px; padding: 0px; line-height: 24px; color: rgb(51, 51, 51); text-align: justify; font-family: arial; white-space: normal;\">但是不是所有闲置的资源都一定有价值？人们对闲置资源的应用需求真的有那么大吗？如同共享充电宝，确实可以解决极少一部分人不带充电宝状态下的需求，但是却很难建立起规模化的格局。或许可以理解为，共享充电宝就是以一种变相的方式来获取用户，甚至有人戏称，如果租借充电宝的人不归还的话，相当于是在以共享充电宝的名义在卖充电宝。</p><p style=\"margin-top: 22px; margin-bottom: 0px; padding: 0px; line-height: 24px; color: rgb(51, 51, 51); text-align: justify; font-family: arial; white-space: normal;\">你听说过共享睡眠吗？中关村的“享睡空间”再次成为共享经济的新爆点，随后上海、成都等地陆续效仿，似乎一副燎原之火的架势。然而仅仅数日，政府约谈、停运停业，很快就寿终正寝。这几天，共享包包、共享衣服又被推到了风口浪尖。</p><p style=\"margin-top: 22px; margin-bottom: 0px; padding: 0px; line-height: 24px; color: rgb(51, 51, 51); text-align: justify; font-family: arial; white-space: normal;\">而共享包包、衣服等品类的一个共性特点就是要满足人们的炫耀性虚荣心理。就拿共享包包来说，如果要具备炫耀属性，可能需要偏奢侈品属性。然而，真正热爱时尚且具有一定经济实力的女性花数万元购买一个喜爱的包包，会在使用不久后便束之高阁甚至是共享出去供他人使用吗？并且，共享包包毫无疑问会带来许多清洁与卫生上的问题，更加容易折旧，这显然不是高收入群体女性追求更高物质需求的目的，对于这个阶层的女性而言，一般是不太愿意选择跟其他人共享私密物品的。</p><p><br/></p>");
        System.out.println(url);
    }
}
