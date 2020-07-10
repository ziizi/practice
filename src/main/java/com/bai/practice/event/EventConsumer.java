package com.bai.practice.event;

import com.alibaba.fastjson.JSONObject;
import com.bai.practice.entity.DiscussPost;
import com.bai.practice.entity.Event;
import com.bai.practice.entity.Message;
import com.bai.practice.service.DiscussPostService;
import com.bai.practice.service.ElasticSearchService;
import com.bai.practice.service.MessageService;
import com.bai.practice.util.CommunitConstant;
import javafx.beans.binding.ObjectExpression;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunitConstant {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Value("${wk.image.storage}")
    private String wkStoragePath;

    @Value("${wk.image.command}")
    private String wkImageCommand;

    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
    public void handelCommentMsg (ConsumerRecord record) {
        if (record == null) {
            logger.error("消息内容为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if (record == null) {
            logger.error("消息格式错误");
            return;
        }

        // 发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());

        if (!event.getData().isEmpty()) {
            for (Map.Entry<String,Object> entry : event.getData().entrySet()){
                content.put(entry.getKey(),entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));

        messageService.addMessage(message);
    }


    // 消费发帖事件
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handelPublishMsg(ConsumerRecord record) {
        if (record == null) {
            logger.error("消息内容为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if (record == null) {
            logger.error("消息格式错误");
            return;
        }

        DiscussPost discussPost = discussPostService.findDiscussPostById(event.getEntityId());
        elasticSearchService.saveDiscussPost(discussPost);
    }


    // 删除帖事件
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handelDeleteMsg(ConsumerRecord record) {
        if (record == null) {
            logger.error("消息内容为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if (record == null) {
            logger.error("消息格式错误");
            return;
        }

        DiscussPost discussPost = discussPostService.findDiscussPostById(event.getEntityId());
        elasticSearchService.deleteDiscussPost(event.getEntityId());
    }


    // 消费分享事件
    @KafkaListener(topics = {TOPIC_SHARE})
    public void handelShareMsg(ConsumerRecord record) {
        if (record == null) {
            logger.error("消息内容为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if (record == null) {
            logger.error("消息格式错误");
            return;
        }

        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        String cmd = wkImageCommand + " --quality 75 "
                + htmlUrl + " " + wkStoragePath + "/" +fileName + suffix;
        try {
            Runtime.getRuntime().exec(cmd);
            logger.info("生成长图成功！" + cmd);
        } catch (IOException e) {
            logger.error("生成长图失败！" + e.getMessage());
        }
    }
}
