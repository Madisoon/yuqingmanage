package com.syx.yuqingmanage.utils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Msater Zg on 2017/2/24.
 */
public class NumberInfoPost {
    public String sendMsgByYunPian(String content, String mobiles) {
        String URI_SEND_SMS = "http://sms.yunpian.com/v2/sms/batch_send.json";
        // 国际短信
        /*String URI_SEND_SMS = "http://sms.yunpian.com/v2/sms/batch_send.json";*/
        // 公司的云片账号
        String apikey = "149b85f2319da3c78e25b49d748c6e2d";
        String text = "【舆情平台】提醒:" + content;
        // 自己的云片账号
        /*String apikey = "b7788163efb1d1d9368b179da116bafe";
        String text = "【舆情服务通知】舆情通知:" + content + "。请注意查收！";*/
        Map<String, String> params = new HashMap<>();
        params.put("apikey", apikey);
        params.put("text", text);
        params.put("mobile", mobiles);
        String result = postMsg(URI_SEND_SMS, params);
        return result;
    }

    public String postMsg(String url, Map<String, String> paramsMap) {
        CloseableHttpClient client = HttpClients.createDefault();
        String ENCODING = "UTF-8";
        String responseText = "";
        CloseableHttpResponse response = null;
        try {
            HttpPost method = new HttpPost(url);
            if (paramsMap != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
                    paramList.add(pair);
                }
                method.setEntity(new UrlEncodedFormEntity(paramList, ENCODING));
            }
            response = client.execute(method);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseText = EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return responseText;
    }

    public static void main(String[] args) {
        NumberInfoPost numberInfoPost = new NumberInfoPost();
        System.out.println(numberInfoPost.sendMsgByYunPian("测试信息", "18752002129"));
    }
}
