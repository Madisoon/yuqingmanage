package com.syx.yuqingmanage.module.move.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;

/**
 * Created by Msater Zg on 2017/7/10.
 */
public interface ITopicService {
    public ExecResult insertTopicContext(String topicId, String topicInfo);

    public JSONObject getTopicContextByTopicId(String topicId, String pageSize, String pageNumber);

    public ExecResult deleteTopicContext(String topicContextId);

    public ExecResult checkTopicContext(String topicContextId);

    public ExecResult updateTopicContext(String topicContextId, String topicContext);

    public ExecResult insertTopic(String topicInfo);

    public ExecResult updateTopic(String topicId, String topicName);

    public ExecResult deleteTopic(String topicId);

    public JSONArray getAllTopic();
}
