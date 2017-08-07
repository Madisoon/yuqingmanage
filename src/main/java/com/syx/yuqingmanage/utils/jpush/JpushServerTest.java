package com.syx.yuqingmanage.utils.jpush;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
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
public class JpushServerTest {
	private static final Logger logger = Logger
			.getLogger(JpushServerTest.class);

	private static final String appKey = "491ed179b9a7fafd2b63f4a0";
	private static final String masterSecret = "cfea2e829c6bab6f60e107d8";

	public static void main(String[] args) {
		JPushClient jpushClient = new JPushClient(masterSecret, appKey);
		JpushServerTest JpushServer = new JpushServerTest();

		String alias = "admin1502093770037";
		String alert = "11111";
		String id = "";
		String url = "";
		String type = "";
		String content = "";

		PushPayload payload1 = JpushServer.buildPushObject(alias, alert, id,
				url, type, content);

		// PushPayload payload2 = JpushServer.buildPushObject("蚌埠蔡黎明",
		// "2017安徽铜陵市义安区事业单位招聘面试成绩公示", "1", "1", "125291", "sys");

		try {
			PushResult result1 = jpushClient.sendPush(payload1);
			logger.info("Got result - " + result1);
			// PushResult result2 = jpushClient.sendPush(payload2);
			// logger.info("Got result - " + result2);

			Thread.sleep(1000);
		} catch (APIConnectionException e) {
			logger.error("Connection error. Should retry later. ", e);

		} catch (APIRequestException e) {
			logger.error(
					"Error response from JPush server. Should review and fix it. ",
					e);
			logger.info("HTTP Status: " + e.getStatus());
			logger.info("Error Code: " + e.getErrorCode());
			logger.info("Error Message: " + e.getErrorMessage());
			logger.info("Msg ID: " + e.getMsgId());
		} catch (InterruptedException e) {
			logger.error("Thread.sleep Exception", e);
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

}
