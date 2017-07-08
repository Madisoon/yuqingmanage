package com.syx.yuqingmanage.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.sun.xml.internal.xsom.impl.ListSimpleTypeImpl;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.collections.map.HashedMap;

import javax.print.attribute.standard.DocumentName;

/**
 * Created by Msater Zg on 2017/4/7.
 */
public class DocumentHandler {
    private Configuration configuration = null;

    public DocumentHandler() {
        configuration = new Configuration();
        configuration.setDefaultEncoding("utf-8");
    }

    public File createDoc(Map dataMap, String fileName, String templateName) throws UnsupportedEncodingException {
        //dataMap 要填入模本的数据文件
        //设置模本装置方法和路径,FreeMarker支持多种模板装载方法。可以重servlet，classpath，数据库装载，
        //这里我们的模板是放在template包下面
        /*System.out.println(this.getClass().getPackage());*/
        configuration.setClassForTemplateLoading(this.getClass(), "/");
        /*try {
            configuration.setDirectoryForTemplateLoading(new File("D:/"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        Template t = null;
        try {
            //test.ftl为要装载的模板
            t = configuration.getTemplate(templateName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //输出文档路径及名称
        File outFile = new File(fileName);
        Writer out = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outFile);
            OutputStreamWriter oWriter = new OutputStreamWriter(fos, "UTF-8");
            //这个地方对流的编码不可或缺，使用main（）单独调用时，应该可以，但是如果是web请求导出时导出后word文档就会打不开，并且包XML文件错误。主要是编码格式不正确，无法解析。
            //out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
            out = new BufferedWriter(oWriter);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        try {
            t.process(dataMap, out);
            out.close();
            fos.close();
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outFile;
        //System.out.println("---------------------------");
    }

    public List getInfoListData(JSONArray jsonArray) {
        List list = new ArrayList();
        int jsonArrayLen = jsonArray.size();
        for (int i = 0; i < jsonArrayLen; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Map map = new HashMap();
            map.put("index", String.valueOf(i + 1));
            map.put("title", jsonObject.getString("infor_title"));
            map.put("source", jsonObject.getString("infor_source"));
            map.put("time", jsonObject.getString("infor_createtime"));
            map.put("context", jsonObject.getString("infor_context"));
            map.put("link", jsonObject.getString("infor_link").replaceAll("&", "&amp;"));
            list.add(map);
        }
        return list;
    }

    public static void main(String[] args) {

        DocumentHandler documentHandler = new DocumentHandler();
        String sql = " SELECT infor_title,infor_context,infor_link,infor_source,infor_createtime FROM sys_infor ";
        JSONResponse jsonResponse = new JSONResponse();
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        Map maps = new HashMap();


        maps.put("inforList", documentHandler.getInfoListData(jsonArray));
        maps.put("inforNumber", String.valueOf(documentHandler.getInfoListData(jsonArray).size() + 5));
        try {
            documentHandler.createDoc(maps, "D:/测试文档.xls", "");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
