package com.syx.yuqingmanage.utils.freemark;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import freemarker.template.TemplateException;

/**
 * 其实docx属于zip的一种，这里只需要操作word/document.xml中的数据，其他的数据不用动
 *
 * @author yigehui
 */
public class XmlToDocx {

    /**
     * @param documentFile 动态生成数据的docunment.xml文件
     * @param docxTemplate docx的模板
     * @throws ZipException
     * @throws IOException
     */

    public void outDocx(File documentFile, String docxTemplate, String toFilePath) throws ZipException, IOException {

        try {
            String fileName = XmlToDocx.class.getClassLoader().getResource("").toURI().getPath() + docxTemplate;

            File docxFile = new File(fileName);
            ZipFile zipFile = new ZipFile(docxFile);
            Enumeration<? extends ZipEntry> zipEntrys = zipFile.entries();
            ZipOutputStream zipout = new ZipOutputStream(new FileOutputStream(toFilePath));
            int len = -1;
            byte[] buffer = new byte[1024];
            while (zipEntrys.hasMoreElements()) {
                ZipEntry next = zipEntrys.nextElement();
                InputStream is = zipFile.getInputStream(next);
                //把输入流的文件传到输出流中 如果是word/document.xml由我们输入
                zipout.putNextEntry(new ZipEntry(next.toString()));
                if ("word/document.xml".equals(next.toString())) {
                    //InputStream in = new FileInputStream(new File(XmlToDocx.class.getClassLoader().getResource("").toURI().getPath()+"template/word.xml"));
                    InputStream in = new FileInputStream(documentFile);
                    while ((len = in.read(buffer)) != -1) {
                        zipout.write(buffer, 0, len);
                    }
                    in.close();
                } else {
                    while ((len = is.read(buffer)) != -1) {
                        zipout.write(buffer, 0, len);
                    }
                    is.close();
                }
            }
            zipout.close();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, TemplateException {

        //xml的模板路径*/*
        String xmlTemplate = "word.xml";

        //设置docx的模板路径 和文件名
        String docxTemplate = "template/word.docx";
        String toFilePath = "d:\\123.docx"; // 导出的路径

        //填充完数据的临时xml
        String xmlTemp = "d:\\temp.xml";
        Writer w = new FileWriter(new File(xmlTemp));

        //1.需要动态传入的数据
        Map<String, Object> p = new HashMap<String, Object>();
        List<Map> students = new ArrayList<>();
        Map map = new HashMap();
        map.put("test", "中俄撒打算阿斯顿");
        students.add(map);
        students.add(map);
        students.add(map);
        p.put("test", "测试一下");
        p.put("students", students);

        //2.把map中的数据动态由freemarker传给xml
        XmlToExcel.process(xmlTemplate, p, w);

        //3.把填充完成的xml写入到docx中
        XmlToDocx xtd = new XmlToDocx();
        xtd.outDocx(new File(xmlTemp), docxTemplate, toFilePath);
    }
}
