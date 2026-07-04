package com.livestream.message.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 弹幕 Kafka 消费者
 * 消费 danmaku-topic 中的弹幕消息，用于跨服务异步处理（如审核、统计等）。
 * 当前仅做日志记录，后续可扩展为入库、审核等逻辑。
 */
@Slf4j
@Component
public class DanmakuKafkaConsumer {

    @KafkaListener(topics = "danmaku-topic", groupId = "message-consumer-group")
    public void consume(ConsumerRecord<String, String> record) {
        log.info("收到弹幕Kafka消息: topic={}, partition={}, offset={}, key={}, value={}",
                record.topic(), record.partition(), record.offset(), record.key(), record.value());
    }
}
