package com.bai.practice;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = PracticeApplication.class)
public class KafkaTest {

    @Autowired
    private KafkaProducer producer;

    @Test
    public void kafkaTest() throws InterruptedException {
        producer.sendMsg("test","您好");
        producer.sendMsg("test","在吗");
        Thread.sleep(1000*10);
    }


}

@Component
class KafkaProducer { // 发送是主动的
    @Autowired
    private KafkaTemplate kafkaTemplate;


    public void sendMsg (String topic,String content) {
        kafkaTemplate.send(topic,content);
    }
}

@Component
class KafkaConsumer { // 处理消息是被动的
   @KafkaListener(topics = {"test"})
    public void handleMsg (ConsumerRecord record) {
       System.out.println(record.value());
   }
}