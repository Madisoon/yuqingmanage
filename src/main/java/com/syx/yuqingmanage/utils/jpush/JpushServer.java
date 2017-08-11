package com.syx.yuqingmanage.utils.jpush;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

@Service
public class JpushServer {
    @Autowired
    JSONResponse jsonResponse;
    private static final Logger logger = Logger.getLogger(JpushServer.class);

    private static final String appKey = "491ed179b9a7fafd2b63f4a0";
    private static final String masterSecret = "cfea2e829c6bab6f60e107d8";

    // 极光的推送Client
    private JPushClient jpushClient;

    public void pushNotification(List<JpushBean> JpushBeanList) {

        List<PushPayload> payloads = buildPushObjects(JpushBeanList);
        for (PushPayload payload : payloads) {
            try {
                JPushClient jpushClient = getJpushClient();
                PushResult result = jpushClient.sendPush(payload);
                logger.info("Got result - " + result);
            } catch (APIConnectionException e) {
                logger.error(
                        "Connection error. Should retry later. 重新初始化client", e);
                jpushClient = new JPushClient(masterSecret, appKey);
            } catch (APIRequestException e) {
                logger.error(
                        "Error response from JPush server. Should review and fix it. ",
                        e);
                logger.info("HTTP Status: " + e.getStatus());
                logger.info("Error Code: " + e.getErrorCode());
                logger.info("Error Message: " + e.getErrorMessage());
                logger.info("Msg ID: " + e.getMsgId());
            } catch (Exception e) {
                logger.error("Exception", e);
            }
        }

    }

    private PushPayload buildPushObject(String alias, String alert, String id,
                                        String url, String type, String content) {
        Map<String, String> pushConfigAndroid = new HashMap<String, String>();
        pushConfigAndroid.put(JpushCommon.DETAIL_ID, id);
        pushConfigAndroid.put(JpushCommon.DETAIL_URL, url);
        pushConfigAndroid.put(JpushCommon.DETAIL_TYPE, type);
        pushConfigAndroid.put(JpushCommon.DETAIL_TITLE, alert);
        pushConfigAndroid.put(JpushCommon.DETAIL_CONTENT, content);

        Map<String, String> pushConfigIOS = new HashMap<String, String>();
        pushConfigIOS.put(JpushCommon.DETAIL_ID, id);
        pushConfigIOS.put(JpushCommon.DETAIL_URL, url);
        pushConfigIOS.put(JpushCommon.DETAIL_TYPE, type);
        pushConfigIOS.put(JpushCommon.DETAIL_TITLE, alert);
        pushConfigIOS.put(JpushCommon.DETAIL_CONTENT, content);

        return PushPayload
                .newBuilder()
                .setPlatform(Platform.android_ios())
                .setOptions(
                        Options.newBuilder().setApnsProduction(true).build())
                .setAudience(Audience.alias(alias))
                .setNotification(
                        Notification
                                .newBuilder()
                                .setAlert(alert)
                                .addPlatformNotification(
                                        AndroidNotification.newBuilder()
                                                .addExtras(pushConfigAndroid)
                                                .build())
                                .addPlatformNotification(
                                        IosNotification.newBuilder()
                                                .incrBadge(1)
                                                .addExtras(pushConfigIOS)
                                                .build()).build()).build();
    }

    private List<PushPayload> buildPushObjects(List<JpushBean> JpushBeanList) {
        List<PushPayload> list = new ArrayList<PushPayload>();

        // 遍历新消息,来确定该新消息需要推送给谁
        for (JpushBean jpushBean : JpushBeanList) {
            // 新消息的类型，用来确定消息的类型
            String type = jpushBean.getType();
            // 新消息所属板块的id
            String tagId = jpushBean.getTagId();
            // 推送需要的alert
            String alert = jpushBean.getTitle();
            // 推送所需要的id
            String id = jpushBean.getId();
            // 推送所需要的url
            String url = jpushBean.getUrl();
            // 焦点信息推送需要的简介
            String content = jpushBean.getContent();

            // 根据type和tagId来找到接收该模块推送的用户
            List<String> aliasList = getUserAlias(type, tagId);

            // 遍历aliasList来构造推送的消息
            for (String alias : aliasList) {
                PushPayload pushPayload = buildPushObject(alias, alert, id,
                        url, type, content);

                list.add(pushPayload);
            }

        }

        return list;
    }

    private List<String> getUserAlias(String type, String tagId) {
        // ==========================需要实现
        String sqlGet = " SELECT a.app_user_loginname,b.app_timestamp FROM app_user_program  a   " +
                "LEFT JOIN app_user b ON a.app_user_loginname = b.app_user_loginname  " +
                "LEFT JOIN app_user_config c ON a.id = c.tag_id " +
                " WHERE a.id = '" + tagId + "' AND c.tag_push = '1'";
        ExecResult execResult = jsonResponse.getSelectResult(sqlGet, null, "");
        List list = new ArrayList();
        if (execResult.getResult() == 1) {
            JSONArray jsonArray = (JSONArray) execResult.getData();
            int jsonArrayLen = jsonArray.size();
            for (int i = 0; i < jsonArrayLen; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                list.add(jsonObject.getString("app_user_loginname") + jsonObject.getString("app_timestamp"));
            }
        }
        return list;
    }

    private JPushClient getJpushClient() {
        if (jpushClient == null) {
            jpushClient = new JPushClient(masterSecret, appKey);
        }

        return jpushClient;
    }
}
